package com.antonio.samir.meteoritelandingsspots.di

import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServerEndPoint
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@Suppress("unused")
object NetworkModule {

    @Provides
    @Reusable
    internal fun providePostApi(): Retrofit {
        val build: Retrofit = Retrofit.Builder()
                .baseUrl(NasaServerEndPoint.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return build
    }

    @Provides
    @JvmStatic
    internal fun provideRetrofitInterface(retrofit: Retrofit): NasaServerEndPoint? {

        return retrofit.create(NasaServerEndPoint::class.java)

    }
}