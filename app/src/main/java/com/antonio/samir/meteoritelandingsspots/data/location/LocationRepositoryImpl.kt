package com.antonio.samir.meteoritelandingsspots.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Replaces the old `GetLocation`, which called `LocationManager.getLastKnownLocation(GPS_PROVIDER)`
 * behind `@SuppressLint("MissingPermission")`. That combination threw `SecurityException` into a
 * `runCatching` that swallowed it, so location was always null in practice.
 *
 * Uses the fused provider: it falls back to network positioning when there is no GPS fix, which is
 * the common case indoors and on a cold start.
 */
@Singleton
class LocationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
) : LocationRepository {

    override fun hasLocationPermission(): Boolean =
        LOCATION_PERMISSIONS.any { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
        }

    override suspend fun currentLocation(): Location? {
        if (!hasLocationPermission()) return null

        val request = CurrentLocationRequest.Builder()
            // Sorting a list by distance does not justify waking the GPS chip.
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setDurationMillis(LOCATION_TIMEOUT_MILLIS)
            .build()

        // Cached position first: ordering a list by distance tolerates a stale fix perfectly
        // well, and asking for a fresh one costs up to LOCATION_TIMEOUT_MILLIS of the user
        // staring at an alphabetical list. Only fall back to an active request when there is no
        // cached position at all.
        return runCatching { lastLocation() ?: requestCurrentLocation(request) }
            .onSuccess { location -> Log.d(TAG, "Resolved location: $location") }
            .getOrElse { throwable ->
                Log.w(TAG, "Could not resolve the current location", throwable)
                null
            }
    }

    private suspend fun lastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        @Suppress("MissingPermission") // Guarded by hasLocationPermission() above.
        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location -> continuation.resumeIfActive(location) }
        task.addOnFailureListener { continuation.resumeIfActive(null) }
        task.addOnCanceledListener { continuation.resumeIfActive(null) }
    }

    private suspend fun requestCurrentLocation(request: CurrentLocationRequest): Location? =
        suspendCancellableCoroutine { continuation ->
            @Suppress("MissingPermission") // Guarded by hasLocationPermission() above.
            val task = fusedLocationClient.getCurrentLocation(request, null)
            task.addOnSuccessListener { location -> continuation.resumeIfActive(location) }
            task.addOnFailureListener { throwable ->
                Log.w(TAG, "Location request failed", throwable)
                continuation.resumeIfActive(null)
            }
            task.addOnCanceledListener { continuation.resumeIfActive(null) }
        }

    private fun CancellableContinuation<Location?>.resumeIfActive(location: Location?) {
        if (isActive) resume(location)
    }

    private companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        const val LOCATION_TIMEOUT_MILLIS = 10_000L
        val TAG: String = LocationRepositoryImpl::class.java.simpleName
    }
}
