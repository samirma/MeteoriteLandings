package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface MeteoriteServiceInterface {

    val location: Location?

    suspend fun loadMeteorites(location: String?): LiveData<List<Meteorite>>

    suspend fun filterList(filter: String)

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    fun addressStatus(): MutableLiveData<String>
}
