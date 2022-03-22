package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.location.Location
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

@FlowPreview
class GetMeteorites(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteViewMapper,
    private val gpsTracker: GPSTrackerInterface,
) : UserCaseBase<String?, PagingData<MeteoriteItemView>>() {

    override fun action(input: String?): Flow<PagingData<MeteoriteItemView>> {
        Log.i(TAG, "Searching $input")
        var location: Location? = null
        return gpsTracker.location.flatMapConcat {
            Log.i(TAG, "Location $it")
            location = it
            Pager(
                PagingConfig(pageSize = PAGE_SIZE)
            ) {
                meteoriteLocalRepository.meteoriteOrdered(
                    filter = input,
                    longitude = location?.longitude,
                    latitude = location?.latitude,
                    limit = LIMIT
                )
            }.flow
        }
            .map { pagingData ->
                pagingData.map {
                    mapper.map(
                        MeteoriteViewMapper.Input(
                            meteorite = it,
                            location = location
                        )
                    )
                }
            }
    }

    companion object {
        private val TAG = GetMeteorites::class.java.simpleName
        const val PAGE_SIZE = 20
        const val LIMIT = 1000L
    }

}