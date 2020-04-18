package com.antonio.samir.meteoritelandingsspots.util

import android.location.Location
import android.location.LocationListener
import com.antonio.samir.meteoritelandingsspots.data.Result
import kotlinx.coroutines.flow.Flow

interface GPSTrackerInterface : LocationListener {

    val isLocationAuthorized: Boolean

    val location: Flow<Result<Location>>

    suspend fun startLocationService()

    fun isLocationServiceStarted(): Boolean

    fun isGPSEnabled(): Boolean

    suspend fun stopUpdates()

    val needAuthorization: Flow<Boolean>
}
