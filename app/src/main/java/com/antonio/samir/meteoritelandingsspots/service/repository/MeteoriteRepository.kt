package com.antonio.samir.meteoritelandingsspots.service.repository

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceInterface

class MeteoriteRepository(
        val meteoriteDao: MeteoriteDao,
        val nasaService: NasaServiceInterface
) : MeteoriteRepositoryInterface {

    override fun meteoriteOrdened(): LiveData<List<Meteorite>> {
        return meteoriteDao.meteoriteOrdened
    }

    override fun insertAll(meteorites: List<Meteorite>) {
        meteoriteDao.insertAll(meteorites)
    }

    override fun update(meteorite: Meteorite) {
        meteoriteDao.update(meteorite)
    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite> {
        return meteoriteDao.getMeteoriteById(id)
    }

    override fun meteoritesWithOutAddress(): List<Meteorite> {
        return meteoriteDao.meteoritesWithOutAddress
    }

    override fun getRemoteMeteorites(): List<Meteorite>? {
        return nasaService.getMeteorites()
    }

}