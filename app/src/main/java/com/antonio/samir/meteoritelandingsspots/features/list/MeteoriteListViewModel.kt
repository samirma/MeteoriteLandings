package com.antonio.samir.meteoritelandingsspots.features.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SwitchUITheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
    private val startAddressRecover: StartAddressRecover,
    private val statusAddressRecover: StatusAddressRecover,
    private val fetchMeteoriteList: FetchMeteoriteList,
    private val getMeteorites: GetMeteorites,
    private val switchUITheme: SwitchUITheme,
) : ViewModel() {

    private val _navigateToMeteoriteDetail = MutableStateFlow<MeteoriteItemView?>(null)

    val navigateToMeteoriteDetail: StateFlow<MeteoriteItemView?> = _navigateToMeteoriteDetail

    private val _searchQuery = MutableStateFlow("")

    val meteorites: Flow<PagingData<MeteoriteItemView>> = _searchQuery.flatMapConcat {
        getMeteorites(GetMeteorites.Input(query = it, location = null))
    }

    private var viewModelState: MutableStateFlow<ListScreenView> = MutableStateFlow(
        ListScreenView(
            addressStatus = ResultOf.InProgress(0f),
            listState = ListState.UiLoading
        )
    )

    // UI state exposed to the UI
    val uiState: StateFlow<ListScreenView> = viewModelState

    init {
        fetchMeteoriteList()
    }

    fun onDarkModeToggleClick() {
        viewModelScope.launch {
            switchUITheme(Unit).collect {
                Log.i(TAG, "SwitchUITheme ${it.javaClass}")
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

    fun onItemClicked(meteoriteItemView: MeteoriteItemView?) {
        _navigateToMeteoriteDetail.value = meteoriteItemView
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
    }

}
