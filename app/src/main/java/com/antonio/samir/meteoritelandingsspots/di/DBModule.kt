package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import androidx.room.Room
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.data.local.database.AppDataBase
import com.antonio.samir.meteoritelandingsspots.data.local.database.DATABASE_NAME
import com.antonio.samir.meteoritelandingsspots.data.local.database.MIGRATION_1_2
import com.antonio.samir.meteoritelandingsspots.data.local.database.MeteoriteDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    /**
     * The database instance itself is exposed (rather than only the DAO) so tests and future
     * callers can run transactions or close it.
     */
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME)
            .createFromAsset(DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()

    @Singleton
    @Provides
    fun provideMeteoriteDao(database: AppDataBase): MeteoriteDao = database.meteoriteDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DBRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMeteoriteLocalRepository(
        impl: MeteoriteLocalRepositoryImpl,
    ): MeteoriteLocalRepository
}
