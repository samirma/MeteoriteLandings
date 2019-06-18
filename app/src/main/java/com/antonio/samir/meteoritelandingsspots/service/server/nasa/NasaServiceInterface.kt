package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import com.antonio.samir.meteoritelandingsspots.model.Meteorite

interface NasaServiceInterface {

    suspend fun getMeteorites(): List<Meteorite>?

}
