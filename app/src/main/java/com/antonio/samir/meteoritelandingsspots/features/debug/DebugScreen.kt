package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun DebugNavigation() {

    val viewModel: DebugViewModel = hiltViewModel()

    val uiState = viewModel.uiState.collectAsState()

    DebugScreen(
        state = uiState.value,
    )
}

@Composable
fun DebugScreen(
    state: DebugListState,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("DEBUG SCREEN")
        Button(onClick = {}) {
            Text(text = "")
        }
        Button(onClick = {}) {
            Text(text = "Rectangle shape")
        }
    }

}

