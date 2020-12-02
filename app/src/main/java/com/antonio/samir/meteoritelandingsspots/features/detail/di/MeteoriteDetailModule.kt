package com.antonio.samir.meteoritelandingsspots.features.detail.di

import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val meteoriteDetailModule = module {
    viewModel { MeteoriteDetailViewModel(get(), get()) }
}