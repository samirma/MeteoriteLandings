package com.antonio.samir.meteoritelandingsspots.common.ui.extension

import android.location.Location
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/** The meteorite's coordinates as an Android [Location], or null if it has none. */
fun Meteorite.toAndroidLocation(): Location? {
    val latitude = this.latitude ?: return null
    val longitude = this.longitude ?: return null
    return Location(PROVIDER).apply {
        this.latitude = latitude
        this.longitude = longitude
    }
}

/**
 * Distance from [currentLocation], formatted for display, or null when either position is
 * unknown. Below a kilometre it is shown in metres.
 */
fun Meteorite.distanceTextFrom(currentLocation: Location?): String? {
    if (currentLocation == null) return null
    val meteoriteLocation = toAndroidLocation() ?: return null
    val distanceInMetres = currentLocation.distanceTo(meteoriteLocation)
    if (distanceInMetres <= 0f) return null

    val useKilometres = distanceInMetres > SHOW_IN_METERS_BELOW
    val value = if (useKilometres) (distanceInMetres / 1000).roundToInt() else distanceInMetres.roundToInt()
    val unit = if (useKilometres) "km" else "m"
    return "${NumberFormat.getInstance(Locale.getDefault()).format(value)} $unit"
}

/** Mass in grams, grouped for the current locale. */
fun Meteorite.massText(): String? =
    massInGrams?.let { NumberFormat.getInstance(Locale.getDefault()).format(it) }

private const val PROVIDER = "meteorite"
private const val SHOW_IN_METERS_BELOW = 999f
