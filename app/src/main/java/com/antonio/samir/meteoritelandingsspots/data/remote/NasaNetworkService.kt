package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteServerException
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class NasaNetworkService(val service: NasaServerEndPoint) : NetworkService {

    override suspend fun getMeteorites(limit: Int, offset: Int): Flow<Result<List<Meteorite>>> = flow {
        emit(Result.InProgress<List<Meteorite>>())
        try {
            emit(Result.Success(service.publicMeteorites(limit, offset)))
        } catch (e: IOException) {
            emit(Result.Error(MeteoriteServerException(e)))
        }
    }

}
