package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView

@Immutable
sealed interface MeteoriteDetailUiState {

    data object Loading : MeteoriteDetailUiState

    /** [meteoriteView] is non-null by construction — "loaded but empty" is not a real state. */
    data class Loaded(val meteoriteView: MeteoriteView) : MeteoriteDetailUiState

    data class Error(@param:StringRes val message: Int) : MeteoriteDetailUiState
}
