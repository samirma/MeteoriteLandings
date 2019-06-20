package com.antonio.samir.meteoritelandingsspots.service.repository.local

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface MeteoriteRepositoryInterface {

    fun meteoriteOrdened(): LiveData<List<Meteorite>>

    suspend fun meteoritesWithOutAddress(): List<Meteorite>

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    suspend fun update(meteorite: Meteorite)

    suspend fun insertAll(meteorites: List<Meteorite>)

    suspend fun getRemoteMeteorites(): List<Meteorite>?

}