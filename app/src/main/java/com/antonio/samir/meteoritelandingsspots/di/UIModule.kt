package com.antonio.samir.meteoritelandingsspots.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provided through the graph rather than via a top-level `Context.dataStore` delegate, so a
     * test can point it at a temporary directory.
     */
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(DATA_STORE_NAME)
    }

    private const val DATA_STORE_NAME = "ui_theme"
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UIModule {

    @Binds
    @Singleton
    abstract fun bindUIThemeRepository(impl: UIThemeRepositoryImpl): UIThemeRepository
}
