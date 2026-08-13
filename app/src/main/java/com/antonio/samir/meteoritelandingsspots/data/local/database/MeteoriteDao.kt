package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.antonio.samir.meteoritelandingsspots.data.local.model.MeteoriteAddress
import com.antonio.samir.meteoritelandingsspots.data.local.model.MeteoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeteoriteDao {

    @Query(
        """
        SELECT * FROM meteorites
        WHERE reclong IS NOT NULL
          AND (LOWER(address) GLOB '*' || :filter || '*' OR LOWER(name) GLOB '*' || :filter || '*')
        ORDER BY name ASC
        LIMIT :limit
        """
    )
    fun meteoriteFiltered(filter: String, limit: Long): PagingSource<Int, MeteoriteEntity>

    /**
     * Nearest-first ordering.
     *
     * [lngScale] is `cos(latitude)^2`, supplied by the repository. Without it the comparison runs
     * in raw degrees, where a degree of longitude is treated as the same distance as a degree of
     * latitude — so away from the equator the ordering disagrees with the distances the UI shows.
     */
    @Query(
        """
        SELECT * FROM meteorites
        WHERE reclong IS NOT NULL
          AND (LOWER(address) GLOB '*' || :filter || '*' OR LOWER(name) GLOB '*' || :filter || '*')
        ORDER BY ((reclat - :lat) * (reclat - :lat))
               + ((reclong - :lng) * (reclong - :lng) * :lngScale) ASC
        LIMIT :limit
        """
    )
    fun meteoriteOrderedByLocationFiltered(
        lat: Double,
        lng: Double,
        lngScale: Double,
        filter: String,
        limit: Long,
    ): PagingSource<Int, MeteoriteEntity>

    /**
     * One-shot batch of rows still missing an address.
     *
     * Deliberately not a [Flow]: the address recovery writes back into this same table, so an
     * observable query would re-trigger itself and never complete.
     */
    @Query(
        """
        SELECT * FROM meteorites
        WHERE reclong IS NOT NULL AND (address IS NULL OR LENGTH(TRIM(address)) = 0)
        ORDER BY id
        LIMIT :limit
        """
    )
    suspend fun meteoritesWithoutAddress(limit: Int): List<MeteoriteEntity>

    @Query("SELECT * FROM meteorites WHERE id = :meteoriteId LIMIT 1")
    fun getMeteoriteById(meteoriteId: Int): Flow<MeteoriteEntity?>

    @Query("SELECT count(id) FROM meteorites WHERE reclong IS NOT NULL")
    suspend fun getValidMeteoritesCount(): Int

    @Query("SELECT count(id) FROM meteorites")
    suspend fun getMeteoritesCount(): Int

    @Query("SELECT count(id) FROM meteorites WHERE reclong IS NOT NULL AND (address IS NULL OR LENGTH(TRIM(address)) = 0)")
    suspend fun getMeteoritesWithoutAddressCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MeteoriteEntity>)

    @Query(
        """
        SELECT id, address FROM meteorites
        WHERE id IN (:ids) AND address IS NOT NULL AND LENGTH(TRIM(address)) > 0
        """
    )
    suspend fun addressesFor(ids: List<Int>): List<MeteoriteAddress>

    /**
     * Refreshes rows from the remote feed without losing locally reverse-geocoded addresses.
     *
     * The feed carries no address column, so a plain REPLACE insert silently blanks every address
     * the app has geocoded — tens of thousands of them, and there is no way to get them back
     * except by geocoding the whole dataset again.
     */
    @Transaction
    suspend fun refresh(items: List<MeteoriteEntity>) {
        val knownAddresses = addressesFor(items.map { it.id }).associate { it.id to it.address }
        insertAll(
            items.map { item ->
                knownAddresses[item.id]?.let { item.copy(address = it) } ?: item
            }
        )
    }

    @Query("UPDATE meteorites SET address = :address WHERE id = :id")
    suspend fun updateAddress(id: Int, address: String?)

    /** Writes a whole geocoding batch in one transaction so the UI sees one invalidation. */
    @Transaction
    suspend fun updateAddresses(addressById: Map<Int, String?>) {
        addressById.forEach { (id, address) -> updateAddress(id, address) }
    }
}
