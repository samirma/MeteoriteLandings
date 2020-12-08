package com.antonio.samir.meteoritelandingsspots.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat.checkSelfPermission
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
class GPSTracker(private val context: Context) : GPSTrackerInterface {

    // flag for GPS status
    private var isGPSEnabled = false

    // Declaring a Location Manager
    private var locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

    private val isLocationServiceStarted = AtomicBoolean(false)

    private var currentLocation = ConflatedBroadcastChannel<Location?>(null)

    /**
     * Function to get the user's current liveLocation
     *
     * @return Location
     */
    override val location: Flow<Location?>
        get() = currentLocation.asFlow()

    private var currentNeedAuthorization = ConflatedBroadcastChannel<Boolean>()

    /**
     * Function to know if the authorization is required or not
     *
     * @return Boolean
     */
    override val needAuthorization: Flow<Boolean>
        get() = currentNeedAuthorization.asFlow()

    override fun isLocationServiceStarted(): Boolean = isLocationServiceStarted.get()

    override val isLocationAuthorized: Boolean
        get() {
            return checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        }

    override suspend fun stopUpdates() {
        isLocationServiceStarted.set(false)
        locationManager.removeUpdates(this)
    }

    override suspend fun startLocationService() {
        if (isGPSEnabled()) {
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
        // if GPS Enabled get lat/long using GPS Services
        if (isGPSEnabled) {
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    MIN_TIME_BW_UPDATES,
//                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
//                    this@GPSTracker)
            Log.d(TAG, "GPS Enabled")
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { onLocationChanged(it) }
        }
    }

    override fun isGPSEnabled(): Boolean {

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return isGPSEnabled
    }


    override fun onLocationChanged(location: Location) {
        if (currentLocation.value == null) {
            Log.i(TAG, "Location received $location")
            location.let { currentLocation.offer(it) }
        }
    }

    companion object {

        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 15 // 1 minute
        private val TAG = GPSTracker::class.java.simpleName
    }

}