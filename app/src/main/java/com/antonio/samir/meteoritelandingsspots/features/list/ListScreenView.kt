package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import kotlinx.coroutines.flow.Flow

data class ListScreenView(
    val addressStatus: ResultOf<Float>,
    val listState: ListState
)

sealed class ListState {
    class UiContent(
        val meteorites: Flow<PagingData<MeteoriteItemView>>,
    ) : ListState()

    class UiMessage(
        @StringRes val message: Int,
    ) : ListState()

    object UiLoading : ListState()
}

