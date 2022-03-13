package com.antonio.samir.meteoritelandingsspots.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleCoroutineScope
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import io.nodle.sdk.INodle
import io.nodle.sdk.NodleBluetoothScanRecord
import io.nodle.sdk.NodleEvent
import io.nodle.sdk.NodleEventType
import io.nodle.sdk.android.Nodle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MarketingImpl(val context: Context, val nodleKey: String) : MarketingInterface {

    private lateinit var nodle: INodle

    override fun init() {
        Nodle.init(context)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("CheckResult")
    override fun start(lifecycleScope: LifecycleCoroutineScope) {

        RealRxPermission.getInstance(context)
            .requestEach(
                Manifest.permission.INTERNET,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .reduce(true) { c, p -> c && p.state() === Permission.State.GRANTED }
            .subscribe { granted ->
                if (granted) {
                    Log.d(TAG, "all the permissions was granted by user")
                    nodle.start("ss58:4mbWpKrrmE6DY78J6sCFbER2qHiLWtL4UZ7EdTpf6hyimpr7")

                    Log.d(TAG,"Is started ${nodle.isStarted()} ")
                    Log.d(TAG,"Is scanning ${nodle.isScanning()} ")

                } else {
                    Log.d(TAG, "some permission was denied by user")
                }
            }

        collectEvents(lifecycleScope = lifecycleScope)
    
    }

    override fun setNodle(nodle: INodle) {
        this.nodle = nodle
    }

    fun collectEvents(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            val events: SharedFlow<NodleEvent> = nodle.getEvents()
            events.collect { event ->
                // collect the NodleEvents events here by chosing a type
                when (event.type) {
                    NodleEventType.BlePayloadEvent -> handlePayload(event)
                    NodleEventType.BleStartSearching -> Log.d(TAG,"Bluetooth started searching")
                    NodleEventType.BleStopSearching -> Log.d(TAG,"Bluetooth stopped searching")
                }
            }

        }
    }

    fun handlePayload(payload: NodleEvent) {
        val data = payload as NodleBluetoothScanRecord
        Log.d(TAG,"Bluetooth payload available ${data.device} ")
    }

    companion object {
        private val TAG = MarketingImpl::class.java.simpleName
    }
}