package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading


@Composable
fun DebugNavigation() {

    val viewModel: DebugViewModel = hiltViewModel()

    val uiState = viewModel.uiState.collectAsState()

    DebugScreen(
        state = uiState.value,
        onFetchRequest = { viewModel.loadRawMeteoriteList() }
    ) { viewModel.loadAddresses() }
}

@Composable
fun DebugScreen(
    state: DebugListState,
    onFetchRequest: () -> Unit,
    onAddressStart: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("DEBUG SCREEN")
        Button(onClick = onFetchRequest) {
            Text(text = "Retrieve meteorites list")
        }
        Button(onClick = onAddressStart) {
            Text(text = "Start address recovery")
        }
    }
    when (state) {
        is DebugListState.Error -> DebugListState.Error(R.string.general_error)
        is DebugListState.Loaded -> AddressProgress(state.addressProgress)
        is DebugListState.Loading -> Loading(modifier = Modifier.fillMaxSize())
    }

}

