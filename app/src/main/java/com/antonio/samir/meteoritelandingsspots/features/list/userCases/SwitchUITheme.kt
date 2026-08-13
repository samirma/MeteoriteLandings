package com.antonio.samir.meteoritelandingsspots.features.list.userCases

import com.antonio.samir.meteoritelandingsspots.data.local.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.UITheme
import javax.inject.Inject

/**
 * Flips between the light and dark themes.
 *
 * Takes the currently rendered value instead of re-reading the preference, which removes the
 * read-modify-write race the previous version had and makes the first tap behave correctly when
 * the stored preference is still [UITheme.SYSTEM].
 */
class SwitchUITheme @Inject constructor(
    private val uiThemeRepository: UIThemeRepository,
) {

    suspend operator fun invoke(currentlyDark: Boolean) {
        uiThemeRepository.setTheme(if (currentlyDark) UITheme.LIGHT else UITheme.DARK)
    }
}
