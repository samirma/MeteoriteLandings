package com.antonio.samir.meteoritelandingsspots.features

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.ui.extension.convertToNumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

fun Meteorite.getDistanceFrom(currentLocation: Location?): String? {
    val meteoriteLocation = getLocation()
    val distance = if (currentLocation != null && meteoriteLocation != null) {
        currentLocation.distanceTo(meteoriteLocation)
    } else {
        0.0f
    }
    return formatDistance(distance)
}

private fun formatDistance(distance: Float) = if (distance > 0) {
    val distanceInKm = distance > SHOW_IN_METERS

    val distance = if (distanceInKm) {
        (distance / 1000).roundToInt().toString()
    } else {
        distance.roundToInt().toString()
    }


    val distanceUnit = if (distanceInKm) {
        "km"
    } else {
        "m"
    }

    "${distance.convertToNumberFormat(distance)} $distanceUnit away"
} else {
    null
}

@SuppressLint("SimpleDateFormat")
val SIMPLE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

val Meteorite.yearString: String?
    get() {
        val value = year
        var yearParsed = value
        if (!value.isNullOrBlank()) {
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

fun Meteorite.getLocation(): Location? = try {
    Location("").apply {
        latitude = reclat!!.toDouble()
        longitude = reclong!!.toDouble()
    }
} catch (e: Exception) {
    null
}

fun Meteorite.finalAddress(location: Location?, currentAddress :String? = this.address): String {
    val list = mutableListOf<String>()

    if (!currentAddress.isNullOrBlank()) {
        list += currentAddress
    }

    val distance = getDistanceFrom(location)
    if (!distance.isNullOrBlank()) {
        list += distance
    }

    return list.joinToString(separator = " - ")
}

fun Meteorite.getLocationText(location: Location?, noAddress: String): String =
        finalAddress(location, if (!this.address.isNullOrEmpty()) {
            this.address
        } else {
            noAddress
        })

private const val SHOW_IN_METERS = 999