package com.antonio.samir.meteoritelandingsspots.service.repository.local

import android.location.Location
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.repository.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.service.repository.remote.NasaRemoteRepositoryInterface
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class MeteoriteRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val meteoriteDao: MeteoriteDao = mock()
    private val nasaRemoteRepository: NasaRemoteRepositoryInterface = mock()
    private val location: Location = mock()

    private lateinit var meteoriteRepository: MeteoriteRepository


    @Before
    fun setUp() {

        meteoriteRepository = MeteoriteRepository(meteoriteDao, nasaRemoteRepository)

    }

    @Test
    fun meteoriteOrderedWithLocationAndFilter() {

        val filter = "aa"

        whenever(location.latitude).thenReturn(2.0)
        whenever(location.longitude).thenReturn(1.0)

        meteoriteRepository.meteoriteOrdered(location, filter)

        verify(meteoriteDao).meteoriteOrderedByLocationFiltered(2.0, 1.0, filter)
    }

    @Test
    fun meteoriteOrderedWithOutLocationAndWithFilter() {

        val filter = "aa"

        meteoriteRepository.meteoriteOrdered(null, filter)

        verify(meteoriteDao).meteoriteFiltered(filter)
    }

    @Test
    fun meteoriteNoFilterNoLocation() {

        meteoriteRepository.meteoriteOrdered(null, null)

        verify(meteoriteDao).meteoriteOrdered()
    }

}