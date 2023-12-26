package com.antonio.samir.meteoritelandingsspots.data.repository

import kotlinx.coroutines.flow.Flow

interface UIThemeRepository {
    fun getTheme(): Flow<UITheme>

    fun setTheme(uiTheme: UITheme)

    enum class UITheme {
        DARK,
        LIGHT
    }
}