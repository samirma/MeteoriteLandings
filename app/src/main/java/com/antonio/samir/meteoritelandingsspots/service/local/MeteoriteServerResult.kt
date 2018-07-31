package com.antonio.samir.meteoritelandingsspots.service.local

import com.antonio.samir.meteoritelandingsspots.model.Meteorite

class MeteoriteServerResult {
    var meteorites: List<Meteorite>? = null
        @Throws(MeteoriteServerException::class)
        get() {
            if (exception != null) {
                throw exception as MeteoriteServerException
            }
            return field
        }
    private var exception: MeteoriteServerException? = null

    fun setException(exception: MeteoriteServerException) {
        this.exception = exception
    }

}
