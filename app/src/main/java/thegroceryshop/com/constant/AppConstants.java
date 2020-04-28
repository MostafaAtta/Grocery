package thegroceryshop.com.constant;

import thegroceryshop.com.webservices.ApiClient;

public class AppConstants {

    public static final int DELIVERY_TYPE_SCHEDULED = 1;
    public static final int DELIVERY_TYPE_EXPRESS = 2;
    public static final String GOOGLE_API_KEY = "AIzaSyDSkcUOJ0dacKqyT34ls5z4riXmyTQzKHA";

    public static String BLANK_STRING = "";
    public static String SPACE = " ";
    public static int PERMISSION_INTERNET = 101;
    public static int REQUEST_PICK_COUNTRY_SELF = 105;
    public static String ERROR_TAG = "The Grocery Shop";
    public static int API_RETRY_COUNT = 5;

    public static String DEVICE_TYPE = "Android";
    /**
     * LoginTypes
     * Facebook = 1,
     * Twitter= 2,
     * GooglePlus = 3
     */
    public static String TYPE_LOGIN_EMAIL = "1";
    public static String TYPE_SOCIAL_FACEBOOK = "3";
    public static String TYPE_SOCIAL_GOOGLE_PLUS = "2";

    // Permissions Request Codes
    public static int REQUEST_GOOGLE_SIGN_IN = 102;
    public static int REQUEST_FACEBOOK_SIGN_IN = 64206;

    public static String URL_TERMS_OF_USE = ApiClient.getPageURL() + "slug=terms-of-use&lang_id=";
    public static String URL_PRIVACY_POLICY  = ApiClient.getPageURL() + "slug=privacy-policy&lang_id=";
    public static String URL_LICENSE  = ApiClient.getPageURL() + "slug=license&lang_id=";
    public static String URL_APP_STORE = "https://play.google.com/store/apps/details?id=thegroceryshop.com";
    public static String PROD_CALLBACK_URL = "https://prod.thegroceryshop.com/payments/transaction_response";
    public static String UAT_CALLBACK_URL = "https://octprod.thegroceryshop.com/payments/transaction_response";
}
