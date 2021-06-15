package com.antonio.samir.meteoritelandingsspots.features.detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.*
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.*
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModel(
    gpsTracker: GPSTrackerInterface,
    private val getMeteoriteById: GetMeteoriteById,
) : ViewModel() {

    private var currentMeteorite = ConflatedBroadcastChannel<String>()

    val location = gpsTracker.location

    fun getMeteorite(context: Context): LiveData<ResultOf<MeteoriteView>> =
        currentMeteorite.asFlow()
            .combine(location) { meteorite, location ->
                getMeteoriteById.execute(
                    Input(
                        id = meteorite,
                        location = location,
                        context = context
                    )
                )
            }
            .flatMapLatest{
                it
            }
            .asLiveData()


    fun loadMeteorite(meteoriteId: String) {
        currentMeteorite.offer(meteoriteId)
    }

    fun requestAddressUpdate(meteorite: MeteoriteView) {

    }

}
