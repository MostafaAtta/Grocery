package thegroceryshop.com.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.prolificinteractive.materialcalendarview.CalenderLocalStore;

import org.json.JSONArray;

import java.util.Locale;

import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Region;


/**
 * Created by rohitg on 12/7/2016.
 */

public class ApiLocalStore {

    private static final String PREF_NAME = "app_local_prefs";
    private final SharedPreferences mPreferences;
    private static ApiLocalStore sLocalStore;

    private static final String KEY_APP_RUN_COUNTER = "key_app_run_counter";
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USER_TYPE = "key_user_type";
    private static final String KEY_USER_EMAIL = "key_user_email";
    private static final String KEY_FIRST_NAME = "key_first_name";
    private static final String KEY_LAST_NAME = "key_last_name";
    private static final String KEY_USER_COUNTRY_CODE = "key_user_country_code";
    private static final String KEY_USER_PHONE = "key_user_phone";
    private static final String KEY_USER_GENDER = "key_user_gender";
    private static final String KEY_USER_DOB = "key_user_dob";
    private static final String KEY_USER_IMAGE = "key_user_img";
    private static final String KEY_USER_CREDITS = "key_user_credits";
    private static final String KEY_LOGIN_TYPE = "key_login_type";
    private static final String KEY_IS_EMAIL_VERFIED = "key_is_email_verfied";
    private static final String KEY_FREE_SHIPPING_AMOUNT = "key_free_shipping_amount";
    private static final String KEY_CART_TOTAL = "key_cart_total";
    private static final String KEY_CART_DATA = "key_cart_data";
    private static final String KEY_SHOW_CASE_TEXT = "key_show_case_text";
    private static final String KEY_REFERRAL_CODE = "key_referral_code";
    private static final String KEY_DEFAULT_PAYMENT_METHOD = "key_default_payment_method";
    private static final String KEY_DEFAULT_SHIPPING_ADDRESS = "key_default_shipping_address";
    private static final String KEY_DEFAULT_SHIPPING_AREA_ID = "key_default_shipping_area_id";
    private static final String KEY_IS_ALREADY_RATED = "key_is_already_rated";
    private static final String KEY_IS_ALARM_SCHEDULED = "key_is_alarm_scheduled";
    private static final String KEY_SHIPPING_CHARGES = "key_shipping_charges";
    private static final String KEY_REGION_LIST = "key_region_list";
    private static final String KEY_IS_USER_ACTIVE = "key_is_user_active";
    private static final String KEY_IS_LANGUAGE_SELECTED = "key_is_language_set";
    private static final String KEY_APP_LANGUAGE = "key_app_language";
    private static final String KEY_IS_CART_UPDATED = "key_is_cart_updated";
    private static final String KEY_MY_WISH_LIST_NAMES = "key_my_wish_list_names";
    private static final String KEY_REGION_ID = "key_region_id";
    private static final String KEY_REGION_NAME_EN = "key_region_name_en";
    private static final String KEY_REGION_NAME_AR = "key_region_name_ar";

