package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.Input
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModel(
    private val getMeteoriteById: GetMeteoriteById,
) : ViewModel() {

    private val currentMeteorite = MutableStateFlow<String?>(null)

    fun getMeteorite() = currentMeteorite
        .filterNotNull()
        .flatMapLatest { meteoriteId ->
            getMeteoriteById.execute(
                Input(
                    id = meteoriteId
                )
            )
        }.asLiveData()


    fun loadMeteorite(meteoriteId: String) {
        this.currentMeteorite.value = meteoriteId
    }

    fun requestAddressUpdate(meteorite: MeteoriteView) {

    }

}
