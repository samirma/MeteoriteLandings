package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.Input
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeteoriteDetailViewModel @Inject constructor(
    private val getMeteoriteById: GetMeteoriteById,
) : ViewModel() {

    private val currentMeteorite = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<MeteoriteListState>(MeteoriteListState.Loading)

    // UI state exposed to the UI
    val meteoriteDetailState: StateFlow<MeteoriteListState> = _state

    init {
        viewModelScope.launch(Dispatchers.Default) {
            getMeteorite().collect { result ->
                if (result is ResultOf.Success) {
                    _state.value = MeteoriteListState.Loaded(result.data)
                }
            }
        }
    }

    private fun getMeteorite() = currentMeteorite.asStateFlow()
        .filterNotNull()
        .flatMapLatest { meteoriteId ->
            getMeteoriteById(
                Input(
                    id = meteoriteId
                )
            )
        }


    fun loadMeteorite(meteoriteId: String) {
        this.currentMeteorite.value = meteoriteId
    }

}
