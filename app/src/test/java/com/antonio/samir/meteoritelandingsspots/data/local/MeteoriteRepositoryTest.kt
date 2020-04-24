package com.antonio.samir.meteoritelandingsspots.data.local

import android.location.Location
import com.antonio.samir.meteoritelandingsspots.data.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MeteoriteRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val meteoriteDao: MeteoriteDao = mock()
    private val location: Location = mock()

    private lateinit var meteoriteLocalRepository: MeteoriteLocalRepository


    @Before
    fun setUp() {

        meteoriteLocalRepository = MeteoriteLocalRepositoryImpl(meteoriteDao, coroutinesTestRule.testDispatcherProvider)

    }

    @Test
    fun `test meteoriteOrdered with filter and location`() = runBlockingTest {

        val filter = "aa"

        val latitude = 2.0
        val longitude = 1.0

        whenever(location.latitude).thenReturn(latitude)
        whenever(location.longitude).thenReturn(longitude)

        meteoriteLocalRepository.meteoriteOrdered(filter, latitude, longitude)

        verify(meteoriteDao).meteoriteOrderedByLocationFiltered(latitude, longitude, filter)
    }

    @Test
    fun `test meteoriteOrdered with filter and invalid location`() = runBlockingTest {

        val filter = "aa"

        meteoriteLocalRepository.meteoriteOrdered(filter, null, 1.0)

        verify(meteoriteDao).meteoriteFiltered(filter)
    }

    @Test
    fun `test meteoriteOrdered without filter and invalid location`() = runBlockingTest {

        meteoriteLocalRepository.meteoriteOrdered(null, 1.0, null)

        verify(meteoriteDao).meteoriteOrdered()
    }

    @Test
    fun `test meteoriteOrdered without filter and location`() = runBlockingTest {

        val latitude = 1.0
        val longitude = 1.3
        meteoriteLocalRepository.meteoriteOrdered(null, latitude, longitude)

        verify(meteoriteDao).meteoriteOrderedByLocation(latitude, longitude)
    }

}