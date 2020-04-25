package com.antonio.samir.meteoritelandingsspots.features.list.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
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

    @Before
    fun setUp() {

        whenever(addressService.recoveryAddress()).thenReturn(flow {
            emit(Result.InProgress<Nothing>())
            emit(Result.Success<Nothing>())
        })

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(Result.InProgress<Nothing>())
            emit(Result.Success<Nothing>())
        })

        viewModel = MeteoriteListViewModel(mockRepository, mockGPSTracker, addressService, coroutinesTestRule.testDispatcherProvider)
    }

    @Test
    fun getRecoveryAddressStatus() {

        val observer: Observer<Result<Int>> = mock()
        viewModel.recoveryAddressStatus.observeForever(observer)

        verify(observer).onChanged(Result.InProgress<Nothing>())
        verify(observer).onChanged(Result.Success<Nothing>())
    }

    @Test
    fun getNetworkLoadStatus() {

        val observer: Observer<Result<Int>> = mock()
        viewModel.networkLoadingStatus.observeForever(observer)

        verify(observer).onChanged(Result.InProgress<Nothing>())
        verify(observer).onChanged(Result.Success<Nothing>())
    }

    @Test
    fun getMeteorites() {
    }

    @Test
    fun getNetworkLoadingStatus() {
    }

    @Test
    fun loadMeteorites() {
    }

    @Test
    fun testLoadMeteorites() {
    }

    @Test
    fun updateLocation() {
    }

    @Test
    fun isAuthorizationRequested() {
    }
}