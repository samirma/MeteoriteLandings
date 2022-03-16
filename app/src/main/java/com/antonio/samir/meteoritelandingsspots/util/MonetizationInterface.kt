package com.antonio.samir.meteoritelandingsspots.util

import androidx.lifecycle.LifecycleCoroutineScope
import io.nodle.sdk.INodle

interface MonetizationInterface {

    fun init()

    fun start(lifecycleScope: LifecycleCoroutineScope)

    fun setNodle(nodle: INodle)

}