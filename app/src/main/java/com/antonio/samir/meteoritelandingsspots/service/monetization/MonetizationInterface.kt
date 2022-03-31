package com.antonio.samir.meteoritelandingsspots.service.monetization

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import io.nodle.sdk.INodle

interface MonetizationInterface {

    fun init()

    fun start(lifecycleScope: LifecycleCoroutineScope, activity: AppCompatActivity)

    fun setNodle(nodle: INodle)

}