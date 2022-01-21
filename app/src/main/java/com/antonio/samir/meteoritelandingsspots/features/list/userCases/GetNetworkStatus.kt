package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.location.Location
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import kotlinx.coroutines.flow.map

class GetNetworkStatus(
    private val meteoriteRepository: MeteoriteRepository,
) : UserCaseBase<Unit, ResultOf<Unit>>() {

    override fun action(input: Unit) = meteoriteRepository.loadDatabase()

}