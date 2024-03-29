package com.antonio.samir.meteoritelandingsspots.service.address

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AddressRecoverWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val addressService: AddressService
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val PROGRESS = "Progress"
    }

    override suspend fun doWork() = withContext(Dispatchers.Default) {

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

        Result.success()
    }
}
