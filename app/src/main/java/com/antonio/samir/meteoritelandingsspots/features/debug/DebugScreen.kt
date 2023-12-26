package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun DebugNavigation(
    viewModel: DebugViewModel
) {
    val uiState = viewModel.uiState.collectAsState()

    DebugScreen(
        debugListState = uiState.value,
    )
}

@Composable
fun DebugScreen(
    debugListState: DebugListState,
) {

}

