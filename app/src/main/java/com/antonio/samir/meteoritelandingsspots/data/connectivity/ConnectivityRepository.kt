package com.antonio.samir.meteoritelandingsspots.data.connectivity

import kotlinx.coroutines.flow.Flow

/**
 * Network connectivity as a data source, per the architecture guide's "network connectivity
 * status providers" — the UI observes this rather than touching ConnectivityManager itself.
 */
interface ConnectivityRepository {

    /** Emits the current state immediately, then on every change. */
    val isOnline: Flow<Boolean>

    /** Point-in-time check, for callers that are about to start a network request. */
    fun isCurrentlyOnline(): Boolean
}
