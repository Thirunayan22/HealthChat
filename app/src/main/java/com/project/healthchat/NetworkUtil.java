package com.project.healthchat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static String getConnectivityStatusString(Context context){
        String status = null;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork!= null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                status = context.getString(R.string.device_online);
                return status;
            } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                status = "Mobile data enabled";
                return status;
            }
        } else {
            status = context.getString(R.string.device_offline);
            return status;
        }
        return status;
    }
}
