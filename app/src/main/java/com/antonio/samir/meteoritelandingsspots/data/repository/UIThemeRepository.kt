package com.antonio.samir.meteoritelandingsspots.data.repository

import kotlinx.coroutines.flow.Flow

interface UIThemeRepository {
    fun getTheme(): Flow<UITheme>

    suspend fun setTheme(uiTheme: UITheme)

    enum class UITheme(val value: Boolean) {
        DARK(true),
        LIGHT(false)
    }
}