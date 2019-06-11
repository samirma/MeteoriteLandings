package com.antonio.samir.meteoritelandingsspots.service.local

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private val isUpdatRequired = AtomicBoolean(false)

    override fun loadMeteorites(): LiveData<List<Meteorite>> {

        val list = MediatorLiveData<List<Meteorite>>()

        val liveData = meteoriteRepository.meteoriteOrdened()

        list.addSource(liveData) { meteorites ->

            if (meteorites.isEmpty() && !isUpdatRequired.getAndSet(true)) {
                //If it is empty so load the data from internet
                recoverFromNetwork()
            } else {
                //Is not empty so remove the observer
                if (gpsTracker.isGPSEnabled()) {
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
            }

            list.value = meteorites

        }

        return list

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
