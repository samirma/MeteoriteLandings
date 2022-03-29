package com.antonio.samir.meteoritelandingsspots.features.detail.userCases

import android.content.Context
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.features.detail.mapper.MeteoriteMapper
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.Input
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMeteoriteById(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteMapper,
    private val context: Context
) : UserCaseBase<Input, ResultOf<MeteoriteView>>() {

    override fun action(input: Input): Flow<ResultOf<MeteoriteView>> =
        meteoriteLocalRepository.getMeteoriteById(input.id)
            .map {
                ResultOf.Success(
                    data = mapper.map(
                        MeteoriteMapper.Input(
                            meteorite = it,
                            location = null,
                            context = context
                        )
                    )
                )
            }

    data class Input(val id: String)

    companion object {
        private val TAG = GetMeteoriteById::class.java.simpleName
    }

}