package com.antonio.samir.meteoritelandingsspots.features.list.ui


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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

    private var currentFilter = ConflatedBroadcastChannel<String?>(null)

    private var currentPosition = gpsTracker.location

    var recoveryAddressStatus = addressService.recoveryAddress().asLiveData()

    val meteorites: LiveData<PagedList<Meteorite>> = currentFilter.asFlow()
            .combine(currentPosition) { filter, location ->
                Pair(filter, location)
            }
            .map {
                LivePagedListBuilder(meteoriteRepository.loadMeteorites(it.first, it.second?.longitude, it.second?.latitude), 1000)
            }
            .asLiveData()
            .switchMap {
                it.build()
            }

    val networkLoadingStatus = meteoriteRepository.loadDatabase().asLiveData()

    val loadingStatus: MutableLiveData<String> = MutableLiveData()

    var filter = ""

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
        loadMeteorites(null)
    }

    fun loadMeteorites(location: String?) {
        if (location == filter) {
            return
        }

        location?.let { this.filter = it }

        currentFilter.offer(location)

    }

    fun updateLocation() {
        viewModelScope.launch(dispatchers.default()) { gpsTracker.startLocationService() }
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization.asLiveData()
    }

}
