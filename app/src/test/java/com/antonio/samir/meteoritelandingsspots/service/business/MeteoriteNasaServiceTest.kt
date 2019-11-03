package com.antonio.samir.meteoritelandingsspots.service.business

import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.mock
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
    fun testLoadMeteoritesFiltered() = runBlockingTest {

        val location = "us"
        meteoriteNasaService.loadMeteorites(location)

    }

}