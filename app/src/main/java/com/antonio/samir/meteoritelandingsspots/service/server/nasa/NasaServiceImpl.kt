package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class NasaServiceImpl : NasaService {

    private val service: NasaServerEndPoint

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(NasaServerEndPoint.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(NasaServerEndPoint::class.java)

    }


    override val meteorites: List<Meteorite>?
        get() {

            val publicMeteorites = service.publicMeteorites

            val meteorites: List<Meteorite>?
            try {
                meteorites = publicMeteorites.execute().body()
            } catch (e: IOException) {
                throw MeteoriteServerException(e)
            }

            return meteorites
        }

    companion object {

        private val NASA_SERVICE = "NasaService"
    }

}
