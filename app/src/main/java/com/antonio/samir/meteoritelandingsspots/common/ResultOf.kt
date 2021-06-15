package com.antonio.samir.meteoritelandingsspots.common

sealed class ResultOf<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultOf<T>()
    data class Error(val exception: Exception) : ResultOf<Nothing>()
    data class InProgress<out T : Any>(val data: T? = null) : ResultOf<T>()
}