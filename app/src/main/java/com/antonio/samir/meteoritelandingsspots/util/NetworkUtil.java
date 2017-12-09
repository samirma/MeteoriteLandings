package com.antonio.samir.meteoritelandingsspots.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    static Boolean hasConnectivity = null;

    /**
     * Static method to return whatever network is available or not
     * @param context
     * @return hasConnectivity
     */
    public static boolean hasConnectivity(final Context context) {

        if (hasConnectivity != null) {
            return hasConnectivity;
        }

        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
