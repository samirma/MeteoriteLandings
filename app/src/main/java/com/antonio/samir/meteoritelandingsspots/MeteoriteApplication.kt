package com.antonio.samir.meteoritelandingsspots

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MeteoriteApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    // Built once rather than on every access. For this to be used at all, the manifest has to
    // remove androidx.work.WorkManagerInitializer — otherwise WorkManager self-initialises with
    // the default factory and cannot construct @HiltWorker workers.
    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()
    }
}
