package com.antonio.samir.meteoritelandingsspots.util

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface GPSTrackerInterface {

    val isLocationAuthorized: Boolean

    val location: Flow<Location?>

    suspend fun startLocationService()

    fun isLocationServiceStarted(): Boolean

    fun isGPSEnabled(): Boolean

    suspend fun stopUpdates()

    val needAuthorization: Flow<Boolean>
}
