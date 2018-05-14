package com.antonio.samir.meteoritelandingsspots.presenter


import android.content.Context

import com.antonio.samir.meteoritelandingsspots.util.GPSTracker

interface MeteoriteListView {
    val context: Context

    val gpsDelegate: GPSTracker.GPSTrackerDelegate

    fun unableToFetch()

    fun hideList()

    fun meteoriteLoadingStarted()

    fun meteoriteLoadingStopped()
}
