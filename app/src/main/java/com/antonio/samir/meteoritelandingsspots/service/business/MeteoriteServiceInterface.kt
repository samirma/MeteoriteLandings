package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface MeteoriteServiceInterface {

    val location: Location?

    suspend fun loadMeteorites(): LiveData<List<Meteorite>>

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    fun addressStatus(): MutableLiveData<String>
}
