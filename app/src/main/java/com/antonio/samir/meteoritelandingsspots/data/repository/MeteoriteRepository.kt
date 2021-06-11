package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import androidx.paging.PagingSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface MeteoriteRepository {

    fun loadMeteorites(
            filter: String?,
            longitude: Double?,
            latitude: Double?,
            limit: Long,
    ): PagingSource<Int, Meteorite>

    fun getMeteoriteById(id: String): Flow<Result<Meteorite>>

    suspend fun update(meteorite: Meteorite)

    suspend fun update(list: List<Meteorite>)

    fun loadDatabase(): Flow<Result<Unit>>
}
