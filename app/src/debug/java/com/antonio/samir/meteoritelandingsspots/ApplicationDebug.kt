package com.antonio.samir.meteoritelandingsspots

import com.facebook.stetho.Stetho
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ApplicationDebug : Application() {

    override fun onCreate() {
        super.onCreate()

        //chrome://inspect
        Stetho.initializeWithDefaults(this)

    }

}
