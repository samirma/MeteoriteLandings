package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkUtilTest {

    private val mockContext: Context = mockk()

    private val mockConnectivityManager: ConnectivityManager = mockk()

    private val mockNetworkInfo: NetworkInfo = mockk()

    private lateinit var service: NetworkUtilInterface


    @Before
    fun setUp() {

        whenever(mockContext.applicationContext).thenReturn(mockContext)

        whenever(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(mockConnectivityManager)

        whenever(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)

        service = NetworkUtil(mockContext)

    }

    @Test
    fun `test internet connectivity as true`() {

        whenever(mockNetworkInfo.isConnected).thenReturn(true)

        assertTrue(service.hasConnectivity())

    }

    @Test
    fun `test internet connectivity as false`() {

        whenever(mockNetworkInfo.isConnected).thenReturn(false)

        assertFalse(service.hasConnectivity())

    }

}