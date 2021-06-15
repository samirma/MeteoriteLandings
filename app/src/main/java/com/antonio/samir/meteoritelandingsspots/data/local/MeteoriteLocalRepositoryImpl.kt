package com.antonio.samir.meteoritelandingsspots.data.local

import androidx.paging.PagingSource
import com.antonio.samir.meteoritelandingsspots.data.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import java.util.*

class MeteoriteLocalRepositoryImpl(
        private val meteoriteDao: MeteoriteDao,
        private val dispatchers: DispatcherProvider

) : MeteoriteLocalRepository {

    override fun meteoriteOrdered(
            filter: String?,
            latitude: Double?,
            longitude: Double?,
            limit: Long,
    ) = if (latitude == null || longitude == null) {
            if (filter.isNullOrEmpty()) {
                meteoriteDao.meteoriteOrdered(limit)
            } else {
                meteoriteDao.meteoriteFiltered(filter.lowercase(Locale.getDefault()))
            }
        } else {

            if (filter != null) {
                meteoriteDao.meteoriteOrderedByLocationFiltered(latitude, longitude, filter.toLowerCase(Locale.getDefault()))
            } else {
                meteoriteDao.meteoriteOrderedByLocation(latitude, longitude)
            }
        }

    override suspend fun getMeteoritesCount(): Int {
        return meteoriteDao.getMeteoritesCount()
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


    override fun meteoritesWithOutAddress(): Flow<List<Meteorite>> {
        return meteoriteDao.meteoritesWithOutAddress()
    }

    override suspend fun insertAll(meteorites: List<Meteorite>) {
        meteoriteDao.insertAll(meteorites)
    }

    override suspend fun updateAll(meteorites: List<Meteorite>) {
        meteoriteDao.updateAll(meteorites)
    }

}