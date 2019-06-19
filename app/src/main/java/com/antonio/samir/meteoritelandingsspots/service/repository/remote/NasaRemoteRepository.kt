package com.antonio.samir.meteoritelandingsspots.service.repository.remote

import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServerException
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class NasaRemoteRepository(val service: NasaServerEndPoint) : NasaRemoteRepositoryInterface {

    override suspend fun getMeteorites(): List<Meteorite>? = withContext(Dispatchers.IO) {

        val meteorites: List<Meteorite>?
        try {
            meteorites = service.publicMeteorites()
        } catch (e: IOException) {
            throw MeteoriteServerException(e)
        }

        return@withContext meteorites
    }

}
