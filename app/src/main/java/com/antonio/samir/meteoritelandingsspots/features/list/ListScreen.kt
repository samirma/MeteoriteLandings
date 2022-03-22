import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ToolbarActions
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.UiState
import kotlinx.coroutines.flow.flowOf

@Composable
fun Title(scrollOffset: Float) {
    val imageSize by animateDpAsState(targetValue = max(300.dp, 128.dp * scrollOffset))
    Box(
        modifier = Modifier
            .height(imageSize)
            .fillMaxWidth()
            .background(ExtendedTheme.colors.header),
    ) {
        Text(
            text = "Search for your meteorite",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = 80.dp
                ),
            color = ExtendedTheme.colors.textPrimary,
            style = MaterialTheme.typography.h4
        )
        ToolbarActions(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    horizontal = 16.dp
                )
        )
    }
}

@Preview("Title")
@Composable
fun TitlePreview() {
    Title(2f)
}

@Composable
fun ListScreen(
    uiState: UiState,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {

    val items = uiState.meteorites.collectAsLazyPagingItems()
    val scrollState = rememberLazyListState()
    val scrollOffset: Float = Math.min(
        1f,
        1 - (scrollState.firstVisibleItemScrollOffset / 600f + scrollState.firstVisibleItemIndex)
    )
    MeteoriteLandingsTheme {
        Surface(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            Column(Modifier.fillMaxSize()) {
                Title(scrollOffset)
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                ) {
                    if (uiState.isLoading) {
                        Loading()
                    } else if (uiState.message == null) {
                        MeteoriteList(scrollState, items, onItemClick)
                    } else {
                        Message(uiState.message)
                    }

                    val addressProgress = uiState.addressStatus.asLiveData().observeAsState().value
                    if (addressProgress != null) {
                        if (addressProgress is ResultOf.InProgress && (addressProgress.data != null)) AddressProgress(
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

@Composable
private fun Message(@StringRes message: Int) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.message_titile),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = stringResource(id = message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }
}


@Composable
private fun Loading() {
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Text("Loading", modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun MeteoriteList(
    scrollState: LazyListState,
    items: LazyPagingItems<MeteoriteItemView>,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {
    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        items(items) { item ->
            item?.let { MeteoriteCell(it, onItemClick) }
        }

        item {
            Text(text = "First item")
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
            meteorites = flowOf(PagingData.from(items))
        )
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
            meteorites = flowOf(PagingData.from(items))
        )
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
            addressStatus = flowOf(ResultOf.Success(100f)),
            meteorites = flowOf(PagingData.from(items))
        )
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