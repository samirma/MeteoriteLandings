package com.antonio.samir.meteoritelandingsspots.data.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Runs under Robolectric because the flow builds a real [NetworkRequest]; a plain JVM unit test
 * returns null from every unmocked Android framework method.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ConnectivityRepositoryImplTest {

    private val connectivityManager = mockk<ConnectivityManager>(relaxed = true)
    private val repository = ConnectivityRepositoryImpl(connectivityManager)

    private fun capabilities(internet: Boolean, validated: Boolean) =
        mockk<NetworkCapabilities> {
            every { hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns internet
            every { hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns validated
        }

    @Test
    fun `a connected but unvalidated network counts as offline`() {
        // Captive portals report a connected network that cannot reach anything.
        val network = mockk<Network>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns
            capabilities(internet = true, validated = false)

        assertFalse(repository.isCurrentlyOnline())
    }

    @Test
    fun `a validated internet network counts as online`() {
        val network = mockk<Network>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns
            capabilities(internet = true, validated = true)

        assertTrue(repository.isCurrentlyOnline())
    }

    @Test
    fun `no active network counts as offline`() {
        every { connectivityManager.activeNetwork } returns null

        assertFalse(repository.isCurrentlyOnline())
    }

    @Test
    fun `emits the current state then follows callbacks`() = runTest {
        every { connectivityManager.activeNetwork } returns null
        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every {
            connectivityManager.registerNetworkCallback(any<NetworkRequest>(), capture(callbackSlot))
        } returns Unit

        repository.isOnline.test {
            assertFalse(awaitItem())

            val network = mockk<Network>()
            callbackSlot.captured.onCapabilitiesChanged(
                network,
                capabilities(internet = true, validated = true),
            )
            assertTrue(awaitItem())

            callbackSlot.captured.onLost(network)
            assertFalse(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }

        verify { connectivityManager.unregisterNetworkCallback(callbackSlot.captured) }
    }
}
