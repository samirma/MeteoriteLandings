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

    private val meteorite: MediatorLiveData<Meteorite> = MediatorLiveData()

    fun loadMeteoriteById(meteoriteId: String) {
        viewModelScope.launch {
            meteoriteService.getMeteoriteById(meteoriteId)?.let {
                meteorite.addSource(it) { value ->
                    meteorite.setValue(value)
                }
            }
        }
    }

    fun getMeteorite(): LiveData<Meteorite> {
        return meteorite
    }

}