package com.antonio.samir.meteoritelandingsspots.features.list.di

import androidx.lifecycle.SavedStateHandle
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val meteoriteListModule = module {

    viewModel { (handle: SavedStateHandle) ->
        MeteoriteListViewModel(
                meteoriteRepository = get(),
                gpsTracker = get(),
                addressService = get(),
                dispatchers = get(),
                state = handle
        )
    }
}