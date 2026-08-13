package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.remote.model.MeteoriteLandingsResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * NASA's export is column-oriented: `meta.view.columns` names the fields and `data` holds
 * positional rows. These tests pin that decoding, including the case where the column order
 * differs from the one we happened to see when writing the parser.
 */
class MeteoriteLandingsResponseTest {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private fun response(columns: List<String>, rows: List<String>) = """
        {
          "meta": { "view": { "columns": [ ${columns.joinToString(",") { """{"fieldName":"$it"}""" }} ] } },
          "data": [ ${rows.joinToString(",")} ]
        }
    """.trimIndent()

    @Test
    fun `decodes a row into the domain model`() {
        val payload = response(
            columns = listOf(":sid", "name", "id", "nametype", "recclass", "mass", "fall", "year", "reclat", "reclong"),
            rows = listOf("""["row-1","Aachen","1","Valid","L5","21","Fell","1880-01-01T00:00:00","50.775000","6.083330"]"""),
        )

        val meteorites = json.decodeFromString<MeteoriteLandingsResponse>(payload).toMeteorites()

        assertEquals(1, meteorites.size)
        with(meteorites.single()) {
            assertEquals(1, id)
            assertEquals("Aachen", name)
            assertEquals("L5", recClass)
            assertEquals(21.0, massInGrams!!, 0.001)
            assertEquals(1880, year)
            assertEquals(50.775, latitude!!, 0.0001)
            assertEquals(6.08333, longitude!!, 0.0001)
            // The feed carries no address; those are geocoded on device.
            assertNull(address)
        }
    }

    @Test
    fun `resolves fields by name rather than fixed position`() {
        val payload = response(
            columns = listOf("id", "reclong", "reclat", "name"),
            rows = listOf("""["7","6.5","50.5","Reordered"]"""),
        )

        val meteorite = json.decodeFromString<MeteoriteLandingsResponse>(payload).toMeteorites().single()

        assertEquals(7, meteorite.id)
        assertEquals("Reordered", meteorite.name)
        assertEquals(50.5, meteorite.latitude!!, 0.0001)
        assertEquals(6.5, meteorite.longitude!!, 0.0001)
    }

    @Test
    fun `skips rows without a usable id`() {
        val payload = response(
            columns = listOf("id", "name"),
            rows = listOf("""[null,"No id"]""", """["not-a-number","Bad id"]""", """["3","Good"]"""),
        )

        val meteorites = json.decodeFromString<MeteoriteLandingsResponse>(payload).toMeteorites()

        assertEquals(listOf("Good"), meteorites.map { it.name })
    }

    @Test
    fun `tolerates rows shorter than the column list`() {
        val payload = response(
            columns = listOf("id", "name", "reclat", "reclong"),
            rows = listOf("""["3","Truncated"]"""),
        )

        val meteorite = json.decodeFromString<MeteoriteLandingsResponse>(payload).toMeteorites().single()

        assertEquals("Truncated", meteorite.name)
        assertNull(meteorite.latitude)
    }

    @Test
    fun `returns nothing when the id column is missing entirely`() {
        val payload = response(columns = listOf("name"), rows = listOf("""["Nameless"]"""))

        assertTrue(json.decodeFromString<MeteoriteLandingsResponse>(payload).toMeteorites().isEmpty())
    }
}
