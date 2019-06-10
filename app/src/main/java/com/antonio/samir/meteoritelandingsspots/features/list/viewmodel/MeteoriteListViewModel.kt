package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel


import androidx.annotation.StringDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteService
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtilInterface

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
class MeteoriteListViewModel(
        private val meteoriteService: MeteoriteService,
        private val gpsTracker: GPSTrackerInterface,
        private val networkUtil: NetworkUtilInterface
) : ViewModel() {

    var recoveryAddressStatus: MutableLiveData<String>? = null

    val meteorites: MutableLiveData<List<Meteorite>> = MutableLiveData()

    val unableToFetch: MutableLiveData<Boolean> = MutableLiveData()

    val loadingStatus: MutableLiveData<String> = MutableLiveData()

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(DONE, LOADING)
    annotation class DownloadStatus {
        companion object {
            const val DONE = "DONE"
            const val LOADING = "LOADING"
        }
    }

    fun loadMeteorites() {
        val data = meteoriteService.loadMeteorites()

        val meteorites = data.value
        val isEmpty = meteorites == null || meteorites.isEmpty()

        if (isEmpty) {
            loadingStatus.value = "meteoriteLoading"
            data.observeForever { meteorites1 ->
                val isNotEmpty = meteorites1 != null && meteorites1.isNotEmpty()
                if (isNotEmpty) {
                    loadingStatus.value = "meteoriteLoadingStopped"

                }
            }
        }

        unableToFetch.value = networkUtil.hasConnectivity()
    }

    fun updateLocation() {
        gpsTracker.startLocationService()
    }

    fun getMeteorite(meteorite: Meteorite): LiveData<Meteorite>? {

        return meteoriteService.getMeteoriteById(meteorite.id.toString())

    }

}
