package thegroceryshop.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.Locale;

import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;

/**
 * Created by rohitg on 12/6/2016.
 */

public class DeviceUtil {

    /**
     * method to get the device id of hardware.
     *
     * @param mContext application context
     * @return deviceId
     */
    public static String getDeviceId(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * method to get the device android version.
     *
     * @return integer value of android version
     */
    public static int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * method to get the device Country code like IN, AS etc.
     *
     * @return
     */
    public static String getDeviceCountryCode() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    /**
     * method to get the device Country Name like India
     *
     * @return
     */
    public static String getDeviceCountryName() {
        Locale locale = Locale.getDefault();
        return locale.getDisplayCountry();
    }

    /**
     * method to get the device Country Code of Network like 91.
     *
     * @return
     */
    public static String getDeviceNetworkCountry(Context context, Activity activity) {

        TelephonyManager manager = null;
        if (AppUtil.checkReadPhoneStatePermission(activity)) {
            manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getNetworkCountryIso();
        }
        return AppConstants.BLANK_STRING;
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {return 0;}
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {return AppConstants.BLANK_STRING;}
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }
}
