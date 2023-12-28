package com.antonio.samir.meteoritelandingsspots.di

import com.antonio.samir.meteoritelandingsspots.service.address.AddressService
import com.antonio.samir.meteoritelandingsspots.service.address.AddressServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(SingletonComponent::class)
abstract class AddressIModule {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Binds
    abstract fun bindAddressService(impl: AddressServiceImpl): AddressService

}
