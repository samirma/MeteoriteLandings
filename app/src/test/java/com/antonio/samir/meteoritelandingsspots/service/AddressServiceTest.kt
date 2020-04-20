package com.antonio.samir.meteoritelandingsspots.service

import android.location.Address
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AddressServiceTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val mockLocalRepository: MeteoriteLocalRepository = mock()
    private val mockGeoLocationUtil: GeoLocationUtilInterface = mock()
    private val address: Address = mock()

    private lateinit var addressService: AddressService


    @Before
    fun setUp() {

        addressService = AddressService(mockLocalRepository, mockGeoLocationUtil, coroutinesTestRule.testDispatcherProvider)

    }

    @Test
    fun `test meteoritesWithOutAddress success`() = runBlockingTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }

        val meteorites = listOf(meteorite)
        whenever(mockLocalRepository.meteoritesWithOutAddress())
                .thenReturn(meteorites)
                .thenReturn(emptyList())

        whenever(address.locality).thenReturn("city")
        whenever(address.adminArea).thenReturn("adminArea")
        whenever(address.countryName).thenReturn("countryName")

        whenever(mockGeoLocationUtil.getAddress(any(), any())).thenReturn(address)

        val expected: List<Result<Nothing>> = listOf(Result.InProgress(), Result.Success())
        val actual = addressService.recoveryAddress().toList()
        assertEquals(expected, actual)

        verify(mockLocalRepository, times(2)).meteoritesWithOutAddress()
        verify(mockLocalRepository).updateAll(meteorites)
        verify(mockGeoLocationUtil).getAddress(any(), any())

    }


    @Test
    fun `test recoverAddress success`() = runBlockingTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }

        val meteorites = listOf(meteorite)
        whenever(mockLocalRepository.meteoritesWithOutAddress())
                .thenReturn(meteorites)
                .thenReturn(emptyList())

        whenever(address.locality).thenReturn("city")
        whenever(address.adminArea).thenReturn("adminArea")
        whenever(address.countryName).thenReturn("countryName")

        whenever(mockGeoLocationUtil.getAddress(any(), any())).thenReturn(address)

        addressService.recoverAddress(meteorite)

        verify(mockLocalRepository).update(meteorite)
        verify(mockGeoLocationUtil).getAddress(any(), any())

    }

}