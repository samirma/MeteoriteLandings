package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.location.Location
import androidx.paging.PagingData
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * The meteorite list, ordered nearest-first when [location] is known and alphabetically otherwise.
 *
 * The location is passed in rather than resolved here: the ViewModel already holds it in order to
 * render distances, and fetching it twice would mean the ordering and the distance labels could
 * disagree.
 *
 * Returns domain models; turning them into view models is the UI layer's job.
 */
class GetMeteorites @Inject constructor(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
) {

    operator fun invoke(query: String, location: Location?): Flow<PagingData<Meteorite>> =
        meteoriteLocalRepository.meteorites(
            filter = query,
            latitude = location?.latitude,
            longitude = location?.longitude,
        )
}
