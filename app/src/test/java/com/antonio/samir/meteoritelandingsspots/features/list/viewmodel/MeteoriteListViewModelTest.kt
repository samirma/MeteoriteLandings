package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MeteoriteListViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var meteoriteService: MeteoriteServiceInterface

    private lateinit var gpsTracker: GPSTrackerInterface

    private lateinit var viewModel: MeteoriteListViewModel

    @Before
    fun setUp() {

        meteoriteService = mock()

        gpsTracker = mock()

        viewModel = MeteoriteListViewModel(meteoriteService, gpsTracker, coroutinesTestRule.testDispatcherProvider)

    }

    @Test
    fun loadMeteoriteSuccess() = runBlockingTest {

        val data = MutableLiveData<List<Meteorite>>()

        val element = Meteorite().apply {
            id = 123
        }

        data.postValue(listOf(element))

        val location = "sa"

        Mockito.`when`(meteoriteService.loadMeteorites(location)).thenReturn(data)

        viewModel.loadMeteorites(location)

        val meteorite = viewModel.meteorites.apply {
            observeForever {}
        }

        val loadingStatus = viewModel.loadingStatus.apply {
            observeForever {}
        }

        val actual = meteorite.value
        val expected = data.value
        assertEquals(expected, actual)

        assertEquals(MeteoriteListViewModel.DownloadStatus.DONE, loadingStatus.value)

    }

    @Test
    fun loadMeteoriteError() = runBlockingTest {

        val data = MutableLiveData<List<Meteorite>>()

        data.value = listOf(Meteorite().apply {
            id = 123
        })

        val location = "sa"

        Mockito.`when`(meteoriteService.loadMeteorites(location)).thenThrow(Error("some error"))

        val loadingStatus = viewModel.loadingStatus.apply {
            observeForever {}
        }

        viewModel.loadMeteorites(location)

        assertEquals(MeteoriteListViewModel.DownloadStatus.UNABLE_TO_FETCH, loadingStatus.value)

    }

}