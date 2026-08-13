package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite

interface MeteoriteRemoteRepository {

    /** @throws MeteoriteServerException when the feed cannot be fetched or parsed. */
    suspend fun getMeteorites(): List<Meteorite>
}
