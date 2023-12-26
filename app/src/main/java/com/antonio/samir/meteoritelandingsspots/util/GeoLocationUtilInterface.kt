package com.antonio.samir.meteoritelandingsspots.util

import android.location.Address


interface GeoLocationUtilInterface {

    fun getAddress(latitude: Double, longitude: Double): Address?

}
