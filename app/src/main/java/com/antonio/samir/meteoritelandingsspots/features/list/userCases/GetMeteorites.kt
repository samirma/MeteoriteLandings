package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.GetLocation
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites.Input
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@FlowPreview
class GetMeteorites(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val mapper: MeteoriteViewMapper,
    private val getLocation: GetLocation,
) : UserCaseBase<Input, PagingData<MeteoriteItemView>>() {

    override fun action(input: Input) = flow {
        Log.i(TAG, "Searching $input")

        getLocation.execute(GetLocation.Input(input.activity)).collect { resultOf ->
            Log.i(TAG, "resultOf $resultOf")

            val location = when (resultOf) {
                is ResultOf.Success -> resultOf.data.location
                else -> null
            }

            val pagingData: Flow<PagingData<MeteoriteItemView>> = Pager(
                PagingConfig(pageSize = PAGE_SIZE)
            ) {
                meteoriteLocalRepository.meteoriteOrdered(
                    filter = input.query,
                    longitude = location?.longitude,
                    latitude = location?.latitude,
                    limit = LIMIT
                )
            }.flow.map { pagingData ->
                pagingData.map {
                    mapper.map(
                        MeteoriteViewMapper.Input(
                            meteorite = it,
                            location = location
                        )
                    )
                }
            }

            emitAll(pagingData)

        }

    }

    data class Input(val query: String?, val activity: AppCompatActivity)

    companion object {
        private val TAG = GetMeteorites::class.java.simpleName
        const val PAGE_SIZE = 20
        const val LIMIT = 1000L
    }

}