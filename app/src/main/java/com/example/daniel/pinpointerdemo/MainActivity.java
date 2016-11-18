package com.example.daniel.pinpointerdemo;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;




public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String code;
    private ArrayList<LatLng> points = new ArrayList<LatLng>();
    protected Boolean mRequestingLocationUpdates;


    // Initialize the Amazon Cognito credentials provider
    CognitoCachingCredentialsProvider credentialsProvider;

    //Create a DynamoDB Client
    AmazonDynamoDBClient ddbClient;
    DynamoDBMapper mapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestingLocationUpdates=false;

        //get the map fragment from the activity
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Setup Google API client for location services
        mGoogleApiClient = new GoogleApiClient.Builder( mapFragment.getContext() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        //Setup Location request interval and accuracy
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Setup arrival button and on click listener
        Button arrival_button = (Button)findViewById(R.id.arrival_button);
        arrival_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Stops getting locations updates and draws the polylines
                stopLocationUpdates();
                sendCoordinatesandCode();
                mapPolylines();
            }
        });


        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-west-2:80b81944-e7f9-41d2-af86-3b696e2e8bf7", // Identity Pool ID
                Regions.US_WEST_2 // Region
        );
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
    }

    @DynamoDBTable(tableName = "Pinpointer")
    public class Pinpointer{
        private String code;
      //  private ArrayList<LatLng> points = new ArrayList<LatLng>();

        @DynamoDBHashKey(attributeName="code")
        public String getCode(){
            return code;
        }


        //@DynamoDBAttribute(attributeName="points")
       // public ArrayList<LatLng> getPoints(){
        //    return points;
        //}

        public void setCode(String myCode){
            this.code=myCode;
        }

       // public void setPoints(ArrayList<LatLng> points) {
        //    this.points = points;
        //}
    }

    //Connect the google api client
    @Override
    protected void onStart() {
        super.onStart();
        //Show dialog to get code
        showDialog();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        //If the app starts up again and mrequestinglocationupdates is true, get location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        //Disconnect the Google Api Client
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //get instance of map object and setup intial settings
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

    }

    @Override
    public void onConnected(Bundle bundle) {
        //Setup google api client for getting location updates and initialize starting camera
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        initCamera();
    }


    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Setup camera when app first loads
    private void initCamera() {
        LatLng latlng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        points.add(latlng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,16f));

    }

    //Save coordinates to arraylist when getting new location update
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("Update",Double.toString(location.getLatitude()));
        saveCoordinates();
    }

    //Add to arraylist
    private void saveCoordinates(){
        LatLng latlng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        points.add(latlng);
    }

    private void sendCoordinatesandCode(){
        /*
        Send arraylist and code to server
         */

        Pinpointer myPin = new Pinpointer();
        myPin.setCode(code);
        //myPin.setPoints(points);
        mapper.save(myPin);

    }

    //Maps the polylines to map based on list of LatLng
    private void mapPolylines(){
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(5)
                .color(Color.RED));

    }

    //Dialog Message to handle getting pinpointer code
    private void showDialog(){

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.pincode_dialog, (ViewGroup)findViewById(R.id.topRoot));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Pinpointer Code");
        builder.setView(layout);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText pinCode = (EditText)layout.findViewById(R.id.codeField);
                code = pinCode.getText().toString();
                TextView codetext = (TextView)findViewById(R.id.pincode_text);
                codetext.setText("Pinpointer Code: "+code);
                mRequestingLocationUpdates = true;
                startLocationUpdates();
                Toast.makeText(MainActivity.this, "Walk to the Landmark", Toast.LENGTH_LONG).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
