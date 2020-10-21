package com.antonio.samir.meteoritelandingsspots.util

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtil(context: Context) : NetworkUtilInterface {

    private var appContext: Context = context.applicationContext

    /**
     * A method to return whatever network is available or not
     * @return hasConnectivity
     */
    override fun hasConnectivity(): Boolean {

        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

}
