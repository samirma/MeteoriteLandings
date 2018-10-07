package com.antonio.samir.meteoritelandingsspots

import com.facebook.stetho.Stetho

class ApplicationDebug : com.antonio.samir.meteoritelandingsspots.Application() {

    override fun onCreate() {
        super.onCreate()

        //chrome://inspect
        Stetho.initializeWithDefaults(this)

    }

}
