package com.antonio.samir.meteoritelandingsspots.data.model

/**
 * Domain model exposed by the repositories.
 *
 * Unlike the Room entity and the network DTO, the values here are already parsed: callers get a
 * year and a mass they can format, not strings they have to re-interpret at every call site.
 */
data class Meteorite(
    val id: Int,
    val name: String?,
    val nameType: String?,
    val recClass: String?,
    val fall: String?,
    val massInGrams: Double?,
    val year: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
)
