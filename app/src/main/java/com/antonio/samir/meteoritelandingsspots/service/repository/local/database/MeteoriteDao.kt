package com.antonio.samir.meteoritelandingsspots.service.repository.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


@Dao
interface MeteoriteDao {

    @Query("SELECT * from meteorites ORDER BY name")
    fun meteoriteOrdered(): LiveData<List<Meteorite>>

    @Query("SELECT * from meteorites WHERE address IS NULL ORDER BY id")
    suspend fun meteoritesWithOutAddress(): List<Meteorite>

    @Insert
    suspend fun insertAll(items: List<Meteorite>)

    @Update
    suspend fun update(meteorite: Meteorite)

    @Query("SELECT * from meteorites where id = :meteoriteId LIMIT 1")
    fun getMeteoriteById(meteoriteId: String): LiveData<Meteorite>

}
