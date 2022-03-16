package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.mapper.MeteoriteViewMapper
import com.antonio.samir.meteoritelandingsspots.service.AddressRecoverWorker
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.util.*

@FlowPreview
class StatusAddressRecover(val context : Context) : UserCaseBase<UUID, ResultOf<Float>>() {

    override fun action(input: UUID) = WorkManager.getInstance(context)
        .getWorkInfoByIdLiveData(input).asFlow().map { workInfo ->
            ResultOf.InProgress(data = workInfo.progress.getFloat(AddressRecoverWorker.PROGRESS, 0.0f))
        }

}