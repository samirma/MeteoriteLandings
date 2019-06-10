package com.antonio.samir.meteoritelandingsspots.util


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject


class GPSTracker @Inject constructor(
        private val gpsTrackerDelegate: GPSTrackerDelegate,
        private val mContext: Context
) : GPSTrackerInterface {

    // flag for GPS status
    private var isGPSEnabled = false
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null
    // flag for network status
    internal var isNetworkEnabled = false

    private val liveLocation: MutableLiveData<Location> // liveLocation

    /**
     * Function to get the user's current liveLocation
     *
     * @return
     */
    val location: MutableLiveData<Location>
        get() {
            try {
                startLocationService()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

            return liveLocation
        }

    fun stopUpdates() {
        locationManager!!.removeUpdates(this)
    }

    interface GPSTrackerDelegate {
        fun requestPermission()
    }

    init {
        liveLocation = MutableLiveData()
    }

    override fun startLocationService() {
        try {
            val isGPSEnabled = isGPSEnabled()
            if (isGPSEnabled) {

                if (!permissionRequested && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    gpsTrackerDelegate.requestPermission()
                    permissionRequested = true
                } else {

                    var location: Location?

                    if (isNetworkEnabled) {

                        locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d(TAG, "Network")
                        if (locationManager != null) {
                            location = locationManager!!
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                onLocationChanged(location)
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (this.isGPSEnabled) {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d(TAG, "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            onLocationChanged(location)
                        }
                    }
                }

            }

        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    fun isGPSEnabled(): Boolean {
        locationManager = mContext
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // getting GPS status
        isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

        Log.v("isGPSEnabled", "=$isGPSEnabled")

        // getting network status
        isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.v("isNetworkEnabled", "=$isNetworkEnabled")

        return isGPSEnabled || isNetworkEnabled
    }


    override fun onLocationChanged(location: Location?) {
        liveLocation.value = location
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {

        // The minimum distance to change Updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters
        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute
        private val TAG = GPSTracker::class.java.simpleName
        private var permissionRequested = false
    }

}