package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel


import android.location.Location
import android.util.Log
import androidx.annotation.StringDef
import androidx.lifecycle.*
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.UNABLE_TO_FETCH
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Layer responsible for manage the interactions between the activity and the services
 */
class MeteoriteListViewModel(
        private val meteoriteService: MeteoriteServiceInterface,
        private val gpsTracker: GPSTrackerInterface,
        private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    var recoveryAddressStatus: LiveData<String> = meteoriteService.addressStatus()

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

    fun loadMeteorites(location: String?) {
        launchDataLoad {
            val loadMeteorites = meteoriteService.loadMeteorites(location)
            meteorites.removeSource(loadMeteorites)
            meteorites.addSource(loadMeteorites) { value ->
                meteorites.value = value
            }
        }
    }

    fun updateLocation() {
        viewModelScope.launch(defaultDispatcher) { gpsTracker.startLocationService() }
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization
    }

    fun getMeteorite(meteorite: Meteorite): LiveData<Meteorite>? {

        return meteoriteService.getMeteoriteById(meteorite.id.toString())

    }

    fun getLocation(): Location? {
        return meteoriteService.location
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch(defaultDispatcher) {
            try {
                if (loadingStatus.value != DONE) {
                    loadingStatus.value = LOADING
                }
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
