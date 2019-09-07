package in.cinderella.testapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConnectivityUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static boolean isNetworkSlow(Context context){
        boolean isSlow=false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        int netSubType = network.getSubtype();
        if(netSubType == TelephonyManager.NETWORK_TYPE_GPRS ||
                netSubType == TelephonyManager.NETWORK_TYPE_EDGE ||
                netSubType == TelephonyManager.NETWORK_TYPE_1xRTT) {
            //user is in slow network
            isSlow=true;
        }
        return isSlow;
    }
}