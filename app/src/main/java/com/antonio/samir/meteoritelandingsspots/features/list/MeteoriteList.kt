package com.antonio.samir.meteoritelandingsspots.features.list


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import kotlinx.coroutines.flow.Flow


@Composable
fun MeteoriteList(
    scrollState: LazyListState,
    meteorites: Flow<PagingData<MeteoriteItemView>>,
    onItemClick: (itemView: MeteoriteItemView) -> Unit
) {
    Text(text = "lalala")
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
            items[index]?.let { MeteoriteCell(it, onItemClick) }
        }

        items.apply {
//            when {
//                loadState.refresh is LoadState.Loading -> {
//                    item { Loading(modifier = Modifier.fillParentMaxSize()) }
//                }
//
//                loadState.append is LoadState.Loading -> {
//                    item { Loading(modifier = Modifier.fillParentMaxSize()) }
//                }
//
//                loadState.refresh is LoadState.Error -> {
//                    val e = items.loadState.refresh as LoadState.Error
//                    item {
//                        MessageError(
//                            message = e.error.localizedMessage!!,
//                            modifier = Modifier.fillParentMaxSize()
//                        )
//                    }
//                }
//            }
            item {
                MessageError(
                    message = "${loadState.append}"
                )
            }
        }

    }
}
