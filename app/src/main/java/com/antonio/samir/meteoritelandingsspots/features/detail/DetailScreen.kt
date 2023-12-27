package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ActionBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetail
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView

@Composable
fun DetailScreenNavigation(
    navController: NavController,
    meteoriteId: String
) {

    val viewModel: MeteoriteDetailViewModel = hiltViewModel()

    val state by viewModel.meteoriteDetailState.collectAsState()

    LaunchedEffect(meteoriteId) {
        viewModel.loadMeteorite(meteoriteId)
    }

    DetailScreen(state) {
        navController.popBackStack()
    }

}

@Composable
fun DetailScreen(
    state: MeteoriteListState,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is MeteoriteListState.Loading -> CircularProgressIndicator()
            is MeteoriteListState.Loaded -> {
                val meteoriteView = state.meteoriteView
                if (meteoriteView != null) {
                    DetailContent(meteoriteView, onBack)
                }
            }

            is MeteoriteListState.Error -> Text(stringResource(id = state.message))
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
