package com.antonio.samir.meteoritelandingsspots.common

sealed class ResultOf<out T> {
    data class Success<out R>(val value: R) : ResultOf<R>()
    object Loading : ResultOf<Nothing>()
    object Failure : ResultOf<Nothing>()
}