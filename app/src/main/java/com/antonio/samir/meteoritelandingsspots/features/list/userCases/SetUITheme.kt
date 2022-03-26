package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.UserCaseBase
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository.UITheme.DARK
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository.UITheme.LIGHT
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow

@FlowPreview
class SetUITheme(private val uiThemeRepository: UIThemeRepository) :
    UserCaseBase<SetUITheme.Input, ResultOf<Unit>>() {

    override fun action(input: Input) = flow {
        try {
            uiThemeRepository.setTheme(
                if (input.isDark) {
                    DARK
                } else {
                    LIGHT
                }
            )
            emit(ResultOf.Success(Unit))
        } catch (e: Exception) {
            emit(ResultOf.Error(e))
        }
    }

    data class Input(val isDark: Boolean)

}