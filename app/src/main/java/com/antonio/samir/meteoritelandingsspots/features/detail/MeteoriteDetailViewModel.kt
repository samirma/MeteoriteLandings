package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.massText
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * The meteorite id is an assisted constructor parameter, so the ViewModel is fully formed the
 * moment it exists.
 *
 * Previously the screen pushed the id in from a `LaunchedEffect` after construction. Combined
 * with the Activity-scoped ViewModel that fixed in [com.antonio.samir.meteoritelandingsspots.features.MeteoriteNavDisplay],
 * that meant opening a second meteorite briefly rendered the first one — the bug commit 2d0a7f3
 * worked around at the map layer.
 */
@HiltViewModel(assistedFactory = MeteoriteDetailViewModel.Factory::class)
class MeteoriteDetailViewModel @AssistedInject constructor(
    @Assisted private val meteoriteId: Int,
    getMeteoriteById: GetMeteoriteById,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(meteoriteId: Int): MeteoriteDetailViewModel
    }

    val uiState: StateFlow<MeteoriteDetailUiState> = getMeteoriteById(meteoriteId)
        .map { result ->
            when (result) {
                is ResultOf.Success -> MeteoriteDetailUiState.Loaded(result.data.toView())
                // Errors used to be dropped silently, leaving the screen spinning forever.
                is ResultOf.Error -> MeteoriteDetailUiState.Error(result.error.toMessageRes())
                is ResultOf.InProgress -> MeteoriteDetailUiState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = MeteoriteDetailUiState.Loading,
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

private fun DataError.toMessageRes() = when (this) {
    DataError.NOT_FOUND -> R.string.meteorite_not_found
    DataError.NETWORK -> R.string.no_network
    else -> R.string.general_error
}

private fun Meteorite.toView() = MeteoriteView(
    id = id,
    name = name.orEmpty(),
    yearString = year?.toString().orEmpty(),
    address = address.orEmpty(),
    type = recClass.orEmpty(),
    mass = massText().orEmpty(),
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
)
