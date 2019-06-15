package com.antonio.samir.meteoritelandingsspots.service.local

import androidx.lifecycle.MutableLiveData

interface AddressServiceInterface {

    val status: MutableLiveData<String>

    fun recoveryAddress()
}