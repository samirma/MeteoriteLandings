package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite

interface MeteoriteRemoteRepository {

    suspend fun getMeteorites(offset: Int, limit: Int): List<Meteorite>

}
