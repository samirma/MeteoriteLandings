package com.antonio.samir.meteoritelandingsspots.service

import android.location.Address
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.address.AddressServiceImpl
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AddressServiceTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val mockLocalRepository: MeteoriteLocalRepository = mockk()
    private val mockGeoLocationUtil: GeoLocationUtilInterface = mockk()
    private val address: Address = mockk()

    private lateinit var addressService: AddressServiceImpl


    @Before
    fun setUp() {

        addressService = AddressServiceImpl(
            mockLocalRepository,
            mockGeoLocationUtil,
            coroutinesTestRule.testDispatcherProvider
        )

    }

    @Test
    fun `test meteoritesWithOutAddress success`() = runTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }

        val meteorites = listOf(meteorite)
        whenever(mockLocalRepository.meteoritesWithOutAddress())
            .thenReturn(flow {
                emit(meteorites)
                emit(emptyList())
            })

        whenever(mockLocalRepository.getMeteoritesWithoutAddressCount()).thenReturn(meteorites.size)
        whenever(mockLocalRepository.getValidMeteoritesCount()).thenReturn(meteorites.size * 2)
        whenever(mockLocalRepository.getMeteoritesWithoutAddressCount()).thenReturn(0)


        whenever(address.locality).thenReturn("city")
        whenever(address.adminArea).thenReturn("adminArea")
        whenever(address.countryName).thenReturn("countryName")

        whenever(mockGeoLocationUtil.getAddress(any(), any())).thenReturn(address)

        val expected = listOf(ResultOf.InProgress(100f), ResultOf.Success(100f))
        assertEquals(expected, addressService.recoveryAddress().toList())

        verify(mockLocalRepository).meteoritesWithOutAddress()
        verify(mockLocalRepository).updateAll(meteorites)
        verify(mockGeoLocationUtil).getAddress(any(), any())

    }


    @Test
    fun `test recoverAddress success`() = runTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }

        val meteorites = listOf(meteorite)
        whenever(mockLocalRepository.meteoritesWithOutAddress())
            .thenReturn(flow {
                emit(meteorites)
                emit(emptyList())
            })

        whenever(address.locality).thenReturn("city")
        whenever(address.adminArea).thenReturn("adminArea")
        whenever(address.countryName).thenReturn("countryName")

        whenever(mockGeoLocationUtil.getAddress(any(), any())).thenReturn(address)

        addressService.recoverAddress(meteorite)

        verify(mockLocalRepository).update(meteorite)
        verify(mockGeoLocationUtil).getAddress(any(), any())

    }

}