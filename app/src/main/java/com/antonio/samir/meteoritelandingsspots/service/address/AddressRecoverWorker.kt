package com.antonio.samir.meteoritelandingsspots.service.address

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch

@HiltWorker
class AddressRecoverWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val addressService: AddressService,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        var failure: ResultOf.Error? = null

        addressService.recoveryAddress()
            .catch { throwable ->
                Log.e(TAG, "Address recovery failed", throwable)
                failure = ResultOf.Error(com.antonio.samir.meteoritelandingsspots.common.DataError.UNKNOWN, throwable)
            }
            .collect { result ->
                when (result) {
                    // PROGRESS is read back as a Float; writing an Int here (as the previous
                    // version effectively did) made every observer see 0.
                    is ResultOf.InProgress -> setProgress(
                        workDataOf(PROGRESS to (result.progress ?: 0f))
                    )

                    is ResultOf.Success -> setProgress(workDataOf(PROGRESS to result.data))
                    is ResultOf.Error -> failure = result
                }
            }

        return if (failure == null) {
            Result.success()
        } else {
            // Geocoding failures are usually throttling or a missing backend: worth another go
            // later, but not forever.
            if (runAttemptCount < MAX_ATTEMPTS) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val PROGRESS = "Progress"
        const val UNIQUE_WORK_NAME = "address-recovery"
        private const val MAX_ATTEMPTS = 3
        private val TAG: String = AddressRecoverWorker::class.java.simpleName
    }
}
