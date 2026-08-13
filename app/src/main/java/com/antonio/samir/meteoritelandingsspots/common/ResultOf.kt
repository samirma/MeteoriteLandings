package com.antonio.samir.meteoritelandingsspots.common

/**
 * Result of an operation that crosses a layer boundary.
 *
 * [Error] carries a typed [DataError] rather than a raw exception so the UI can pick a message
 * without inspecting exception types, and so the data layer does not leak its transport details.
 */
sealed interface ResultOf<out T> {

    data class Success<out T>(val data: T) : ResultOf<T>

    /** @param progress completion between 0f and 100f, or null when it cannot be determined. */
    data class InProgress(val progress: Float? = null) : ResultOf<Nothing>

    data class Error(val error: DataError, val cause: Throwable? = null) : ResultOf<Nothing>
}

/** The failure modes the UI needs to distinguish. */
enum class DataError {
    /** No usable connection, or the request never reached the server. */
    NETWORK,

    /** The server answered, but not with something we can use. */
    SERVER,

    /** The requested record does not exist locally. */
    NOT_FOUND,

    /** No reverse-geocoding backend is available on this device. */
    GEOCODER_UNAVAILABLE,

    UNKNOWN,
}
