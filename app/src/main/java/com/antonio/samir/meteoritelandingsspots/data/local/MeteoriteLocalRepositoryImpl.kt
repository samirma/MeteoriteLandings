package com.antonio.samir.meteoritelandingsspots.data.local

import com.antonio.samir.meteoritelandingsspots.data.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject

class MeteoriteLocalRepositoryImpl @Inject constructor(
    private val meteoriteDao: MeteoriteDao
) : MeteoriteLocalRepository {

    override fun meteoriteOrdered(
        filter: String?,
        latitude: Double?,
        longitude: Double?,
        limit: Long,
    ) = if (latitude == null || longitude == null) {
        meteoriteDao.meteoriteFiltered(filter = prepareFilter(filter))
    } else {
        meteoriteDao.meteoriteOrderedByLocationFiltered(
            lat = latitude,
            lng = longitude,
            filter = prepareFilter(filter)
        )
    }

    private fun prepareFilter(filter: String?) = filter?.lowercase(Locale.getDefault()) ?: ""

    override suspend fun getMeteoritesCount(): Int {
        return meteoriteDao.getMeteoritesCount()
    }

    override suspend fun getValidMeteoritesCount(): Int {
        return meteoriteDao.getValidMeteoritesCount()
    }

    override suspend fun getMeteoritesWithoutAddressCount(): Int {
        return meteoriteDao.getMeteoritesWithoutAddressCount()
    }

    override suspend fun update(meteorite: Meteorite) {
        meteoriteDao.update(meteorite)
    }

    override fun getMeteoriteById(id: String): Flow<Meteorite> {
        return meteoriteDao.getMeteoriteById(id)
    }


    override fun meteoritesWithOutAddress() = meteoriteDao.meteoritesWithOutAddress()

    override suspend fun insertAll(meteorites: List<Meteorite>) {
        meteoriteDao.insertAll(meteorites)
    }

    override suspend fun updateAll(meteorites: List<Meteorite>) {
        meteoriteDao.updateAll(meteorites)
    }

}