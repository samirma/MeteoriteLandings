package com.antonio.samir.meteoritelandingsspots.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.data.local.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.data.local.model.MeteoriteEntity
import com.antonio.samir.meteoritelandingsspots.data.local.model.toDomain
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.pow

@Singleton
class MeteoriteLocalRepositoryImpl @Inject constructor(
    private val meteoriteDao: MeteoriteDao,
) : MeteoriteLocalRepository {

    override fun meteorites(
        filter: String?,
        latitude: Double?,
        longitude: Double?,
        limit: Long,
    ): Flow<PagingData<Meteorite>> {
        val query = prepareFilter(filter)
        return Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
            if (latitude == null || longitude == null) {
                meteoriteDao.meteoriteFiltered(filter = query, limit = limit)
            } else {
                meteoriteDao.meteoriteOrderedByLocationFiltered(
                    lat = latitude,
                    lng = longitude,
                    // Longitude degrees shrink towards the poles; squared because the ordering
                    // compares squared distances.
                    lngScale = cos(Math.toRadians(latitude)).pow(2),
                    filter = query,
                    limit = limit,
                )
            }
        }.flow.map { pagingData -> pagingData.map(MeteoriteEntity::toDomain) }
    }

    override fun getMeteoriteById(id: Int): Flow<Meteorite?> =
        meteoriteDao.getMeteoriteById(id).map { it?.toDomain() }

    override suspend fun meteoritesWithoutAddress(limit: Int): List<Meteorite> =
        meteoriteDao.meteoritesWithoutAddress(limit).map(MeteoriteEntity::toDomain)

    override suspend fun updateAddresses(addressById: Map<Int, String?>) =
        meteoriteDao.updateAddresses(addressById)

    override suspend fun getValidMeteoritesCount() = meteoriteDao.getValidMeteoritesCount()

    override suspend fun getMeteoritesWithoutAddressCount() =
        meteoriteDao.getMeteoritesWithoutAddressCount()

    override suspend fun getMeteoritesCount() = meteoriteDao.getMeteoritesCount()

    override suspend fun insertAll(meteorites: List<Meteorite>) =
        meteoriteDao.refresh(meteorites.map(Meteorite::toEntity))

    /**
     * The stored `name`/`address` columns are lowercased with SQLite's ASCII `LOWER()`, so the
     * filter has to be lowercased the same way. Using the default locale here would break search
     * for Turkish users, where `I`.lowercase() is `ı`.
     */
    private fun prepareFilter(filter: String?) = filter?.lowercase(Locale.ROOT).orEmpty()

    private companion object {
        const val PAGE_SIZE = 30
    }
}

private fun Meteorite.toEntity() = MeteoriteEntity(
    id = id,
    mass = massInGrams?.toString(),
    nametype = nameType,
    recclass = recClass,
    name = name,
    fall = fall,
    year = year?.toString(),
    reclong = longitude,
    reclat = latitude,
    address = address,
)
