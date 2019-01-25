package com.googlemap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.googlemap.custom.GPSTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GPSTracker gps;
    private GoogleMap googleMap;


    private LatLng camera;
    double current_latitude, current_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // google
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            current_latitude = GlobalElements.NumberFormater(gps.getLatitude());
            current_longitude = GlobalElements.NumberFormater(gps.getLongitude());
            camera = new LatLng(current_latitude, current_longitude);
        } else {
            gps.showSettingsAlert();
        }

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //this.googleMap.getUiSettings().setCompassEnabled(false);
        //this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        updateLocationUI();
    }

    private void updateLocationUI() {
        try {
            if (googleMap != null) {
                camera = new LatLng(current_latitude, current_longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 16));
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 25), 4000, null);
                googleMap.addMarker(new MarkerOptions().position(camera).title("Craftbox")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                camera = new LatLng(22.281786, 70.768037);
                googleMap.addMarker(new MarkerOptions().position(camera).title("Atmiya")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                camera = new LatLng(22.279264, 70.768380);
                googleMap.addMarker(new MarkerOptions().position(camera).title("Siplan")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(Polygon polygon) {
                        // Flip the red, green and blue components of the polygon's stroke color.
                        polygon.setStrokeColor(polygon.getStrokeColor() ^ 0x00ffffff);
                    }
                });

                String[] top_left_lat_long = "22.279479, 70.770293".split(",");
                double top_left_lat = Double.parseDouble(top_left_lat_long[0]);
                double top_left_long = Double.parseDouble(top_left_lat_long[1]);

                String[] top_right_lat_long = "22.279635, 70.770803".split(",");
                double top_right_lat = Double.parseDouble(top_right_lat_long[0]);
                double top_right_long = Double.parseDouble(top_right_lat_long[1]);

                String[] bottom_left_lat_long = "22.279063, 70.770428".split(",");
                double bottom_left_lat = Double.parseDouble(bottom_left_lat_long[0]);
                double bottom_left_long = Double.parseDouble(bottom_left_lat_long[1]);

                String[] bottom_right_lat_long = "22.279259, 70.770944".split(",");
                double bottom_right_lat = Double.parseDouble(bottom_right_lat_long[0]);
                double bottom_right_long = Double.parseDouble(bottom_right_lat_long[1]);


                Polygon polygon;
                ArrayList<LatLng> Terminal2 = new ArrayList<LatLng>(createRectangle(new LatLng(top_left_lat, top_left_long),
                        new LatLng(top_right_lat, top_right_long),
                        new LatLng(bottom_right_lat, bottom_right_long),
                        new LatLng(bottom_left_lat, bottom_left_long)));

                polygon = googleMap.addPolygon(new PolygonOptions()
                        .addAll(Terminal2)
                        .strokeColor(0xFF00AA00)
                        .fillColor(0x2200FFFF)
                        .strokeWidth(2));
                polygon.setClickable(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> createRectangle(LatLng topLeft, LatLng topRight, LatLng
            bottomLeft, LatLng bottomRight) {
        return Arrays.asList(topLeft,
                topRight,
                bottomLeft,
                bottomRight); // royal uniform
    }

}
