package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NasaNetworkServiceTest {

    private val mockNasaServerEndPoint: NasaServerEndPoint = mockk()

    @Fixture
    private var fixtOffset: Int = 0

    @Fixture
    private val fixtLimit: Int = 5000

    @Fixture
    private lateinit var listMeteorites: List<Meteorite>

    @Before
    fun setUp() {
        FixtureAnnotations.initFixtures(this, JFixture())
    }

    @Test
    fun getMeteorites() = runTest {

        whenever(mockNasaServerEndPoint.publicMeteorites(fixtOffset, fixtLimit))
            .thenReturn(listMeteorites)

        val meteorites =
            NasaNetworkService(mockNasaServerEndPoint).getMeteorites(fixtOffset, fixtLimit)

        assertEquals(listMeteorites, meteorites)

        verify(mockNasaServerEndPoint).publicMeteorites(fixtOffset, fixtLimit)

    }
}