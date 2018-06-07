package com.antonio.samir.meteoritelandingsspots.service.local

import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.Application
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService.Status.DONE
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService.Status.LOADING
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtil
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class AddressService {

    private val mMeteoriteDao: MeteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(Application.getContext())

    enum class Status {
        DONE, LOADING
    }

    fun recoveryAddress(): MutableLiveData<Status> {

        executor.execute {
            if (status.value == null || status.value === DONE) {
                val meteorites = mMeteoriteDao.meteoritesWithOutAddress
                try {
                    if (!meteorites.isEmpty()) {
                        status.postValue(LOADING)

                        val size = meteorites.size

                        for (i in 0 until size) {
                            val met = meteorites[i]
                            recoverAddress(met, met.reclat, met.reclong)
                        }

                        status.postValue(DONE)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Fail to retrieve address", e)
                }

            }

        }

        return status

    }

    fun recoverAddress(meteorite: Meteorite, recLat: String, recLong: String) {

        val address = getAddress(recLat, recLong)
        meteorite.address = address
        mMeteoriteDao.update(meteorite)
        Log.i(TAG, String.format("Address for id %s recovered", meteorite.id))


    }

    private fun getAddress(recLat: String, recLong: String): String {
        var addressString = ""
        if (StringUtils.isNoneEmpty(recLat) && StringUtils.isNoneEmpty(recLong)) {
            val address = GeoLocationUtil.getAddress(java.lang.Double.parseDouble(recLat), java.lang.Double.parseDouble(recLong), Application.getContext())
            if (address != null) {
                val finalAddress = ArrayList<String>()
                val city = address.locality
                if (StringUtils.isNoneEmpty(city)) {
                    finalAddress.add(city)
                }

                val state = address.adminArea
                if (StringUtils.isNoneEmpty(state)) {
                    finalAddress.add(state)
                }

                val countryName = address.countryName
                if (StringUtils.isNoneEmpty(countryName)) {
                    finalAddress.add(countryName)
                }

                addressString = StringUtils.join(finalAddress, ", ")

            }
        }

        return addressString
    }

    companion object {

        val TAG = AddressService::class.java.simpleName
        internal val executor = ThreadPoolExecutor(0, 3,
                1L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue())
        private val status = MutableLiveData<Status>()
    }
}
