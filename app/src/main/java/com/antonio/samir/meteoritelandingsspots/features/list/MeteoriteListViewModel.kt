package com.antonio.samir.meteoritelandingsspots.features.list

import android.location.Location
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
        private val meteoriteRepository: MeteoriteRepository,
        private val gpsTracker: GPSTrackerInterface,
        private val addressService: AddressServiceInterface,
        private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val currentFilter = ConflatedBroadcastChannel<String?>()

    private val meteorite = ConflatedBroadcastChannel<Meteorite?>()

    val selectedMeteorite = meteorite.asFlow().asLiveData()

    val loadedDetail = MutableLiveData<Pair<String?, Location?>>()

    var filter = ""

    fun loadMeteorites(location: String? = null) {
        if (location == filter) {
            return
        }

        location?.let { this.filter = it }

        currentFilter.offer(location)

    }

    fun selectMeteorite(meteorite: Meteorite) {
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

    fun getMeteorites(loadedDetail: Pair<String?, Location?>?): LiveData<PagedList<Meteorite>> {
        return currentFilter.asFlow()
                .flowOn(dispatchers.default())
                .combine<String?, Location?, Pair<String?, Location?>>(gpsTracker.location) { filter, location ->
                    Pair(this.filter, location)
                }
                .filter {
                    !Objects.equals(it, loadedDetail)
                }
                .onEach { this.loadedDetail.postValue(it) }
                .map<Pair<String?, Location?>, LivePagedListBuilder<Int, Meteorite>> {
                    LivePagedListBuilder(meteoriteRepository.loadMeteorites(it.first, it.second?.longitude, it.second?.latitude), 30)
                }
                .asLiveData(dispatchers.default())
                .switchMap {
                    it.build()
                }
    }

    fun getNetworkLoadingStatus() = meteoriteRepository.loadDatabase().asLiveData()

}
