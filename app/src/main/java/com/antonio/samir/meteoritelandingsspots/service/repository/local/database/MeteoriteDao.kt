package com.antonio.samir.meteoritelandingsspots.service.repository.local.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


@Dao
interface MeteoriteDao {

    @Query("SELECT * from meteorites ORDER BY name LIMIT 1000")
    fun meteoriteOrdered(): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites ORDER BY ((reclat-:lat)*(reclat-:lat)) + ((reclong - :lng)*(reclong - :lng)) ASC")
    fun meteoriteOrderedByLocation(lat: Double, lng: Double): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE (LOWER(address) GLOB '*' || :filter|| '*') or (LOWER(name) GLOB '*' || :filter|| '*') ORDER BY ((reclat-:lat)*(reclat-:lat)) + ((reclong - :lng)*(reclong - :lng)) ASC")
    fun meteoriteOrderedByLocationFiltered(lat: Double, lng: Double, filter: String): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE (LOWER(address) GLOB '*' || :filter|| '*') or (LOWER(name) GLOB '*' || :filter|| '*') ORDER BY name ASC LIMIT 5000")
    fun meteoriteFiltered(filter: String): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE address IS NULL OR LENGTH(address) = 0 ORDER BY id LIMIT 30")
    suspend fun meteoritesWithOutAddress(): List<Meteorite>

    @Query("SELECT * from meteorites where id = :meteoriteId LIMIT 1")
    fun getMeteoriteById(meteoriteId: String): LiveData<Meteorite>

    @Query("SELECT count(id) from meteorites")
    suspend fun getMeteoritesCount(): Int

    @Query("SELECT count(id) from meteorites WHERE address IS NULL OR LENGTH(address) = 0")
    suspend fun getMeteoritesWithoutAddressCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Meteorite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(items: List<Meteorite>)

    @Update
    suspend fun update(meteorite: Meteorite)


}
