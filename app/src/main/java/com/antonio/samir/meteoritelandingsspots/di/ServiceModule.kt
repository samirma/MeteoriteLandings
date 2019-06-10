package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServerEndPoint
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@Suppress("unused")
object ServiceModule {

    @Provides
    @Reusable
    internal fun providePostApi(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(NasaServerEndPoint.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @JvmStatic
    internal fun provideMeteoriteDao(context: Context): MeteoriteDao {

        return MeteoriteRepositoryFactory.getMeteoriteDao(context)

    }

}