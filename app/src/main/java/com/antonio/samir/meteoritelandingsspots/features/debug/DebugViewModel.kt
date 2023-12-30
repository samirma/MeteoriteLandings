package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugListState.Error
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugListState.Loaded
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugListState.Loading
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val startAddressRecover: StartAddressRecover,
    private val statusAddressRecover: StatusAddressRecover,
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
                        is ResultOf.Success -> Loaded(100f)
                        else -> Error(R.string.general_error)
                    }
                }
            }
        }
    }

    private fun recoverAddressStatus() = startAddressRecover(Unit)
        .flatMapConcat { statusAddressRecover(it) }

    fun loadAddresses() {
        viewModelScope.launch {
            recoverAddressStatus().collect { it: ResultOf<Float> ->
                _uiState.value = when (it) {
                    is ResultOf.Error -> Error(R.string.general_error)
                    is ResultOf.InProgress -> Loaded(it.data ?: 0f)
                    is ResultOf.Success -> Loaded(100f)
                }
            }
        }
    }

}