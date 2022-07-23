package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import kotlinx.coroutines.FlowPreview

@FlowPreview
class FetchMeteoriteList(
    private val meteoriteRepository: MeteoriteRepository,
) : UserCaseBase<Unit, ResultOf<Unit>>() {

    override fun action(input: Unit) = meteoriteRepository.loadDatabase()

}