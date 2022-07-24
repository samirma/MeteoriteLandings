package com.antonio.samir.meteoritelandingsspots.common.userCase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class UserCaseBase<I, O>(private val coroutineContext: CoroutineContext = Dispatchers.IO) {

    operator fun invoke(input: I) = action(input).flowOn(coroutineContext)

    protected abstract fun action(input: I): Flow<O>

}

