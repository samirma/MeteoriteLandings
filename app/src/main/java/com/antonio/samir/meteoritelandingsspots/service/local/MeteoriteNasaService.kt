package com.antonio.samir.meteoritelandingsspots.service.local

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class MeteoriteNasaService(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val addressService: AddressServiceInterface,
        private val gpsTracker: GPSTrackerInterface
) : MeteoriteService {

    private val isUpdateRequired = AtomicBoolean(false)


    override fun loadMeteorites(): LiveData<List<Meteorite>> {

        val result = MediatorLiveData<List<Meteorite>>()

        val liveData = meteoriteRepository.meteoriteOrdened()

        result.addSource(liveData) { meteorites ->

            if (meteorites.isEmpty() && !isUpdateRequired.getAndSet(true)) {
                //If it is empty so load the data from internet
                recoverFromNetwork()
            } else {
                if (gpsTracker.isGPSEnabled()) {
                    gpsTracker.startLocationService()
                    orderByLocation(meteorites, result)
                } else {
                    result.value = meteorites
                }
                addressService.recoveryAddress()
            }

        }

        return result

    }

    override fun addressStatus(): MutableLiveData<String> {
        return addressService.status
    }

    private fun orderByLocation(meteorites: List<Meteorite>, list: MediatorLiveData<List<Meteorite>>) {
        val trackerLocation = gpsTracker.location
        trackerLocation.observeForever(object : Observer<Location> {
            override fun onChanged(location: Location?) {
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    //String sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);

                    val sortedSet = TreeSet<Meteorite> { m1, m2 ->
                        val distance1 = m1.distance(latitude, longitude)
                        val distance2 = m2.distance(latitude, longitude)
                        if (distance1 > 0 && distance2 > 0) distance1.compareTo(distance2) else 0
                    }

                    sortedSet.addAll(meteorites)

                    val value = ArrayList(sortedSet)
                    list.value = value

                    trackerLocation.removeObserver(this)
                    gpsTracker.stopUpdates()

                }
            }
        })
    }

    private fun recoverFromNetwork() {
        GlobalScope.launch(Dispatchers.Default) {
            val remoteMeteorites = meteoriteRepository.getRemoteMeteorites()
            remoteMeteorites?.let { meteoriteRepository.insertAll(it) }
            addressService.recoveryAddress()
        }
    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite>? {
        return meteoriteRepository.getMeteoriteById(id)
    }

}
