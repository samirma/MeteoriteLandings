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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    var currentJob: Job? = null

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

    fun loadMeteorites() {
        loadList(null, null)
    }

    fun loadMeteorites(location: String?) {
        if (location == filter) {
            return
        }

        location?.let { this.filter = it }

        val emptyStatus = NO_RESULTS

        loadList(location, emptyStatus)

    }

    private fun loadList(location: String?, emptyStatus: String?) {
        loadMeteoritesCurrent?.let {
            meteorites.removeSource(it)
        }

        currentJob?.let {
            Log.v(TAG, "isActive: ${it.isActive} isCompleted: ${it.isCompleted}")
            if (!it.isCompleted) {
                it.cancel()
            }
        }

        val launch = viewModelScope.launch(dispatchers.default()) {
            try {
                loadingStatus.postValue(LOADING)
                updateFilter(location, emptyStatus)
            } catch (error: Exception) {
                Log.e(TAG, error.message, error)
                loadingStatus.postValue(UNABLE_TO_FETCH)
            }
        }

        currentJob = launch
    }

    private suspend fun updateFilter(filter: String?, emptyStatus: String?) = withContext(dispatchers.main()) {

        val dataSourceFactory = meteoriteService.loadMeteorites(filter?.trim())

        val loadMeteorites = LivePagedListBuilder<Int, Meteorite>(dataSourceFactory, 1000)
                .build()

        loadMeteoritesCurrent = loadMeteorites

        meteorites.addSource(loadMeteorites) { value ->
            meteorites.value = value
            if (value.isEmpty()) {
                emptyStatus?.let(loadingStatus::postValue)
            } else {
                if (filter != null && filter.isNotBlank()) { meteoriteService.requestAddressUpdate(value) }
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
