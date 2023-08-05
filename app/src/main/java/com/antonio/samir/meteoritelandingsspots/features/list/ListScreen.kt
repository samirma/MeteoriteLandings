package com.antonio.samir.meteoritelandingsspots.features.list


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Header
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.HeaderState
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf

@OptIn(FlowPreview::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreenNavigation(
    viewModel: MeteoriteListViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        uiState = uiState,
        onItemClick = viewModel::selectMeteorite,
        onEnterSearch = { viewModel.setHeaderState(HeaderState.Search) },
        onExitSearch = {
            viewModel.setHeaderState(HeaderState.Expanded)
        },
        onTopList = viewModel::onTopList,
        onSearch = {}
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreen(
    uiState: ListScreenView,
    onItemClick: (itemView: MeteoriteItemView) -> Unit = {},
    onTopList: (scrollOffset: Float) -> Unit = {},
    onEnterSearch: () -> Unit = {},
    onExitSearch: () -> Unit = {},
    onSearch: (query: String) -> Unit = {},
) {

    val scrollState = rememberLazyListState()

    val scrollOffset: Float = Math.min(
        1f,
        1 - (scrollState.firstVisibleItemScrollOffset / 600f +
                scrollState.firstVisibleItemIndex)
    )

    val headerState = uiState.headerState

    onTopList(scrollOffset)

    val weight: Float by animateFloatAsState(
        targetValue = if (headerState.isCollapsed()) {
            1f
        } else {
            6f
        }
    )

    val isDark by uiState.isDark.collectAsState()

    MeteoriteLandingsTheme(darkTheme = isDark) {
        Surface(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            Column(Modifier.fillMaxSize()) {
                Header(
                    headerState = headerState,
                    modifier = Modifier.weight(weight),
                    onDarkModeToggleClick = uiState.onDarkModeToggleClick,
                    onEnterSearch = onEnterSearch,
                    onExitSearch = onExitSearch,
                    onSearch = onSearch
                )
                Box(
                    Modifier.weight(10f, fill = true)
                ) {
                    when (uiState.listState) {
                        is ListState.UiContent -> MeteoriteList(
                            scrollState = scrollState,
                            meteorites = uiState.listState.meteorites,
                            onItemClick = onItemClick
                        )
//                        ListState.UiLoading -> Loading(modifier = Modifier.fillMaxSize())
//                        is ListState.UiMessage -> MessageError(
//                            message = uiState.listState.message
//                        )
                        else -> {}
                    }

                    val addressProgress = uiState.addressStatus
                    if (addressProgress is ResultOf.InProgress && (addressProgress.data != null)) {
                        AddressProgress(
                            progress = addressProgress.data,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                        )
                    }
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Meteorite list view")
@Composable
fun ListScreenPreview() {
    val items = getFakeListItems()

    ListScreen(
        uiState = ListScreenView(
            addressStatus = ResultOf.Success(100f),
            listState = ListState.UiContent(meteorites = flowOf(PagingData.from(items))),
            onDarkModeToggleClick = { },
            headerState = HeaderState.Expanded
        ),
        {}
    ) {}

}
//
//@ExperimentalAnimationApi
//@ExperimentalComposeUiApi
//@Preview("Meteorite list loading")
//@Composable
//fun ListScreenLoadingPreview() {
//    ListScreen(
//        uiState = ListScreenView(
//            addressStatus = ResultOf.Success(100f),
//            listState = ListState.UiLoading,
//            onDarkModeToggleClick = { },
//        ),
//        {}
//    ) {}
//
//}
//
//@ExperimentalAnimationApi
//@ExperimentalComposeUiApi
//@Preview("Meteorite list error")
//@Composable
//fun ListScreenErrorPreview() {
//    ListScreen(
//        uiState = ListScreenView(
//            listState = ListState.UiMessage(R.string.general_error),
//            addressStatus = ResultOf.Success(100f),
//            onDarkModeToggleClick = { },
//        ),
//        {}
//    ) {}
//
//}
//
//@ExperimentalAnimationApi
//@ExperimentalComposeUiApi
//@Preview("Meteorite list message loading 10%")
//@Composable
//fun ListScreenMessagePreview() {
//    val items = getFakeListItems()
//
//    ListScreen(
//        uiState = ListScreenView(
//            addressStatus = ResultOf.InProgress(10.0f),
//            listState = ListState.UiContent(meteorites = flowOf(PagingData.from(items))),
//            onDarkModeToggleClick = { }
//        ),
//        onSearch = {}
//    )
//
//}
//
//@Preview
//@Composable
//fun TextLazyColumn() {
//
//    val items = getFakeListItems()
//
//    LazyColumn(Modifier.height(500.dp)) {
//
//        items.forEach {
//            item {
//                MeteoriteCell(it, null)
//            }
//        }
//
//    }
//
//}

private fun getFakeListItems(): List<MeteoriteItemView> = (1..10).map {
    MeteoriteItemView(
        id = "$it",
        name = "name $it",
        yearString = "yearString $it",
        address = "address $it",
        distance = "distance $it",
    )
}
