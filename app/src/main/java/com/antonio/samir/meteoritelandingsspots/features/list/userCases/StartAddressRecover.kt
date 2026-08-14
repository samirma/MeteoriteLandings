package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import javax.inject.Inject

/**
 * Enqueues the reverse-geocoding worker.
 *
 * Returns nothing: the work is unique, so [StatusAddressRecover] observes it by name. Handing back
 * this request's id would be misleading — under [ExistingWorkPolicy.KEEP] the id names a request
 * that is discarded whenever a pass is already running.
 */
class StartAddressRecover @Inject constructor(
    private val workManager: WorkManager,
) {

    operator fun invoke() {
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
    }
}
