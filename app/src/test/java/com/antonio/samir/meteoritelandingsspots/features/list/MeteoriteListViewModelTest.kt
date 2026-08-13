package com.antonio.samir.meteoritelandingsspots.features.list

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import com.antonio.samir.meteoritelandingsspots.common.ui.permission.LocationPermissionStatus
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SwitchUITheme
import com.antonio.samir.meteoritelandingsspots.testing.FakeConnectivityRepository
import com.antonio.samir.meteoritelandingsspots.testing.FakeLocationRepository
import com.antonio.samir.meteoritelandingsspots.testing.FakeMeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.testing.FakeUIThemeRepository
import com.antonio.samir.meteoritelandingsspots.testing.MainDispatcherRule
import com.antonio.samir.meteoritelandingsspots.testing.meteorite
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MeteoriteListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val localRepository = FakeMeteoriteLocalRepository((1..3).map { meteorite(it) })
    private val themeRepository = FakeUIThemeRepository()
    private val connectivityRepository = FakeConnectivityRepository()
    private val locationRepository = FakeLocationRepository()
    private val savedStateHandle = SavedStateHandle()

    private fun viewModel() = MeteoriteListViewModel(
        getMeteorites = GetMeteorites(localRepository),
        switchUITheme = SwitchUITheme(themeRepository),
        locationRepository = locationRepository,
        uiThemeRepository = themeRepository,
        connectivityRepository = connectivityRepository,
        savedStateHandle = savedStateHandle,
    )

    /**
     * `uiState` is shared with `WhileSubscribed`, so it only produces real values while something
     * is collecting. Tests subscribe for the duration rather than reading `.value` cold.
     */
    private fun TestScope.collecting(viewModel: MeteoriteListViewModel) {
        backgroundScope.launch { viewModel.uiState.collect { } }
        backgroundScope.launch { viewModel.meteorites.collect { } }
    }

    @Test
    fun `search query is kept in saved state so it survives process death`() = runTest {
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Aachen")
        advanceUntilIdle()

        assertEquals("Aachen", savedStateHandle.get<String>("query"))
        assertEquals("Aachen", viewModel.uiState.value.query)
    }

    @Test
    fun `closing search clears the query`() = runTest {
        val viewModel = viewModel()
        collecting(viewModel)
        viewModel.onSearchOpened()
        viewModel.onSearchQueryChange("Aachen")
        advanceUntilIdle()

        viewModel.onSearchClosed()
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.query)
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `the search query reaches the repository`() = runTest {
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Aachen")
        advanceUntilIdle()

        assertEquals("Aachen", localRepository.lastQuery)
    }

    @Test
    fun `toggling the theme writes the opposite of what is on screen`() = runTest {
        themeRepository.themeState.value = UITheme.DARK
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()

        viewModel.onDarkModeToggleClick(currentlyDark = viewModel.uiState.value.isDarkTheme)
        advanceUntilIdle()

        assertEquals(UITheme.LIGHT, themeRepository.themeState.value)
    }

    @Test
    fun `connectivity changes reach the ui state`() = runTest {
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isOnline)

        connectivityRepository.onlineState.value = false
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isOnline)
    }

    @Test
    fun `granting location resolves a position and orders the list by it`() = runTest {
        // The regression this guards: permission was stubbed to always succeed while location was
        // always null, so distance ordering silently never happened.
        locationRepository.location = mockk(relaxed = true) {
            every { latitude } returns 50.0
            every { longitude } returns 6.0
        }
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()

        viewModel.onLocationPermissionChanged(LocationPermissionStatus.GRANTED)
        advanceUntilIdle()

        assertEquals(50.0, localRepository.lastLatitude)
        assertEquals(6.0, localRepository.lastLongitude)
    }

    @Test
    fun `denied location leaves the list unordered and offers the prompt`() = runTest {
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()

        viewModel.onLocationPermissionChanged(LocationPermissionStatus.DENIED)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(LocationPermissionStatus.DENIED, state.locationPermission)
        assertTrue(state.shouldOfferLocationPermission)
        assertEquals(null, localRepository.lastLatitude)
    }

    @Test
    fun `a permanently denied permission still offers a way forward`() = runTest {
        val viewModel = viewModel()
        collecting(viewModel)
        advanceUntilIdle()

        viewModel.onLocationPermissionChanged(LocationPermissionStatus.PERMANENTLY_DENIED)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldOfferLocationPermission)
    }
}

/** Unused import guard: [Location] is referenced only through the relaxed mock above. */
private typealias UnusedLocation = Location
