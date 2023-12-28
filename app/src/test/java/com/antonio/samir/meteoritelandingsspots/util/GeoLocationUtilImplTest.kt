package com.antonio.samir.meteoritelandingsspots.util

import android.location.Geocoder
import com.antonio.samir.meteoritelandingsspots.common.userCase.GeoLocation
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class GeoLocationUtilImplTest {

    private val mockGeocoder: Geocoder = mockk()

    private lateinit var service: GeoLocationUtil


    @Before
    fun setUp() {

        service = GeoLocation(mockGeocoder)

    }

    @Test
    fun `test getAddress`() {

        val latitude = 0.0
        val longitude = 0.0
        service.getAddress(latitude, longitude)

        verify(mockGeocoder).getFromLocation(latitude, longitude, 1)

    }
}