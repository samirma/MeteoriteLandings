package com.antonio.samir.meteoritelandingsspots.features

import android.annotation.SuppressLint
import android.location.Location
import android.text.TextUtils
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import org.apache.commons.lang3.StringUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

fun Meteorite.getDistanceFrom(currentLocation: Location?): String {
    val distance = currentLocation?.distanceTo(getLocation()) ?: 0.0f
    return formatDistance(distance)
}

private fun formatDistance(distance: Float) = if (distance > 0) {
    val distanceTo = if (distance > SHOW_IN_METERS) {
        val kilometers = (distance / 1000).roundToInt()
        "$kilometers km"
    } else {
        val meters = distance.roundToInt()
        "$meters m"
    }
    "- $distanceTo from you"
} else {
    StringUtils.EMPTY
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

fun Meteorite.getLocation(): Location = Location("").apply {
    latitude = reclat!!.toDouble()
    longitude = reclong!!.toDouble()
}

private const val SHOW_IN_METERS = 999