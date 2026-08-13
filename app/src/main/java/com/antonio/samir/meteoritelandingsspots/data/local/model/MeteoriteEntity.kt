package com.antonio.samir.meteoritelandingsspots.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room representation of a meteorite landing.
 *
 * Column names are kept as they are in the shipped `meteorites_v2.db` asset. `reclat`/`reclong`
 * are REAL (they were TEXT until schema v2) because the distance ordering does arithmetic on
 * them — SQLite would otherwise coerce every row on every query and sort malformed values as 0.
 */
@Entity(tableName = "meteorites")
data class MeteoriteEntity(
    @PrimaryKey val id: Int,
    val mass: String? = null,
    val nametype: String? = null,
    val recclass: String? = null,
    val name: String? = null,
    val fall: String? = null,
    val year: String? = null,
    val reclong: Double? = null,
    val reclat: Double? = null,
    val address: String? = null,
)

/** Projection used to carry existing addresses across a remote refresh. */
data class MeteoriteAddress(val id: Int, val address: String)

private val YEAR_FORMATS = listOf(
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
)

/**
 * NASA publishes the landing date as an ISO timestamp, with and without milliseconds depending on
 * the row. Only the year is ever displayed, so parse it once here instead of at every render.
 */
internal fun String?.toLandingYear(): Int? {
    val raw = this?.trim().orEmpty()
    if (raw.isEmpty()) return null
    for (format in YEAR_FORMATS) {
        runCatching { return LocalDateTime.parse(raw, format).year }
    }
    // Fall back to a bare year, which some rows use.
    return raw.take(4).toIntOrNull()
}

fun MeteoriteEntity.toDomain() = Meteorite(
    id = id,
    name = name,
    nameType = nametype,
    recClass = recclass,
    fall = fall,
    massInGrams = mass?.toDoubleOrNull(),
    year = year.toLandingYear(),
    latitude = reclat,
    longitude = reclong,
    address = address?.takeIf { it.isNotBlank() },
)
