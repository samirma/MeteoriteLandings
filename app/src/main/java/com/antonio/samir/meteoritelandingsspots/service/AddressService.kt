package com.antonio.samir.meteoritelandingsspots.service

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Evaluate convert this class to work manager
 */
@ExperimentalCoroutinesApi
class AddressService(
        private val meteoriteLocalRepository: MeteoriteLocalRepository,
        private val geoLocationUtil: GeoLocationUtilInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : AddressServiceInterface {

    val TAG = AddressService::class.java.simpleName

    override fun recoveryAddress(): Flow<Result<Float>> = meteoriteLocalRepository.meteoritesWithOutAddress()
            .onEach { recoverAddress(it) }
            .flowOn(dispatchers.default())
            .map {
                getReturn(it)
            }

    private suspend fun getReturn(it: List<Meteorite>): Result<Float> {
        return if (!it.isNullOrEmpty()) {
            val meteoritesWithoutAddressCount = meteoriteLocalRepository.getMeteoritesWithoutAddressCount()
            val meteoritesCount = meteoriteLocalRepository.getMeteoritesCount()
            val progress = (1 - (meteoritesWithoutAddressCount.toFloat() / meteoritesCount)) * 100
            Result.InProgress(progress)
        } else {
            Result.Success(100f)
        }
    }

    override suspend fun recoverAddress(list: List<Meteorite>) = withContext(dispatchers.default()) {
        list.onEach { meteorite ->
            try {
                meteorite.address = getAddressFromMeteorite(meteorite)
            } catch (e: Exception) {
                Log.e(TAG, "Fail to retrieve address", e)
            }
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
