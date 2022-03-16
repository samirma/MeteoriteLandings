import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import kotlinx.coroutines.flow.Flow

@Composable
fun MeteoriteList(meteorites: Flow<PagingData<MeteoriteItemView>>, onItemClick: (itemView: MeteoriteItemView) -> Unit) {
    val collectAsLazyPagingItems = meteorites.collectAsLazyPagingItems()
    LazyColumn(
        Modifier
            .fillMaxWidth()
    ) {
        items(collectAsLazyPagingItems) { character ->
            character?.let {
                MeteoriteItem(it, onItemClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoriteItem(itemView: MeteoriteItemView?, onItemClick: ((itemView: MeteoriteItemView) -> Unit)?) {
    Card(
        onClick =  { onItemClick?.invoke(itemView!!) },
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier.padding(2.dp),
        backgroundColor = colorResource(R.color.unselected_item_color),
        elevation = dimensionResource(R.dimen.unselected_item_elevation)
    ) {
        Column(
            Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                color = colorResource( R.color.title_color),
                text = itemView?.name ?: "lalala"
            )
            Text(
                color = colorResource(R.color.detail_accent_label),
                text = itemView?.address ?: "lalala"
            )
        }
    }
}

@Preview("Meteorite view")
@Composable
fun MeteoriteItem() {
    MeteoriteItem(
        itemView = MeteoriteItemView(
            id = 123,
            name = "title",
            address = "address"
        ),
        onItemClick = { }
    )
}
