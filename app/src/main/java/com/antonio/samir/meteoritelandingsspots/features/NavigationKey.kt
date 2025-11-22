package com.antonio.samir.meteoritelandingsspots.features

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface NavigationKey : Parcelable {
    @Parcelize
    data object List : NavigationKey

    @Parcelize
    data class Detail(val meteoriteId: String) : NavigationKey

    @Parcelize
    data object Debug : NavigationKey
}
