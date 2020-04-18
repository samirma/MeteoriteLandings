package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.list.ui.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.test.assertEquals

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val meteoriteService: MeteoriteRepository = mock()

    private val gpsTracker: GPSTrackerInterface = mock()

    private lateinit var viewModel: MeteoriteListViewModel

    private val mockAddressService: AddressServiceInterface = mock()

    @Before
    fun setUp() {

        viewModel = MeteoriteListViewModel(meteoriteService, gpsTracker, coroutinesTestRule.testDispatcherProvider, mockAddressService)

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