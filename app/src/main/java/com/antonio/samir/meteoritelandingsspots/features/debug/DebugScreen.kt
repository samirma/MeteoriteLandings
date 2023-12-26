package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun DebugNavigation() {

    val viewModel: DebugViewModel = hiltViewModel()

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

