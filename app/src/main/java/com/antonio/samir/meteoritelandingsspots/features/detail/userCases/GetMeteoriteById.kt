package com.antonio.samir.meteoritelandingsspots.features.detail.userCases

import android.content.Context
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.features.detail.mapper.MeteoriteMapper
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.Input
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.flow.*

class GetMeteoriteById(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val gpsTracker: GPSTrackerInterface,
    private val mapper: MeteoriteMapper,
    private val context: Context
) : UserCaseBase<Input, ResultOf<MeteoriteView>>() {

    override fun action(input: Input): Flow<ResultOf<MeteoriteView>> = flow {
        gpsTracker.requestLocation()
        gpsTracker.location.collect { location ->
            val map: Flow<ResultOf.Success<MeteoriteView>> =
                meteoriteLocalRepository.getMeteoriteById(input.id)
                    .map {
                        ResultOf.Success(
                            data = mapper.map(
                                MeteoriteMapper.Input(
                                    meteorite = it,
                                    location = location,
                                    context = context
                                )
                            )
                        )
                    }
            emitAll(map)
        }
    }

    data class Input(val id: String)

    companion object {
        private val TAG = GetMeteoriteById::class.java.simpleName
    }

}