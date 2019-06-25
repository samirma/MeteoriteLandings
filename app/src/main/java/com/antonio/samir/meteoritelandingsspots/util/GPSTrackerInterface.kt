package com.antonio.samir.meteoritelandingsspots.util

import android.location.Location
import android.location.LocationListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface GPSTrackerInterface : LocationListener {

    val isLocationAutorized: Boolean

    val location: MutableLiveData<Location>

    suspend fun startLocationService(): Any

    fun isLocationServiceStarted(): Boolean

    fun isGPSEnabled(): Boolean

    fun stopUpdates()

    val needAuthorization: LiveData<Boolean>
}
