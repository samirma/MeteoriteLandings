package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ActionBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetail
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

@Composable
fun DetailScreenNavigation(
    navController: NavController,
    meteoriteId: String
) {

    val viewModel: MeteoriteDetailViewModel = hiltViewModel()

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadMeteorite(meteoriteId)
    }

    DetailScreen(state) {
        navController.popBackStack()
    }

}

@Composable
fun DetailScreen(
    uiState: UiState,
    onBack: () -> Unit
) {

    MeteoriteLandingsTheme(darkTheme = uiState.isDark) {

        Column(modifier = Modifier.fillMaxSize()) {

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                val meteoriteView = uiState.meteoriteView
                if (meteoriteView != null) {
                    DetailContent(meteoriteView, onBack)
                }
            }
        }

    }

}

@Composable
private fun DetailContent(
    meteoriteView: MeteoriteView,
    onItemClick: (() -> Unit) = {}
) {
    ActionBar(title = meteoriteView.name, onItemClick)
    MeteoriteDetail(meteoriteView = meteoriteView)
}
