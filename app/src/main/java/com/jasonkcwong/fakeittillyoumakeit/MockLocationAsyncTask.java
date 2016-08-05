package com.jasonkcwong.fakeittillyoumakeit;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jason on 16-08-04.
 */
public class MockLocationAsyncTask extends AsyncTask {
    private LatLng nextLatLng;
    private MockLocationProvider mMockLocationProvider;

    public static final String TAG = MockLocationAsyncTask.class.getName();
    public MockLocationAsyncTask(LatLng latLng, MockLocationProvider mockLocationProvider){
        this.nextLatLng = latLng;
        mMockLocationProvider = mockLocationProvider;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.d(TAG, "Pushing Location");
        mMockLocationProvider.pushLocation(nextLatLng);
        return null;
    }
}
