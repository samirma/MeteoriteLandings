package com.antonio.samir.meteoritelandingsspots.service.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface MeteoriteServiceInterface {

    suspend fun loadMeteorites(): LiveData<List<Meteorite>>

    suspend fun getMeteoriteById(id: String): LiveData<Meteorite>?

    fun addressStatus(): MutableLiveData<String>
}
