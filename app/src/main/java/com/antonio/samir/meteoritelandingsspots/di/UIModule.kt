package com.antonio.samir.meteoritelandingsspots.di

import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UIModule {
    @Binds
    abstract fun bindAppRepository(impl: UIThemeRepositoryImpl): UIThemeRepository

}
