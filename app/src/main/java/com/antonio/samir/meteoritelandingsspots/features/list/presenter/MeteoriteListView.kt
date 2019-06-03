package com.antonio.samir.meteoritelandingsspots.features.list.presenter


import com.antonio.samir.meteoritelandingsspots.util.GPSTracker

interface MeteoriteListView {
    val gpsDelegate: GPSTracker.GPSTrackerDelegate

    fun unableToFetch()

    fun hideList()

    fun meteoriteLoadingStarted()

    fun meteoriteLoadingStopped()
}
