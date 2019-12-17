package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import com.antonio.samir.meteoritelandingsspots.model.Meteorite

import retrofit2.Call
import retrofit2.http.GET

/**
 * Interface needed by retrofit library in order to request from the server
 */
interface NasaServerEndPoint {


    @get:GET("resource/y77d-th95.json")
    val publicMeteorites: Call<List<Meteorite>>

    companion object {

        val URL = "https://data.nasa.gov"
    }

}
