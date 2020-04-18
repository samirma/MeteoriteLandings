package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
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

    private val mockLocalRepository: MeteoriteLocalRepository = mock()
    private val mockLiveDataListMeteorite: DataSource.Factory<Int, Meteorite> = mock()

    private lateinit var meteoriteNasaService: MeteoriteRepository

    @Before
    fun setUp() {

        whenever(mockLocalRepository.meteoriteOrdered(null, null)).thenReturn(mockLiveDataListMeteorite)

        meteoriteNasaService = MeteoriteRepositoryImpl(mockLocalRepository, coroutinesTestRule.testDispatcherProvider)

    }

    @Test
    fun testLoadMeteoritesAll() = runBlockingTest {

        whenever(mockLocalRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

    }

    @Test
    fun testLoadMeteoritesFilteredByLocation() = runBlockingTest {

        whenever(mockLocalRepository.getMeteoritesCount()).doReturn(0)

        val location = "us"
        meteoriteNasaService.loadMeteorites(location)

        verify(mockLocalRepository).meteoriteOrdered(null, location)

    }

    @Test
    fun testLoadMeteoritesNoFilteredByLocation() = runBlockingTest {

        whenever(mockLocalRepository.getMeteoritesCount()).doReturn(0)

        meteoriteNasaService.loadMeteorites(null)

        verify(mockLocalRepository, times(2)).meteoriteOrdered(null, null)

    }

    @Test
    fun testFilterList() = runBlockingTest {

        whenever(mockLocalRepository.getMeteoritesCount()).doReturn(0)

        val filter = "us"
        meteoriteNasaService.loadMeteorites(filter)

        verify(mockLocalRepository).meteoriteOrdered(null, filter)

    }

}