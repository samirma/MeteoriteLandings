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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MonetizationImpl(
    val context: Context,
) : MonetizationInterface {

    override fun init() {

    }

    override fun start(lifecycleScope: LifecycleCoroutineScope, activity: AppCompatActivity) {

    }

    companion object {
        private val TAG = MonetizationImpl::class.java.simpleName
    }
}