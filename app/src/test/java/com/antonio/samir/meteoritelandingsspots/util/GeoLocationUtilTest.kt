package com.antonio.samir.meteoritelandingsspots.util

import android.location.Geocoder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class GeoLocationUtilTest {

    private val mockGeocoder: Geocoder = mock()

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