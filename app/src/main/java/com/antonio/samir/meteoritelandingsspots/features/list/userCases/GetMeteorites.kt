package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import androidx.appcompat.app.AppCompatActivity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.GetLocation
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites.Input
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMeteorites @Inject constructor(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteViewMapper,
    private val getLocation: GetLocation,
) : UserCaseBase<Input, PagingData<MeteoriteItemView>>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun action(input: Input): Flow<PagingData<MeteoriteItemView>> =
        getLocation(GetLocation.Input(activity = input.activity)).flatMapLatest {

            val location = (it as? ResultOf.Success)?.data?.location

            val paging = Pager(
                PagingConfig(pageSize = PAGE_SIZE)
            ) {
                meteoriteLocalRepository.meteoriteOrdered(
                    filter = input.query,
                    longitude = location?.longitude,
                    latitude = location?.latitude,
                    limit = LIMIT
                )
            }.flow.map { pagingData ->
                withContext(Dispatchers.Default) {
                    pagingData.map { meteorite ->
                        mapper.map(
                            MeteoriteViewMapper.Input(
                                meteorite = meteorite,
                                location = location
                            )
                        )
                    }
                }
            }
            paging
        }

    data class Input(val query: String?, val activity: AppCompatActivity)

    companion object {
        const val PAGE_SIZE = 20
        const val LIMIT = 1000L
    }

}