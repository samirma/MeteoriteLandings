import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import kotlinx.coroutines.flow.Flow
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
            .clickable {
                onItemClick?.invoke(itemView)
            }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_map),
            contentDescription = null // decorative element
        )
        Column {
            Text(
                color = ExtendedTheme.colors.textPrimary,
                text = itemView.name ?: ""
            )
            Text(
                color = ExtendedTheme.colors.textSecondary,
                text = itemView.address ?: "",
                maxLines = 2
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
                address = "address $sample"
            )
        ) {}
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Title(scrollOffset: Float) {
    val imageSize by animateDpAsState(targetValue = max(72.dp, 128.dp * scrollOffset))
    Box(
        modifier = Modifier
            .height(imageSize)
            .fillMaxWidth()
            .background(Color.LightGray),
    ) {
        Text("Search for your meteorite", modifier = Modifier.align(Alignment.Center))
    }
}

@Preview("Title")
@Composable
fun TitlePreview() {
    Title(2f)
}

@Composable
fun ListScreen(
    meteorites: Flow<PagingData<MeteoriteItemView>>,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {
    val items = meteorites.collectAsLazyPagingItems()
    val scrollState = rememberLazyListState()
    val scrollOffset: Float = Math.min(
        1f,
        1 - (scrollState.firstVisibleItemScrollOffset / 600f + scrollState.firstVisibleItemIndex)
    )
    MeteoriteLandingsTheme(darkTheme = true) {
        Column(
            modifier = Modifier.background(Color.Blue).fillMaxHeight()
        ) {
            Title(scrollOffset)
            LazyColumn(
                Modifier.fillMaxSize(),
                scrollState,
            ) {
//               items(items) { item ->
//                    item?.let { MeteoriteCell(it) {} }
//                }
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
            address = "address $it"
        )
    }

    ListScreen(
        flowOf(PagingData.from(items)), {}
    )

}