    /**
     * Protected constructor. use {@link #getInstance(Context)} instead.
     *
     * @param context context.
     */
    private ApiLocalStore(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get Singleton instance of ApiLocal store create new if needed
     *
     * @param context Context
     * @return singleton instance
     */
    public static ApiLocalStore getInstance(Context context) {
        if (sLocalStore == null) {
            sLocalStore = new ApiLocalStore(context);
        }
        return sLocalStore;
    }

    /**
     * Get saved app run counter
     *
     * @return counter
     */
    public int getAppRunCounter() {
        return mPreferences.getInt(KEY_APP_RUN_COUNTER, 0);
    }

    /**
     * Save app run counter
     *
     * @param counter integer value of counter
     */
    public void saveAppRunCounter(int counter) {
        mPreferences.edit().putInt(KEY_APP_RUN_COUNTER, counter).apply();
    }

    /**
     * Get saved user id
     *
     * @return user id
     */
    public String getUserId() {
        return mPreferences.getString(KEY_USER_ID, AppConstants.BLANK_STRING);
    }

    /**
     * Save user id
     *
     * @param id id of user
     */
    public void saveUserId(String id) {
        mPreferences.edit().putString(KEY_USER_ID, id).apply();
    }

    /**
     * Get saved user type
     *
     * @return user type
     */
    public String getUserType() {
        return mPreferences.getString(KEY_USER_TYPE, AppConstants.BLANK_STRING);
    }

    /**
     * Save user type
     *
     * @param type type of user
     */
    public void saveUserType(String type) {
        mPreferences.edit().putString(KEY_USER_TYPE, type).apply();
    }

    /**
     * Get saved user email
     *
     * @return user email
     */
    public String getUserEmail() {
        return mPreferences.getString(KEY_USER_EMAIL, AppConstants.BLANK_STRING);
    }

    /**
     * Save user email
     *
     * @param email email of user
     */
    public void saveUserEmail(String email) {
        mPreferences.edit().putString(KEY_USER_EMAIL, email.toLowerCase()).apply();
    }

    /**
     * Get saved user first name
     *
     * @return user first name
     */
    public String getFirstName() {
        return mPreferences.getString(KEY_FIRST_NAME, AppConstants.BLANK_STRING);
    }

    /**
     * Save user first name
     *
     * @param name first name of user
     */
    public void saveFirstName(String name) {
        mPreferences.edit().putString(KEY_FIRST_NAME, name).apply();
    }

    /**
     * Get saved user last name
     *
     * @return user last name
     */
    public String getLastName() {
        return mPreferences.getString(KEY_LAST_NAME, AppConstants.BLANK_STRING);
    }

    /**
     * Save user last name
     *
     * @param name last name of user
     */
    public void saveLastName(String name) {
        mPreferences.edit().putString(KEY_LAST_NAME, name).apply();
    }

    /**
     * Get saved user country code
     *
     * @return user country code
     */
    public String getUserCountryCode() {
        return mPreferences.getString(KEY_USER_COUNTRY_CODE, AppConstants.BLANK_STRING);
    }

    /**
     * Save user country code
     *
     * @param countryCode country code id of user
     */
    public void saveUserCountryCode(String countryCode) {
        mPreferences.edit().putString(KEY_USER_COUNTRY_CODE, countryCode).apply();
    }

    /**
     * Get saved user phone
     *
     * @return user phone
     */
    public String getUserPhone() {
        return mPreferences.getString(KEY_USER_PHONE, AppConstants.BLANK_STRING);
    }

    /**
     * Save user phone
     *
     * @param phone phone of user
     */
    public void saveUserPhone(String phone) {
        mPreferences.edit().putString(KEY_USER_PHONE, phone).apply();
    }

    /**
     * Get saved user gender
     *
     * @return user gender
     */
    public String getUserGender() {
        return mPreferences.getString(KEY_USER_GENDER, AppConstants.BLANK_STRING);
    }

    /**
     * Save user gender
     *
     * @param gender gender of user
     */
    public void saveUserGender(String gender) {
        mPreferences.edit().putString(KEY_USER_GENDER, gender).apply();
    }

    /**
     * Get saved user dob
     *
     * @return user dob
     */
    public String getUserDob() {
        return mPreferences.getString(KEY_USER_DOB, AppConstants.BLANK_STRING);
    }

    /**
     * Save user dob
     *
     * @param dob dob of user
     */
    public void saveUserDob(String dob) {
        mPreferences.edit().putString(KEY_USER_DOB, dob).apply();
    }

    /**
     * Get saved user image
     *
     * @return user image
     */
    public String getUserImage() {
        return mPreferences.getString(KEY_USER_IMAGE, AppConstants.BLANK_STRING);
    }

    /**
     * Save user image
     *
     * @param image image of user
     */
    public void saveUserImage(String image) {
        mPreferences.edit().putString(KEY_USER_IMAGE, image).apply();
    }

    /**
     * Save user credits
     *
     * @param user_credits credits of user
     */
    public void saveUserCredit(float user_credits) {
        //mPreferences.edit().putFloat(KEY_USER_CREDITS, user_credits).apply();
        mPreferences.edit().putFloat(KEY_USER_CREDITS, 0.0f).apply();
    }

    /**
     * Get saved user credits
     *
     * @return user credits
     */
    public float getUserCredit() {
        return mPreferences.getFloat(KEY_USER_CREDITS, 0.0f);
        //return 200.00f;
    }

    /**
     * Save user credits
     *
     * @param socialType credits of user
     */
    public void saveLoginType(String socialType) {
        mPreferences.edit().putString(KEY_LOGIN_TYPE, socialType).apply();
    }

    /**
     * Get saved user credits
     *
     * @return user credits
     */
    public String getLoginType() {
        return mPreferences.getString(KEY_LOGIN_TYPE, AppConstants.BLANK_STRING);
    }

    /**
     * Save user credits
     *
     * @param text credits of user
     */
    public void saveShowCaseText(String text) {
        mPreferences.edit().putString(KEY_SHOW_CASE_TEXT, text).apply();
    }

    /**
     * Get saved user credits
     *
     * @return user credits
     */
    public String getShowCaseText() {
        return mPreferences.getString(KEY_SHOW_CASE_TEXT, AppConstants.BLANK_STRING);
    }

    public void saveIsEmailVerified(boolean isEmailVerify) {
        mPreferences.edit().putBoolean(KEY_IS_EMAIL_VERFIED, isEmailVerify).apply();
    }

    public boolean isEmailVerified() {
        return mPreferences.getBoolean(KEY_IS_EMAIL_VERFIED, false);
    }

    public void saveFreeShippingAmount(float amount) {
        mPreferences.edit().putFloat(KEY_FREE_SHIPPING_AMOUNT, amount).apply();
    }

    public float getFreeShippingAmount() {
        return mPreferences.getFloat(KEY_FREE_SHIPPING_AMOUNT, 0.0f);
    }

    public void saveIsAlreadyRated(boolean isAlreadyRated) {
        mPreferences.edit().putBoolean(KEY_IS_ALREADY_RATED, isAlreadyRated).apply();
    }

    public boolean isAlarmScheduled() {
        return mPreferences.getBoolean(KEY_IS_ALARM_SCHEDULED, false);
    }

    public void saveIsAlarmScheduled(boolean isAlarmScheduled) {
        mPreferences.edit().putBoolean(KEY_IS_ALARM_SCHEDULED, isAlarmScheduled).apply();
    }

    public boolean isAlreadyRated() {
        return mPreferences.getBoolean(KEY_IS_ALREADY_RATED, false);
    }

    public void saveCartTotal(float amount) {
        if(amount < 0.0f){
            mPreferences.edit().putFloat(KEY_CART_TOTAL, 0.0f).apply();
        }else{
            mPreferences.edit().putFloat(KEY_CART_TOTAL, amount).apply();
        }
    }

    public float getCartTotal() {
        return mPreferences.getFloat(KEY_CART_TOTAL, 0.0f);
    }

    public void saveReferralCOde(String code) {
        mPreferences.edit().putString(KEY_REFERRAL_CODE, code).apply();
    }

    public String getReferralCode() {
        return mPreferences.getString(KEY_REFERRAL_CODE, AppConstants.BLANK_STRING);
    }

    public void saveCartData(String data) {
        if(!ValidationUtil.isNullOrBlank(data)){
            mPreferences.edit().putString(KEY_CART_DATA, data).apply();
        }else{
            mPreferences.edit().putString(KEY_CART_DATA, new JSONArray().toString()).apply();
        }
    }

    public String getCartData() {
        if(!ValidationUtil.isNullOrBlank(mPreferences.getString(KEY_CART_DATA, new JSONArray().toString()))){
            return mPreferences.getString(KEY_CART_DATA, new JSONArray().toString());
        }else{
            return new JSONArray().toString();
        }
    }

    public void saveDefaultPaymentMethod(String method) {
        mPreferences.edit().putString(KEY_DEFAULT_PAYMENT_METHOD, method).apply();
    }

    public String getDefaultPaymentMethod() {
        return mPreferences.getString(KEY_DEFAULT_PAYMENT_METHOD, AppConstants.BLANK_STRING);
    }






    //Added by Naresh
    public void saveDefaultShippingAreaId(String shipping_address) {
        mPreferences.edit().putString(KEY_DEFAULT_SHIPPING_AREA_ID, shipping_address).apply();
    }

    public String getDefaultShippingAreaId() {
        return mPreferences.getString(KEY_DEFAULT_SHIPPING_AREA_ID, AppConstants.BLANK_STRING);
    }



    public void saveDefaultShippingAddress(String shipping_address) {
        mPreferences.edit().putString(KEY_DEFAULT_SHIPPING_ADDRESS, shipping_address).apply();
    }

    public String getDefaultShippingAddress() {
        return mPreferences.getString(KEY_DEFAULT_SHIPPING_ADDRESS, AppConstants.BLANK_STRING);
    }

    public void saveShippingCharges(String shipping_charges) {
        mPreferences.edit().putString(KEY_SHIPPING_CHARGES, shipping_charges).apply();
    }

    public String getShippingCharges() {
        return mPreferences.getString(KEY_SHIPPING_CHARGES, AppConstants.BLANK_STRING);
    }

    public void saveRegionList(String regionList) {
        mPreferences.edit().putString(KEY_REGION_LIST, regionList).apply();
    }

    public String getRegionList() {
        return mPreferences.getString(KEY_REGION_LIST, AppConstants.BLANK_STRING);
    }

    public void saveUserActive(boolean isUserActive) {
        mPreferences.edit().putBoolean(KEY_IS_USER_ACTIVE, isUserActive).apply();
    }

    public boolean isUserActive() {
        return mPreferences.getBoolean(KEY_IS_USER_ACTIVE, true);
    }

    public boolean isLanguageSelected() {
        return mPreferences.getBoolean(KEY_IS_LANGUAGE_SELECTED, false);
    }

    public void saveLanguageSelected(boolean is_language_selected) {
        mPreferences.edit().putBoolean(KEY_IS_LANGUAGE_SELECTED, is_language_selected).apply();
    }

    public String getAppLanguage() {
        return mPreferences.getString(KEY_APP_LANGUAGE, new Locale("en").getLanguage());
    }

    public void setCartUpdated(boolean is_cart_updated) {
        mPreferences.edit().putBoolean(KEY_IS_CART_UPDATED, is_cart_updated).apply();
    }

    public String getMyWishListNames() {
        return mPreferences.getString(KEY_MY_WISH_LIST_NAMES, AppConstants.BLANK_STRING);
    }

    public void setMyWishListNames(String wish_list_names) {
        mPreferences.edit().putString(KEY_MY_WISH_LIST_NAMES, wish_list_names).apply();
    }

    public boolean isCartUpdated() {
        return mPreferences.getBoolean(KEY_IS_CART_UPDATED, false);
    }

    public String getAppLangId() {
        if(getAppLanguage().startsWith("en")){
            return "1";
        }else{
            return "2";
        }
    }

    public void setAppLanguage(String app_language, Context context) {
        mPreferences.edit().putString(KEY_APP_LANGUAGE, app_language).apply();
        CalenderLocalStore.getInstance(context).setAppLanguage(app_language);
    }

    public void setSelectedRegion(Region selected_region) {
        if(selected_region != null){
            mPreferences.edit().putString(KEY_REGION_ID, selected_region.getRegionId()).apply();
            mPreferences.edit().putString(KEY_REGION_NAME_EN, selected_region.getRegionNameEnglish()).apply();
            mPreferences.edit().putString(KEY_REGION_NAME_AR, selected_region.getRegionNameArabic()).apply();
        }else{
            mPreferences.edit().putString(KEY_REGION_ID, AppConstants.BLANK_STRING).apply();
            mPreferences.edit().putString(KEY_REGION_NAME_EN, AppConstants.BLANK_STRING).apply();
            mPreferences.edit().putString(KEY_REGION_NAME_AR, AppConstants.BLANK_STRING).apply();
        }
    }

    public String getSelectedRegionId() {
        return mPreferences.getString(KEY_REGION_ID, AppConstants.BLANK_STRING);
    }

    public String getSelectedRegionName() {
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return mPreferences.getString(KEY_REGION_NAME_EN, AppConstants.BLANK_STRING);
        }else{
            return mPreferences.getString(KEY_REGION_NAME_AR, AppConstants.BLANK_STRING);
        }
    }

    /**
     * clear localy saved user data
     */
    public void clearOnLogout() {
        mPreferences.edit().putString(KEY_USER_IMAGE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_DOB, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_FIRST_NAME, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_LAST_NAME, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_GENDER, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_PHONE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_COUNTRY_CODE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_EMAIL, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_TYPE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_ID, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_LOGIN_TYPE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_USER_IMAGE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putString(KEY_REFERRAL_CODE, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putBoolean(KEY_IS_USER_ACTIVE, false).apply();
        mPreferences.edit().putString(KEY_DEFAULT_PAYMENT_METHOD, AppConstants.BLANK_STRING).apply();
        mPreferences.edit().putBoolean(KEY_IS_EMAIL_VERFIED, false).apply();
        mPreferences.edit().putFloat(KEY_USER_CREDITS, 0.0f).apply();
    }


}
