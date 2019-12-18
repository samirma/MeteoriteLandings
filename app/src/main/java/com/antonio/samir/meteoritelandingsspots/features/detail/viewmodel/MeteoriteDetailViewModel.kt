package com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


class MeteoriteDetailViewModel(
        private val meteoriteService: MeteoriteServiceInterface
) : ViewModel() {

    private var currentMeteorite: LiveData<Meteorite>? = null

    private val meteorite: MediatorLiveData<Meteorite> = MediatorLiveData()

    fun loadMeteorite(meteoriteRef: Meteorite) {
        val meteorite = meteoriteService.getMeteoriteById(meteoriteRef.id.toString())
        meteorite?.let {
            this@MeteoriteDetailViewModel.meteorite.addSource(it) { value ->
                this@MeteoriteDetailViewModel.meteorite.postValue(value)
            }
            currentMeteorite = meteorite
        }
    }

    fun getMeteorite(): LiveData<Meteorite> {
        return meteorite
    }

    fun getLocation(): Location? {
        return meteoriteService.location
    }

    suspend fun requestAddressUpdate(meteorite: Meteorite) {
        meteoriteService.requestAddressUpdate(meteorite)
    }

}