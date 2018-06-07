package com.antonio.samir.meteoritelandingsspots.service.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.antonio.samir.meteoritelandingsspots.model.Meteorite


@Dao
interface MeteoriteDao {

    @get:Query("SELECT * from meteorites ORDER BY name")
    val meteoriteOrdened: LiveData<List<Meteorite>>

    @get:Query("SELECT * from meteorites ORDER BY name")
    val meteoriteOrdenedSync: List<Meteorite>

    @get:Query("SELECT * from meteorites WHERE address IS NULL ORDER BY id")
    val meteoritesWithOutAddress: List<Meteorite>

    @Insert
    fun insertAll(items: List<Meteorite>)

    @Update
    fun update(meteorite: Meteorite)

    @Query("SELECT * from meteorites where id = :meteoriteId LIMIT 1")
    fun getMeteoriteById(meteoriteId: String): LiveData<Meteorite>
}
