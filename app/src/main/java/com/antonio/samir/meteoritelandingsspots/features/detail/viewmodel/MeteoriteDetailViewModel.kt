package com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import kotlinx.coroutines.launch


class MeteoriteDetailViewModel(
        private val meteoriteService: MeteoriteServiceInterface
) : ViewModel() {

    private var currentMeteorite: LiveData<Meteorite>? = null

    private val meteorite: MediatorLiveData<Meteorite> = MediatorLiveData()

    fun loadMeteorite(meteoriteId: Meteorite) {
        val meteoriteById = meteoriteService.getMeteoriteById(meteoriteId.id.toString())
        viewModelScope.launch {
            meteoriteById?.let {
                meteorite.addSource(it) { value ->
                    val currentValue = meteorite.value
                    if (value.id != currentValue?.id || value.address != currentValue.address) {
                        meteorite.value = value
                    }
                }
            }
            currentMeteorite?.let { meteorite.removeSource(it) }
            currentMeteorite = meteoriteById
        }
    }

    fun getMeteorite(): LiveData<Meteorite> {
        return meteorite
    }

}