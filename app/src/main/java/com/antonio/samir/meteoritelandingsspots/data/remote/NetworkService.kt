package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite

interface NetworkService {

    suspend fun getMeteorites(limit: Int, offset: Int): List<Meteorite>

}
