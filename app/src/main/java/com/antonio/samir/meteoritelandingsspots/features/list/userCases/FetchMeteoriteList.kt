package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.onEach

@FlowPreview
class FetchMeteoriteList(
    private val meteoriteRepository: MeteoriteRepository,
    private val startAddressRecover: StartAddressRecover,
) : UserCaseBase<Unit, ResultOf<Unit>>() {

    override fun action(input: Unit) = meteoriteRepository.loadDatabase().onEach {
        if (it is ResultOf.Success) startAddressRecover(Unit)
    }

}