package com.antonio.samir.meteoritelandingsspots.service.repository.local

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface MeteoriteRepositoryInterface {

    fun meteoriteOrdered(location: Location?, filter: String?): DataSource.Factory<Int, Meteorite>

    suspend fun meteoritesWithOutAddress(): List<Meteorite>

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    suspend fun update(meteorite: Meteorite)

    suspend fun insertAll(meteorites: List<Meteorite>)

    suspend fun getRemoteMeteorites(limit: Int, offset: Int): List<Meteorite>

    suspend fun getMeteoritesCount(): Int

}