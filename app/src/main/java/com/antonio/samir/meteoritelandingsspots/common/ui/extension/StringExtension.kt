package com.antonio.samir.meteoritelandingsspots.common.ui.extension

import java.text.NumberFormat
import java.util.*

fun String?.convertToNumberFormat(defaultValue: String): String {
    return if (this.isNullOrBlank()) {
        defaultValue
    } else {
        try {
            NumberFormat.getInstance(Locale.getDefault()).format(this.toDouble())
        } catch (ex: Exception) {
            defaultValue
        }
    }
}