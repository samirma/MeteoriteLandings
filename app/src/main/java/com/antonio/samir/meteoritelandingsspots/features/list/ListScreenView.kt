package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.HeaderState
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ListScreenView(
    val isDark: StateFlow<Boolean> = MutableStateFlow(true),
    val addressStatus: ResultOf<Float>,
    val onDarkModeToggleClick: () -> Unit,
    val headerState: HeaderState = HeaderState.Expanded,
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

