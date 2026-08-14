package com.antonio.samir.meteoritelandingsspots.features.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val startAddressRecover: StartAddressRecover,
    private val statusAddressRecover: StatusAddressRecover,
    private val fetchMeteoriteList: FetchMeteoriteList,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DebugUiState())
    val uiState: StateFlow<DebugUiState> = _uiState.asStateFlow()

    fun loadRawMeteoriteList() {
        viewModelScope.launch {
            fetchMeteoriteList().collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is ResultOf.InProgress -> state.copy(isSyncing = true, message = null)
                        is ResultOf.Success -> state.copy(
                            isSyncing = false,
                            message = R.string.debug_sync_complete,
                        )

                        is ResultOf.Error -> state.copy(
                            isSyncing = false,
                            message = result.error.toMessageRes(),
                        )
                    }
                }
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            startAddressRecover()
            _uiState.update { it.copy(isRecoveringAddresses = true, message = null) }
            statusAddressRecover().collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is ResultOf.InProgress -> state.copy(
                            addressProgress = result.progress ?: 0f,
                        )

                        is ResultOf.Success -> state.copy(
                            addressProgress = result.data,
                            isRecoveringAddresses = false,
                            message = R.string.debug_addresses_complete,
                        )

                        is ResultOf.Error -> state.copy(
                            isRecoveringAddresses = false,
                            message = result.error.toMessageRes(),
                        )
                    }
                }
            }
        }
    }
}

private fun DataError.toMessageRes() = when (this) {
    DataError.NETWORK -> R.string.no_network
    DataError.GEOCODER_UNAVAILABLE -> R.string.geocoder_unavailable
    else -> R.string.general_error
}
