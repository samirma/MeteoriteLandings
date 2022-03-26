package com.antonio.samir.meteoritelandingsspots.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class UIThemeRepositoryImpl : UIThemeRepository {

    private val isDark = MutableStateFlow(true)

    override fun getTheme(): Flow<UIThemeRepository.UITheme> = isDark.map { isDark ->
        if (isDark) return@map UIThemeRepository.UITheme.DARK else return@map UIThemeRepository.UITheme.LIGHT
    }

    override fun setTheme(uiTheme: UIThemeRepository.UITheme) {
        isDark.value = uiTheme == UIThemeRepository.UITheme.DARK
    }
}