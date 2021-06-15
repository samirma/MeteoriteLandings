package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.PagingSource
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface MeteoriteRepository {

    suspend fun update(meteorite: Meteorite)

    suspend fun update(list: List<Meteorite>)

    fun loadDatabase(): Flow<ResultOf<Unit>>
}
