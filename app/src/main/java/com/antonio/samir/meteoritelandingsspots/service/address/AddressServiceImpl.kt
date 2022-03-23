package com.antonio.samir.meteoritelandingsspots.service.address

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils

@ExperimentalCoroutinesApi
class AddressServiceImpl(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val geoLocationUtil: GeoLocationUtilInterface,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : AddressServiceInterface {

    private val TAG = AddressServiceImpl::class.java.simpleName

    override fun recoveryAddress(): Flow<ResultOf<Float>> =
        meteoriteLocalRepository.meteoritesWithOutAddress()
            .onEach { recoverAddress(it) }
            .flowOn(dispatchers.default())
            .map {
                getReturn(it)
            }
            .catch { throwable ->
                val exception = if (throwable is Exception) {
                    throwable
                } else {
                    Exception("Fail to load addresses")
                }
                emit(ResultOf.Error(exception))
            }

    private suspend fun getReturn(it: List<Meteorite>): ResultOf<Float> {
        return if (!it.isNullOrEmpty()) {
            val meteoritesWithoutAddressCount =
                meteoriteLocalRepository.getMeteoritesWithoutAddressCount()
            val meteoritesCount = meteoriteLocalRepository.getValidMeteoritesCount()
            val progress = (1 - (meteoritesWithoutAddressCount.toFloat() / meteoritesCount)) * 100
            ResultOf.InProgress(progress)
        } else {
            ResultOf.Success(100f)
        }
    }

    private suspend fun recoverAddress(list: List<Meteorite>) = withContext(dispatchers.default()) {
        list.onEach { meteorite ->
            meteorite.address = getAddressFromMeteorite(meteorite)
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
