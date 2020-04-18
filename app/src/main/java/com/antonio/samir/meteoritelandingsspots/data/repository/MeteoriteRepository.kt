package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface MeteoriteRepository {

    suspend fun loadMeteorites(filter: String?): DataSource.Factory<Int, Meteorite>

    fun getMeteoriteById(id: String): Flow<Result<Meteorite>>

    suspend fun requestAddressUpdate(meteorite: Meteorite)

    fun requestAddressUpdate(list: List<Meteorite>)
}
