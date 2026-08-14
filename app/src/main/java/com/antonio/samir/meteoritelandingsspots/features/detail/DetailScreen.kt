package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetail
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetailTopBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

@Composable
fun DetailScreenNavigation(
    meteoriteId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The id is handed to the ViewModel at construction time via assisted injection, so there is
    // no window in which the screen renders the previously selected meteorite.
    val viewModel =
        hiltViewModel<MeteoriteDetailViewModel, MeteoriteDetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(meteoriteId) }
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailScreen(uiState = uiState, onBack = onBack, modifier = modifier)
}

@Composable
fun DetailScreen(
    uiState: MeteoriteDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = (uiState as? MeteoriteDetailUiState.Loaded)?.meteoriteView?.name
        ?: stringResource(R.string.app_name)

    Scaffold(
        modifier = modifier,
        topBar = { MeteoriteDetailTopBar(title = title, onBack = onBack) },
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (uiState) {
                is MeteoriteDetailUiState.Loading -> Loading(Modifier.fillMaxSize())
                is MeteoriteDetailUiState.Loaded -> MeteoriteDetail(
                    meteoriteView = uiState.meteoriteView,
                    modifier = Modifier.fillMaxSize(),
                )

                is MeteoriteDetailUiState.Error -> MessageError(
                    message = uiState.message,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private val previewMeteorite = MeteoriteView(
    id = 1,
    name = "Aachen",
    yearString = "1880",
    address = "Aachen, Nordrhein-Westfalen, Germany",
    type = "L5",
    mass = "21",
    latitude = 50.775,
    longitude = 6.08333,
)

@PreviewLightDark
@Composable
private fun DetailScreenLoadedPreview() {
    MeteoriteLandingsTheme {
        DetailScreen(MeteoriteDetailUiState.Loaded(previewMeteorite), onBack = {})
    }
}

@Preview(name = "Detail loading")
@Composable
private fun DetailScreenLoadingPreview() {
    MeteoriteLandingsTheme {
        DetailScreen(MeteoriteDetailUiState.Loading, onBack = {})
    }
}

@Preview(name = "Detail error")
@Composable
private fun DetailScreenErrorPreview() {
    MeteoriteLandingsTheme {
        DetailScreen(MeteoriteDetailUiState.Error(R.string.meteorite_not_found), onBack = {})
    }
}
