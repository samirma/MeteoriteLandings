package com.antonio.samir.meteoritelandingsspots.service.monetization

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import com.antonio.samir.meteoritelandingsspots.BuildConfig
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.RequestPermission
import io.nodle.sdk.INodle
import io.nodle.sdk.NodleBluetoothScanRecord
import io.nodle.sdk.NodleEvent
import io.nodle.sdk.NodleEventType
import io.nodle.sdk.android.Nodle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MonetizationImpl(
    val context: Context,
    val nodleKey: String,
    val requestPermission: RequestPermission
) : MonetizationInterface {

    private lateinit var nodle: INodle

    override fun init() {
        Nodle.init(context)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("CheckResult")
    override fun start(lifecycleScope: LifecycleCoroutineScope, activity: AppCompatActivity) {

        if (BuildConfig.DEBUG) {

            lifecycleScope.launch(Dispatchers.IO) {
                requestPermission(
                    RequestPermission.Input(
                        activity = activity,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                    .collect {
                        when (it) {
                            is ResultOf.Success -> {
                                if (it.data) {
                                    Log.d(TAG, "all the permissions was granted by user")
                                    nodle.start(nodleKey)

                                    Log.d(TAG, "Is started ${nodle.isStarted()} ")
                                    Log.d(TAG, "Is scanning ${nodle.isScanning()} ")
                                }
                            }
                            else -> Log.d(TAG, "Some permission was denied by user")
                        }

                    }
            }

            collectEvents(lifecycleScope = lifecycleScope)

        }

    }

    override fun setNodle(nodle: INodle) {
        this.nodle = nodle
    }

    private fun collectEvents(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            val events: SharedFlow<NodleEvent> = nodle.getEvents()
            events.collect { event ->
                // collect the NodleEvents events here by chosing a type
                when (event.type) {
                    NodleEventType.BlePayloadEvent -> handlePayload(event)
                    NodleEventType.BleStartSearching -> Log.d(TAG, "Bluetooth started searching")
                    NodleEventType.BleStopSearching -> Log.d(TAG, "Bluetooth stopped searching")
                }
            }

        }
    }

    private fun handlePayload(payload: NodleEvent) {
        val data = payload as NodleBluetoothScanRecord
        Log.d(TAG, "Bluetooth payload available ${data.device} ")
    }

    companion object {
        private val TAG = MonetizationImpl::class.java.simpleName
    }
}