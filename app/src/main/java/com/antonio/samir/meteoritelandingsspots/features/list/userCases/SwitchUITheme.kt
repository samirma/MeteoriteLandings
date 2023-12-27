package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import com.antonio.samir.meteoritelandingsspots.common.userCase.IsDarkTheme
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SwitchUITheme @Inject constructor(
    private val uiThemeRepository: UIThemeRepository,
    private val isDarkTheme: IsDarkTheme
) {

    suspend operator fun invoke() = withContext(Dispatchers.Default) {
        val isDark = isDarkTheme(Unit).first()
        val uiTheme = if (!isDark) UITheme.DARK else UITheme.LIGHT
        uiThemeRepository.setTheme(uiTheme)
    }

}