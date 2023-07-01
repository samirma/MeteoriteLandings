package com.antonio.samir.meteoritelandingsspots.service.monetization

import android.app.Activity
import androidx.lifecycle.LifecycleCoroutineScope

interface MonetizationInterface {

    fun init()

    fun start(lifecycleScope: LifecycleCoroutineScope, activity: Activity)

}