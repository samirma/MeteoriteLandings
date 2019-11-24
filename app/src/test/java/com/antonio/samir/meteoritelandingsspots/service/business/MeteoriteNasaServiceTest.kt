package com.antonio.samir.meteoritelandingsspots.service.business

import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MeteoriteNasaServiceTest {

    private val meteoriteRepository: MeteoriteRepositoryInterface = mock()
    private val addressService: AddressServiceInterface = mock()
    private val gpsTracker: GPSTrackerInterface = mock()

    private lateinit var meteoriteNasaService: MeteoriteServiceInterface

    @Before
    fun setUp() {

        meteoriteNasaService = MeteoriteNasaService(meteoriteRepository, addressService, gpsTracker)

    }

    @Test
    fun testLoadMeteoritesAll() = runBlockingTest {

        meteoriteNasaService.loadMeteorites(null)

    }

    @Test
    fun testLoadMeteoritesFilteredByLocation() = runBlockingTest {

        whenever(meteoriteRepository.getMeteoritesCount()).doReturn(0)

        whenever(gpsTracker.isLocationServiceStarted()).doReturn(false)
        whenever(gpsTracker.isGPSEnabled()).doReturn(false)

        val location = "us"
        meteoriteNasaService.loadMeteorites(location)

        verify(meteoriteRepository).meteoriteOrdered(null, location)

    }

    @Test
    fun testLoadMeteoritesNoFilteredByLocation() = runBlockingTest {

        whenever(meteoriteRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

        verify(meteoriteRepository).meteoriteOrdered(null, null)

    }


    @Test
    fun testFilterList() = runBlockingTest {

        whenever(meteoriteRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

        val filter = "us"
        meteoriteNasaService.filterList(filter)

        verify(meteoriteRepository).meteoriteOrdered(null, filter)

    }

}