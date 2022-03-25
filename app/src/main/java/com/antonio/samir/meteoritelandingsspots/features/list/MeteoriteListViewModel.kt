package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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
    private val gpsTracker: GPSTrackerInterface,
    private val dispatchers: DispatcherProvider,
    private val getMeteorites: GetMeteorites,
) : ViewModel() {

    private val meteorite = MutableStateFlow<MeteoriteItemView?>(stateHandle[METEORITE])

    private var isDarkMode = true

    val selectedMeteorite = meteorite.asLiveData()

    private val debounceState = MutableStateFlow<HeaderState?>(null)

    private val _meteorites =
        MutableStateFlow<PagingData<MeteoriteItemView>>(PagingData.empty())
    val meteorites = _meteorites

    private val viewModelState = MutableStateFlow(
        UiState(
            isLoading = true,
            addressStatus = recoverAddressStatus(),
            meteorites = meteorites,
            onDarkModeToggleClick = {
                onDarkModeToggleClick()
            },
        )
    )

    private fun onDarkModeToggleClick() {
        isDarkMode = !isDarkMode
        viewModelState.update { it.copy(isDark = isDarkMode) }
    }

    // UI state exposed to the UI
    val uiState = viewModelState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value
    )

    init {
        fetchMeteoriteList()

        viewModelScope.launch(dispatchers.default()) {
            debounceState.debounce(200).collect { headerState ->
                if (headerState != null) {
                    viewModelState.update {
                        it.copy(
                            headerState = headerState
                        )
                    }
                }
            }
        }

    }

    private fun fetchMeteoriteList() {

        viewModelScope.launch {
            fetchMeteoriteList.execute(Unit).collect { resultOf ->
                viewModelState.update {
                    when (resultOf) {
                        is ResultOf.Error -> it.copy(
                            isLoading = false,
                            message = R.string.general_error
                        )
                        is ResultOf.InProgress -> it.copy(isLoading = true, message = null)
                        is ResultOf.Success -> it.copy(isLoading = false, message = null)
                    }
                }
            }
        }
    }

    private fun recoverAddressStatus(): StateFlow<ResultOf<Float>> =
        startAddressRecover.execute(Unit)
            .flatMapConcat(statusAddressRecover::execute).stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ResultOf.InProgress(0.0f)
            )

    private val _searchQuery: MutableState<String?> = mutableStateOf<String?>(null)
    val searchQuery: State<String?> = _searchQuery

    fun searchLocation(query: String?) {
        _searchQuery.value = query
        viewModelScope.launch {
            getMeteorites.execute(query)
                .cachedIn(viewModelScope)
                .collect {
                    _meteorites.value = it
                }
        }
    }

    fun selectMeteorite(meteorite: MeteoriteItemView?) {
        stateHandle[METEORITE] = meteorite
        this.meteorite.value = meteorite
    }

    fun updateLocation() {
        viewModelScope.launch(dispatchers.default()) {
            gpsTracker.requestLocation()
        }
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization.asLiveData()
    }

    fun clearSelectedMeteorite() {
        selectMeteorite(null)
    }

    fun onTopList(offset: Float) {
        debounceState.value = if (offset > 0) {
            HeaderState.Expanded
        } else {
            HeaderState.Collapsed
        }
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
        const val METEORITE = "METEORITE"
    }

}
