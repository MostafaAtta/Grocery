package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;


/**
 * Created by rohitg on 12/7/2016.
 */

public class CalenderLocalStore {

    private static final String PREF_NAME = "cal_local_prefs";
    private static Context context;
    private final SharedPreferences mPreferences;
    private static CalenderLocalStore sLocalStore;
    private static final String KEY_APP_LANGUAGE = "key_app_language";

    /**
     * Protected constructor. use {@link #getInstance(Context)} instead.
     *
     * @param context context.
     */
    private CalenderLocalStore(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get Singleton instance of ApiLocal store create new if needed
     *
     * @param context Context
     * @return singleton instance
     */
    public static CalenderLocalStore getInstance(Context context) {
        if (sLocalStore == null) {
            sLocalStore = new CalenderLocalStore(context);
        }
        return sLocalStore;
    }

    public static CalenderLocalStore getObj(){
        if (sLocalStore == null) {
            return sLocalStore;
        }else{
            sLocalStore = new CalenderLocalStore(context);
            return sLocalStore;
        }
    }

    public static void setContext(Context context) {
        CalenderLocalStore.context = context;
        if (sLocalStore == null) {
            sLocalStore = new CalenderLocalStore(context);
        }
    }

    public String getAppLanguage() {
        return mPreferences.getString(KEY_APP_LANGUAGE, new Locale("en").getLanguage());
    }

    public String getAppLangId() {
        if(getAppLanguage().startsWith("en")){
            return "1";
        }else{
            return "2";
        }
    }

    public void setAppLanguage(String app_language) {
        mPreferences.edit().putString(KEY_APP_LANGUAGE, app_language).apply();
    }
}
