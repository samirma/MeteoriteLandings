package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import java.util.UUID
import javax.inject.Inject

/**
 * Enqueues the reverse-geocoding worker and returns its id so the caller can observe progress.
 */
class StartAddressRecover @Inject constructor(
    private val workManager: WorkManager,
) {

    operator fun invoke(): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = OneTimeWorkRequestBuilder<AddressRecoverWorker>()
            .setConstraints(constraints)
            .build()

        // KEEP rather than plain enqueue(): pressing the button twice used to stack a second
        // full geocoding pass on top of the one already running.
        workManager.enqueueUniqueWork(
            AddressRecoverWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )
        return request.id
    }
}
