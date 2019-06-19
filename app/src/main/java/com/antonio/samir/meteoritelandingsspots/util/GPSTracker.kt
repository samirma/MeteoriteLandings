package com.antonio.samir.meteoritelandingsspots.util


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicBoolean


class GPSTracker(
        private val context: Context
) : GPSTrackerInterface {

    // flag for GPS status
    private var isGPSEnabled = false
    // Declaring a Location Manager
    private var locationManager: LocationManager? = null
    // flag for network status
    private var isNetworkEnabled = false

    private val isLocationServiceStarted = AtomicBoolean(false)

    private val liveLocation: MutableLiveData<Location> = MutableLiveData() // liveLocation

    /**
     * Function to know if the authorization is required or not
     *
     * @return Boolean
     */
    override val needAuthorization: MutableLiveData<Boolean> = MutableLiveData()

    companion object {

        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters
        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute
        private val TAG = GPSTracker::class.java.simpleName
    }

    /**
     * Function to get the user's current liveLocation
     *
     * @return Location
     */
    override val location: MutableLiveData<Location>
        get() {
            return liveLocation
        }

    override fun isLocationServiceStarted(): Boolean {
        return isLocationServiceStarted.get()
    }

    override val isLocationAutorized: Boolean
        get() {
            val hasFine = ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarse = ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            return hasFine || hasCoarse
        }


    override fun stopUpdates() {
        isLocationServiceStarted.set(false)
        locationManager?.removeUpdates(this)
    }

    override fun startLocationService() {
        try {

            if (isLocationAutorized) {
                startLocation()
            }

            needAuthorization.value = !isLocationAutorized

        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
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

    override fun isGPSEnabled(): Boolean {
        locationManager = context
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // getting GPS status
        isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

        // getting network status
        isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return isGPSEnabled || isNetworkEnabled
    }


    override fun onLocationChanged(location: Location?) {
        liveLocation.value = location
    }

    override fun onProviderDisabled(provider: String) {
        isLocationServiceStarted.set(false)
    }

    override fun onProviderEnabled(provider: String) {
        isLocationServiceStarted.set(true)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}


}