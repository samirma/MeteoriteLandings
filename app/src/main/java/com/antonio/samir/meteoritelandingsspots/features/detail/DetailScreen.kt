package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ActionBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteDetail
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView

@Composable
fun DetailScreenNavigation(
    meteoriteId: String,
    onBack: () -> Unit
) {

    val viewModel: MeteoriteDetailViewModel = hiltViewModel()

    val state by viewModel.meteoriteDetailState.collectAsState()

    LaunchedEffect(meteoriteId) {
        viewModel.loadMeteorite(meteoriteId)
    }

    DetailScreen(state) {
        onBack()
    }

}


@Composable
fun DetailScreen(
    state: MeteoriteDetailState,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is MeteoriteDetailState.Loading -> CircularProgressIndicator()
            is MeteoriteDetailState.Loaded -> {
                val meteoriteView = state.meteoriteView
                if (meteoriteView != null) {
                    DetailContent(meteoriteView, onBack)
                }
            }

            is MeteoriteDetailState.Error -> MessageError(
                message = stringResource(id = state.message)
            )
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
