package com.antonio.samir.meteoritelandingsspots.service.local

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import java.util.*

internal class MeteoriteNasaService(private val mContext: Context, private val mGpsTracker: GPSTracker) : MeteoriteService {

    override fun loadMeteorites(): LiveData<List<Meteorite>> {

        //Return meteorites
        val meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(mContext)

        val gpsEnabled = mGpsTracker.isGPSEnabled()

        val list = MutableLiveData<List<Meteorite>>()

        val liveData = meteoriteDao.meteoriteOrdened

        val observer = object : Observer<List<Meteorite>> {

            override fun onChanged(meteorites: List<Meteorite>?) {

                if (meteorites!!.isEmpty()) {
                    //If it is empty so load the data from internet
                    val nasaService = NasaServiceFactory.getNasaService()
                    MeteoriteNasaAsyncTaskService(nasaService, meteoriteDao, mContext).execute()
                } else {
                    //Is not empty so remove the observer
                    list.value = meteorites
                    liveData.removeObserver(this)
                }

                if (gpsEnabled) {
                    val mGpsTrackerLocation = mGpsTracker.location
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
                                mGpsTracker.stopUpdates()

                            }
                        }
                    })

                }

            }
        }
        liveData.observeForever(observer)

        return list

    }

}
