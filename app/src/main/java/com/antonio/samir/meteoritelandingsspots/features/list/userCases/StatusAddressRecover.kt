package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

@FlowPreview
class StatusAddressRecover(val context: Context) : UserCaseBase<UUID, ResultOf<Float>>() {

    override fun action(input: UUID): Flow<ResultOf.InProgress<Float>> {
        val map = WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(input).asFlow().map { workInfo ->
                ResultOf.InProgress(
                    data = workInfo.progress.getFloat(
                        AddressRecoverWorker.PROGRESS,
                        0.0f
                    )
                )
            }
        return map
    }

}