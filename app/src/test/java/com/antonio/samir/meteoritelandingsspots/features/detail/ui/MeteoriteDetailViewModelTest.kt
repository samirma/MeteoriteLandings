package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import android.content.Context
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.InProgress
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteDetailViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val mockRepository: MeteoriteRepository = mockk()
    private val mockLocation: Location = mockk()
    private val mockContext: Context = mockk()

    @Fixture
    lateinit var fixtMeteoriteView: MeteoriteView

    @Fixture
    lateinit var fixtString: String

    private lateinit var viewModel: MeteoriteDetailViewModel

    private val mockMeteoriteObserver: Observer<ResultOf<MeteoriteView>> = mockk()

    private var currentLocation = ConflatedBroadcastChannel<Location?>(null)

    @Before
    fun setUp() {
        FixtureAnnotations.initFixtures(this, JFixture())

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(InProgress())
            emit(Success(Unit))
        })

//        whenever(mockContext.getString(R.string.unkown)).thenReturn(fixtString)
//        whenever(mockContext.getString(R.string.without_address_placeholder)).thenReturn(fixtString)

//        viewModel = MeteoriteDetailViewModel(mockRepository, mockGPSTracker, get())
//
//        viewModel.getMeteorite(mockContext).observeForever(mockMeteoriteObserver)

    }

    @Test
    fun `test getMeteorite with location`() {

        val meteorite = Meteorite().apply {
            id = 123
            reclong = "0"
            reclat = "1"
            mass = "0"
        }

//        whenever(mockRepository.getMeteoriteById(any())).thenReturn(flow {
//            emit(InProgress())
//            emit(Success(meteorite))
//        })

        currentLocation.trySend(mockLocation).isSuccess

        viewModel.loadMeteorite(fixtMeteoriteView.id!!)

        verify(mockMeteoriteObserver).onChanged(InProgress())
        verify(mockMeteoriteObserver).onChanged(
            Success(
                MeteoriteView(
                    id = "123",
                    name = "",
                    yearString = "",
                    address = fixtString,
                    type = "",
                    mass = meteorite.mass!!,
                    reclat = 1.0,
                    reclong = 0.0,
                    hasAddress = false
                )
            )
        )

    }

//    @Test
//    fun requestAddressUpdate() = runTest {
//
//        viewModel.requestAddressUpdate(fixtMeteoriteView)
//
////        verify(mockRepository).update(fixtMeteoriteView)
//
//    }
}
