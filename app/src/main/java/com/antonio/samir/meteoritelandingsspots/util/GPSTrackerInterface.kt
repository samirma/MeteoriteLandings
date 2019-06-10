package com.antonio.samir.meteoritelandingsspots.util

import android.location.LocationListener

interface GPSTrackerInterface : LocationListener {

    fun startLocationService()
}
