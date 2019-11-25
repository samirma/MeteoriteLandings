package com.antonio.samir.meteoritelandingsspots.service.business

import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MeteoriteNasaServiceTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val mockRepository: MeteoriteRepositoryInterface = mock()
    private val mockAddressService: AddressServiceInterface = mock()
    private val mockGpsTracker: GPSTrackerInterface = mock()
    private val mockLiveDataListMeteorite: LiveData<List<Meteorite>> = mock()

    private lateinit var meteoriteNasaService: MeteoriteServiceInterface

    @Before
    fun setUp() {

        whenever(mockRepository.meteoriteOrdered(null, null)).thenReturn(mockLiveDataListMeteorite)

        meteoriteNasaService = MeteoriteNasaService(mockRepository, mockAddressService, mockGpsTracker, coroutinesTestRule.testDispatcherProvider)

    }

    @Test
    fun testLoadMeteoritesAll() = runBlockingTest {

        whenever(mockRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

    }

    @Test
    fun testLoadMeteoritesFilteredByLocation() = runBlockingTest {

        whenever(mockRepository.getMeteoritesCount()).doReturn(0)

        whenever(mockGpsTracker.isLocationServiceStarted()).doReturn(false)
        whenever(mockGpsTracker.isGPSEnabled()).doReturn(false)

        val location = "us"
        meteoriteNasaService.loadMeteorites(location)

        verify(mockRepository).meteoriteOrdered(null, location)

    }

    @Test
    fun testLoadMeteoritesNoFilteredByLocation() = runBlockingTest {

        whenever(mockRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

        verify(mockRepository, times(2)).meteoriteOrdered(null, null)

    }


    @Test
    fun testFilterList() = runBlockingTest {

        whenever(mockRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

        val filter = "us"
        meteoriteNasaService.filterList(filter)

        verify(mockRepository).meteoriteOrdered(null, filter)

    }

}