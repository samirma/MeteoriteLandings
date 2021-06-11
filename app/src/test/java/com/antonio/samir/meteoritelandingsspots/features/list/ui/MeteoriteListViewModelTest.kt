package com.antonio.samir.meteoritelandingsspots.features.list.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel.ContentStatus
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var viewModel: MeteoriteListViewModel

    private val mockRepository: MeteoriteRepository = mock()
    private val mockGPSTracker: GPSTrackerInterface = mock()
    private val addressService: AddressServiceInterface = mock()
    private val mockSavedStateHandle = SavedStateHandle()

    private val mockObserverPageList: Observer<PagedList<Meteorite>> = mock()
    private val mockObserverContentStatus: Observer<ContentStatus> = mock()

    @Before
    fun setUp() {

        viewModel = MeteoriteListViewModel(
                stateHandle = mockSavedStateHandle,
                meteoriteRepository = mockRepository,
                gpsTracker = mockGPSTracker,
                addressService = addressService,
                dispatchers = coroutinesTestRule.testDispatcherProvider
        )

        viewModel.getMeteorites().observeForever(mockObserverPageList)
        viewModel.getContentStatus().observeForever(mockObserverContentStatus)

    }

    @Test
    fun `test getRecoveryAddressStatus success with address service stopped`() {
        val success = Success(100f)
        val inProgress = InProgress(50f)

        viewModel.addressServiceControl.value = false

        whenever(addressService.recoveryAddress()).thenReturn(flow {
            emit(inProgress)
            emit(success)
        })

        val observer: Observer<Result<Float>> = mock()
        viewModel.getRecoverAddressStatus().observeForever(observer)

        verify(observer).onChanged(success)
    }

    @Test
    fun `test getRecoveryAddressStatus success with address service resumed`() {
        val success = Success(100f)
        val inProgress = InProgress(50f)

        viewModel.addressServiceControl.value = true

        whenever(addressService.recoveryAddress()).thenReturn(flow {
            emit(inProgress)
            emit(success)
        })

        val observer: Observer<Result<Float>> = mock()
        viewModel.getRecoverAddressStatus().observeForever(observer)

        verify(observer).onChanged(inProgress)
        verify(observer).onChanged(success)
    }

    @Test
    fun `test getNetworkLoadStatus success`() {

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(InProgress())
            emit(Success(Unit))
        })

        val observer: Observer<Result<Unit>> = mock()
        viewModel.getNetworkLoadingStatus().observeForever(observer)

        verify(observer).onChanged(InProgress())
        verify(observer).onChanged(Success(Unit))
    }

    @Test
    fun `test getNetworkLoadStatus error`() {

        val value = Result.Error(Exception("test"))

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(value)
        })

        val observer: Observer<Result<Unit>> = mock()
        viewModel.getNetworkLoadingStatus().observeForever(observer)

        verify(observer).onChanged(value)
    }

    @Test
    fun `test loadMeteorites`() = runBlockingTest {

        whenever(mockGPSTracker.location).thenReturn(flow {
            emit(null)
        })

        val mockMet: PagingSource<Int, Meteorite> = mock()

        whenever(mockRepository.loadMeteorites(any(), any(), any(), any())).thenReturn(mockMet)

        viewModel.loadMeteorites("test")

        verify(mockObserverContentStatus).onChanged(ContentStatus.Loading)

    }

    @Test
    fun updateLocation() = runBlockingTest {

        viewModel.updateLocation()

        verify(mockGPSTracker).startLocationService()

    }

    @Test
    fun `test isAuthorizationRequested required`() {

        val isRequired = true
        whenever(mockGPSTracker.needAuthorization).thenReturn(flow {
            emit(isRequired)
        })

        val observer: Observer<Boolean> = mock()
        viewModel.isAuthorizationRequested().observeForever(observer)

        verify(observer).onChanged(isRequired)

    }

    @Test
    fun `test isAuthorizationRequested not required`() {

        val isRequired = false
        whenever(mockGPSTracker.needAuthorization).thenReturn(flow {
            emit(isRequired)
        })

        val observer: Observer<Boolean> = mock()
        viewModel.isAuthorizationRequested().observeForever(observer)

        verify(observer).onChanged(isRequired)

    }
}