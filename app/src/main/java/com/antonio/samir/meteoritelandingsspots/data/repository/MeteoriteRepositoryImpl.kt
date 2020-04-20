package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NetworkService
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class MeteoriteRepositoryImpl(
        private val meteoriteLocalRepository: MeteoriteLocalRepository,
        private val meteoriteRepository: NetworkService,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : MeteoriteRepository {

    private val OLD_DATABASE_COUNT = 1000

    override suspend fun loadMeteorites(filter: String?, longitude: Double?, latitude: Double?): DataSource.Factory<Int, Meteorite> {
        return meteoriteLocalRepository.meteoriteOrdered(filter, latitude, longitude)
    }

    override fun getMeteoriteById(id: String): Flow<Result<Meteorite>> {
        return meteoriteLocalRepository.getMeteoriteById(id)
    }

    override suspend fun update(meteorite: Meteorite) {
        meteoriteLocalRepository.update(meteorite)
    }

    override suspend fun update(list: List<Meteorite>) {
        meteoriteLocalRepository.updateAll(list)
    }

    override fun loadDatabase(): Flow<Result<Nothing>> = flow {
        val meteoritesCount = meteoriteLocalRepository.getMeteoritesCount()
        if (meteoritesCount <= OLD_DATABASE_COUNT) {
            emit(InProgress())
            //If it is empty so load the data from internet
            recoverFromNetwork()
        }
        emit(Success())
    }

    private suspend fun recoverFromNetwork() = withContext(dispatchers.default()) {

        val limit = 5000
        var currentPage = 0
        var currentLoaded = -1

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
