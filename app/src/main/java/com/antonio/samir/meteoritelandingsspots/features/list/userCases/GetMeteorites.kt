package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.location.Location
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import kotlinx.coroutines.flow.map

class GetMeteorites(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteViewMapper
) : UserCaseBase<Pair<String?, Location?>, PagingData<MeteoriteItemView>>() {

    override fun action(input: Pair<String?, Location?>) = Pager(
        PagingConfig(pageSize = PAGE_SIZE)
    ) {
        meteoriteLocalRepository.meteoriteOrdered(
            filter = input.first,
            longitude = input.second?.longitude,
            latitude = input.second?.latitude,
            limit = LIMIT
        )
    }
        .flow
        .map { pagingData ->
            pagingData.map { mapper.map(it) }
        }

    companion object {
        const val PAGE_SIZE = 30
        const val LIMIT = 1000L
    }

}