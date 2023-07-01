package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ActionBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetail
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Composable
fun DetailScreen(
    meteoriteId: String,
    navController: NavController,
    viewModel: MeteoriteDetailViewModel
) {

    viewModel.loadMeteorite(meteoriteId)

    val uiState = viewModel.uiState.collectAsState().value

    MeteoriteLandingsTheme(darkTheme = uiState.isDark) {

        Column(modifier = Modifier.fillMaxSize()) {

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                val meteoriteView = uiState.meteoriteView
                if (meteoriteView != null) {
                    DetailContent(meteoriteView) {
                        navController.popBackStack()
                    }
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
