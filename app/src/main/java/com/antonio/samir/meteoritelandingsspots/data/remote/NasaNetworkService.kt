package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import javax.inject.Inject

class NasaNetworkService @Inject constructor(val service: NasaServerEndPoint) :
    MeteoriteRemoteRepository {

    override suspend fun getMeteorites(offset: Int, limit: Int): List<Meteorite> {
        return service.publicMeteorites(offset, limit)
    }

}
