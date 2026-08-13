package com.antonio.samir.meteoritelandingsspots.data.remote

import com.antonio.samir.meteoritelandingsspots.common.DataError

/**
 * Wraps transport failures so callers above the data layer never see Retrofit or OkHttp types.
 */
class MeteoriteServerException(
    val error: DataError,
    cause: Throwable? = null,
) : Exception("Meteorite feed unavailable: $error", cause)
