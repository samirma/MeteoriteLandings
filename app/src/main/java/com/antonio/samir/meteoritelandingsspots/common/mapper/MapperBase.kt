package com.antonio.samir.meteoritelandingsspots.common.mapper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class MapperBase<I, O> (private val coroutineContext: CoroutineContext = Dispatchers.IO) {

    suspend fun map(input: I) : O = withContext(coroutineContext) {
        assert(Thread.currentThread().name != "main")
        return@withContext action(input)
    }

    protected abstract suspend fun action(input: I): O

}