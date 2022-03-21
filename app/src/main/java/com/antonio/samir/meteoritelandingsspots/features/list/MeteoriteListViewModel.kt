package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.PagingData
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

    val selectedMeteorite = meteorite.asLiveData()


    private val _meteorites =
        MutableStateFlow<PagingData<MeteoriteItemView>>(PagingData.empty())
    val meteorites = _meteorites

    private val viewModelState = MutableStateFlow(
        UiState(
            isLoading = true,
            addressStatus = recoverAddressStatus(),
            fetchMeteoriteList = fetchMeteoriteList(),
            meteorites = meteorites
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = viewModelState.value
    )

    private fun fetchMeteoriteList() = fetchMeteoriteList.execute(Unit)

    private fun recoverAddressStatus(): Flow<ResultOf<Float>> = startAddressRecover.execute(Unit)
        .flatMapConcat(statusAddressRecover::execute)

    @VisibleForTesting
    val addressServiceControl = MutableLiveData(false)

    private val _searchQuery: MutableState<String?> = mutableStateOf<String?>(null)
    val searchQuery: State<String?> = _searchQuery

    fun searchLocation(query: String?) {
        _searchQuery.value = query
        viewModelScope.launch {
            getMeteorites.execute(query)
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
            gpsTracker.startLocationService()
        }
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization.asLiveData()
    }

    fun clearSelectedMeteorite() {
        selectMeteorite(null)
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
        const val COMPLETED = 100f
        const val METEORITE = "METEORITE"
    }

}
