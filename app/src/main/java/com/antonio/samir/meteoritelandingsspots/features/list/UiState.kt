package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UiState(
    val isLoading: Boolean,
    @StringRes val message: Int? = null,
    val searchInput: String? = null,
    val addressStatus: StateFlow<ResultOf<Float>>,
    val meteorites: Flow<PagingData<MeteoriteItemView>>,
    val isDark: StateFlow<Boolean> = MutableStateFlow(true),
    val onDarkModeToggleClick: () -> Unit,
    val headerState: HeaderState = HeaderState.Expanded
)

sealed class HeaderState {
    object Collapsed : HeaderState()
    object Expanded : HeaderState()
    object Search : HeaderState()

    fun isCollapsed(): Boolean = this == Collapsed
    fun isSearch(): Boolean = this == Search

}