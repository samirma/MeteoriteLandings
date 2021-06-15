package com.antonio.samir.meteoritelandingsspots.features.detail.userCases

import android.content.Context
import android.location.Location
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteLocalException
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.features.detail.mapper.MeteoriteMapper
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.Input
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class GetMeteoriteById(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteMapper
) : UserCaseBase<Input, ResultOf<MeteoriteView>>() {

    override fun action(input: Input): Flow<ResultOf<MeteoriteView>> = flow {
        emit(ResultOf.InProgress())
        try {
            emitAll(
                meteoriteLocalRepository.getMeteoriteById(input.id)
                    .map {
                        ResultOf.Success(
                            data = mapper.map(
                                MeteoriteMapper.Input(
                                    meteorite = it,
                                    location = input.location,
                                    context = input.context
                                )
                            )
                        )
                    }
            )
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            emit(ResultOf.Error(MeteoriteLocalException(e)))
        }
    }

    data class Input(val id: String, val location: Location?, val context: Context)

    companion object {
        private val TAG = GetMeteoriteById::class.java.simpleName
    }

}