package com.antonio.samir.meteoritelandingsspots.di

import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UIModule {
    @Binds
    abstract fun bindUIThemeRepository(impl: UIThemeRepositoryImpl): UIThemeRepository

}
