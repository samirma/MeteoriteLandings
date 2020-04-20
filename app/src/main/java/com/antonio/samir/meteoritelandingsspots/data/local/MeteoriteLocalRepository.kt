package com.antonio.samir.meteoritelandingsspots.data.local

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface MeteoriteLocalRepository {

    fun meteoriteOrdered(filter: String?, latitude: Double?, longitude: Double?): DataSource.Factory<Int, Meteorite>

    suspend fun meteoritesWithOutAddress(): List<Meteorite>

    fun getMeteoriteById(id: String): Flow<Result<Meteorite>>

    suspend fun update(meteorite: Meteorite)

    suspend fun getMeteoritesCount(): Int

    suspend fun getMeteoritesWithoutAddressCount(): Int

    suspend fun insertAll(meteorites: List<Meteorite>)

    suspend fun updateAll(meteorites: List<Meteorite>)

}
