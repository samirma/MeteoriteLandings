package com.antonio.samir.meteoritelandingsspots.data.local

import android.location.Location
import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteServerException
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.*

class MeteoriteLocalRepositoryImpl(
        val meteoriteDao: MeteoriteDao
) : MeteoriteLocalRepository {

    override fun meteoriteOrdered(location: Location?, filter: String?): DataSource.Factory<Int, Meteorite> {

        return if (location == null) {
            if (filter.isNullOrEmpty()) {
                meteoriteDao.meteoriteOrdered()
            } else {
                meteoriteDao.meteoriteFiltered(filter.toLowerCase(Locale.getDefault()))
            }
        } else {
            val lng = location.longitude
            val lat = location.latitude

            if (filter != null) {
                meteoriteDao.meteoriteOrderedByLocationFiltered(lat, lng, filter.toLowerCase(Locale.getDefault()))
            } else {
                meteoriteDao.meteoriteOrderedByLocation(lat, lng)
            }
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

    @ExperimentalCoroutinesApi
    override fun getMeteoriteById(id: String): Flow<Result<Meteorite>> = flow {
        emit(Result.InProgress<Meteorite>())
        try {
            emitAll(meteoriteDao.getMeteoriteById(id).map { Result.Success(it) })
        } catch (e: IOException) {
            emit(Result.Error(MeteoriteServerException(e)))
        }
    }

    override suspend fun meteoritesWithOutAddress(): List<Meteorite> {
        return meteoriteDao.meteoritesWithOutAddress()
    }

    override suspend fun insertAll(meteorites: List<Meteorite>) {
        meteoriteDao.insertAll(meteorites)
    }

    override suspend fun updateAll(meteorites: List<Meteorite>) {
        meteoriteDao.updateAll(meteorites)
    }

}