package com.antonio.samir.meteoritelandingsspots.service.repository.local

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.service.repository.remote.NasaRemoteRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MeteoriteRepository(
        val meteoriteDao: MeteoriteDao,
        val nasaRemoteRepository: NasaRemoteRepositoryInterface
) : MeteoriteRepositoryInterface {

    override suspend fun meteoriteOrdened(): LiveData<List<Meteorite>> =
            withContext(Dispatchers.IO) {
                return@withContext meteoriteDao.meteoriteOrdered()
            }

    override suspend fun insertAll(meteorites: List<Meteorite>) =
            withContext(Dispatchers.IO) {
                meteoriteDao.insertAll(meteorites)
            }

    override suspend fun update(meteorite: Meteorite) = withContext(Dispatchers.IO) {
        return@withContext meteoriteDao.update(meteorite)
    }

    override suspend fun getMeteoriteById(id: String): LiveData<Meteorite> = withContext(Dispatchers.IO) {
        return@withContext meteoriteDao.getMeteoriteById(id)
    }

    override suspend fun meteoritesWithOutAddress(): List<Meteorite> =
            withContext(Dispatchers.IO) {
                return@withContext meteoriteDao.meteoritesWithOutAddress()
            }

    override suspend fun getRemoteMeteorites(): List<Meteorite>? = withContext(Dispatchers.IO) {
        return@withContext nasaRemoteRepository.getMeteorites()
    }

}