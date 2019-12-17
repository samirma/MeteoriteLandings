package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import java.util.concurrent.atomic.AtomicBoolean

class MeteoriteNasaService(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val addressService: AddressServiceInterface,
        private val gpsTracker: GPSTrackerInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : MeteoriteServiceInterface {

    private val isUpdateRequired = AtomicBoolean(false)

    private val addressServiceStarted = AtomicBoolean(false)

    private var mediatorLiveData: MediatorLiveData<List<Meteorite>> = MediatorLiveData()

    override var location: Location? = null

    private val meteoritesByName = meteoriteRepository.meteoriteOrdered(null, null)

    override suspend fun loadMeteorites(filter: String?): DataSource.Factory<Int, Meteorite> = withContext(dispatchers.default()) {

        GlobalScope.launch(dispatchers.unconfined()) {
            if (meteoriteRepository.getMeteoritesCount() < 100 && !isUpdateRequired.getAndSet(true)) {
                //If it is empty so load the data from internet
                recoverFromNetwork()
            }
        }

        if (!addressServiceStarted.getAndSet(true)) {
            addressService.recoveryAddress()
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

    private suspend fun updateLocation() = withContext(dispatchers.main()) {

        gpsTracker.startLocationService()

        val trackerLocation = gpsTracker.location
        trackerLocation.observeForever(object : Observer<Location> {
            override fun onChanged(location: Location?) {
                if (location != null) {

                    this@MeteoriteNasaService.location = location

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
                    .filter { it.reclong?.toDoubleOrNull() != null && it.reclat?.toDoubleOrNull() != null }

            meteoriteRepository.insertAll(filteredList)

            currentLoaded = remoteMeteorites.size

            currentPage++
        } while (currentLoaded == limit)

    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite>? {
        return meteoriteRepository.getMeteoriteById(id)
    }

}
