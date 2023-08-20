package com.antonio.samir.meteoritelandingsspots.features.list


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Header
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.HeaderState
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.ListState.UiContent
import com.antonio.samir.meteoritelandingsspots.features.list.ListState.UiLoading
import com.antonio.samir.meteoritelandingsspots.features.list.ListState.UiMessage
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
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        uiState = uiState,
        onItemClick = viewModel::onItemClicked,
        onEnterSearch = { viewModel.setHeaderState(HeaderState.Search) },
        onExitSearch = {
            viewModel.setHeaderState(HeaderState.Expanded)
        },
        onSearch = viewModel::searchLocation
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ListScreen(
    uiState: ListScreenView,
    onItemClick: (itemView: MeteoriteItemView) -> Unit,
    onEnterSearch: () -> Unit,
    onExitSearch: () -> Unit,
    onSearch: (query: String) -> Unit,
) {

    val scrollState = rememberLazyListState()

    val isCollapsed: Boolean by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }

    val headerState = if (isCollapsed) HeaderState.Collapsed else HeaderState.Expanded

    Surface(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            Header(
                headerState = headerState,
                onDarkModeToggleClick = uiState.onDarkModeToggleClick,
                onEnterSearch = onEnterSearch,
                onExitSearch = onExitSearch,
                onSearch = onSearch
            )
            Box(
                Modifier.weight(10f, fill = true)
            ) {
                val listState: ListState = uiState.listState
                ContentList(
                    listState = listState,
                    scrollState = scrollState,
                    onItemClick = onItemClick
                )

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

@Composable
private fun ContentList(
    listState: ListState,
    scrollState: LazyListState,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {
    when (listState) {
        is UiContent -> MeteoriteList(
            scrollState = scrollState,
            meteorites = listState.meteorites,
            onItemClick = onItemClick
        )

        UiLoading -> Loading(modifier = Modifier.fillMaxSize())
        is UiMessage -> MessageError(
            message = listState.message
        )
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
            onDarkModeToggleClick = { },
            listState = UiContent(meteorites = flowOf(PagingData.from(items)))
        ),
        onItemClick = {},
        onEnterSearch = {},
        onExitSearch = {}
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
