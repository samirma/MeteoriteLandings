package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite

class NasaNetworkService(val service: NasaServerEndPoint) : NetworkService {

    override suspend fun getMeteorites(limit: Int, offset: Int): List<Meteorite> {
        return service.publicMeteorites(limit, offset)
    }

}
