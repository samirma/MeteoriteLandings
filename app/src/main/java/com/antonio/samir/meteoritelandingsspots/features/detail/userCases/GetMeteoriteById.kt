package com.antonio.samir.meteoritelandingsspots.features.detail.userCases

import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMeteoriteById @Inject constructor(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
) {

    operator fun invoke(id: Int): Flow<ResultOf<Meteorite>> =
        meteoriteLocalRepository.getMeteoriteById(id)
            .map { meteorite ->
                if (meteorite == null) {
                    ResultOf.Error(DataError.NOT_FOUND)
                } else {
                    ResultOf.Success(meteorite)
                }
            }
            .catch { throwable -> emit(ResultOf.Error(DataError.UNKNOWN, throwable)) }
}
