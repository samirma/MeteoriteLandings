package com.antonio.samir.meteoritelandingsspots.di

import com.antonio.samir.meteoritelandingsspots.data.location.LocationRepository
import com.antonio.samir.meteoritelandingsspots.data.location.LocationRepositoryImpl
import com.antonio.samir.meteoritelandingsspots.service.address.AddressService
import com.antonio.samir.meteoritelandingsspots.service.address.AddressServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AddressModule {

    @Binds
    @Singleton
    abstract fun bindAddressService(impl: AddressServiceImpl): AddressService

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
