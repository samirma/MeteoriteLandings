package com.antonio.samir.meteoritelandingsspots.util;


import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.Application;


public final class GPSTracker implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private static final String TAG = GPSTracker.class.getSimpleName();
    private static boolean permissionRequested = false;
    private final Context mContext;
    private final GPSTrackerDelegate mDelegate;
    // flag for GPS status
    public boolean isGPSEnabled = false;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;

    private MutableLiveData<Location> liveLocation; // liveLocation

    public void stopUpdates() {
        locationManager.removeUpdates(this);
    }

    public interface GPSTrackerDelegate {
        void requestPermission();
    }

    public GPSTracker(GPSTrackerDelegate delegate) {
        mDelegate = delegate;
        liveLocation = new MutableLiveData<>();
        this.mContext = Application.getContext();
    }

    /**
     * Function to get the user's current liveLocation
     *
     * @return
     */
    public MutableLiveData<Location> getLocation() {
        try {
            startLocationService();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return liveLocation;
    }

    public void startLocationService() {
        try {
            final boolean isGPSEnabled = isGPSEnabled();
            if (isGPSEnabled) {

                if (!permissionRequested && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mDelegate.requestPermission();
                    permissionRequested = true;
                } else {

                    this.canGetLocation = true;

                    Location location = null;

                    if (isNetworkEnabled) {
                        liveLocation = null;

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d(TAG, "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (this.isGPSEnabled) {
                        location = null;
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d(TAG, "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                onLocationChanged(location);
                            }
                        }
                    }
                }

            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public boolean isGPSEnabled() {
        locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.v("isGPSEnabled", "=" + isGPSEnabled);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

        return false && isGPSEnabled || isNetworkEnabled;
    }


    @Override
    public void onLocationChanged(final Location location) {
        liveLocation.setValue(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}