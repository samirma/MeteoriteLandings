package com.antonio.samir.meteoritelandingsspots.features.detail

import app.cash.turbine.test
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.userCases.GetMeteoriteById
import com.antonio.samir.meteoritelandingsspots.testing.FakeMeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.testing.MainDispatcherRule
import com.antonio.samir.meteoritelandingsspots.testing.meteorite
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MeteoriteDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeMeteoriteLocalRepository, id: Int) =
        MeteoriteDetailViewModel(
            meteoriteId = id,
            getMeteoriteById = GetMeteoriteById(repository),
        )

    @Test
    fun `exposes the meteorite it was constructed with`() = runTest {
        val repository = FakeMeteoriteLocalRepository(
            listOf(meteorite(id = 7, name = "Aachen", address = "Aachen, Germany"))
        )

        viewModel(repository, id = 7).uiState.test {
            assertEquals(MeteoriteDetailUiState.Loading, awaitItem())
            val loaded = awaitItem() as MeteoriteDetailUiState.Loaded
            assertEquals("Aachen", loaded.meteoriteView.name)
            assertEquals("Aachen, Germany", loaded.meteoriteView.address)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `surfaces an error when the meteorite is missing`() = runTest {
        // The regression this guards: non-Success results used to be dropped, so a missing
        // meteorite left the screen on its spinner forever.
        val repository = FakeMeteoriteLocalRepository(emptyList())

        viewModel(repository, id = 99).uiState.test {
            assertEquals(MeteoriteDetailUiState.Loading, awaitItem())
            assertEquals(
                MeteoriteDetailUiState.Error(R.string.meteorite_not_found),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `two view models never share state`() = runTest {
        // Each navigation entry gets its own ViewModelStore, so opening a second meteorite must
        // not inherit the first one's data.
        val repository = FakeMeteoriteLocalRepository(
            listOf(meteorite(id = 1, name = "First"), meteorite(id = 2, name = "Second"))
        )

        val first = viewModel(repository, id = 1)
        val second = viewModel(repository, id = 2)

        first.uiState.test {
            skipItems(1)
            assertEquals("First", (awaitItem() as MeteoriteDetailUiState.Loaded).meteoriteView.name)
            cancelAndIgnoreRemainingEvents()
        }
        second.uiState.test {
            skipItems(1)
            assertEquals("Second", (awaitItem() as MeteoriteDetailUiState.Loaded).meteoriteView.name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
