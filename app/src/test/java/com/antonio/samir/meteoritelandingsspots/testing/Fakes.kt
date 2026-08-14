package com.antonio.samir.meteoritelandingsspots.testing

import android.location.Location
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.data.connectivity.ConnectivityRepository
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import com.antonio.samir.meteoritelandingsspots.data.location.LocationRepository
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Hand-written fakes rather than mocks, per Google's testing guidance: they exercise the real
 * contract (ordering, filtering, write-then-read) instead of asserting on call sequences.
 */

fun meteorite(
    id: Int,
    name: String = "Meteorite $id",
    year: Int? = 1900 + id,
    latitude: Double? = 0.0,
    longitude: Double? = 0.0,
    address: String? = null,
    massInGrams: Double? = 100.0,
    recClass: String? = "L5",
) = Meteorite(
    id = id,
    name = name,
    nameType = "Valid",
    recClass = recClass,
    fall = "Fell",
    massInGrams = massInGrams,
    year = year,
    latitude = latitude,
    longitude = longitude,
    address = address,
)

class FakeMeteoriteLocalRepository(
    initial: List<Meteorite> = emptyList(),
) : MeteoriteLocalRepository {

    val stored = initial.associateBy { it.id }.toMutableMap()

    /** Records what the last [meteorites] call was asked to order by. */
    var lastQuery: String? = null
    var lastLatitude: Double? = null
    var lastLongitude: Double? = null

    override fun meteorites(
        filter: String?,
        latitude: Double?,
        longitude: Double?,
        limit: Long,
    ): Flow<PagingData<Meteorite>> {
        lastQuery = filter
        lastLatitude = latitude
        lastLongitude = longitude
        val matches = stored.values
            .filter { meteorite ->
                filter.isNullOrBlank() ||
                    meteorite.name.orEmpty().contains(filter, ignoreCase = true) ||
                    meteorite.address.orEmpty().contains(filter, ignoreCase = true)
            }
            .sortedBy { it.name }
            .take(limit.toInt())
        return flowOf(PagingData.from(matches))
    }

    override fun getMeteoriteById(id: Int): Flow<Meteorite?> = flowOf(stored[id])

    override suspend fun meteoritesWithoutAddress(limit: Int): List<Meteorite> =
        stored.values.filter { it.address.isNullOrBlank() }.take(limit)

    override suspend fun updateAddresses(addressById: Map<Int, String?>) {
        addressById.forEach { (id, address) ->
            stored[id]?.let { stored[id] = it.copy(address = address) }
        }
    }

    override suspend fun getValidMeteoritesCount() = stored.size

    override suspend fun getMeteoritesWithoutAddressCount() =
        stored.values.count { it.address.isNullOrBlank() }

    override suspend fun insertAll(meteorites: List<Meteorite>) {
        meteorites.forEach { incoming ->
            // Mirrors the production behaviour of carrying a known address forward.
            val existingAddress = stored[incoming.id]?.address
            stored[incoming.id] = incoming.copy(address = incoming.address ?: existingAddress)
        }
    }
}

class FakeLocationRepository(
    var location: Location? = null,
    private var granted: Boolean = true,
) : LocationRepository {
    override suspend fun currentLocation(): Location? = location.takeIf { granted }
}

class FakeConnectivityRepository(online: Boolean = true) : ConnectivityRepository {
    val onlineState = MutableStateFlow(online)
    override val isOnline: Flow<Boolean> = onlineState
    override fun isCurrentlyOnline(): Boolean = onlineState.value
}

class FakeUIThemeRepository(theme: UITheme = UITheme.SYSTEM) : UIThemeRepository {
    val themeState = MutableStateFlow(theme)
    override fun getTheme(): Flow<UITheme> = themeState
    override suspend fun setTheme(uiTheme: UITheme) {
        themeState.value = uiTheme
    }
}
