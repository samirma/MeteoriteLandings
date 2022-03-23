package com.antonio.samir.meteoritelandingsspots.data.repository

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.*
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean


@ExperimentalCoroutinesApi
class MeteoriteRepositoryImpl(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val meteoriteRemoteRepository: MeteoriteRemoteRepository,
    private val dispatchers: DispatcherProvider,
) : MeteoriteRepository {

    private val shouldLoad = AtomicBoolean(true)

    override suspend fun update(meteorite: Meteorite) {
        meteoriteLocalRepository.update(meteorite)
    }

    override suspend fun update(list: List<Meteorite>) {
        meteoriteLocalRepository.updateAll(list)
    }

    override fun loadDatabase(): Flow<ResultOf<Unit>> = flow {
        if (shouldLoad.getAndSet(false)) {
            val meteoritesCount = meteoriteLocalRepository.getMeteoritesCount()
            emit(InProgress())
            try {
                recoverFromNetwork(
                    if (meteoritesCount <= OLDDATABASE_COUNT) {
                        0 //Download from beginner
                    } else {
                        meteoritesCount
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                emit(Error(MeteoriteServerException(e)))
            }
        }
        emit(Success(Unit))
    }

    private suspend fun recoverFromNetwork(offset: Int) = withContext(dispatchers.io()) {

        var currentPage = 0

        do {

            val serviceOffset = (PAGE_SIZE * currentPage) + offset
            val meteorites = meteoriteRemoteRepository.getMeteorites(
                offset = serviceOffset,
                limit = PAGE_SIZE
            )

            meteoriteLocalRepository.insertAll(meteorites)

            currentPage++
        } while (meteorites.isNotEmpty())


    }

    companion object {

        private const val OLDDATABASE_COUNT = 1000

        private const val PAGE_SIZE = 5000

        private val TAG = MeteoriteRepositoryImpl::class.java.simpleName
    }

}
