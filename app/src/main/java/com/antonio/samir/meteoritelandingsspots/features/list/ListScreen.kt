import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.lang.Double.max
import java.lang.Float.min


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoriteItem(
    itemView: MeteoriteItemView,
    onItemClick: ((itemView: MeteoriteItemView) -> Unit)?
) {
    Card(
        onClick = { onItemClick?.invoke(itemView!!) },
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier.padding(2.dp).fillMaxWidth(),
        backgroundColor = colorResource(R.color.unselected_item_color),
        elevation = dimensionResource(R.dimen.unselected_item_elevation)
    ) {
        Column(
            Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                color = colorResource(R.color.title_color),
                text = itemView?.name ?: "lalala"
            )
            Text(
                color = colorResource(R.color.detail_accent_label),
                text = itemView?.address ?: "lalala"
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Title(scrollOffset: Float) {
    val imageSize by animateDpAsState(targetValue = max(72.dp, 128.dp * scrollOffset))
    Box(
        modifier = Modifier.height(imageSize)
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MetCell(item: MeteoriteItemView) {
    Box(
        Modifier.fillMaxWidth().background(Color.Gray),
    ) {
        Text(item.name)
    }
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
    Column {

        LazyColumn(
            Modifier.fillMaxSize(),
            scrollState,
        ) {
            items(items) { item ->
                item?.let { MeteoriteItem(it) {} }
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