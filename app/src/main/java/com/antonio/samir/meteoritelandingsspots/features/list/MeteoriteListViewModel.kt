package com.antonio.samir.meteoritelandingsspots.features.list

import android.location.Location
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel.ContentStatus.*
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
        private val stateHandle: SavedStateHandle,
        private val meteoriteRepository: MeteoriteRepository,
        private val gpsTracker: GPSTrackerInterface,
        private val addressService: AddressServiceInterface,
        private val dispatchers: DispatcherProvider,
) : ViewModel() {

    var filter = ""

    private val currentFilter = ConflatedBroadcastChannel<String?>()

    private val meteorite = ConflatedBroadcastChannel<Meteorite?>(stateHandle[METEORITE])

    val selectedMeteorite = meteorite.asFlow().asLiveData()

    private val contentStatus = MutableLiveData<ContentStatus>(Loading)

    fun getNetworkLoadingStatus() = meteoriteRepository.loadDatabase().asLiveData()

    @VisibleForTesting
    val addressServiceControl = MutableLiveData(false)

    fun getContentStatus(): LiveData<ContentStatus> {
        return contentStatus.distinctUntilChanged().map { status ->
            val shouldResumeAddressService = when (status) {
                Loading -> false
                else -> true
            }
            addressServiceControl.postValue(shouldResumeAddressService)
            return@map status
        }
    }

    fun getRecoverAddressStatus() = Transformations.switchMap(addressServiceControl) {
        if (it) {
            addressService.recoveryAddress().asLiveData()
        } else {
            liveData<Result<Float>> {
                emit(Success(COMPLETED))
            }
        }
    }.distinctUntilChanged()


    fun loadMeteorites(location: String? = null) {

        contentStatus.postValue(Loading)

        location?.let { this.filter = it }

        currentFilter.trySend(location).isSuccess

    }

    fun selectMeteorite(meteorite: Meteorite?) {
        stateHandle[METEORITE] = meteorite
        this.meteorite.trySend(meteorite).isSuccess
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


    fun getMeteorites(): LiveData<PagedList<Meteorite>> {
        return currentFilter.asFlow()
            .flowOn(dispatchers.default())
            .combine<String?, Location?, Pair<String?, Location?>>(gpsTracker.location) { _, location ->
                Pair(this.filter, location)
            }
            .map<Pair<String?, Location?>, LivePagedListBuilder<Int, Meteorite>> {
                LivePagedListBuilder(
                    meteoriteRepository.loadMeteorites(
                        filter = it.first,
                        longitude = it.second?.longitude,
                        latitude = it.second?.latitude,
                        limit = LIMIT
                    ), PAGE_SIZE
                )
            }
            .asLiveData(dispatchers.default())
            .switchMap {
                it.build()
            }
            .map { pagedList ->
                if (pagedList.isEmpty()) {
                    contentStatus.postValue(NoContent)

                } else {
                    contentStatus.postValue(ShowContent)
                }
                return@map pagedList
            }
    }

    fun clearSelectedMeteorite() {
        selectMeteorite(null)
    }

    sealed class ContentStatus {
        object ShowContent : ContentStatus()
        object NoContent : ContentStatus()
        object Loading : ContentStatus()
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
        const val PAGE_SIZE = 1000
        const val LIMIT = 1000L
        const val COMPLETED = 100f
        const val METEORITE = "METEORITE"
    }

}
