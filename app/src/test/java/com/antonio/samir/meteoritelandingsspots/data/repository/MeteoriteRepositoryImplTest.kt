package com.antonio.samir.meteoritelandingsspots.data.repository

import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NetworkService
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MeteoriteRepositoryImplTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val mockLocalRepository: MeteoriteLocalRepository = mock()
    private val mockRemoteRepository: NetworkService = mock()

    private lateinit var repository: MeteoriteRepository

    @Before
    fun setUp() {

        repository = MeteoriteRepositoryImpl(mockLocalRepository, mockRemoteRepository, coroutinesTestRule.testDispatcherProvider)

    }

    @Test
    fun `test loadDatabase`() = runBlockingTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }

        val meteorites = listOf(meteorite)

        whenever(mockLocalRepository.getMeteoritesCount()).thenReturn(1)

        whenever(mockRemoteRepository.getMeteorites(any(), any())).thenReturn(meteorites)

        val expected: List<Result<Nothing>> = listOf(Result.InProgress(), Result.Success())
        val actual = repository.loadDatabase().toList()
        assertEquals(expected, actual)

    }


    @Test
    fun `test getMeteoriteById`() = runBlockingTest {

        val meteorite = Meteorite().apply {
            id = 43
            reclong = "0"
            reclat = "1"
        }

        whenever(mockLocalRepository.getMeteoriteById(43.toString())).thenReturn(flow {
            emit(meteorite)
        })

        val expected: List<Result<Meteorite>> = listOf(Result.InProgress(), Result.Success(meteorite))
        val actual = repository.getMeteoriteById(meteorite.id.toString()).toList()
        assertEquals(expected, actual)

    }
    
}