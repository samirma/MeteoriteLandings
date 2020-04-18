package com.antonio.samir.meteoritelandingsspots.data.repository

import android.util.Log
import androidx.annotation.StringDef
import com.antonio.samir.meteoritelandingsspots.data.Result.InProgress
import com.antonio.samir.meteoritelandingsspots.data.Result.Success
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.AddressService.Status.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.data.repository.AddressService.Status.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import java.util.*


class AddressService(
        private val meteoriteLocalRepository: MeteoriteLocalRepository,
        private val geoLocationUtil: GeoLocationUtilInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : AddressServiceInterface {

    val TAG = AddressService::class.java.simpleName

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(DONE, LOADING)
    annotation class Status {
        companion object {
            const val DONE = "DONE"
            const val LOADING = "LOADING"
        }
    }

    override fun recoveryAddress() = flow {

        Log.i(TAG, "recoveryAddress $LOADING")

        emit(InProgress(LOADING))

        var meteorites = meteoriteLocalRepository.meteoritesWithOutAddress()

        while (meteorites.isNotEmpty()) {
            recoverAddress(meteorites)
            meteorites = meteoriteLocalRepository.meteoritesWithOutAddress()
        }

        emit(Success(LOADING))
    }

    override suspend fun recoverAddress(list: List<Meteorite>) {
        try {
            list.onEach { meteorite ->
                meteorite.address = getAddressFromMeteorite(meteorite)
            }
            val meteoritesWithoutAddressCount: Int = meteoriteLocalRepository.getMeteoritesWithoutAddressCount()
            Log.i(TAG, "recoveryAddress ${list.size} $meteoritesWithoutAddressCount")

        } catch (e: Exception) {
            Log.e(TAG, "Fail to retrieve address", e)
        }
        meteoriteLocalRepository.updateAll(list)
    }

    override suspend fun recoverAddress(meteorite: Meteorite) = withContext(dispatchers.default()) {
        meteorite.address = getAddressFromMeteorite(meteorite)
        meteoriteLocalRepository.update(meteorite)
    }

    private fun getAddressFromMeteorite(meteorite: Meteorite): String {
        val recLat = meteorite.reclat
        val recLong = meteorite.reclong

        var metAddress = " "
        if (recLat != null && recLong != null) {
            val address = getAddress(recLat.toDouble(), recLong.toDouble())
            metAddress = address ?: " "
        }
        return metAddress
    }


    private fun getAddress(recLat: Double, recLong: Double): String? {
        var addressString: String? = null
        val address = geoLocationUtil.getAddress(recLat, recLong)
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

            if (finalAddress.isNotEmpty()) {
                addressString = finalAddress.joinToString(", ")
            }

        }

        return addressString
    }
}
