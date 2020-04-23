package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface MeteoriteRepository {

    val pageSize: Int

    suspend fun loadMeteorites(filter: String?, longitude: Double?, latitude: Double?): DataSource.Factory<Int, Meteorite>

    fun getMeteoriteById(id: String): Flow<Result<Meteorite>>

    suspend fun update(meteorite: Meteorite)

    suspend fun update(list: List<Meteorite>)

    fun loadDatabase(): Flow<Result<Nothing>>
}
