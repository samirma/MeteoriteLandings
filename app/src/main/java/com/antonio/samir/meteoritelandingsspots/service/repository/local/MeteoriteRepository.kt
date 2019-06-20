package com.antonio.samir.meteoritelandingsspots.service.repository.local

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.service.repository.remote.NasaRemoteRepositoryInterface

class MeteoriteRepository(
        val meteoriteDao: MeteoriteDao,
        val nasaRemoteRepository: NasaRemoteRepositoryInterface
) : MeteoriteRepositoryInterface {

    override fun meteoriteOrdened(): LiveData<List<Meteorite>> {
        return meteoriteDao.meteoriteOrdered()
    }

    override suspend fun insertAll(meteorites: List<Meteorite>) {
        meteoriteDao.insertAll(meteorites)
    }

    override suspend fun update(meteorite: Meteorite) {
        meteoriteDao.update(meteorite)
    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite> {
        return meteoriteDao.getMeteoriteById(id)
    }

    override suspend fun meteoritesWithOutAddress(): List<Meteorite> {
        return meteoriteDao.meteoritesWithOutAddress()
    }

    override suspend fun getRemoteMeteorites(): List<Meteorite>? {
        return nasaRemoteRepository.getMeteorites()
    }

}