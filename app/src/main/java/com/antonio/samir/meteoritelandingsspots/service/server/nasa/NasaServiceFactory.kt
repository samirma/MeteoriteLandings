package com.antonio.samir.meteoritelandingsspots.service.server.nasa

object NasaServiceFactory {
    private val nasaService: NasaService? = null

    fun getNasaService(): NasaService {

        val service: NasaService

        if (nasaService != null) {
            service = nasaService
        } else {
            service = NasaServiceImpl()
        }

        return service
    }
}
