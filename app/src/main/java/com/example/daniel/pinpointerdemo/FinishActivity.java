package com.example.daniel.pinpointerdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Daniel on 12/1/16.
 */

public class FinishActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_activity);

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ImageView staticMap = (ImageView)findViewById(R.id.static_map);
        Intent intent  = getIntent();
        ArrayList<LatLng> points = intent.getParcelableArrayListExtra("Points");
        String path = buildPathString(points);
        String url = "http://maps.google.com/maps/api/staticmap?size=400x600&path=" +path;
        new getStaticMap(staticMap).execute(url);


    }

    public String buildPathString(ArrayList<LatLng>points){
        String s="color:0xff0000ff|weight:5";
        for(LatLng lt : points){
            s+="|"+lt.latitude+","+lt.longitude;
        }
        return s;
    }
    //Setup camera when app first loads
/*    private void initCamera() {
        LatLng latlng = new LatLng(loc.getLatitude(),loc.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,16f));

    }*/
}
