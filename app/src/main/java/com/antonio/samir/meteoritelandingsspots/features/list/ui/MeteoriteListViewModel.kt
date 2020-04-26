package com.antonio.samir.meteoritelandingsspots.features.list.ui

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
        private val meteoriteRepository: MeteoriteRepository,
        private val gpsTracker: GPSTrackerInterface,
        private val addressService: AddressServiceInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : ViewModel() {

    private val currentFilter = ConflatedBroadcastChannel<String?>(null)

    var filter = ""

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

    fun getRecoverAddressStatus() = addressService.recoveryAddress().asLiveData()

    fun getMeteorites(): LiveData<PagedList<Meteorite>> = currentFilter.asFlow()
            .flowOn(dispatchers.default())
            .combine(gpsTracker.location) { filter, location ->
                Pair(filter, location)
            }
            .map {
                LivePagedListBuilder(meteoriteRepository.loadMeteorites(it.first, it.second?.longitude, it.second?.latitude), 1000)
            }
            .asLiveData(dispatchers.default())
            .switchMap {
                it.build()
            }

    fun getNetworkLoadingStatus() = meteoriteRepository.loadDatabase().asLiveData()

}
