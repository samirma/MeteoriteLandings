package com.antonio.samir.meteoritelandingsspots.features.list

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.distanceTextFrom
import com.antonio.samir.meteoritelandingsspots.common.ui.permission.LocationPermissionStatus
import com.antonio.samir.meteoritelandingsspots.data.connectivity.ConnectivityRepository
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import com.antonio.samir.meteoritelandingsspots.data.location.LocationRepository
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SwitchUITheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeteoriteListViewModel @Inject constructor(
    private val getMeteorites: GetMeteorites,
    private val switchUITheme: SwitchUITheme,
    private val locationRepository: LocationRepository,
    uiThemeRepository: UIThemeRepository,
    connectivityRepository: ConnectivityRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val isSearching = savedStateHandle.getStateFlow(KEY_IS_SEARCHING, false)
    private val locationPermission =
        MutableStateFlow(LocationPermissionStatus.DENIED)
    private val currentLocation = MutableStateFlow<Location?>(null)
    private val addressProgress = MutableStateFlow(0f)

    val uiState: StateFlow<MeteoriteListUiState> = combine(
        query,
        isSearching,
        uiThemeRepository.getTheme(),
        connectivityRepository.isOnline,
        combine(locationPermission, addressProgress, ::Pair),
    ) { query, isSearching, theme, isOnline, (permission, progress) ->
        MeteoriteListUiState(
            isLoading = false,
            query = query,
            isSearching = isSearching,
            isDarkTheme = theme == UITheme.DARK,
            isOnline = isOnline,
            locationPermission = permission,
            addressProgress = progress,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = MeteoriteListUiState(),
    )

    /**
     * The paging stream, kept beside the UI state rather than inside it.
     *
     * Exposed as a plain `Flow` (not a `StateFlow`): `cachedIn` already multicasts and replays,
     * and `PagingData` is explicitly not safe to hand to more than one collector. `cachedIn` is
     * also what lets the list survive configuration changes and re-subscription without
     * refetching from page zero — its absence is why returning from the detail screen used to
     * reload the list.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val meteorites: Flow<PagingData<MeteoriteItemView>> =
        combine(
            query.debounce { if (it.isEmpty()) 0 else SEARCH_DEBOUNCE_MILLIS }
                .distinctUntilChanged(),
            currentLocation,
            ::Pair,
        )
            .flatMapLatest { (query, location) ->
                getMeteorites(query, location).map { pagingData ->
                    pagingData.map { meteorite ->
                        MeteoriteItemView(
                            id = meteorite.id,
                            name = meteorite.name.orEmpty(),
                            yearString = meteorite.year?.toString().orEmpty(),
                            address = meteorite.address.orEmpty(),
                            distance = meteorite.distanceTextFrom(location).orEmpty(),
                        )
                    }
                }
            }
            .cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        savedStateHandle[KEY_QUERY] = query
    }

    fun onSearchOpened() {
        savedStateHandle[KEY_IS_SEARCHING] = true
    }

    fun onSearchClosed() {
        savedStateHandle[KEY_IS_SEARCHING] = false
        savedStateHandle[KEY_QUERY] = ""
    }

    /**
     * @param currentlyDark what the caller is rendering right now. Passed in rather than read
     *   from [uiState], whose value is the initial placeholder whenever nothing is collecting it.
     */
    fun onDarkModeToggleClick(currentlyDark: Boolean) {
        viewModelScope.launch { switchUITheme(currentlyDark) }
    }

    /** Called by the UI whenever the live permission state changes. */
    fun onLocationPermissionChanged(status: LocationPermissionStatus) {
        locationPermission.update { status }
        if (status == LocationPermissionStatus.GRANTED && currentLocation.value == null) {
            viewModelScope.launch {
                currentLocation.update { locationRepository.currentLocation() }
            }
        }
    }

    fun onAddressProgress(progress: Float) {
        addressProgress.update { progress }
    }

    private companion object {
        const val KEY_QUERY = "query"
        const val KEY_IS_SEARCHING = "isSearching"
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
