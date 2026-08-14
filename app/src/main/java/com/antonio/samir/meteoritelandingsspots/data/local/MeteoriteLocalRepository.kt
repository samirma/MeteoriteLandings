package com.antonio.samir.meteoritelandingsspots.data.local

import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for meteorite data.
 *
 * The contract is in terms of the domain [Meteorite] and `Flow<PagingData<…>>`; Room entities and
 * `PagingSource` stay inside the implementation.
 */
interface MeteoriteLocalRepository {

    /**
     * @param filter free-text match against name and address; blank matches everything.
     * @param latitude/longitude when both are present the results are ordered nearest-first,
     *   otherwise alphabetically.
     */
    fun meteorites(
        filter: String?,
        latitude: Double?,
        longitude: Double?,
        limit: Long = DEFAULT_LIMIT,
    ): Flow<PagingData<Meteorite>>

    fun getMeteoriteById(id: Int): Flow<Meteorite?>

    suspend fun meteoritesWithoutAddress(limit: Int): List<Meteorite>

    suspend fun updateAddresses(addressById: Map<Int, String?>)

    suspend fun getValidMeteoritesCount(): Int

    suspend fun getMeteoritesWithoutAddressCount(): Int

    suspend fun insertAll(meteorites: List<Meteorite>)

    companion object {
        const val DEFAULT_LIMIT = 5_000L
    }
}
