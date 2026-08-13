package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.compose.runtime.Immutable
import com.antonio.samir.meteoritelandingsspots.common.ui.permission.LocationPermissionStatus

/**
 * Everything the list screen renders, as a single value.
 *
 * The paging stream is deliberately *not* part of this state. It used to be — `Loaded` held a
 * `Flow<PagingData<…>>` — which made the state unstable to the Compose compiler, restarted paging
 * on every state emission, and reset the scroll position each time.
 */
@Immutable
data class MeteoriteListUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val isSearching: Boolean = false,
    val isDarkTheme: Boolean = false,
    val isOnline: Boolean = true,
    val locationPermission: LocationPermissionStatus = LocationPermissionStatus.DENIED,
    val addressProgress: Float = 0f,
) {
    /** Only worth nagging about location once the list itself is up. */
    val shouldOfferLocationPermission: Boolean
        get() = !isLoading && locationPermission != LocationPermissionStatus.GRANTED
}
