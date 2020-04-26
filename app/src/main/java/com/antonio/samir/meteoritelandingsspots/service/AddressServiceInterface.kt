package com.antonio.samir.meteoritelandingsspots.service

import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface AddressServiceInterface {

    fun recoveryAddress(): Flow<Result<Float>>

    suspend fun recoverAddress(meteorite: Meteorite)

    suspend fun recoverAddress(list: List<Meteorite>)
}