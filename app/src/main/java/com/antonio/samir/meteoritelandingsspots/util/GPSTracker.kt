package com.antonio.samir.meteoritelandingsspots.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

@FlowPreview
@ExperimentalCoroutinesApi
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

    private var currentLocation = ConflatedBroadcastChannel<Location?>(null)

    /**
     * Function to get the user's current liveLocation
     *
     * @return Location
     */
    override val location: Flow<Location?>
        get() {
            return currentLocation.asFlow()
        }

    private var currentNeedAuthorization = ConflatedBroadcastChannel<Boolean>()

    /**
     * Function to know if the authorization is required or not
     *
     * @return Boolean
     */
    override val needAuthorization: Flow<Boolean>
        get() {
            return currentNeedAuthorization.asFlow()
        }

    companion object {

        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute
        private val TAG = GPSTracker::class.java.simpleName
    }

    override fun isLocationServiceStarted(): Boolean {
        return isLocationServiceStarted.get()
    }

    override val isLocationAuthorized: Boolean
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


    override suspend fun stopUpdates() {
        isLocationServiceStarted.set(false)
        locationManager?.removeUpdates(this)
    }

    override suspend fun startLocationService() {
        withContext(Dispatchers.Main) {
            try {

                if (isLocationAuthorized) {
                    startLocation()
                }
                currentNeedAuthorization.offer(!isLocationAuthorized)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun startLocation() = withContext(Dispatchers.Main) {
        var location: Location?

        val locationListener: LocationListener = this@GPSTracker

        if (isNetworkEnabled) {

            locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), locationListener)
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
        if (isGPSEnabled) {
            locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), locationListener)
            Log.d(TAG, "GPS Enabled")
            if (locationManager != null) {
                location = locationManager!!
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.let { onLocationChanged(it) }
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


    override fun onLocationChanged(location: Location) {
        location?.let { currentLocation.offer(it) }
    }

    override fun onProviderDisabled(provider: String) {
        isLocationServiceStarted.set(false)
    }

    override fun onProviderEnabled(provider: String) {
        isLocationServiceStarted.set(true)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}


}