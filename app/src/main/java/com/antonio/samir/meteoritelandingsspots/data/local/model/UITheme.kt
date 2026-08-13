package com.antonio.samir.meteoritelandingsspots.data.local.model

/**
 * User's theme choice. [SYSTEM] is the default so a fresh install follows the OS setting instead
 * of forcing dark, which is what the previous boolean-only preference did.
 */
enum class UITheme {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    companion object {
        fun fromStorage(value: String?): UITheme =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
