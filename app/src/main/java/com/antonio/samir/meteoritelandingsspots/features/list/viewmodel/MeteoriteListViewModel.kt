package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel


import androidx.annotation.StringDef
import androidx.lifecycle.*
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.UNABLE_TO_FETCH
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteService
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtilInterface
import kotlinx.coroutines.launch

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
class MeteoriteListViewModel(
        private val meteoriteService: MeteoriteService,
        private val gpsTracker: GPSTrackerInterface,
        private val networkUtil: NetworkUtilInterface
) : ViewModel() {

    var recoveryAddressStatus: MediatorLiveData<String> = MediatorLiveData()

    val meteorites: MediatorLiveData<List<Meteorite>> = MediatorLiveData()

    val loadingStatus: MutableLiveData<String> = MutableLiveData()

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(DONE, LOADING, UNABLE_TO_FETCH)
    annotation class DownloadStatus {
        companion object {
            const val DONE = "DONE"
            const val LOADING = "LOADING"
            const val UNABLE_TO_FETCH = "UNABLE_TO_FETCH"
        }
    }

    fun loadMeteorites() {

        loadingStatus.value = LOADING

        viewModelScope.launch {
            meteorites.addSource(meteoriteService.loadMeteorites()) { value ->
                val isNotEmpty = value.isNotEmpty()
                when {
                    isNotEmpty -> loadingStatus.value = DONE
                    !networkUtil.hasConnectivity() -> loadingStatus.value = UNABLE_TO_FETCH
                }
                meteorites.value = value
            }
        }

        recoveryAddressStatus.addSource(meteoriteService.addressStatus()) {
            recoveryAddressStatus.value = it
        }

    }

    fun updateLocation() {
        gpsTracker.startLocationService()
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization
    }

    fun getMeteorite(meteorite: Meteorite): LiveData<Meteorite>? {

        return meteoriteService.getMeteoriteById(meteorite.id.toString())

    }

}
