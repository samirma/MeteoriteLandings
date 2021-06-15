package com.antonio.samir.meteoritelandingsspots.common.userCase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

abstract class UserCaseBase<I, O>(val coroutineContext: CoroutineContext = Dispatchers.Default) {

    fun execute(input: I) = action(input).flowOn(coroutineContext)

    protected abstract fun action(input: I): Flow<O>

}

