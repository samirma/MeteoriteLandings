package com.antonio.samir.meteoritelandingsspots.util

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat.checkSelfPermission
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean

@FlowPreview
@ExperimentalCoroutinesApi
class GPSTracker(private val context: Context) : GPSTrackerInterface {

    // flag for GPS status
    private var isGPSEnabled = false

    // Declaring a Location Manager
    private var locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

    private val isLocationServiceStarted = AtomicBoolean(false)

    private var currentLocation = MutableStateFlow<Location?>(null)

    /**
     * Function to get the user's current liveLocation
     *
     * @return Location
     */
    override val location: Flow<Location?>
        get() = currentLocation

    private var currentNeedAuthorization = MutableSharedFlow<Boolean>()

    /**
     * Function to know if the authorization is required or not
     *
     * @return Boolean
     */
    override val needAuthorization: Flow<Boolean>
        get() = currentNeedAuthorization

    override fun isLocationServiceStarted(): Boolean = isLocationServiceStarted.get()

    override val isLocationAuthorized: Boolean
        get() {
            return checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        }

    override suspend fun stopUpdates() {
        isLocationServiceStarted.set(false)
//        locationManager.removeUpdates(this)
    }

    override suspend fun requestLocation() {
        if (isGPSEnabled()) {
            try {
                if (isLocationAuthorized) {
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        //            locationManager.requestLocationUpdates(
                        //                    LocationManager.GPS_PROVIDER,
                        //                    MIN_TIME_BW_UPDATES,
                        //                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        //                    this@GPSTracker)
                        Log.d(TAG, "GPS Enabled")
                        if (checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PERMISSION_GRANTED && checkSelfPermission(
                                context,
                                ACCESS_COARSE_LOCATION
                            ) != PERMISSION_GRANTED
                        ) {
                            return
                        }
                        val lastKnownLocation =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        Log.i(TAG, "Location received $lastKnownLocation")
                        currentLocation.emit(lastKnownLocation)
                    } else {
                        currentLocation.emit(null)
                    }
                }
                currentNeedAuthorization.emit(!isLocationAuthorized)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }

    override fun isGPSEnabled(): Boolean {

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return isGPSEnabled
    }


    companion object {

        private val TAG = GPSTracker::class.java.simpleName
    }

}