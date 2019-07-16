package com.antonio.samir.meteoritelandingsspots.features

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

fun Meteorite.getDistanceFrom(latitude: Double, longitude: Double): String {
    return "${betweenLatlong(latitude, longitude, reclat!!.toDouble(), reclong!!.toDouble())} km from you"
}

private fun betweenLatlong(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Long {

    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val earthRadius = 6371.01 //Kilometers
    return (earthRadius * acos(sin(lat1Rad) * sin(lat2Rad) + cos(lat1Rad) * cos(lat2Rad) * cos(lon1Rad - lon2Rad))).roundToLong()
}