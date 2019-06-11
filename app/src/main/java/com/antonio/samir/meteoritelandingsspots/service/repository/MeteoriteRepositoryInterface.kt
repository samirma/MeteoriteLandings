package com.antonio.samir.meteoritelandingsspots.service.repository

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.model.Meteorite

interface MeteoriteRepositoryInterface {

    fun meteoriteOrdened(): LiveData<List<Meteorite>>

    fun meteoritesWithOutAddress(): List<Meteorite>

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    fun update(meteorite: Meteorite)

    suspend fun insertAll(meteorites: List<Meteorite>)

    suspend fun getRemoteMeteorites(): List<Meteorite>?
}