package com.antonio.samir.meteoritelandingsspots.features.list


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Header
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoristListState.Loading
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoristListState.UiContent
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoristListState.UiMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreenNavigation(
    viewModel: MeteoriteListViewModel
) {
    val uiState = viewModel.uiState.collectAsState()

    val onItemClick = remember { return@remember viewModel::onItemClicked }
    val onDarkModeToggleClick = remember { return@remember viewModel::onDarkModeToggleClick }
    val onSearch = remember { return@remember viewModel::searchLocation }

    ListScreen(
        uiState = uiState.value,
        onItemClick = onItemClick,
        onDarkModeToggleClick = onDarkModeToggleClick,
        onSearch = onSearch
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreen(
    uiState: MeteoristListState,
    onItemClick: (itemView: MeteoriteItemView) -> Unit = {},
    onDarkModeToggleClick: () -> Unit = {},
    onSearch: (query: String) -> Unit = {},
) {

    val scrollState = rememberLazyListState()

    val isScrollOnTop: Boolean by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }

    Surface(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            Header(
                isScrollOnTop = isScrollOnTop,
                onDarkModeToggleClick = onDarkModeToggleClick,
                onSearch = onSearch
            )
            Box(
                Modifier.weight(10f, fill = true)
            ) {
                when (uiState) {
                    is UiContent -> MeteoriteList(
                        scrollState = scrollState,
                        meteorites = uiState.meteorites,
                        onItemClick = onItemClick
                    )

                    Loading -> Loading(modifier = Modifier.fillMaxSize())
                    is UiMessage -> MessageError(
                        message = uiState.message
                    )
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list view Dark")
@Composable
fun ListScreenPreviewDark() {
    val items = getFakeListItems()
    MeteoriteLandingsTheme(darkTheme = true) {
        ListScreen(
            uiState = UiContent(meteorites = flowOf(PagingData.from(items)))
        )
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list view Light")
@Composable
fun ListScreenPreviewLight() {
    val items = getFakeListItems()
    MeteoriteLandingsTheme(darkTheme = false) {
        ListScreen(
            uiState = UiContent(meteorites = flowOf(PagingData.from(items)))
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list loading")
@Composable
fun ListScreenLoadingPreview() {
    ListScreen(
        uiState = Loading
    )

}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list error")
@Composable
fun ListScreenErrorPreview() {
    ListScreen(
        uiState = UiMessage(R.string.general_error)
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list message loading 10%")
@Composable
fun ListScreenMessagePreview() {
    val items = getFakeListItems()
    ListScreen(
        uiState = UiContent(meteorites = flowOf(PagingData.from(items)))
    )
}

@Preview
@Composable
fun TextLazyColumn() {

    val items = getFakeListItems()

    LazyColumn(Modifier.height(500.dp)) {

        items.forEach {
            item {
                MeteoriteCell(it, null)
            }
        }

    }

}

private fun getFakeListItems(): List<MeteoriteItemView> = (1..10).map {
    MeteoriteItemView(
        id = "$it",
        name = "name $it",
        yearString = "yearString $it",
        address = "address $it",
        distance = "distance $it",
    )
}
