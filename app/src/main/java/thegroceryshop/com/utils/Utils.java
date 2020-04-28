package thegroceryshop.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;


/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils {

    /**
     * Setting the layout for arabic and english
     */
    //AppUtils.setDeviceLanguage(this);

    public static void setDeviceLanguage(Activity currentActivity){

        /**
         * Detecting current selected language of device
         */
        String currentDeviceLanguage = Locale.getDefault().getDisplayLanguage();


         if( currentDeviceLanguage.equals("فارسی") || currentDeviceLanguage.equals("العربية"))
        {
            setLocale(currentActivity.getApplicationContext(), "ar");
        }
    }
    /**
     * this method identifies for selection of layout for specific language like Arabic
     * @param context
     * @param lang
     */
    public static void setLocale(final Context context, final String lang) {
        final Locale loc = new Locale(lang);
        Locale.setDefault(loc);
        final Configuration cfg = new Configuration();
        cfg.locale = loc;
        context.getResources().updateConfiguration(cfg, null);
    }

    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }

        String number1 = new String(chars);
        return number1.replace("٫", ".");
    }
}
