package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*


object GeoLocationUtil {

    fun getAddress(latitude: Double?, longitude: Double?, context: Context): Address? {
        val geocoder: Geocoder

        geocoder = Geocoder(context, Locale.getDefault())

        var address: Address? = null

        try {

            val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && !addresses.isEmpty()) {

                address = addresses[0]

            }
        } catch (e: IOException) {
            Log.e(GeoLocationUtil::class.java.simpleName, e.message, e)
        }

        return address
    }

}
