package com.antonio.samir.meteoritelandingsspots.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.IsDarkTheme
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById.Input
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModel(
    private val getMeteoriteById: GetMeteoriteById,
    val isDarkTheme: IsDarkTheme,
    val dispatchers: DispatcherProvider,
) : ViewModel() {

    private val currentMeteorite = MutableStateFlow<String?>(null)

    private val viewModelState = MutableStateFlow(
        UiState(
            isDark = isDarkTheme.execute(Unit).stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )
        )
    )

    // UI state exposed to the UI
    val uiState: StateFlow<UiState> = viewModelState

    init {
        viewModelScope.launch(dispatchers.default()) {
            getMeteorite().collect { result ->
                if (result is ResultOf.Success) viewModelState.update {
                    it.copy(meteoriteView = result.data, isLoading = false)
                }
            }
        }
    }

    private fun getMeteorite() = currentMeteorite.asStateFlow()
        .filterNotNull()
        .flatMapLatest { meteoriteId ->
            getMeteoriteById.execute(
                Input(
                    id = meteoriteId
                )
            )
        }


    fun loadMeteorite(meteoriteId: String) {
        this.currentMeteorite.value = meteoriteId
    }

    fun requestAddressUpdate(meteorite: MeteoriteView) {

    }

}
