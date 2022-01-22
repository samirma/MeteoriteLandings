package com.antonio.samir.meteoritelandingsspots.features.list

import android.location.Location
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import androidx.paging.*
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.Success
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel.ContentStatus.*
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
    private val stateHandle: SavedStateHandle,
    private val fetchMeteoriteList: FetchMeteoriteList,
    private val gpsTracker: GPSTrackerInterface,
    private val addressService: AddressServiceInterface,
    private val dispatchers: DispatcherProvider,
    private val getMeteorites: GetMeteorites,
) : ViewModel() {

    var filter = ""

    private val currentFilter = MutableStateFlow<String?>(null)

    private val meteorite = MutableStateFlow<MeteoriteItemView?>(stateHandle[METEORITE])

    val selectedMeteorite = meteorite.asLiveData()

    private val contentStatus = MutableLiveData<ContentStatus>(Loading)

    fun fetchMeteoriteList() = fetchMeteoriteList.execute(Unit).asLiveData()

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
            liveData<ResultOf<Float>> {
                emit(Success(COMPLETED))
            }
        }
    }.distinctUntilChanged()


    fun loadMeteorites(location: String? = null) {

        contentStatus.postValue(Loading)

        location?.let { this.filter = it }

        currentFilter.value = location

    }

    fun selectMeteorite(meteorite: MeteoriteItemView?) {
        stateHandle[METEORITE] = meteorite
        this.meteorite.value = meteorite
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


    fun getMeteorites(): Flow<PagingData<MeteoriteItemView>> = currentFilter
        .combine<String?, Location?, Pair<String?, Location?>>(gpsTracker.location) { _, location ->
            Pair(this.filter, location)
        }
        .map {
            getMeteorites.execute(it)
        }.flatMapConcat {
            it
        }
        .cachedIn(viewModelScope)

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
        const val COMPLETED = 100f
        const val METEORITE = "METEORITE"
    }

}
