package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class DebugUiState(
    val isSyncing: Boolean = false,
    val addressProgress: Float = 0f,
    val isRecoveringAddresses: Boolean = false,
    @param:StringRes val message: Int? = null,
)
