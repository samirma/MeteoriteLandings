package com.antonio.samir.meteoritelandingsspots.service.business

import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface AddressServiceInterface {

    val status: MutableLiveData<String>

    fun recoveryAddress()

    suspend fun recoverAddress(meteorite: Meteorite)
}