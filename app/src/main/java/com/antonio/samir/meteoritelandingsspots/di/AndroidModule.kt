package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

/**
 * Android system services, provided so that everything above can be constructor-injected and
 * substituted in tests instead of reaching for `getSystemService` at the call site.
 */
@Module
@InstallIn(SingletonComponent::class)
object AndroidModule {

    @Singleton
    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        requireNotNull(context.getSystemService()) { "ConnectivityManager is unavailable" }

    @Singleton
    @Provides
    fun provideFusedLocationClient(
        @ApplicationContext context: Context,
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder =
        Geocoder(context, Locale.getDefault())

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}
