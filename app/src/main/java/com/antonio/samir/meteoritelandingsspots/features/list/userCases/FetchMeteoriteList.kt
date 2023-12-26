package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteServerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.time.measureTimedValue

class FetchMeteoriteList @Inject constructor(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val meteoriteRemoteRepository: MeteoriteRemoteRepository,
) : UserCaseBase<Unit, ResultOf<Unit>>() {

    private val shouldLoad = AtomicBoolean(true)

    override fun action(input: Unit) = flow {
        val timeInMillis = measureTimedValue {
            if (shouldLoad.getAndSet(false)) {
                val meteoritesCount = meteoriteLocalRepository.getMeteoritesCount()
                emit(ResultOf.InProgress())
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
                    emit(ResultOf.Error(MeteoriteServerException(e)))
                }
            }
        }
        Log.i(TAG, "Base loaded in ${timeInMillis.duration.inWholeSeconds} seconds")
        emit(ResultOf.Success(Unit))
    }

    private suspend fun recoverFromNetwork(offset: Int) = withContext(Dispatchers.Default) {

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

        private val TAG = FetchMeteoriteList::class.java.simpleName
    }

}