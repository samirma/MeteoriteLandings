package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.*
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NetworkService
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException


@ExperimentalCoroutinesApi
class MeteoriteRepositoryImpl(
        private val meteoriteLocalRepository: MeteoriteLocalRepository,
        private val meteoriteRepository: NetworkService,
        private val dispatchers: DispatcherProvider
) : MeteoriteRepository {

    private val OLDDATABASE_COUNT = 1000

    override suspend fun loadMeteorites(filter: String?, longitude: Double?, latitude: Double?): DataSource.Factory<Int, Meteorite> {
        return meteoriteLocalRepository.meteoriteOrdered(filter, latitude, longitude)
    }

    override fun getMeteoriteById(id: String): Flow<Result<Meteorite>> = flow {
        emit(InProgress<Meteorite>())
        try {
            emitAll(meteoriteLocalRepository.getMeteoriteById(id).map { Success(it) })
        } catch (e: IOException) {
            emit(Error(MeteoriteServerException(e)))
        }
    }

    override suspend fun update(meteorite: Meteorite) {
        meteoriteLocalRepository.update(meteorite)
    }

    override suspend fun update(list: List<Meteorite>) {
        meteoriteLocalRepository.updateAll(list)
    }

    override fun loadDatabase(): Flow<Result<Nothing>> = flow {
        val meteoritesCount = meteoriteLocalRepository.getMeteoritesCount()
        if (meteoritesCount <= OLDDATABASE_COUNT) {
            emit(InProgress())
            //If it is empty so load the data from internet
            recoverFromNetwork()
        }
        emit(Success())
    }

    private suspend fun recoverFromNetwork() = withContext(dispatchers.default()) {

        val limit = 5000
        var currentPage = 0
        var currentLoaded: Int

        do {

            val filteredList = meteoriteRepository.getMeteorites(limit, limit * currentPage)
                    .filter { isValid(it) }

            meteoriteLocalRepository.insertAll(filteredList)

            currentLoaded = meteoriteRepository.getMeteorites(limit, limit * currentPage).size

            currentPage++
        } while (currentLoaded == limit)

    }

    private fun isValid(it: Meteorite) =
            it.reclong?.toDoubleOrNull() != null && it.reclat?.toDoubleOrNull() != null && !it.year.isNullOrEmpty()

}
