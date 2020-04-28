package thegroceryshop.com.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleHelper {

	private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

	public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
		return setLocale(context, lang);
	}

	public static Context onAttach(Context context, String defaultLanguage) {
		String lang = getPersistedData(context, defaultLanguage);
		return setLocale(context, lang);
	}

	public static String getLanguage(Context context) {
		return getPersistedData(context, Locale.getDefault().getLanguage());
	}

	public static Context setLocale(Context context, String language) {
		persist(context, language);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return updateResources(context, language);
		}

		return updateResourcesLegacy(context, language);
	}

	private static String getPersistedData(Context context, String defaultLanguage) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
	}

	private static void persist(Context context, String language) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putString(SELECTED_LANGUAGE, language);
		editor.apply();
	}

	@TargetApi(Build.VERSION_CODES.N)
	private static Context updateResources(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);

		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		Configuration configuration = resources.getConfiguration();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			configuration.setLayoutDirection(locale);
			setSystemLocale(configuration, locale);
			return context.createConfigurationContext(configuration);
		} else {
			setSystemLocale(configuration, locale);
			context.getResources().updateConfiguration(configuration, displayMetrics);
			return context;
		}
	}

	@SuppressWarnings("deprecation")
	private static Context updateResourcesLegacy(Context context, String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);

		Resources resources = context.getResources();

		Configuration configuration = resources.getConfiguration();
		//configuration.locale = locale;
        setSystemLocale(configuration, locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

		resources.updateConfiguration(configuration, resources.getDisplayMetrics());

		return context;
	}

	@SuppressWarnings("deprecation")
	private static void setSystemLocale(Configuration config, Locale locale) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			config.setLocale(locale);
		} else {
			config.locale = locale;
		}
	}
}