package com.example.ssdev;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    String latStr, latDMS, lonStr, lonDMS;
    GoogleMap gMap;

    private String getCoord(final float coord) {
        int coordDeg = (int)coord;
        BigDecimal coordMins = BigDecimal.valueOf(coord).subtract(
                BigDecimal.valueOf(Math.floor(coord))
        ).multiply(BigDecimal.valueOf(60));//*/
        BigDecimal coordSecs = coordMins.subtract(
                BigDecimal.valueOf(coordMins.intValue())
        ).multiply(BigDecimal.valueOf(60));
        return String.format("%d° %d' %d''", coordDeg, coordMins.intValue(), coordSecs.intValue());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("log", "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String DIR = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
        ).toString();
        File file = new File(DIR + File.separator + "data.csv");

        Button convertBtn = findViewById(R.id.convertBtn);
        Button expBtn = findViewById(R.id.exportBtn);
        Button showBtn = findViewById(R.id.showBtn);
        EditText latEText = findViewById(R.id.latEText);
        EditText lonEText = findViewById(R.id.lonEText);
        TextView textViewLat = findViewById(R.id.textViewLat);
        TextView textViewLon = findViewById(R.id.textViewLon);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {}
        }
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latStr = latEText.getText().toString();
                lonStr = lonEText.getText().toString();
                if (latStr.length() == 0 || lonStr.length() == 0) {
                    return;
                }
                latDMS = getCoord(Float.parseFloat(latStr));
                lonDMS = getCoord(Float.parseFloat(lonStr));
                textViewLat.setText("lat: " + latDMS);
                textViewLon.setText("lon: " + lonDMS);
                expBtn.setEnabled(true);
            }
        });
        expBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OutputStream os = new FileOutputStream(file, true);
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    if (file.length() == 0) {
                        osw.append("lat, lon, formatted lat, formatted lon \n");
                    }
                    osw.append(String.format("%s,%s,%s,%s\n", latStr, lonStr, latDMS, lonDMS));
                    osw.close();
                } catch (Exception e) {}
                expBtn.setEnabled(false);
            }
        });

        final OnMapReadyCallback onMapReadyCallback = this;
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_map);
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(onMapReadyCallback);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng latLng = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
        gMap.addMarker(new MarkerOptions().position(latLng).title("selected location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}