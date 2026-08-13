package com.antonio.samir.meteoritelandingsspots.data.remote.model

import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * NASA's meteorite-landings export.
 *
 * The old Socrata endpoint (`/resource/y77d-th95.json`) now returns 404 — data.nasa.gov moved to
 * CKAN and republished the dataset as a static export at
 * `/docs/legacy/meteorite_landings/Meteorite_Landings.json`.
 *
 * That export is column-oriented: `meta.view.columns` declares the field names and `data` is a
 * list of positional rows. Indices are resolved from the header at parse time rather than
 * hardcoded, so a reordered or extended export keeps working.
 */
@Serializable
data class MeteoriteLandingsResponse(
    @SerialName("meta") val meta: Meta,
    @SerialName("data") val data: List<JsonArray> = emptyList(),
) {

    @Serializable
    data class Meta(@SerialName("view") val view: View)

    @Serializable
    data class View(@SerialName("columns") val columns: List<Column> = emptyList())

    @Serializable
    data class Column(@SerialName("fieldName") val fieldName: String? = null)

    fun toMeteorites(): List<Meteorite> {
        val index = columns()
        val idIndex = index[FIELD_ID] ?: return emptyList()
        return data.mapNotNull { row ->
            val id = row.text(idIndex)?.toIntOrNull() ?: return@mapNotNull null
            Meteorite(
                id = id,
                name = row.text(index[FIELD_NAME]),
                nameType = row.text(index[FIELD_NAME_TYPE]),
                recClass = row.text(index[FIELD_REC_CLASS]),
                fall = row.text(index[FIELD_FALL]),
                massInGrams = row.text(index[FIELD_MASS])?.toDoubleOrNull(),
                year = row.text(index[FIELD_YEAR])?.take(4)?.toIntOrNull(),
                latitude = row.text(index[FIELD_REC_LAT])?.toDoubleOrNull(),
                longitude = row.text(index[FIELD_REC_LONG])?.toDoubleOrNull(),
                // Addresses are reverse-geocoded on device; the feed does not carry them.
                address = null,
            )
        }
    }

    private fun columns(): Map<String, Int> = buildMap {
        meta.view.columns.forEachIndexed { position, column ->
            column.fieldName?.let { put(it, position) }
        }
    }

    private fun JsonArray.text(position: Int?): String? {
        val element: JsonElement = getOrNull(position ?: return null) ?: return null
        val primitive = element as? JsonPrimitive ?: return null
        return primitive.jsonPrimitive.contentOrNull?.takeIf { it.isNotBlank() }
    }

    private companion object {
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_NAME_TYPE = "nametype"
        const val FIELD_REC_CLASS = "recclass"
        const val FIELD_MASS = "mass"
        const val FIELD_FALL = "fall"
        const val FIELD_YEAR = "year"
        const val FIELD_REC_LAT = "reclat"
        const val FIELD_REC_LONG = "reclong"
    }
}
