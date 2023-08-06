package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.location.Location
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites.Input
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@FlowPreview
class GetMeteorites(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteViewMapper,
) : UserCaseBase<Input, PagingData<MeteoriteItemView>>() {

    override fun action(input: Input): Flow<PagingData<MeteoriteItemView>> = Pager(
        PagingConfig(pageSize = PAGE_SIZE)
    ) {
        meteoriteLocalRepository.meteoriteOrdered(
            filter = input.query,
            longitude = input.location?.longitude,
            latitude = input.location?.latitude,
            limit = LIMIT
        )
    }.flow.map { pagingData ->
        return@map withContext(Dispatchers.Default) {
            pagingData.map { meteorite ->
                mapper.map(
                    MeteoriteViewMapper.Input(
                        meteorite = meteorite,
                        location = input.location
                    )
                )
            }
        }
    }

    data class Input(val query: String?, val location: Location?)

    companion object {
        const val PAGE_SIZE = 30
        const val LIMIT = 1000L
    }

}