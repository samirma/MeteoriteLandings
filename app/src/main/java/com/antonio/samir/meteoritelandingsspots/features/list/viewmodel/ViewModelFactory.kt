package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteService
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtilInterface

/**
 * Factory for ViewModels
 */
class ViewModelFactory(private val meteoriteService: MeteoriteService,
                       private val gpsTracker: GPSTrackerInterface,
                       private val networkUtil: NetworkUtilInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeteoriteListViewModel::class.java)) {
            return MeteoriteListViewModel(meteoriteService, gpsTracker, networkUtil) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}