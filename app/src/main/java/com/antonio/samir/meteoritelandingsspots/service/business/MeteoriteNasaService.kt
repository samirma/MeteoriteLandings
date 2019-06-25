package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class MeteoriteNasaService(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val addressService: AddressServiceInterface,
        private val gpsTracker: GPSTrackerInterface
) : MeteoriteServiceInterface {

    private val isUpdateRequired = AtomicBoolean(false)

    private val isGpsOrdered = AtomicBoolean(false)

    private val addressServiceStarted = AtomicBoolean(false)

    private var location: Location? = null

    override suspend fun loadMeteorites(): LiveData<List<Meteorite>> = withContext(Dispatchers.Default) {

        if (meteoriteRepository.getMeteoritesCount() == 0 && !isUpdateRequired.getAndSet(true)) {
            //If it is empty so load the data from internet
            recoverFromNetwork()
        } else {

            if (!isGpsOrdered.getAndSet(true) && !gpsTracker.isLocationServiceStarted() && gpsTracker.isGPSEnabled()) {
                updateLocation()
            }

            if (!addressServiceStarted.getAndSet(true)) {
                addressService.recoveryAddress()
            }
        }

        return@withContext meteoriteRepository.meteoriteOrdened()

    }

    override fun addressStatus(): MutableLiveData<String> {
        return addressService.status
    }

    private suspend fun updateLocation() = withContext(Dispatchers.Main) {

        gpsTracker.startLocationService()

        val trackerLocation = gpsTracker.location
        trackerLocation.observeForever(object : Observer<Location> {
            override fun onChanged(location: Location?) {
                if (location != null) {
                    this@MeteoriteNasaService.location = location
                    trackerLocation.removeObserver(this)
                    gpsTracker.stopUpdates()

                }
            }
        })
    }

    private suspend fun orderList(
            location: Location,
            meteorites: List<Meteorite>
    ): ArrayList<Meteorite>? = withContext(Dispatchers.Default)
    {
        val latitude = location.latitude
        val longitude = location.longitude

        val sortedSet = TreeSet<Meteorite> { m1, m2 ->
            val distance1 = m1.distance(latitude, longitude)
            val distance2 = m2.distance(latitude, longitude)
            if (distance1 > 0 && distance2 > 0) distance1.compareTo(distance2) else 0
        }

        sortedSet.addAll(meteorites)

        return@withContext ArrayList(sortedSet)

    }


    private suspend fun recoverFromNetwork() {
        val remoteMeteorites = meteoriteRepository.getRemoteMeteorites()
        remoteMeteorites?.let { meteoriteRepository.insertAll(it) }
    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite>? {
        return meteoriteRepository.getMeteoriteById(id)
    }

}
