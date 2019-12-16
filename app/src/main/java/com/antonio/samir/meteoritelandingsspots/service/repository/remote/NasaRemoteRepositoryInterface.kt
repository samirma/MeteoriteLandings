package com.antonio.samir.meteoritelandingsspots.service.repository.remote

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface NasaRemoteRepositoryInterface {

    suspend fun getMeteorites(limit: Int, offset: Int): List<Meteorite>

}
