package com.antonio.samir.meteoritelandingsspots.util

import android.location.Geocoder
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class GeoLocationUtilTest {

    private val mockGeocoder: Geocoder = mockk()

    private lateinit var service: GeoLocationUtilInterface


    @Before
    fun setUp() {

        service = GeoLocationUtil(mockGeocoder)

    }

    @Test
    fun `test getAddress`() {

        val latitude = 0.0
        val longitude = 0.0
        service.getAddress(latitude, longitude)

        verify(mockGeocoder).getFromLocation(latitude, longitude, 1)

    }
}