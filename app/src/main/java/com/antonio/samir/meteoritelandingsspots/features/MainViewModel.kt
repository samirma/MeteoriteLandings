package com.antonio.samir.meteoritelandingsspots.features

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Immutable
data class ThemeState(
    /** null means "not read yet" or "follow the system". */
    val isDark: Boolean? = null,
    val isResolved: Boolean = false,
)

/**
 * Owns the app-wide theme.
 *
 * This used to be an `IsDarkTheme` use case field-injected straight into the Activity and
 * collected inside `setContent`. Because the use case returned a fresh cold Flow on every call,
 * collection restarted on every recomposition, and the `initial = false` seed contradicted the
 * persisted default — hence the white flash on every cold start.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    uiThemeRepository: UIThemeRepository,
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = uiThemeRepository.getTheme()
        .map { theme ->
            ThemeState(
                isDark = when (theme) {
                    UITheme.DARK -> true
                    UITheme.LIGHT -> false
                    UITheme.SYSTEM -> null
                },
                isResolved = true,
            )
        }
        .stateIn(
            scope = viewModelScope,
            // Eagerly, not WhileSubscribed: the splash screen asks for this value before the
            // first composition subscribes.
            started = SharingStarted.Eagerly,
            initialValue = ThemeState(),
        )
}
