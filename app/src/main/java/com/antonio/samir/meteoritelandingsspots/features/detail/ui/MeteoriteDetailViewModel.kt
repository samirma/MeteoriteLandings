package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModel(
        private val meteoriteRepository: MeteoriteRepository,
        private val gpsTracker: GPSTrackerInterface
) : ViewModel() {

    private var currentMeteorite = ConflatedBroadcastChannel<Meteorite>()

    val location = gpsTracker.location

    val meteorite: LiveData<Result<Meteorite>> = currentMeteorite.asFlow()
            .flatMapLatest {
                meteoriteRepository.getMeteoriteById(it.id.toString())
            }
            .asLiveData()

    fun loadMeteorite(meteoriteRef: Meteorite) {
        currentMeteorite.offer(meteoriteRef)
    }


    fun requestAddressUpdate(meteorite: Meteorite) {
        viewModelScope.launch {
            meteoriteRepository.requestAddressUpdate(meteorite)
        }
    }

}