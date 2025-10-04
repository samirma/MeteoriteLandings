package com.antonio.samir.meteoritelandingsspots.common.userCase

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    fun default(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun main(): CoroutineDispatcher
    fun unconfined(): CoroutineDispatcher
}
