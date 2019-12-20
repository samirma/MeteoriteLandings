package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

interface MeteoriteServiceInterface {

    val location: Location?

    suspend fun loadMeteorites(filter: String?): DataSource.Factory<Int, Meteorite>

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    fun addressStatus(): MutableLiveData<String>

    suspend fun requestAddressUpdate(meteorite: Meteorite)

    fun requestAddressUpdate(meteorite: List<Meteorite>)
}
