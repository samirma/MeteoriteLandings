package com.antonio.samir.meteoritelandingsspots.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException

class UIThemeRepositoryImplTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private fun repository(): UIThemeRepositoryImpl {
        val file = File(temporaryFolder.newFolder(), "ui_theme.preferences_pb")
        return UIThemeRepositoryImpl(PreferenceDataStoreFactory.create { file })
    }

    @Test
    fun `defaults to following the system`() = runTest {
        // Not DARK: the previous boolean preference defaulted to dark and ignored the OS setting.
        assertEquals(UITheme.SYSTEM, repository().getTheme().first())
    }

    @Test
    fun `round-trips a stored theme`() = runTest {
        val repository = repository()

        repository.setTheme(UITheme.LIGHT)

        assertEquals(UITheme.LIGHT, repository.getTheme().first())
    }

    @Test
    fun `falls back to the default when preferences cannot be read`() = runTest {
        // Without the catch, an IOException from DataStore propagates into the first
        // composition and takes the app down at startup.
        val failing = object : DataStore<Preferences> {
            override val data = flow<Preferences> { throw IOException("disk is unhappy") }
            override suspend fun updateData(
                transform: suspend (Preferences) -> Preferences,
            ): Preferences = error("not used")
        }

        assertEquals(UITheme.SYSTEM, UIThemeRepositoryImpl(failing).getTheme().first())
    }
}
