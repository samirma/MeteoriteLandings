package com.antonio.samir.meteoritelandingsspots.features.detail

import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView

data class UiState(
    val isLoading: Boolean = false,
    val meteoriteView: MeteoriteView? = null,
    val isDark: Boolean = false
)
