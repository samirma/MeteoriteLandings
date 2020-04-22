package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val mockRepository: MeteoriteRepository = mock()
    private val mockGPSTracker: GPSTrackerInterface = mock()
    private val mockLocation: Location = mock()

    private lateinit var viewModel: MeteoriteDetailViewModel

    private val observer: Observer<Pair<Result<Meteorite>, Location?>> = mock()

    private var currentLocation = ConflatedBroadcastChannel<Location?>(null)

    @Before
    fun setUp() {

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(InProgress<Nothing>())
            emit(Success<Nothing>())
        })

        whenever(mockGPSTracker.location).thenReturn(currentLocation.asFlow())

        viewModel = MeteoriteDetailViewModel(mockRepository, mockGPSTracker)

    }

    @Test
    fun `test getMeteorite with location`() {

        val meteorite = Meteorite().apply {
            id = 123
            reclong = "0"
            reclat = "1"
        }

        whenever(mockRepository.getMeteoriteById(meteorite.id.toString())).thenReturn(flow {
            emit(Success(meteorite))
        })

        viewModel.loadMeteorite(meteorite)

        viewModel.meteorite.observeForever(observer)

        currentLocation.offer(mockLocation)

        verify(observer).onChanged(Pair(Success(meteorite), null))
        verify(observer).onChanged(Pair(Success(meteorite), mockLocation))

    }

    @Test
    fun requestAddressUpdate() = runBlockingTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }

        viewModel.requestAddressUpdate(meteorite)

        verify(mockRepository).update(meteorite)

    }
}
