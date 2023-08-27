package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import kotlinx.coroutines.flow.Flow

sealed class MeteoristListState {
    class UiContent(
        val meteorites: Flow<PagingData<MeteoriteItemView>>,
    ) : MeteoristListState()

    class UiMessage(
        @StringRes val message: Int,
    ) : MeteoristListState()

    object Loading : MeteoristListState()
}

