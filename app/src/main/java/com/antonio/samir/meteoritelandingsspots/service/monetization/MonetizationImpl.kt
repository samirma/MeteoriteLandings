package com.antonio.samir.meteoritelandingsspots.service.monetization

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope

class MonetizationImpl(
    val context: Context,
) : MonetizationInterface {

    override fun init() {

    }

    override fun start(lifecycleScope: LifecycleCoroutineScope, activity: Activity) {

    }

    companion object {
        private val TAG = MonetizationImpl::class.java.simpleName
    }
}