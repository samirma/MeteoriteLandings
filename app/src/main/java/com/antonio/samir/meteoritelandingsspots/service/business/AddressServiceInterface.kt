package com.antonio.samir.meteoritelandingsspots.service.business

import androidx.lifecycle.MutableLiveData

interface AddressServiceInterface {

    val status: MutableLiveData<String>

    fun recoveryAddress()
}