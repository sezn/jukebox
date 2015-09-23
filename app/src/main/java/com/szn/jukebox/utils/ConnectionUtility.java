package com.szn.jukebox.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Julien Sezn on 18/09/2015.
 *
 */
public class ConnectionUtility {


    private static final String TAG = ConnectionUtility.class.getSimpleName();



    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager nInfo = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            nInfo.getActiveNetworkInfo().isConnectedOrConnecting();
            Log.d(TAG, "Net avail:" + nInfo.getActiveNetworkInfo().isConnectedOrConnecting());
            ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                Log.d(TAG, "Network available:true");
                return true;
            } else {
                Log.d(TAG, "Network available:false");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean checkGPSAvailability(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e("GPS Checking", "Exception checking location manager status");
            return false;
        }

    }
}
