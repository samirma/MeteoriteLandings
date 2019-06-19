package com.antonio.samir.meteoritelandingsspots.service.business

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    private val mediatorLiveData = MediatorLiveData<List<Meteorite>>()

    override suspend fun loadMeteorites(): LiveData<List<Meteorite>> {

        val liveData = meteoriteRepository.meteoriteOrdened()

        mediatorLiveData.addSource(liveData) { meteoritesFromDB ->

            if (meteoritesFromDB.isEmpty() && !isUpdateRequired.getAndSet(true)) {
                //If it is empty so load the data from internet
                GlobalScope.launch {
                    recoverFromNetwork()
                }
            } else {
                if (!isGpsOrdered.get() && !gpsTracker.isLocationServiceStarted() && gpsTracker.isGPSEnabled()) {
                    gpsTracker.startLocationService()
                    orderByLocation(meteoritesFromDB)
                }
                this.mediatorLiveData.value = meteoritesFromDB
//                addressService.recoveryAddress()
            }

        }

        return mediatorLiveData

    }

    override fun addressStatus(): MutableLiveData<String> {
        return addressService.status
    }

    private fun orderByLocation(meteorites: List<Meteorite>) {

        val trackerLocation = gpsTracker.location
        trackerLocation.observeForever(object : Observer<Location> {
            override fun onChanged(location: Location?) {
                if (location != null) {

                    GlobalScope.launch(Dispatchers.Main) {

                        val orderList = orderList(location, meteorites)

                        mediatorLiveData.value = orderList
                    }

                    trackerLocation.removeObserver(this)
                    gpsTracker.stopUpdates()

                }
            }
        })
    }

    private suspend fun orderList(
            location: Location,
            meteorites: List<Meteorite>
    ): ArrayList<Meteorite>? = withContext(Dispatchers.IO)
    {
        val latitude = location.latitude
        val longitude = location.longitude
        //String sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);

        val sortedSet = TreeSet<Meteorite> { m1, m2 ->
            val distance1 = m1.distance(latitude, longitude)
            val distance2 = m2.distance(latitude, longitude)
            if (distance1 > 0 && distance2 > 0) distance1.compareTo(distance2) else 0
        }

        sortedSet.addAll(meteorites)

        isGpsOrdered.set(true)

        return@withContext ArrayList(sortedSet)

    }


    private fun recoverFromNetwork() {
        GlobalScope.launch(Dispatchers.Default) {
            val remoteMeteorites = meteoriteRepository.getRemoteMeteorites()
            remoteMeteorites?.let { meteoriteRepository.insertAll(it) }
        }
    }

    override suspend fun getMeteoriteById(id: String): LiveData<Meteorite>? {
        return meteoriteRepository.getMeteoriteById(id)
    }

}
