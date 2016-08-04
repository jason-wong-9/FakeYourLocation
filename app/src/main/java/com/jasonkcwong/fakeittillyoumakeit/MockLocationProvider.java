package com.jasonkcwong.fakeittillyoumakeit;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jason on 16-08-03.
 */
public class MockLocationProvider {
    private String providerName;
    private Context mContext;
    private LocationManager mLocationManager;

    public MockLocationProvider(String name, Context context){
        this.providerName = name;
        this.mContext = context;

        mLocationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addTestProvider(providerName, false, false, false, false, false, false, false, 0, 5);
        mLocationManager.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(LatLng latLng){
        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(latLng.latitude);
        mockLocation.setLongitude(latLng.longitude);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(5);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        mLocationManager.setTestProviderLocation(providerName, mockLocation);
    }

    public void shutdown(){
        mLocationManager.removeTestProvider(providerName);
    }
}
