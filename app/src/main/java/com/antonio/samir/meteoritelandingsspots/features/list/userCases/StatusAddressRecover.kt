package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class StatusAddressRecover @Inject constructor(@ApplicationContext val context: Context) :
    UserCaseBase<UUID, ResultOf<Float>>() {

    override fun action(input: UUID): Flow<ResultOf.InProgress<Float>> {
        return WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(input).asFlow().map { workInfo ->
                ResultOf.InProgress(
                    data = workInfo.progress.getFloat(
                        AddressRecoverWorker.PROGRESS,
                        0.0f
                    )
                )
            }
    }

}