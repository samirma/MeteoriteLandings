package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import androidx.room.Room
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.data.local.database.AppDataBase
import com.antonio.samir.meteoritelandingsspots.data.local.database.DATABASE_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// Hilt module for providing the necessary dependencies
@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDataBase::class.java, DATABASE_NAME
    )
        .createFromAsset(DATABASE_NAME)
        .build()
        .meteoriteDao()

}

@Module
@InstallIn(SingletonComponent::class)
abstract class DBRepositoryModule {
    @Binds
    abstract fun bindAppRepository(impl: MeteoriteLocalRepositoryImpl): MeteoriteLocalRepository

}
