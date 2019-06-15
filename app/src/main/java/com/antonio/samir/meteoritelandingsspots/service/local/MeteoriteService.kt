package com.antonio.samir.meteoritelandingsspots.service.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.model.Meteorite

interface MeteoriteService {

    fun loadMeteorites(): LiveData<List<Meteorite>>

    fun getMeteoriteById(id: String): LiveData<Meteorite>?

    fun addressStatus(): MutableLiveData<String>
}
