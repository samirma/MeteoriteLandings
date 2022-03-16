package com.antonio.samir.meteoritelandingsspots.service

import android.content.Context
import androidx.work.*
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import kotlinx.coroutines.flow.onEach

class AddressRecoverWorker(
    val addressService: AddressServiceInterface,
    context: Context,
    parameters: WorkerParameters
) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val PROGRESS = "Progress"
    }

    override suspend fun doWork(): Result {

        addressService
            .recoveryAddress()
            .onEach { result ->
                when (result) {
                    is ResultOf.InProgress -> {
                        setProgress(workDataOf("Progress" to result.data))
                    }
                    is ResultOf.Error -> {}
                    is ResultOf.Success -> {}
                }
            }

        return Result.success()
    }
}
