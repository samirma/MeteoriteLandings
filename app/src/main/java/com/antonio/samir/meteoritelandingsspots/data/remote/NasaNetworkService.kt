package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.common.DataError
import com.antonio.samir.meteoritelandingsspots.common.di.IoDispatcher
import com.antonio.samir.meteoritelandingsspots.data.connectivity.ConnectivityRepository
import com.antonio.samir.meteoritelandingsspots.data.model.Meteorite
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NasaNetworkService @Inject constructor(
    private val service: NasaServerEndPoint,
    private val connectivityRepository: ConnectivityRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MeteoriteRemoteRepository {

    override suspend fun getMeteorites(): List<Meteorite> = withContext(ioDispatcher) {
        if (!connectivityRepository.isCurrentlyOnline()) {
            throw MeteoriteServerException(DataError.NETWORK)
        }
        try {
            service.meteoriteLandings().toMeteorites()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (io: IOException) {
            throw MeteoriteServerException(DataError.NETWORK, io)
        } catch (http: HttpException) {
            throw MeteoriteServerException(DataError.SERVER, http)
        } catch (serialization: SerializationException) {
            throw MeteoriteServerException(DataError.SERVER, serialization)
        }
    }
}
