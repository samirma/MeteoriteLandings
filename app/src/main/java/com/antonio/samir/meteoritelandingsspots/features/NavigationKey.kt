package com.antonio.samir.meteoritelandingsspots.features

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation 3 destinations.
 *
 * Implements [NavKey] and is `@Serializable` so `rememberNavBackStack` can save and restore the
 * stack itself, instead of the hand-rolled `listSaver` the app used before.
 */
sealed interface NavigationKey : NavKey {

    @Serializable
    data object List : NavigationKey

    @Serializable
    data class Detail(val meteoriteId: Int) : NavigationKey

    @Serializable
    data object Debug : NavigationKey
}
