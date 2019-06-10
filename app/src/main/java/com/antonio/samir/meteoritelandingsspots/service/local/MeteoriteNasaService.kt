package com.antonio.samir.meteoritelandingsspots.service.local

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import java.util.*
import javax.inject.Inject

class MeteoriteNasaService @Inject constructor(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val addressService: AddressServiceInterface,
        private val gpsTracker: GPSTracker
) : MeteoriteService {

    override fun loadMeteorites(): LiveData<List<Meteorite>> {

        val list = MutableLiveData<List<Meteorite>>()

        val liveData = meteoriteRepository.meteoriteOrdened()

        liveData.observeForever(object : Observer<List<Meteorite>> {

            override fun onChanged(meteorites: List<Meteorite>?) {

                if (meteorites!!.isEmpty()) {
                    //If it is empty so load the data from internet
                    saveMeteorites()
                } else {
                    //Is not empty so remove the observer
                    list.value = meteorites
                    liveData.removeObserver(this)
                }

                orderByCurrentLocation(meteorites, list)

            }
        })

        return list

    }

    private fun orderByCurrentLocation(meteorites: List<Meteorite>, list: MutableLiveData<List<Meteorite>>) {
        if (gpsTracker.isGPSEnabled()) {
            val mGpsTrackerLocation = gpsTracker.location
            mGpsTrackerLocation.observeForever(object : Observer<Location> {
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

                        mGpsTrackerLocation.removeObserver(this)
                        gpsTracker.stopUpdates()

                    }
                }
            })
        }
    }

    private fun saveMeteorites() {

        meteoriteRepository.getRemoteMeteriorites()?.let { meteoriteRepository.insertAll(it) }

        addressService.recoveryAddress()

    }

    override fun getMeteoriteById(id: String): LiveData<Meteorite>? {
        return meteoriteRepository.getMeteoriteById(id)
    }


}
