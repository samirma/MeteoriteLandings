package com.antonio.samir.meteoritelandingsspots.features.list


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItem
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import kotlinx.coroutines.flow.Flow


@Composable
fun MeteoriteList(
    scrollState: LazyListState,
    meteorites: Flow<PagingData<MeteoriteItemView>>,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {
    val items = meteorites.collectAsLazyPagingItems()
    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        items(
            items.itemCount,
            key = items.itemKey { it.id }
        ) { index ->
            items[index]?.let {
                MeteoriteItem(it, onItemClick)
            }
        }

        items.apply {
            when {
                loadState.refresh is Loading -> item { Loading(modifier = Modifier.fillParentMaxSize()) }
                loadState.append is Loading -> item { Loading(modifier = Modifier.fillParentMaxSize()) }
                loadState.refresh is Error -> {
                    val e = items.loadState.refresh as Error
                    item {
                        MessageError(
                            message = e.error.localizedMessage!!,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }
        }

    }
}
