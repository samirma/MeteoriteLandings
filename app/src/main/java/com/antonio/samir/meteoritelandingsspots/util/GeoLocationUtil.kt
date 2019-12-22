package com.antonio.samir.meteoritelandingsspots.util

import android.location.Address
import android.location.Geocoder
import android.util.Log


class GeoLocationUtil(private val geoCoder: Geocoder) : GeoLocationUtilInterface {

    override fun getAddress(latitude: Double, longitude: Double): Address? {

        var address: Address? = null

        try {

            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)

            if (addresses.isNotEmpty()) {

                address = addresses[0]

            }
        } catch (e: Throwable) {
            Log.e(GeoLocationUtil::class.java.simpleName, e.message, e)
        }

        return address
    }

}
