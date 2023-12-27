package com.antonio.samir.meteoritelandingsspots.service.address

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import kotlinx.coroutines.flow.Flow

interface AddressService {

    fun recoveryAddress(): Flow<ResultOf<Float>>

    suspend fun recoverAddress(meteorite: Meteorite)

}