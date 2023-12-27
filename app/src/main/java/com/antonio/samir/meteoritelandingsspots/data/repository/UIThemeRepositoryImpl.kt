package com.antonio.samir.meteoritelandingsspots.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository.UITheme
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository.UITheme.DARK
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository.UITheme.LIGHT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ui_theme")

class UIThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UIThemeRepository {

    // Define a key for the theme preference
    companion object {
        val THEME_KEY = booleanPreferencesKey("is_dark_theme")
    }

    // Create a flow of UITheme from the DataStore
    override fun getTheme(): Flow<UITheme> = context.dataStore.data.map { preferences ->
        // Get the theme preference, defaulting to DARK if not set
        val isDark = preferences[THEME_KEY] ?: true
        // Convert the boolean value to a UITheme enum
        if (isDark == DARK.value) DARK else LIGHT
    }

    // Update the theme preference in the DataStore
    override suspend fun setTheme(uiTheme: UITheme) {
        context.dataStore.edit { preferences ->
            // Set the theme preference to the value of the enum
            preferences[THEME_KEY] = uiTheme.value
        }
    }
}
