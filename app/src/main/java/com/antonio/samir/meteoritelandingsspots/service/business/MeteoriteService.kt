package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.NotNull
import java.util.concurrent.atomic.AtomicBoolean

class MeteoriteService(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val addressService: AddressServiceInterface,
        private val gpsTracker: GPSTrackerInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : MeteoriteServiceInterface {

    private val isUpdateRequired = AtomicBoolean(false)

    private val addressServiceStarted = AtomicBoolean(false)

    override var location: Location? = null

    private val FIRST_WITHOUT_ADDRESS = 30

    private val OLD_DATABASE_COUNT = 1000

    override suspend fun loadMeteorites(filter: String?): DataSource.Factory<Int, Meteorite> = withContext(dispatchers.default()) {

        GlobalScope.launch(dispatchers.unconfined()) {
            val meteoritesCount = meteoriteRepository.getMeteoritesCount()
            if (meteoritesCount < OLD_DATABASE_COUNT && !isUpdateRequired.getAndSet(true)) {
                //If it is empty so load the data from internet
                recoverFromNetwork()
            }
            if (!addressServiceStarted.getAndSet(true)) {
                addressService.recoveryAddress()
            }
        }


//        if (!gpsTracker.isLocationServiceStarted() && gpsTracker.isGPSEnabled()) {
//            updateLocation()
//        }

        return@withContext meteoriteRepository.meteoriteOrdered(location, filter)

    }

    private fun changeMeteoritesSource(source: DataSource.Factory<Int, Meteorite>) {

//        mediatorLiveData.apply {
//            removeSource(meteoritesByName)
//
//            addSource(source) { value ->
//                mediatorLiveData.value = value
//            }
//
//        }
    }

    override fun addressStatus(): MutableLiveData<String> {
        return addressService.status
    }

    override suspend fun requestAddressUpdate(meteorite: Meteorite) {
        addressService.recoverAddress(meteorite)
    }

    /**
     * Select the first meteorites without address and update them
     */
    override fun requestAddressUpdate(@NotNull list: List<Meteorite>) {
        GlobalScope.launch(dispatchers.default()) {
            val firstWithOutAddress = list
                    .filterNotNull()
                    .filter { it.address == null }.take(FIRST_WITHOUT_ADDRESS)
            if (firstWithOutAddress.isNotEmpty()) {
                addressService.recoverAddress(firstWithOutAddress)
            }
        }
    }

    private suspend fun updateLocation() = withContext(dispatchers.main()) {

        gpsTracker.startLocationService()

        val trackerLocation = gpsTracker.location
        trackerLocation.observeForever(object : Observer<Location> {
            override fun onChanged(location: Location?) {
                if (location != null) {

                    this@MeteoriteService.location = location

                    changeMeteoritesSource(meteoriteRepository.meteoriteOrdered(location, null))

                    trackerLocation.removeObserver(this)

                    gpsTracker.stopUpdates()

                }
            }
        })
    }

    private suspend fun recoverFromNetwork() = withContext(dispatchers.default()) {

        val limit = 5000
        var currentPage = 0
        var currentLoaded = -1

        do {
            val remoteMeteorites = meteoriteRepository.getRemoteMeteorites(limit, limit * currentPage)

            val filteredList = remoteMeteorites
                    .filter { it.reclong?.toDoubleOrNull() != null && it.reclat?.toDoubleOrNull() != null && !it.year.isNullOrEmpty() }

            meteoriteRepository.insertAll(filteredList)

            currentLoaded = remoteMeteorites.size

            currentPage++
        } while (currentLoaded == limit)

    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite>? {
        return meteoriteRepository.getMeteoriteById(id)
    }

}
