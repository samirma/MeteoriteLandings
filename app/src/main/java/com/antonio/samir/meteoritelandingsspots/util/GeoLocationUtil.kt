package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*


class GeoLocationUtil(val context: Context) : GeoLocationUtilInterface {

    override fun getAddress(latitude: Double, longitude: Double): Address? {

        var address: Address? = null

        try {

            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && !addresses.isEmpty()) {

                address = addresses[0]

            }
        } catch (e: IOException) {
            Log.e(GeoLocationUtil::class.java.simpleName, e.message, e)
        }

        return address
    }

}
