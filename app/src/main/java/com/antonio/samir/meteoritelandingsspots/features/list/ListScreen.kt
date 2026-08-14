package com.antonio.samir.meteoritelandingsspots.features.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.antonio.samir.meteoritelandingsspots.common.ui.permission.LocationPermissionStatus
import com.antonio.samir.meteoritelandingsspots.common.ui.permission.rememberLocationPermissionState
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.LocationPermissionBanner
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteListTopBar
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteSearchField
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.OfflineBanner
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun ListScreenNavigation(
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
) {
    val viewModel: MeteoriteListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items = viewModel.meteorites.collectAsLazyPagingItems()

    val permissionState = rememberLocationPermissionState()
    // The permission lives in the composition (only an Activity can request it) but the ordering
    // decision lives in the ViewModel, so the live status is pushed down whenever it changes.
    LaunchedEffect(permissionState.status) {
        viewModel.onLocationPermissionChanged(permissionState.status)
    }

    ListScreen(
        uiState = uiState,
        items = items,
        modifier = modifier,
        onItemClick = { itemView -> onItemClick(itemView.id) },
        onDarkModeToggleClick = { viewModel.onDarkModeToggleClick(uiState.isDarkTheme) },
        onQueryChange = viewModel::onSearchQueryChange,
        onSearchOpened = viewModel::onSearchOpened,
        onSearchClosed = viewModel::onSearchClosed,
        onGrantLocation = {
            if (uiState.locationPermission == LocationPermissionStatus.PERMANENTLY_DENIED) {
                permissionState.openAppSettings()
            } else {
                permissionState.request()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    uiState: MeteoriteListUiState,
    items: LazyPagingItems<MeteoriteItemView>,
    modifier: Modifier = Modifier,
    onItemClick: (itemView: MeteoriteItemView) -> Unit = {},
    onDarkModeToggleClick: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearchOpened: () -> Unit = {},
    onSearchClosed: () -> Unit = {},
    onGrantLocation: () -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (uiState.isSearching) {
                MeteoriteSearchField(
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    onClose = onSearchClosed,
                )
            } else {
                MeteoriteListTopBar(
                    isDarkTheme = uiState.isDarkTheme,
                    onEnterSearch = onSearchOpened,
                    onDarkModeToggleClick = onDarkModeToggleClick,
                    scrollBehavior = scrollBehavior,
                )
            }
        },
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            OfflineBanner(visible = !uiState.isOnline)
            LocationPermissionBanner(
                visible = uiState.shouldOfferLocationPermission,
                permanentlyDenied =
                    uiState.locationPermission == LocationPermissionStatus.PERMANENTLY_DENIED,
                onGrant = onGrantLocation,
            )
            AddressProgress(progress = uiState.addressProgress)
            MeteoriteList(
                items = items,
                scrollState = scrollState,
                onItemClick = onItemClick,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ListScreenPreview() {
    val items = (1..10).map {
        MeteoriteItemView(
            id = it,
            name = "Meteorite $it",
            yearString = "19${50 + it}",
            address = "Somewhere $it",
            distance = "$it km",
        )
    }
    MeteoriteLandingsTheme {
        ListScreen(
            uiState = MeteoriteListUiState(isLoading = false),
            items = flowOf(PagingData.from(items)).collectAsLazyPagingItems(),
        )
    }
}
