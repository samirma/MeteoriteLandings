package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import kotlinx.coroutines.flow.Flow

data class UiState(
    val isLoading: Boolean,
    val message: String? = null,
    val searchInput: String? = null,
    val addressStatus: Flow<ResultOf<Float>>,
    val fetchMeteoriteList: Flow<ResultOf<Unit>>,
    val meteorites: Flow<PagingData<MeteoriteItemView>>
)