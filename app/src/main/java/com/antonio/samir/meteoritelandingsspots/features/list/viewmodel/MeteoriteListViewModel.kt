package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel


import android.util.Log
import androidx.annotation.StringDef
import androidx.lifecycle.*
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.UNABLE_TO_FETCH
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtilInterface
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Layer responsible for manage the interactions between the activity and the services
 */
class MeteoriteListViewModel(
        private val meteoriteServiceInterface: MeteoriteServiceInterface,
        private val gpsTracker: GPSTrackerInterface,
        private val networkUtil: NetworkUtilInterface
) : ViewModel() {

    var recoveryAddressStatus: LiveData<String> = meteoriteServiceInterface.addressStatus()

    val meteorites: MediatorLiveData<List<Meteorite>> = MediatorLiveData()

    val loadingStatus: MutableLiveData<String> = MutableLiveData()

    val TAG = MeteoriteListViewModel::class.java.simpleName

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
        launchDataLoad {
            val loadMeteorites = meteoriteServiceInterface.loadMeteorites()
            meteorites.addSource(loadMeteorites) { value ->
                meteorites.value = value
            }
        }
    }

    fun updateLocation() {
        gpsTracker.startLocationService()
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization
    }

    fun getMeteorite(meteorite: Meteorite): LiveData<Meteorite>? {

        return meteoriteServiceInterface.getMeteoriteById(meteorite.id.toString())

    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                loadingStatus.value = LOADING
                block()
            } catch (error: Exception) {
                Log.e(TAG, error.message, error)
                loadingStatus.value = UNABLE_TO_FETCH
            } finally {
                loadingStatus.value = DONE
            }
        }
    }
}
