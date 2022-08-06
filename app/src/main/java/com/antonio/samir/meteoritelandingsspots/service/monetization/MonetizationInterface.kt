package com.antonio.samir.meteoritelandingsspots.service.monetization

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope

interface MonetizationInterface {

    fun init()

    fun start(lifecycleScope: LifecycleCoroutineScope, activity: AppCompatActivity)

}