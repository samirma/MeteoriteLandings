package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.Loading
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MessageError
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItem
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView

const val TAG_METEORITE_LIST = "meteorite_list"
private const val ITEM_CONTENT_TYPE = "meteorite"

@Composable
fun MeteoriteList(
    items: LazyPagingItems<MeteoriteItemView>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onItemClick: (itemView: MeteoriteItemView) -> Unit = {},
) {
    when (val refresh = items.loadState.refresh) {
        is LoadState.Loading -> Loading(modifier = modifier.fillMaxSize())

        is LoadState.Error -> MessageError(
            // Previously `refresh.error.localizedMessage!!` — a crash for any exception without a
            // localised message, and a raw exception string shown to the user when it had one.
            message = R.string.general_error,
            modifier = modifier.fillMaxSize(),
        )

        is LoadState.NotLoading -> if (items.itemCount == 0) {
            MessageError(message = R.string.no_result_found, modifier = modifier.fillMaxSize())
        } else {
            LazyColumn(
                state = scrollState,
                modifier = modifier
                    .fillMaxSize()
                    .testTag(TAG_METEORITE_LIST),
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey { it.id },
                    // Without a content type LazyColumn cannot reuse item nodes between scrolls.
                    contentType = { ITEM_CONTENT_TYPE },
                ) { index ->
                    items[index]?.let { item ->
                        MeteoriteItem(itemView = item, onItemClick = onItemClick)
                    }
                }

                if (items.loadState.append is LoadState.Loading) {
                    item(contentType = "append-loading") {
                        Box(Modifier.fillMaxSize()) { Loading() }
                    }
                }
            }
        }
    }
}
