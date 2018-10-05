package com.antonio.samir.meteoritelandingsspots.service.local

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
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


class AddressService(val context: Context) {

    private val mMeteoriteDao: MeteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(context)

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

                        meteorites.onEach { meteorite ->
                            recoverAddress(meteorite)
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

    private fun recoverAddress(meteorite: Meteorite) {

        val recLat = meteorite.reclat
        val recLong = meteorite.reclong

        val address = getAddress(recLat, recLong)
        meteorite.address = address
        mMeteoriteDao.update(meteorite)
        Log.i(TAG, String.format("Address for id %s recovered", meteorite.id))


    }

    private fun getAddress(recLat: String?, recLong: String?): String {
        var addressString = ""
        if (StringUtils.isNoneEmpty(recLat) && StringUtils.isNoneEmpty(recLong)) {
            val address = GeoLocationUtil.getAddress(java.lang.Double.parseDouble(recLat), java.lang.Double.parseDouble(recLong), context)
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
