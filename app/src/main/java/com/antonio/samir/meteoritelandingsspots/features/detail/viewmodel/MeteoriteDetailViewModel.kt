package com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import kotlinx.coroutines.launch
import java.util.*


class MeteoriteDetailViewModel(
        private val meteoriteService: MeteoriteServiceInterface
) : ViewModel() {

    private var currentMeteorite: LiveData<Meteorite>? = null

    private val meteorite: MediatorLiveData<Meteorite> = MediatorLiveData()

    fun loadMeteoriteById(meteoriteId: String) {
        val meteoriteById = meteoriteService.getMeteoriteById(meteoriteId)
        viewModelScope.launch {
            meteoriteById?.let {
                meteorite.addSource(it) { value ->
                    if (!Objects.equals(meteorite.value, value)) {
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