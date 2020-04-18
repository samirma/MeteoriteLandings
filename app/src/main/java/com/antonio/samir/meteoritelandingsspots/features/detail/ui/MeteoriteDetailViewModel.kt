package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import android.location.Location
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
import kotlinx.coroutines.flow.combine
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

    val meteorite: LiveData<Pair<Result<Meteorite>, Result<Location>>> = currentMeteorite.asFlow()
            .flatMapLatest {
                meteoriteRepository.getMeteoriteById(it.id.toString())
            }
            .combine(gpsTracker.location) { meteorite, location -> //Add location
                Pair(meteorite, location)
            }
            .asLiveData()

    fun loadMeteorite(meteoriteRef: Meteorite) {
        currentMeteorite.offer(meteoriteRef)
    }

    fun requestAddressUpdate(meteorite: Meteorite) {
        viewModelScope.launch {
            meteoriteRepository.updateAddress(meteorite)
        }
    }

}