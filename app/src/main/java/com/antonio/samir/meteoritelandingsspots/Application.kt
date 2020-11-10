package com.antonio.samir.meteoritelandingsspots

import com.antonio.samir.meteoritelandingsspots.di.appModules
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
