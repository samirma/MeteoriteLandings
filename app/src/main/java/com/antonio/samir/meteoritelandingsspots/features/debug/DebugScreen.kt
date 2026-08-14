package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetailTopBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

@Composable
fun DebugNavigation(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DebugViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DebugScreen(
        uiState = uiState,
        onBack = onBack,
        modifier = modifier,
        onFetchRequest = viewModel::loadRawMeteoriteList,
        onAddressStart = viewModel::loadAddresses,
    )
}

@Composable
fun DebugScreen(
    uiState: DebugUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onFetchRequest: () -> Unit = {},
    onAddressStart: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MeteoriteDetailTopBar(title = stringResource(R.string.debug_title), onBack = onBack)
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onFetchRequest,
                enabled = !uiState.isSyncing,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.debug_fetch_list))
            }
            Button(
                onClick = onAddressStart,
                enabled = !uiState.isRecoveringAddresses,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.debug_start_addresses))
            }

            if (uiState.isSyncing) {
                Loading(Modifier.fillMaxWidth())
            }

            AddressProgress(progress = uiState.addressProgress)

            // The previous version built a state object here and rendered nothing at all, so
            // failures were invisible.
            uiState.message?.let { message ->
                Text(
                    text = stringResource(message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DebugScreenPreview() {
    MeteoriteLandingsTheme {
        DebugScreen(
            uiState = DebugUiState(addressProgress = 42f, message = R.string.general_error),
            onBack = {},
        )
    }
}
