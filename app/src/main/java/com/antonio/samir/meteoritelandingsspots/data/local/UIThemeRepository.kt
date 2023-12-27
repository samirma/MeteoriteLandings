package com.antonio.samir.meteoritelandingsspots.data.local

import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import kotlinx.coroutines.flow.Flow

interface UIThemeRepository {
    fun getTheme(): Flow<UITheme>

    suspend fun setTheme(uiTheme: UITheme)

}