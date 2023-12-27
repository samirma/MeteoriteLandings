package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.annotation.StringRes
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView

sealed class MeteoriteListState {

    class Error(
        @StringRes val message: Int,
    ) : MeteoriteListState()

    data class Loaded(
        val meteoriteView: MeteoriteView? = null,
    ) : MeteoriteListState()

    data object Loading : MeteoriteListState()

}
