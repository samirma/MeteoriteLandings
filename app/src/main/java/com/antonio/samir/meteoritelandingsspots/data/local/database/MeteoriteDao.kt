package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import kotlinx.coroutines.flow.Flow


@Dao
interface MeteoriteDao {

    @Query("SELECT * from meteorites WHERE recLong IS NOT NULL ORDER BY name LIMIT :limit")
    fun meteoriteOrdered(limit: Long): PagingSource<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE recLong IS NOT NULL ORDER BY ((reclat-:lat)*(reclat-:lat)) + ((reclong - :lng)*(reclong - :lng)) ASC")
    fun meteoriteOrderedByLocation(lat: Double, lng: Double): PagingSource<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE recLong IS NOT NULL AND ((LOWER(address) GLOB '*' || :filter|| '*') or (LOWER(name) GLOB '*' || :filter|| '*')) ORDER BY ((reclat-:lat)*(reclat-:lat)) + ((reclong - :lng)*(reclong - :lng)) ASC")
    fun meteoriteOrderedByLocationFiltered(
        lat: Double,
        lng: Double,
        filter: String
    ): PagingSource<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE recLong IS NOT NULL AND ((LOWER(address) GLOB '*' || :filter|| '*') or (LOWER(name) GLOB '*' || :filter|| '*')) ORDER BY name ASC LIMIT 5000")
    fun meteoriteFiltered(filter: String): PagingSource<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE recLong IS NOT NULL AND (address IS NULL OR LENGTH(address) = 0) ORDER BY id LIMIT 30")
    fun meteoritesWithOutAddress(): Flow<List<Meteorite>>

    @Query("SELECT * from meteorites WHERE id = :meteoriteId LIMIT 1")
    fun getMeteoriteById(meteoriteId: String): Flow<Meteorite>

    @Query("SELECT count(id) from meteorites WHERE recLong IS NOT NULL")
    suspend fun getValidMeteoritesCount(): Int

    @Query("SELECT count(id) from meteorites")
    suspend fun getMeteoritesCount(): Int

    @Query("SELECT count(id) from meteorites WHERE recLong IS NOT NULL AND (address IS NULL OR LENGTH(address) = 0)")
    suspend fun getMeteoritesWithoutAddressCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Meteorite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(items: List<Meteorite>)

    @Update
    suspend fun update(meteorite: Meteorite)


}
