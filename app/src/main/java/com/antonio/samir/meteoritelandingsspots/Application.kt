package com.antonio.samir.meteoritelandingsspots

import appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


open class Application : android.app.Application() {


    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // declare used Android context
            androidContext(this@Application)
            // declare modules
            modules(appModules)
        }
    }

}
