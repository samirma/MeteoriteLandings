package com.antonio.samir.meteoritelandingsspots.data.location

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Reverse geocoding, replacing the old `GeoLocation` helper.
 *
 * Two changes matter. The listener-based `Geocoder` API is used on API 33+ instead of the
 * blocking overload deprecated there, and a failure now returns null rather than the `" "`
 * sentinel the old code stored — a single space is not blank, so those rows were written off as
 * "already geocoded" and never retried.
 */
@Singleton
class GeocoderDataSource @Inject constructor(
    private val geocoder: Geocoder,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    fun isAvailable(): Boolean = Geocoder.isPresent()

    /** @return "City, State, Country" for the coordinates, or null if it cannot be resolved. */
    suspend fun addressFor(latitude: Double, longitude: Double): String? {
        if (!isAvailable()) return null
        return runCatching { lookup(latitude, longitude)?.toDisplayString() }
            .getOrElse { throwable ->
                if (throwable is InterruptedException) throw throwable
                Log.w(TAG, "Reverse geocoding failed for $latitude/$longitude", throwable)
                null
            }
    }

    private suspend fun lookup(latitude: Double, longitude: Double): Address? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (continuation.isActive) continuation.resume(addresses.firstOrNull())
                    }

                    override fun onError(errorMessage: String?) {
                        Log.w(TAG, "Geocoder error: $errorMessage")
                        if (continuation.isActive) continuation.resume(null)
                    }
                })
            }
        } else {
            withContext(ioDispatcher) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
            }
        }

    private fun Address.toDisplayString(): String? = listOfNotNull(
        locality?.takeIf { it.isNotBlank() },
        adminArea?.takeIf { it.isNotBlank() },
        countryName?.takeIf { it.isNotBlank() },
    ).joinToString(", ").takeIf { it.isNotBlank() }

    private companion object {
        val TAG: String = GeocoderDataSource::class.java.simpleName
    }
}
