package com.antonio.samir.meteoritelandingsspots.di

import com.antonio.samir.meteoritelandingsspots.BuildConfig
import com.antonio.samir.meteoritelandingsspots.data.connectivity.ConnectivityRepository
import com.antonio.samir.meteoritelandingsspots.data.connectivity.ConnectivityRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.data.remote.MeteoriteRemoteRepository
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaNetworkService
import com.antonio.samir.meteoritelandingsspots.data.remote.NasaServerEndPoint
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        // NASA's export carries far more metadata than the app models.
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .apply {
            // Request/response logging must never ship in a release build.
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                )
            }
        }
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    fun provideNasaServerEndPoint(client: OkHttpClient, json: Json): NasaServerEndPoint =
        Retrofit.Builder()
            .baseUrl(NasaServerEndPoint.URL)
            .addConverterFactory(json.asConverterFactory(APPLICATION_JSON.toMediaType()))
            .client(client)
            .build()
            .create(NasaServerEndPoint::class.java)

    private const val APPLICATION_JSON = "application/json"
    private const val CONNECT_TIMEOUT_SECONDS = 30L

    // The landings export is a single ~12 MB document, and data.nasa.gov's origin is erratic:
    // the same file has been observed returning in 3s and in 77s, with some requests stalling
    // past 60s entirely. Size is not the bottleneck, the origin is — hence the wide window.
    private const val READ_TIMEOUT_SECONDS = 120L
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMeteoriteRemoteRepository(
        impl: NasaNetworkService,
    ): MeteoriteRemoteRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityRepository(
        impl: ConnectivityRepositoryImpl,
    ): ConnectivityRepository
}
