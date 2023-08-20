package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.IsDarkTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.HeaderState
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SetUITheme
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
    private val stateHandle: SavedStateHandle,
    private val startAddressRecover: StartAddressRecover,
    private val statusAddressRecover: StatusAddressRecover,
    private val fetchMeteoriteList: FetchMeteoriteList,
    private val getMeteorites: GetMeteorites,
    private val setDarkMode: SetUITheme,
    private val isDarkTheme: IsDarkTheme,
) : ViewModel() {

    private val meteorite = MutableStateFlow<MeteoriteItemView?>(stateHandle[METEORITE])

    private val _searchQuery = MutableStateFlow("")

    private var isDarkMode = true

    private val debounceState = MutableStateFlow<HeaderState?>(null)

    val meteorites: Flow<PagingData<MeteoriteItemView>> = _searchQuery.flatMapConcat {
        getMeteorites(GetMeteorites.Input(query = it, location = null))
    }

    private var viewModelState: MutableStateFlow<ListScreenView> = MutableStateFlow(
        ListScreenView(
            isDark = false,
            addressStatus = ResultOf.InProgress(0f),
            onDarkModeToggleClick = {
                onDarkModeToggleClick()
            },
            listState = ListState.UiLoading
        )
    )


    // UI state exposed to the UI
    val uiState: StateFlow<ListScreenView> = viewModelState

    private fun onDarkModeToggleClick() {
        isDarkMode = !isDarkMode
        viewModelScope.launch {
            setDarkMode(SetUITheme.Input(isDarkMode)).collect()
        }
    }

    init {
        fetchMeteoriteList()
        fetchThemeMode()
    }

    private fun fetchThemeMode() {
        viewModelScope.launch(Dispatchers.Default) {
            isDarkTheme(Unit).collect { isDark ->
                viewModelState.update {
                    it.copy(isDark = isDark)
                }
            }
        }
    }

    private fun fetchMeteoriteList() {
        viewModelScope.launch {
            fetchMeteoriteList(Unit).collect { resultOf ->
                viewModelState.update {
                    when (resultOf) {
                        is ResultOf.Error -> it.copy(
                            listState = ListState.UiMessage(R.string.general_error)
                        )

                        is ResultOf.InProgress -> it.copy(listState = ListState.UiLoading)
                        is ResultOf.Success -> {
                            it.copy(
                                listState = ListState.UiContent(
                                    meteorites = meteorites
                                )
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            recoverAddressStatus().collect { resultOf ->
                viewModelState.update {
                    it.copy(addressStatus = resultOf)
                }
            }
        }
    }

    private fun recoverAddressStatus() = startAddressRecover(Unit)
        .flatMapConcat { statusAddressRecover(it) }

    fun searchLocation(query: String) {
        _searchQuery.value = query
    }

    fun selectMeteorite(meteorite: MeteoriteItemView?) {
        stateHandle[METEORITE] = meteorite
        this.meteorite.value = meteorite
    }

    fun onTopList(offset: Float) {
        if (debounceState.value != HeaderState.Search) {
            debounceState.value = if (offset > 0) {
                HeaderState.Expanded
            } else {
                HeaderState.Collapsed
            }
        }
    }

    fun setHeaderState(headerState: HeaderState) {
        debounceState.value = headerState
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
        const val METEORITE = "METEORITE"
    }

}
