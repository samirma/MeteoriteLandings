package com.antonio.samir.meteoritelandingsspots.features.detail.userCases

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteLocalException
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class GetMeteoriteById (
    private val meteoriteLocalRepository: MeteoriteLocalRepository
    ) : UserCaseBase<String, Result<Meteorite>>() {


    override fun action(input: String): Flow<Result<Meteorite>> = flow {
        emit(Result.InProgress())
        try {
            emitAll(meteoriteLocalRepository.getMeteoriteById(input).map { Result.Success(it) })
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            emit(Result.Error(MeteoriteLocalException(e)))
        }
    }

    companion object {
        private val TAG = GetMeteoriteById::class.java.simpleName
    }

}