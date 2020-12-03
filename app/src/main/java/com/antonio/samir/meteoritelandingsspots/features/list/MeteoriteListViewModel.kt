package com.antonio.samir.meteoritelandingsspots.features.list

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
        private val handle: SavedStateHandle,
        private val meteoriteRepository: MeteoriteRepository,
        private val gpsTracker: GPSTrackerInterface,
        private val addressService: AddressServiceInterface,
        private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val currentFilter = ConflatedBroadcastChannel<String?>(null)

    private val meteorite = ConflatedBroadcastChannel(handle.get<Meteorite>(METEORITE))

    val selectedMeteorite = meteorite.asFlow().asLiveData()

    var filter = ""

    init {
        Log.i(TAG, "Created $this ${meteorite.value?.id} $handle")
    }

    fun loadMeteorites(location: String? = null) {
        if (location == filter) {
            return
        }

        location?.let { this.filter = it }

        currentFilter.offer(location)

    }

    fun selectMeteorite(meteorite: Meteorite) {
        handle[METEORITE] = meteorite
        this.meteorite.offer(meteorite)
    }

    fun updateLocation() {
        viewModelScope.launch(dispatchers.default()) {
            gpsTracker.startLocationService()
        }
    }

    fun isAuthorizationRequested(): LiveData<Boolean> {
        return gpsTracker.needAuthorization.asLiveData()
    }

    fun getLocation(): LiveData<Location?> {
        return gpsTracker.location.asLiveData()
    }

    fun getRecoverAddressStatus() = addressService.recoveryAddress().asLiveData()

    fun getMeteorites(): LiveData<PagedList<Meteorite>> = currentFilter.asFlow()
            .flowOn(dispatchers.default())
            .combine(gpsTracker.location) { filter, location ->
                Pair(filter, location)
            }
            .map {
                LivePagedListBuilder(meteoriteRepository.loadMeteorites(it.first, it.second?.longitude, it.second?.latitude), 30)
            }
            .asLiveData(dispatchers.default())
            .switchMap {
                it.build()
            }

    fun getNetworkLoadingStatus() = meteoriteRepository.loadDatabase().asLiveData()

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
        const val METEORITE = "METEORITE"
    }

}
