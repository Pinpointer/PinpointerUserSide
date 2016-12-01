package com.example.daniel.pinpointerdemo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Daniel on 12/1/16.
 */

public class FinishActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.finish_map);
        mapFragment.getMapAsync(this);
        Intent intent  = getIntent();
        //loc = (Location)intent.getSerializableExtra("Location");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //initCamera();
    }

    //Setup camera when app first loads
/*    private void initCamera() {
        LatLng latlng = new LatLng(loc.getLatitude(),loc.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,16f));

    }*/
}
