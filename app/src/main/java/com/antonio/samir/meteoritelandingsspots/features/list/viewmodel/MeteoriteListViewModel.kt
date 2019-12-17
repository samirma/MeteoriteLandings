package com.antonio.samir.meteoritelandingsspots.features.list.viewmodel


import android.location.Location
import android.util.Log
import androidx.annotation.StringDef
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.NO_RESULTS
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel.DownloadStatus.Companion.UNABLE_TO_FETCH
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.launch

/**
 * Layer responsible for manage the interactions between the activity and the services
 */
class MeteoriteListViewModel(
        private val meteoriteService: MeteoriteServiceInterface,
        private val gpsTracker: GPSTrackerInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : ViewModel() {

    var recoveryAddressStatus: LiveData<String> = meteoriteService.addressStatus()

    val meteorites: MediatorLiveData<PagedList<Meteorite>> = MediatorLiveData()

    private var loadMeteoritesCurrent: LiveData<PagedList<Meteorite>>? = null

    val loadingStatus: MutableLiveData<String> = MutableLiveData()

    var filter = ""

    val TAG = MeteoriteListViewModel::class.java.simpleName

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(DONE, LOADING, UNABLE_TO_FETCH, NO_RESULTS)
    annotation class DownloadStatus {
        companion object {
            const val DONE = "DONE"
            const val LOADING = "LOADING"
            const val UNABLE_TO_FETCH = "UNABLE_TO_FETCH"
            const val NO_RESULTS = "NO_RESULTS"
        }
    }

    fun loadMeteorites(location: String?) {
        if (location == filter) {
            return
        }
        viewModelScope.launch(dispatchers.main()) {
            try {
                if (loadingStatus.value != DONE) {
                    loadingStatus.value = LOADING
                }
                updateFilter(location)
            } catch (error: Exception) {
                Log.e(TAG, error.message, error)
                loadingStatus.postValue(UNABLE_TO_FETCH)
            }
        }
    }

    suspend fun updateFilter(filter: String?) {
        filter?.let { this.filter = it }
        loadMeteoritesCurrent?.let {
            meteorites.removeSource(it)
        }

        val loadMeteorites = LivePagedListBuilder<Int, Meteorite>(meteoriteService.loadMeteorites(filter), 1000)
                .build()

        loadMeteoritesCurrent = loadMeteorites

        meteorites.addSource(loadMeteorites) { value ->
            meteorites.value = value
            if (value.isEmpty()) {
                loadingStatus.postValue(NO_RESULTS)
            } else {
                loadingStatus.postValue(DONE)
            }
        }
    }

    fun updateLocation() {
        viewModelScope.launch(dispatchers.default()) { gpsTracker.startLocationService() }
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

}
