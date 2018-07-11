package com.antonio.samir.meteoritelandingsspots.service.local

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.model.Meteorite

interface MeteoriteService {

    fun loadMeteorites(): LiveData<List<Meteorite>>

}
