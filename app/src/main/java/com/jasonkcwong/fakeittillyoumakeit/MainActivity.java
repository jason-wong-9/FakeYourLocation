package com.jasonkcwong.fakeittillyoumakeit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    public static final String TAG = MainActivity.class.getName();
    public static final String TYPE_WALK = "WALK";
    public static final String TYPE_RUNNING = "RUNNING";
    public static final String TYPE_INSTANT = "INSTANT";
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Marker currentMarker;
    private Marker destinationMarker;
    private LatLng destinationLatLng;
    private LatLng currentLatLng;
    private TextView mDestinationText;
    private RadioGroup mRadioGroup;
    private Button mStartButton;
    private String type;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    private final long LOCATION_REFRESH_TIME = 1000;
    private final float LOCATION_REFRESH_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDestinationText = (TextView) findViewById(R.id.destinationText);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(this);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "Status Changed");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "Location turned on");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "Location turned off");
            }
        };
        
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    
    private void statusCheck(){
        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        requestPermission();
        statusCheck();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            updateMap(lastLocation);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (destinationMarker != null){
                    destinationMarker.remove();
                }
                MarkerOptions currentMarkerOptions = new MarkerOptions().position(latLng).title("Destination");
                destinationMarker = mMap.addMarker(currentMarkerOptions);
                mDestinationText.setText("(" + latLng.latitude + ", " + latLng.longitude + ")");
                destinationLatLng = latLng;
            }
        });
    }


    private void updateMap(Location location) {
        if (location == null) return;
        Log.d(TAG, location.toString() + "updated");
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentMarker != null){
            currentMarker.remove();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                    14.0f));
        } else {
            Log.d(TAG, "Zoom Camera");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f));
        }

        MarkerOptions currentMarkerOptions = new MarkerOptions().position(currentLatLng).title("Current Location");
        currentMarker = mMap.addMarker(currentMarkerOptions);
    }

    private void requestPermission() {
        Log.v(TAG, "Requesting Permission");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission Granted");
                    if ((ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)){
                        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                                LOCATION_REFRESH_DISTANCE, mLocationListener);
                    }

                } else {
                    Log.v(TAG, "Permission Denied");
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case(R.id.startButton):
                if (validateForm()){
                    Log.d(TAG, "Validated Form");
                }
                break;
        }
    }
    private boolean validateForm(){
        int checkedRadioId = mRadioGroup.getCheckedRadioButtonId();
        getCheckedRadioType(checkedRadioId);
        Log.d(TAG, type);
        if (destinationLatLng != null && currentLatLng != null && type != null){
            return true;
        }
        return false;
    }

    private void getCheckedRadioType(int id){
        switch(id){
            case(R.id.radio_walk):
                type = TYPE_WALK;
                break;
            case(R.id.radio_running):
                type = TYPE_RUNNING;
                break;
            case(R.id.radio_instant):
                type = TYPE_INSTANT;
                break;
        }
    }
}
