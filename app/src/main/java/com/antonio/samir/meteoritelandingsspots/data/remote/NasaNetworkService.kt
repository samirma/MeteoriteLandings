package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite

class NasaNetworkService(val service: NasaServerEndPoint) : MeteoriteRemoteRepository {

    override suspend fun getMeteorites(offset: Int, limit: Int): List<Meteorite> {
        return service.publicMeteorites(offset, limit)
    }

}
