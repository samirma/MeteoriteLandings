package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Header
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.Route
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListState.Error
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListState.Loaded
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListState.Loading
import kotlinx.coroutines.flow.flowOf

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreenNavigation(navController: NavHostController, activity: AppCompatActivity) {

    val viewModel: MeteoriteListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.fetchMeteoriteList(activity)
    }

    ListScreen(
        uiState = uiState.value,
        onItemClick = { itemView: MeteoriteItemView ->
            navController.navigate(Route.getDetailUrlById(itemView.id))
        },
        onDarkModeToggleClick = viewModel::onDarkModeToggleClick,
        onSearch = { query: String ->
            viewModel.searchLocation(
                query = query,
                activity = activity
            )
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreen(
    uiState: MeteoriteListState,
    onItemClick: (itemView: MeteoriteItemView) -> Unit = {},
    onDarkModeToggleClick: () -> Unit = {},
    onSearch: (query: String) -> Unit = {},
) {

    val scrollState = rememberLazyListState()

    val isScrollOnTop: Boolean by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }

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
                is Loaded -> MeteoriteList(
                    scrollState = scrollState,
                    meteorites = uiState.meteorites,
                    onItemClick = onItemClick
                )

                Loading -> Loading(modifier = Modifier.fillMaxSize())
                is Error -> MessageError(
                    message = uiState.message
                )
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
            uiState = Loaded(meteorites = flowOf(PagingData.from(items)))
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
            uiState = Loaded(meteorites = flowOf(PagingData.from(items)))
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
        uiState = Error(R.string.general_error)
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list message loading 10%")
@Composable
fun ListScreenMessagePreview() {
    val items = getFakeListItems()
    ListScreen(
        uiState = Loaded(meteorites = flowOf(PagingData.from(items)))
    )
}

@Preview
@Composable
fun TextLazyColumn() {

    val items = getFakeListItems()

    LazyColumn(Modifier.height(500.dp)) {

        items.forEach {
            item {
                MeteoriteItem(it, null)
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
