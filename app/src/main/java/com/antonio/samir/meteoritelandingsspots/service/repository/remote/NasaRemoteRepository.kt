package com.antonio.samir.meteoritelandingsspots.service.repository.remote

import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServerException
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import java.io.IOException

class NasaRemoteRepository(val service: NasaServerEndPoint) : NasaRemoteRepositoryInterface {

    override suspend fun getMeteorites(limit: Int, offset: Int): List<Meteorite> {

        val meteorites: List<Meteorite>?
        try {
            meteorites = service.publicMeteorites(limit, offset)
        } catch (e: IOException) {
            throw MeteoriteServerException(e)
        }
        return meteorites
    }

}
