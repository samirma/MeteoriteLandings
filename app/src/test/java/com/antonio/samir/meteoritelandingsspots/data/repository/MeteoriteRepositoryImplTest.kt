package com.antonio.samir.meteoritelandingsspots.data.repository

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MeteoriteRepositoryImplTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val mockLocalRepository: MeteoriteLocalRepository = mockk()
    private val mockRemoteRepository: MeteoriteRemoteRepository = mockk()

    private lateinit var repository: MeteoriteRepository

    private val fixtPageSize: Int = 5000

    @Before
    fun setUp() {

        repository = MeteoriteRepositoryImpl(
            mockLocalRepository,
            mockRemoteRepository,
            coroutinesTestRule.testDispatcherProvider
        )

    }

    @Test
    fun `test loadDatabase already loaded`() = runTest {

        val meteorites = mutableListOf<Meteorite>()

        for (i in 1..1001) {
            meteorites += Meteorite().apply {
                id = i
                reclong = "0"
                reclat = "1"
                year = "2020"
            }
        }

        whenever(mockLocalRepository.getValidMeteoritesCount()).thenReturn(meteorites.size)

        whenever(mockRemoteRepository.getMeteorites(any(), any())).thenReturn(meteorites)
            .thenReturn(emptyList())

        val expected: List<ResultOf<Unit>> = listOf(ResultOf.InProgress(), ResultOf.Success(Unit))
        val loadDatabase = repository.loadDatabase()
        val actual = loadDatabase.toList()
        assertEquals(expected, actual)

        verify(mockLocalRepository).getValidMeteoritesCount()

        verify(mockRemoteRepository).getMeteorites(meteorites.size, fixtPageSize)

    }

    @Test
    fun `test loadDatabase with valid meteorite`() = runTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
            year = "2020"
        }

        val meteorites = listOf(meteorite)

        whenever(mockLocalRepository.getValidMeteoritesCount()).thenReturn(50001)

        whenever(mockRemoteRepository.getMeteorites(any(), any())).thenReturn(meteorites)
            .thenReturn(emptyList())

        val expected: List<ResultOf<Unit>> = listOf(ResultOf.InProgress(), ResultOf.Success(Unit))
        val actual = repository.loadDatabase().toList()
        assertEquals(expected, actual)

        verify(mockLocalRepository).getValidMeteoritesCount()
        verify(mockLocalRepository).insertAll(meteorites)
        verify(mockRemoteRepository, times(2)).getMeteorites(any(), any())

    }


    @Test
    fun `test update`() = runTest {

        val meteorite = Meteorite().apply {
            id = 43
            reclong = "0"
            reclat = "1"
        }

        repository.update(meteorite)

        verify(mockLocalRepository).update(meteorite)

    }

    @Test
    fun `test update from list`() = runTest {

        val meteorite = Meteorite().apply {
            id = 43
            reclong = "0"
            reclat = "1"
        }

        val meteorites = listOf(meteorite)

        repository.update(meteorites)

        verify(mockLocalRepository).updateAll(meteorites)

    }


    @Test
    fun `test getMeteoriteById`() = runTest {

        val meteorite = Meteorite().apply {
            id = 43
            reclong = "0"
            reclat = "1"
        }

        whenever(mockLocalRepository.getMeteoriteById(43.toString())).thenReturn(flow {
            emit(meteorite)
        })

//        val expected: List<ResultOf<Meteorite>> = listOf(ResultOf.InProgress(), ResultOf.Success(meteorite))
//        val actual = repository.getMeteoriteById(meteorite.id.toString()).toList()
//        assertEquals(expected, actual)

    }

}