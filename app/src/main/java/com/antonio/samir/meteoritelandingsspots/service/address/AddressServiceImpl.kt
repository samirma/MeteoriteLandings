package com.antonio.samir.meteoritelandingsspots.service.address

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.di.IoDispatcher
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.location.GeocoderDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Drains the "meteorites without an address" queue in bounded batches.
 *
 * The previous implementation collected an observable Room query and wrote back into the same
 * table, so every write re-triggered the query: the flow never completed and the worker driving it
 * ran until WorkManager's 10-minute ceiling killed it. Here each batch is fetched with a one-shot
 * `suspend` query and the loop ends when a batch comes back empty.
 */
@Singleton
class AddressServiceImpl @Inject constructor(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val geocoderDataSource: GeocoderDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AddressService {

    override fun recoveryAddress(): Flow<ResultOf<Float>> = flow {
        if (!geocoderDataSource.isAvailable()) {
            emit(ResultOf.Error(DataError.GEOCODER_UNAVAILABLE))
            return@flow
        }

        val total = meteoriteLocalRepository.getValidMeteoritesCount()
        if (total == 0) {
            emit(ResultOf.Success(FULLY_RECOVERED))
            return@flow
        }

        var consecutiveFailures = 0

        while (true) {
            currentCoroutineContext().ensureActive()

            val batch = meteoriteLocalRepository.meteoritesWithoutAddress(BATCH_SIZE)
            if (batch.isEmpty()) break

            val resolved = buildMap {
                batch.forEach { meteorite ->
                    currentCoroutineContext().ensureActive()
                    val latitude = meteorite.latitude
                    val longitude = meteorite.longitude
                    if (latitude != null && longitude != null) {
                        put(meteorite.id, geocoderDataSource.addressFor(latitude, longitude))
                    }
                    // The platform geocoder throttles aggressively; pacing the calls is the
                    // difference between filling the column and getting nulls back.
                    delay(GEOCODE_INTERVAL_MILLIS)
                }
            }

            val recovered = resolved.filterValues { !it.isNullOrBlank() }
            if (recovered.isEmpty()) {
                // Nothing in this batch resolved. Retrying immediately would spin forever
                // against a throttled or offline geocoder, so give up and let WorkManager retry.
                if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILED_BATCHES) {
                    Log.w(TAG, "Geocoder returned nothing for $consecutiveFailures batches")
                    emit(ResultOf.Error(DataError.GEOCODER_UNAVAILABLE))
                    return@flow
                }
            } else {
                consecutiveFailures = 0
                meteoriteLocalRepository.updateAddresses(recovered)
            }

            val remaining = meteoriteLocalRepository.getMeteoritesWithoutAddressCount()
            emit(ResultOf.InProgress((1f - remaining.toFloat() / total) * 100f))
        }

        emit(ResultOf.Success(FULLY_RECOVERED))
    }.flowOn(ioDispatcher)

    private companion object {
        const val BATCH_SIZE = 30
        const val GEOCODE_INTERVAL_MILLIS = 120L
        const val MAX_CONSECUTIVE_FAILED_BATCHES = 3
        const val FULLY_RECOVERED = 100f
        val TAG: String = AddressServiceImpl::class.java.simpleName
    }
}
