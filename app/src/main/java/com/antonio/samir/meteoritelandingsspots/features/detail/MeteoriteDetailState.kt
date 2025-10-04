package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.annotation.StringRes
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView

sealed class MeteoriteDetailState {

    class Error(
        @StringRes val message: Int,
    ) : MeteoriteDetailState()

    data class Loaded(
        val meteoriteView: MeteoriteView? = null,
    ) : MeteoriteDetailState()

    data object Loading : MeteoriteDetailState()

}
