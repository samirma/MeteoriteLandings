package com.antonio.samir.meteoritelandingsspots.service

import android.location.Address
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
    fun testMeteoritesWithOutAddress() = runBlockingTest {

        val meteorite = Meteorite().apply {
            reclong = "0"
            reclat = "1"
        }
        whenever(mockLocalRepository.meteoritesWithOutAddress()).thenReturn(listOf(meteorite))

        whenever(address.locality).thenReturn("city")
        whenever(address.adminArea).thenReturn("adminArea")
        whenever(address.countryName).thenReturn("countryName")

        whenever(mockGeoLocationUtil.getAddress(any(), any())).thenReturn(address)

        addressService.recoveryAddress()

        verify(mockLocalRepository).meteoritesWithOutAddress()
        verify(mockLocalRepository).update(meteorite)
        verify(mockGeoLocationUtil).getAddress(any(), any())

    }

}