package com.antonio.samir.meteoritelandingsspots.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UIThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UIThemeRepository {

    override fun getTheme(): Flow<UITheme> = dataStore.data
        // DataStore surfaces read failures through the flow; without this a corrupt or
        // unreadable preferences file would crash the app during its first composition.
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences -> UITheme.fromStorage(preferences[THEME_KEY]) }

    override suspend fun setTheme(uiTheme: UITheme) {
        dataStore.edit { preferences -> preferences[THEME_KEY] = uiTheme.name }
    }

    companion object {
        val THEME_KEY = stringPreferencesKey("ui_theme")
    }
}
