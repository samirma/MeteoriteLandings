package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.service.address.AddressRecoverWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Progress of the [AddressRecoverWorker].
 *
 * Observes the work by its unique name rather than by request id. An id only names the request the
 * caller just built, and [StartAddressRecover] enqueues with `KEEP` — so whenever a pass is already
 * running the new request is discarded and its id never produces a `WorkInfo`. By name, an observer
 * attaches to whichever pass is actually running, including one started before this process.
 *
 * Reads the progress back as the Float the worker writes, and reports terminal states so the UI can
 * stop showing a spinner.
 */
class StatusAddressRecover @Inject constructor(
    private val workManager: WorkManager,
) {

    operator fun invoke(): Flow<ResultOf<Float>> =
        workManager.getWorkInfosForUniqueWorkFlow(AddressRecoverWorker.UNIQUE_WORK_NAME)
            .map { workInfos ->
                // The query returns the unique name's history, so a finished pass can still be in
                // the list while a new one is enqueued. Prefer the live pass; fall back to the most
                // recent terminal state when there is none.
                val workInfo = workInfos.firstOrNull { !it.state.isFinished }
                    ?: workInfos.lastOrNull()
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
