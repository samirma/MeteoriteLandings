package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface NetworkService {

    suspend fun getMeteorites(limit: Int, offset: Int): Flow<Result<List<Meteorite>>>

}
