package com.antonio.samir.meteoritelandingsspots.util

import android.location.Location
import android.location.LocationListener
import androidx.lifecycle.MutableLiveData

interface GPSTrackerInterface : LocationListener {

    val location: MutableLiveData<Location>

    fun startLocationService(gpsTrackerDelegate: GPSTracker.GPSTrackerDelegate?)

    fun isGPSEnabled(): Boolean

    fun stopUpdates()
}
