package com.antonio.samir.meteoritelandingsspots.service.address

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddressRecoverWorker(
    context: Context,
    parameters: WorkerParameters
) : KoinComponent, CoroutineWorker(context, parameters) {

    companion object {
        const val PROGRESS = "Progress"
    }

    override suspend fun doWork(): Result {

        val addressService: AddressServiceInterface by inject()

        addressService
            .recoveryAddress()
            .collect { result ->
                when (result) {
                    is ResultOf.InProgress -> {
                        setProgress(workDataOf(PROGRESS to result.data))
                    }
                    else -> {}
                }
            }

        return Result.success()
    }
}
