package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException
import java.io.IOException

class NasaService(val service: NasaServerEndPoint) : NasaServiceInterface {

    override fun getMeteorites(): List<Meteorite>? {
        val publicMeteorites = service.publicMeteorites

        val meteorites: List<Meteorite>?
        try {
            val response = publicMeteorites.execute()
            meteorites = response.body()
        } catch (e: IOException) {
            throw MeteoriteServerException(e)
        }

        return meteorites
    }

}
