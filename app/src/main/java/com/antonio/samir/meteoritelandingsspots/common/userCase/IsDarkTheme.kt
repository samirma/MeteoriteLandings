package com.antonio.samir.meteoritelandingsspots.common.userCase

import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map

@FlowPreview
class IsDarkTheme(private val uiThemeRepository: UIThemeRepository) :
    UserCaseBase<Unit, Boolean>() {

    override fun action(input: Unit) = uiThemeRepository.getTheme().map { uiTheme ->
        uiTheme == UIThemeRepository.UITheme.DARK
    }

}