package com.antonio.samir.meteoritelandingsspots.features.list.ui


import android.util.Log
import androidx.annotation.StringDef
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.list.ui.MeteoriteListViewModel.DownloadStatus.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.features.list.ui.MeteoriteListViewModel.DownloadStatus.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.features.list.ui.MeteoriteListViewModel.DownloadStatus.Companion.NO_RESULTS
import com.antonio.samir.meteoritelandingsspots.features.list.ui.MeteoriteListViewModel.DownloadStatus.Companion.UNABLE_TO_FETCH
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
        private val meteoriteRepository: MeteoriteRepository,
        private val gpsTracker: GPSTrackerInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider(),
        addressService: AddressServiceInterface
) : ViewModel() {

    val location = gpsTracker.location

    private var currentFilter = ConflatedBroadcastChannel<String>()

    var recoveryAddressStatus = addressService.recoveryAddress().asLiveData()

    val meteorites: LiveData<PagedList<Meteorite>> = currentFilter.asFlow()
            .map {
                LivePagedListBuilder(meteoriteRepository.loadMeteorites(filter.trim()), 1000)
            }
            .asLiveData()
            .switchMap {
                it.build()
            }

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
                if (error.message == "Job was cancelled") {
                    Log.v(TAG, error.message)
                } else {
                    Log.e(TAG, error.message, error)
                    loadingStatus.postValue(UNABLE_TO_FETCH)
                }
            }
        }

        currentJob = launch
    }

    private suspend fun updateFilter(filter: String?, emptyStatus: String?) = withContext(dispatchers.main()) {
        filter?.let { currentFilter::offer }
    }

    fun updateLocation() {
        viewModelScope.launch(dispatchers.default()) { gpsTracker.startLocationService() }
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization.asLiveData()
    }

}
