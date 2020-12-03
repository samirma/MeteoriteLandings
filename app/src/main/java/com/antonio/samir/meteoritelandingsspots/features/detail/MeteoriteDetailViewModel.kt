package com.antonio.samir.meteoritelandingsspots.features.detail

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.getDistanceFrom
import com.antonio.samir.meteoritelandingsspots.features.yearString
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModel(
        private val meteoriteRepository: MeteoriteRepository,
        gpsTracker: GPSTrackerInterface
) : ViewModel() {

    private var currentMeteorite = ConflatedBroadcastChannel<String>()

    val location = gpsTracker.location

    val meteorite: LiveData<Result<MeteoriteView>> = currentMeteorite.asFlow()
            .flatMapLatest(meteoriteRepository::getMeteoriteById)
            .combine(location) { meteorite, location -> //Add location
                when (meteorite) {
                    is Result.Success -> Result.Success(getMeteoriteView(meteorite.data, location))
                    is Result.Error -> Result.Error(meteorite.exception)
                    is Result.InProgress -> Result.InProgress()
                }
            }
            .asLiveData()

    private fun getMeteoriteView(meteorite: Meteorite, location: Location?): MeteoriteView {

        val finalAddress = listOf(meteorite.address, meteorite.getDistanceFrom(location)).joinToString(
                separator = " - "
        )

        return MeteoriteView(
                id = meteorite.id.toString(),
                name = meteorite.name,
                yearString = meteorite.yearString,
                address = finalAddress,
                recclass = meteorite.recclass,
                mass = meteorite.mass,
                reclat = meteorite.reclat?.toDouble() ?: 0.0,
                reclong = meteorite.reclong?.toDouble() ?: 0.0,
                hasAddress = meteorite.address.isNullOrEmpty()
        )
    }

    fun loadMeteorite(meteoriteId: String) {
        currentMeteorite.offer(meteoriteId)
    }

    fun requestAddressUpdate(meteorite: MeteoriteView) {

    }

}