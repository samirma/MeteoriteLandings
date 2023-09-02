package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.annotation.StringRes

sealed class DebugListState {

    class UiMessage(
        @StringRes val message: Int,
    ) : DebugListState()

    object Loading : DebugListState()

    object Loaded : DebugListState()
}

