package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context

interface NetworkUtilInterface {

    fun hasConnectivity(context: Context?): Boolean
}
