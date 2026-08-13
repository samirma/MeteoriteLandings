package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Progress of an enqueued [AddressRecoverWorker].
 *
 * Uses `getWorkInfoByIdFlow` rather than the deprecated LiveData bridge, reads the progress back
 * as the Float the worker actually writes, and reports terminal states so the UI can stop showing
 * a spinner.
 */
class StatusAddressRecover @Inject constructor(
    private val workManager: WorkManager,
) {

    operator fun invoke(id: UUID): Flow<ResultOf<Float>> =
        workManager.getWorkInfoByIdFlow(id).map { workInfo ->
            val progress = workInfo?.progress?.getFloat(AddressRecoverWorker.PROGRESS, 0f) ?: 0f
            when (workInfo?.state) {
                WorkInfo.State.SUCCEEDED -> ResultOf.Success(COMPLETE)
                WorkInfo.State.FAILED, WorkInfo.State.CANCELLED ->
                    ResultOf.Error(DataError.GEOCODER_UNAVAILABLE)

                else -> ResultOf.InProgress(progress)
            }
        }

    private companion object {
        const val COMPLETE = 100f
    }
}
