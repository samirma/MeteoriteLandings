package com.antonio.samir.meteoritelandingsspots.data.repository

import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface AddressServiceInterface {

    fun recoveryAddress(): Flow<Result<String>>

    suspend fun recoverAddress(meteorite: Meteorite)

    suspend fun recoverAddress(list: List<Meteorite>)
}