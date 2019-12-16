package com.antonio.samir.meteoritelandingsspots.service.repository.remote

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface needed by retrofit library in order to request from the server
 */
interface NasaServerEndPoint {

    companion object {

        val URL = "https://data.nasa.gov"
    }

    @GET("resource/y77d-th95.json")
    suspend fun publicMeteorites(@Query("\$limit") limit: Int, @Query("\$offset") offset: Int): List<Meteorite>

}
