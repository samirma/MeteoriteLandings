package com.antonio.samir.meteoritelandingsspots.features.detail

import kotlinx.coroutines.flow.StateFlow

data class UiState(
    val isLoading: Boolean = false,
    val meteoriteView: MeteoriteView? = null,
    val isDark: StateFlow<Boolean>
)
