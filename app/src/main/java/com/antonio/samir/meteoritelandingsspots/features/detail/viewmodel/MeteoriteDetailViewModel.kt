package com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryInterface

class MeteoriteDetailViewModel(
        private val meteoriteRepository: MeteoriteRepositoryInterface
) : ViewModel() {

    private val meteorite: MediatorLiveData<Meteorite> = MediatorLiveData()

    fun loadMeteoriteById(meteoriteId: String) {
        meteoriteRepository.getMeteoriteById(meteoriteId)?.let {
            meteorite.addSource(it) { value ->
                meteorite.setValue(value)
            }
        }
    }

    fun getMeteorite(): LiveData<Meteorite> {
        return meteorite
    }

}