package com.antonio.samir.meteoritelandingsspots.data.repository

import androidx.paging.DataSource
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow


class MeteoriteRepositoryImpl(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : MeteoriteRepository {

    override suspend fun loadMeteorites(filter: String?): DataSource.Factory<Int, Meteorite> {
        TODO("Not yet implemented")
    }

    override fun getMeteoriteById(id: String): Flow<Result<Meteorite>> {
        TODO("Not yet implemented")
    }

    override suspend fun requestAddressUpdate(meteorite: Meteorite) {
        TODO("Not yet implemented")
    }

    override fun requestAddressUpdate(list: List<Meteorite>) {
        TODO("Not yet implemented")
    }


}
