package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class StartAddressRecover @Inject constructor(val context: Context) : UserCaseBase<Unit, UUID>() {

    override fun action(input: Unit) = flow {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request: WorkRequest =
            OneTimeWorkRequestBuilder<AddressRecoverWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueue(request)

        val uuid = request.id

        emit(uuid)
    }


}