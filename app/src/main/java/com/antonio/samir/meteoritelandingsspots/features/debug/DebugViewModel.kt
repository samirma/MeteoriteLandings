package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugListState.Loaded
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugListState.Loading
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugListState.UiMessage
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DebugViewModel(
    private val fetchMeteoriteList: FetchMeteoriteList,
) : ViewModel() {

    private var _uiState = MutableStateFlow<DebugListState>(Loading)

    // UI state exposed to the UI
    val uiState: StateFlow<DebugListState> = _uiState

    fun loadRawMeteoriteList() {
        viewModelScope.launch(Dispatchers.Default) {
            fetchMeteoriteList(Unit).collect { resultOf ->
                _uiState.update {
                    when (resultOf) {
                        is ResultOf.InProgress -> Loading
                        is ResultOf.Success -> Loaded
                        else -> UiMessage(R.string.general_error)
                    }
                }
            }
        }
    }

}