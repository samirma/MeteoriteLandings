package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context
import android.location.Address


interface GeoLocationUtilInterface {

    fun getAddress(latitude: Double?, longitude: Double?, context: Context): Address?

}
