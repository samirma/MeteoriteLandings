import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ToolbarButtons
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.list.HeaderState
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun Header(
    headerState: HeaderState,
    modifier: Modifier = Modifier,
    onDarkModeToggleClick: () -> Unit
) {

    val isCollapsed = headerState.isCollapsed()

    var headerModifier = modifier

    if (!isCollapsed) {
        headerModifier = headerModifier.height(72.dp)
    }

    Column(
        modifier = headerModifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedVisibility(
            visible = !isCollapsed,
            modifier = headerModifier,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            Text(
                text = stringResource(R.string.title_header),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentSize(align = Alignment.Center)
                    .padding(
                        horizontal = 80.dp
                    ),
                color = ExtendedTheme.colors.textPrimary,
                style = MaterialTheme.typography.h4
            )
        }
        Row(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = isCollapsed,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = stringResource(R.string.title_header),
                    textAlign = TextAlign.Start,
                    color = ExtendedTheme.colors.textPrimary,
                    style = MaterialTheme.typography.h6
                )
            }
            ToolbarButtons(
                modifier = Modifier,
                onDarkModeToggleClick = onDarkModeToggleClick
            )
        }
    }
}

@Preview("Header Collapsed")
@Composable
fun HeaderPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        Surface() {
            Header(HeaderState.Collapsed) {}
        }
    }
}


@Preview("Header Expanded")
@Composable
fun HeaderExpandedPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        Surface() {
            Header(HeaderState.Expanded) {}
        }
    }
}


@Composable
fun ListScreen(
    uiState: UiState,
    onItemClick: (itemView: MeteoriteItemView) -> Unit,
    onTopList: (scrollOffset: Float) -> Unit,
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
            7f
        }
    )

    MeteoriteLandingsTheme(darkTheme = uiState.isDark) {
        Surface(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            Column(Modifier.fillMaxSize()) {
                Header(
                    headerState = headerState,
                    modifier = Modifier.weight(weight),
                    onDarkModeToggleClick = uiState.onDarkModeToggleClick
                )
                Box(
                    Modifier.weight(10f, fill = true)
                ) {
                    if (uiState.isLoading) {
                        Loading(modifier = Modifier.fillMaxSize())
                    } else if (uiState.message == null) {
                        MeteoriteList(scrollState, uiState.meteorites, onItemClick)
                    } else {
                        Message(uiState.message)
                    }

                    val addressProgress = uiState.addressStatus.asLiveData().observeAsState().value
                    if (addressProgress != null) {
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
}

@Composable
private fun Message(
    @StringRes message: Int,
    modifier: Modifier = Modifier
) = Message(message = stringResource(id = message), modifier = modifier)

@Composable
private fun Message(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.message_titile),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }
}


@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MeteoriteList(
    scrollState: LazyListState,
    meteorites: Flow<PagingData<MeteoriteItemView>>,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {
    val items = meteorites.collectAsLazyPagingItems()
    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {

        items(items) { item ->
            item?.let { MeteoriteCell(it, onItemClick) }
        }

        items.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { Loading(modifier = Modifier.fillParentMaxSize()) }
                }
                loadState.append is LoadState.Loading -> {
                    item { Loading(modifier = Modifier.fillParentMaxSize()) }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = items.loadState.refresh as LoadState.Error
                    item {
                        Message(
                            message = e.error.localizedMessage!!,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    val e = items.loadState.append as LoadState.Error
                    item {
                        Message(
                            message = e.error.localizedMessage!!
                        )
                    }
                }
            }
        }

    }
}

@Preview("Meteorite list view")
@Composable
fun ListScreenPreview() {
    val items = (1..10).map {
        MeteoriteItemView(
            id = "$it",
            name = "name $it",
            yearString = "yearString $it",
            address = "address $it",
            distance = "distance $it",
        )
    }

    ListScreen(
        uiState = UiState(
            isLoading = false,
            addressStatus = flowOf(ResultOf.Success(100f)),
            meteorites = flowOf(PagingData.from(items)),
            onDarkModeToggleClick = { },
            headerState = HeaderState.Expanded
        ),
        {}
    ) {}

}

@Preview("Meteorite list loading")
@Composable
fun ListScreenLoadingPreview() {
    val items = (1..10).map {
        MeteoriteItemView(
            id = "$it",
            name = "name $it",
            yearString = "yearString $it",
            address = "address $it",
            distance = "distance $it",
        )
    }

    ListScreen(
        uiState = UiState(
            isLoading = true,
            addressStatus = flowOf(ResultOf.Success(100f)),
            meteorites = flowOf(PagingData.from(items)),
            onDarkModeToggleClick = { },
        ),
        {}
    ) {}

}

@Preview("Meteorite list message")
@Composable
fun ListScreenMessagePreview() {
    val items = (1..10).map {
        MeteoriteItemView(
            id = "$it",
            name = "name $it",
            yearString = "yearString $it",
            address = "address $it",
            distance = "distance $it",
        )
    }

    ListScreen(
        uiState = UiState(
            isLoading = false,
            message = null,
            addressStatus = flowOf(ResultOf.InProgress(100f)),
            meteorites = flowOf(PagingData.from(items)),
            onDarkModeToggleClick = { },
            isDark = false
        ),
        {}
    ) {}

}

@Preview
@Composable
fun TextLazyColumn() {

    val items = (1..10).map {
        MeteoriteItemView(
            id = "$it",
            name = "name $it",
            yearString = "yearString $it",
            address = "address $it",
            distance = "distance $it",
        )
    }

    LazyColumn(Modifier.height(500.dp)) {

        items.forEach {
            item {
                MeteoriteCell(it, null)
            }
        }

    }

}