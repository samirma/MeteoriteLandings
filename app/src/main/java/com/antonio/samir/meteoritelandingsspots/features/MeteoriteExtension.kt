package com.antonio.samir.meteoritelandingsspots.features

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

fun Meteorite.getDistanceFrom(latitude: Double, longitude: Double): String {
    return "${betweenLatlong(latitude, longitude, reclat!!.toDouble(), reclong!!.toDouble())} km from you"
}

@SuppressLint("SimpleDateFormat")
val SIMPLE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

val Meteorite.yearString: String?
    get() {
        val value = year
        var yearParsed = value
        if (!TextUtils.isEmpty(value)) {
            try {
                val date = SIMPLE_DATE_FORMAT.parse(year!!.trim { it <= ' ' })

                val cal = Calendar.getInstance()
                cal.time = date
                yearParsed = cal.get(Calendar.YEAR).toString()

            } catch (e: ParseException) {
                Log.e(this@yearString::class.java.simpleName, e.message, e)
            }

        }
        return yearParsed
    }

private fun betweenLatlong(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Long {

    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val earthRadius = 6371.01 //Kilometers
    return (earthRadius * acos(sin(lat1Rad) * sin(lat2Rad) + cos(lat1Rad) * cos(lat2Rad) * cos(lon1Rad - lon2Rad))).roundToLong()
}