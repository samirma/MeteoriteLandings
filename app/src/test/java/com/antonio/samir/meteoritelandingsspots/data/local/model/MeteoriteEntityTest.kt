package com.antonio.samir.meteoritelandingsspots.data.local.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MeteoriteEntityTest {

    @Test
    fun `parses the NASA timestamp with milliseconds`() {
        assertEquals(1880, "1880-01-01T00:00:00.000".toLandingYear())
    }

    @Test
    fun `parses the NASA timestamp without milliseconds`() {
        assertEquals(1951, "1951-01-01T00:00:00".toLandingYear())
    }

    @Test
    fun `falls back to a bare year`() {
        assertEquals(2012, "2012".toLandingYear())
    }

    @Test
    fun `returns null for blank or unparseable years`() {
        assertNull(null.toLandingYear())
        assertNull("".toLandingYear())
        assertNull("   ".toLandingYear())
        assertNull("not a date".toLandingYear())
    }

    @Test
    fun `maps to the domain model with parsed numbers`() {
        val domain = MeteoriteEntity(
            id = 1,
            mass = "21",
            nametype = "Valid",
            recclass = "L5",
            name = "Aachen",
            fall = "Fell",
            year = "1880-01-01T00:00:00.000",
            reclong = 6.08333,
            reclat = 50.775,
            address = "Aachen, Germany",
        ).toDomain()

        assertEquals(1, domain.id)
        assertEquals("Aachen", domain.name)
        assertEquals(21.0, domain.massInGrams!!, 0.001)
        assertEquals(1880, domain.year)
        assertEquals(50.775, domain.latitude!!, 0.0001)
        assertEquals("Aachen, Germany", domain.address)
    }

    @Test
    fun `treats a blank address as absent`() {
        // The old geocoder wrote " " on failure, which is not the same as "has an address".
        val domain = MeteoriteEntity(id = 1, address = " ").toDomain()
        assertNull(domain.address)
    }

    @Test
    fun `survives a mass that is not a number`() {
        val domain = MeteoriteEntity(id = 1, mass = "unknown").toDomain()
        assertNull(domain.massInGrams)
    }
}
