import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.ToolbarActions
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.UiState
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoriteCell(
    itemView: MeteoriteItemView,
    onItemClick: ((itemView: MeteoriteItemView) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .height(84.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                onItemClick?.invoke(itemView)
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_map),
            contentDescription = "",
            modifier = Modifier
                .align(CenterVertically)
                .size(40.dp)
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)

            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        color = ExtendedTheme.colors.textPrimary,
                        text = itemView.name ?: "",
                        modifier = Modifier.wrapContentHeight(CenterVertically)
                    )
                    Text(
                        color = ExtendedTheme.colors.textSecondary,
                        text = itemView.address ?: "",
                        maxLines = 2
                    )
                }
                Text(
                    color = ExtendedTheme.colors.textSecondary,
                    text = itemView.distance ?: "800m",
                    maxLines = 2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .wrapContentWidth(Alignment.End),
                )
            }
            Image(
                painter = painterResource(id = R.drawable.divider),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 1.dp)
            )
        }
    }

}


@Preview("MeteoriteCell Dark")
@Composable
fun MeteoriteCellPreviewDark() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = true) {
        MeteoriteCell(
            MeteoriteItemView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                distance = "distance $sample",
                address = "address $sample"
            )
        ) {}
    }
}


@Preview("MeteoriteCell Light")
@Composable
fun MeteoriteCellPreviewLight() {
    val sample = "test"
    MeteoriteLandingsTheme(darkTheme = false) {
        MeteoriteCell(
            MeteoriteItemView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                distance = "distance $sample",
                address = "address $sample"
            )
        ) {}
    }
}


@OptIn(ExperimentalMaterialApi::class)
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
            fontSize = 28.sp
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
                if (uiState.isLoading) {
                    Loading()
                } else if (uiState.message.isNullOrBlank()) {
                    MeteoriteList(scrollState, items, onItemClick)
                } else {
                    Message(uiState.message)
                }
            }
        }
    }
}

@Composable
private fun Message(message: String) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.message_titile),
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun Loading() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)) {
        Text("This text is drawn first", modifier = Modifier.align(Alignment.TopCenter))
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

        item {
            Text(text = "First item")
        }

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
            fetchMeteoriteList = flowOf(ResultOf.Success(Unit)),
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
            fetchMeteoriteList = flowOf(ResultOf.Success(Unit)),
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
            message = "Message",
            addressStatus = flowOf(ResultOf.Success(100f)),
            fetchMeteoriteList = flowOf(ResultOf.Success(Unit)),
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