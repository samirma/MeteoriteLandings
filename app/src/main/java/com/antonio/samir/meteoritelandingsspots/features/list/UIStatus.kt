package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.StringRes
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import kotlinx.coroutines.flow.Flow

data class UiState(
    val isLoading: Boolean,
    @StringRes val message: Int? = null,
    val searchInput: String? = null,
    val addressStatus: Flow<ResultOf<Float>>,
    val meteorites: Flow<PagingData<MeteoriteItemView>>,
    val isDark: Boolean = true,
    val onDarkModeToggleClick: () -> Unit,
    val headerState: HeaderState = HeaderState.Expanded
)

sealed class HeaderState {
    object Collapsed : HeaderState()
    object Expanded : HeaderState()
    object Search : HeaderState()

    fun isCollapsed(): Boolean = this == Collapsed

}