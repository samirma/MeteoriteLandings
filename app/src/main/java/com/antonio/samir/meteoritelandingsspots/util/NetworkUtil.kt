package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    internal var hasConnectivity: Boolean? = null

    /**
     * Static method to return whatever network is available or not
     * @param context
     * @return hasConnectivity
     */
    fun hasConnectivity(context: Context?): Boolean {

        if (hasConnectivity != null) {
            return hasConnectivity!!
        }

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

}
