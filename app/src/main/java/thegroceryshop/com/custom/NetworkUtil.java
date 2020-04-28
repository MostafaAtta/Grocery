package thegroceryshop.com.custom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;


/**
 * Created by rohitg on 12/7/2016.
 */

public class NetworkUtil {

    /**
     * method to check active internet connection on device.
     *
     * @param context
     * @return true, if device connected to the internet. if it is false, also shows a error dialog.
     */
    public static boolean networkStatus(Context context) {
        if(context != null){
            if (NetworkUtil.isWifiAvailable(context) || NetworkUtil.isMobileNetworkAvailable(context)) {
                return true;
            } else {
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getString(R.string.error_internet_connection), context.getString(R.string.ok));
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * method to check if device has enabled mobile data.
     *
     * @param context
     * @return true, if mobile data is enabled in mobile.
     */
    public static boolean isMobileNetworkAvailable(Context context) {

        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();

        return activeNetworkInfo != null
                && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                && activeNetworkInfo.isConnected();
    }

    /**
     * method to check device has connected with active Wifi connection.
     *
     * @param context
     * @return true, if device connected with any Wifi network.
     */
    public static boolean isWifiAvailable(Context context) {

        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();

        return activeNetworkInfo != null
                && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetworkInfo.isConnected();
    }
}
