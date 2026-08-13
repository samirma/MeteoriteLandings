package com.antonio.samir.meteoritelandingsspots.data.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Replaces the old `NetworkUtil`, which used `ConnectivityManager.activeNetworkInfo` — deprecated
 * since API 29, i.e. on every device this app supports (minSdk 30).
 *
 * Connectivity is exposed as a stream rather than a one-shot boolean so the UI can react to the
 * network coming and going while a screen is open.
 */
@Singleton
class ConnectivityRepositoryImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager,
) : ConnectivityRepository {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val networks = mutableSetOf<Network>()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities,
            ) {
                // VALIDATED matters as much as INTERNET: captive portals and dead Wi-Fi both
                // report a connected network that cannot actually reach anything.
                if (capabilities.isUsable()) networks += network else networks -= network
                trySend(networks.isNotEmpty())
            }

            override fun onLost(network: Network) {
                networks -= network
                trySend(networks.isNotEmpty())
            }
        }

        trySend(isCurrentlyOnline())

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
        .conflate()
        .distinctUntilChanged()

    override fun isCurrentlyOnline(): Boolean {
        val active = connectivityManager.activeNetwork ?: return false
        return connectivityManager.getNetworkCapabilities(active)?.isUsable() == true
    }

    private fun NetworkCapabilities.isUsable() =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
