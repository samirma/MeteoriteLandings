package com.antonio.samir.meteoritelandingsspots.service.address

import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.location.GeocoderDataSource
import com.antonio.samir.meteoritelandingsspots.testing.FakeMeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.testing.meteorite
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddressServiceImplTest {

    private val geocoder = mockk<GeocoderDataSource>()

    // The dispatcher has to share runTest's scheduler, otherwise the pacing delay between
    // geocoder calls runs in real time instead of virtual time and the test hangs.
    private fun TestScope.service(repository: FakeMeteoriteLocalRepository) = AddressServiceImpl(
        meteoriteLocalRepository = repository,
        geocoderDataSource = geocoder,
        ioDispatcher = StandardTestDispatcher(testScheduler),
    )

    @Test
    fun `completes once every meteorite has an address`() = runTest {
        // The regression this guards: the old implementation collected an observable Room query
        // that its own writes re-triggered, so the flow never finished and the worker driving it
        // ran until WorkManager killed it.
        val repository = FakeMeteoriteLocalRepository((1..5).map { meteorite(it) })
        every { geocoder.isAvailable() } returns true
        coEvery { geocoder.addressFor(any(), any()) } returns "Somewhere"

        val results = service(repository).recoveryAddress().toList()

        assertTrue(results.last() is ResultOf.Success)
        assertEquals(0, repository.getMeteoritesWithoutAddressCount())
    }

    @Test
    fun `reports increasing progress`() = runTest {
        val repository = FakeMeteoriteLocalRepository((1..40).map { meteorite(it) })
        every { geocoder.isAvailable() } returns true
        coEvery { geocoder.addressFor(any(), any()) } returns "Somewhere"

        val progress = service(repository).recoveryAddress()
            .toList()
            .filterIsInstance<ResultOf.InProgress>()
            .mapNotNull { it.progress }

        assertTrue("expected progress updates", progress.isNotEmpty())
        assertEquals(progress.sorted(), progress)
    }

    @Test
    fun `fails fast when the device has no geocoder`() = runTest {
        val repository = FakeMeteoriteLocalRepository(listOf(meteorite(1)))
        every { geocoder.isAvailable() } returns false

        val result = service(repository).recoveryAddress().toList().single()

        assertEquals(ResultOf.Error(DataError.GEOCODER_UNAVAILABLE), result)
    }

    @Test
    fun `gives up instead of spinning when the geocoder keeps returning nothing`() = runTest {
        val repository = FakeMeteoriteLocalRepository((1..100).map { meteorite(it) })
        every { geocoder.isAvailable() } returns true
        coEvery { geocoder.addressFor(any(), any()) } returns null

        val results = service(repository).recoveryAddress().toList()

        assertEquals(
            ResultOf.Error(DataError.GEOCODER_UNAVAILABLE),
            results.last(),
        )
    }

    @Test
    fun `skips meteorites that have no coordinates`() = runTest {
        val repository = FakeMeteoriteLocalRepository(
            listOf(meteorite(1, latitude = null, longitude = null))
        )
        every { geocoder.isAvailable() } returns true
        coEvery { geocoder.addressFor(any(), any()) } returns "Somewhere"

        val results = service(repository).recoveryAddress().toList()

        // Nothing resolvable, so it ends rather than looping on the same unresolvable row.
        assertTrue(results.last() is ResultOf.Error)
    }
}
