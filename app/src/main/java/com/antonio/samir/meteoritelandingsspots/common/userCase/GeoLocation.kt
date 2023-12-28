package com.antonio.samir.meteoritelandingsspots.common.userCase

import android.location.Address
import android.location.Geocoder
import android.util.Log
import javax.inject.Inject

class GeoLocation @Inject constructor(private val geoCoder: Geocoder) {

    fun getAddress(latitude: Double, longitude: Double): Address? {

        var address: Address? = null

        try {

            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {

                address = addresses[0]

            }
        } catch (e: Throwable) {
            Log.e(GeoLocation::class.java.simpleName, e.message, e)
        }

        return address
    }

}
