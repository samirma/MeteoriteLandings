package com.antonio.samir.meteoritelandingsspots.service.address

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import kotlinx.coroutines.flow.Flow

interface AddressService {

    /**
     * Reverse-geocodes every meteorite that is still missing an address, emitting progress as it
     * goes and **completing** when there is nothing left to do.
     */
    fun recoveryAddress(): Flow<ResultOf<Float>>
}
