package com.antonio.samir.meteoritelandingsspots.features.list

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.antonio.samir.meteoritelandingsspots.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeteoriteItemView(
    val id: Int,
    val name: String? = null,
    val yearString: String? = null,
    val address: String? = null,
    val reclat: String? = null,
    val reclong: String? = null,
    val isSelected: Boolean = false,
) : Parcelable