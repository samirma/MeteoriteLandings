package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import kotlinx.coroutines.flow.Flow

sealed class MeteoriteListState {
    class Loaded(
        val meteorites: Flow<PagingData<MeteoriteItemView>>,
    ) : MeteoriteListState()

    class Error(
        @StringRes val message: Int,
    ) : MeteoriteListState()

    data object Loading : MeteoriteListState()
}

