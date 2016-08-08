package com.contafe.trakttv.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Natig on 7/27/16.
 */
public class ConnectionDetector {
    private Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isConnectiongToInternet() {
        // creating an instance of ConnectivityManager class
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // getting information about all possible networks on device
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                // if any of them is connected return true
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
