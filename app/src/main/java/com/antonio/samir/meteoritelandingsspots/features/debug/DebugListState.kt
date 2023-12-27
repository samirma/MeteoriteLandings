package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.annotation.StringRes

sealed class DebugListState {

    class Error(
        @StringRes val message: Int,
    ) : DebugListState()

    data object Loading : DebugListState()

    data class Loaded(val addressProgress: Float) : DebugListState()
}

