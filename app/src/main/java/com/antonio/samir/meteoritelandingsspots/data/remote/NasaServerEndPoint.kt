package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface needed by com.antonio.samir.meteoritelandingsspots.di.retrofit library in order to request from the server
 */
interface NasaServerEndPoint {

    companion object {

        val URL = "https://data.nasa.gov"
    }

    @GET("resource/y77d-th95.json")
    suspend fun publicMeteorites(@Query("\$offset") offset: Int, @Query("\$limit") limit: Int): List<Meteorite>

}
