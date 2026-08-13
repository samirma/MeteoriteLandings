package com.antonio.samir.meteoritelandingsspots.common.userCase

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteServerException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.measureTime

/**
 * Refreshes the local database from NASA's published dataset.
 *
 * The app ships a prepopulated database, so this is a top-up rather than the primary load path;
 * it is reachable from the debug screen.
 */
@Singleton
class FetchMeteoriteList @Inject constructor(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val meteoriteRemoteRepository: MeteoriteRemoteRepository,
) {

    operator fun invoke(): Flow<ResultOf<Unit>> = flow {
        emit(ResultOf.InProgress())
        try {
            val elapsed = measureTime {
                val meteorites = meteoriteRemoteRepository.getMeteorites()
                if (meteorites.isEmpty()) {
                    emit(ResultOf.Error(DataError.SERVER))
                    return@flow
                }
                meteorites.chunked(INSERT_CHUNK).forEach { chunk ->
                    meteoriteLocalRepository.insertAll(chunk)
                }
            }
            Log.i(TAG, "Refreshed the meteorite list in ${elapsed.inWholeSeconds}s")
            emit(ResultOf.Success(Unit))
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (server: MeteoriteServerException) {
            // Emitting Success unconditionally after an error, as the previous version did,
            // meant the UI overwrote every failure with a "done" state.
            emit(ResultOf.Error(server.error, server))
        } catch (unexpected: Exception) {
            Log.e(TAG, "Unexpected failure refreshing the meteorite list", unexpected)
            emit(ResultOf.Error(DataError.UNKNOWN, unexpected))
        }
    }

    private companion object {
        /** Keeps a single SQLite transaction from holding ~46k rows at once. */
        const val INSERT_CHUNK = 2_000
        val TAG: String = FetchMeteoriteList::class.java.simpleName
    }
}
