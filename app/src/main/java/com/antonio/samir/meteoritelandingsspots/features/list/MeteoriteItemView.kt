package com.antonio.samir.meteoritelandingsspots.features.list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeteoriteItemView(
    val id: String,
    val name: String,
    val yearString: String,
    val address: String,
    val distance: String,
    val isSelected: Boolean = false,
) : Parcelable