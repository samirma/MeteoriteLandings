package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException
import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil
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
                AnalyticsUtil.logEvent(NASA_SERVICE, "Meteorites recovered from Nasa server")
            } catch (e: IOException) {
                AnalyticsUtil.logEvent(NASA_SERVICE, "Failed to recover data from Nasa server")
                throw MeteoriteServerException(e)
            }

            return meteorites
        }

    companion object {

        private val NASA_SERVICE = "NasaService"
    }

}
