package thegroceryshop.com.custom;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.loginModule.LoginModuleActivity;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.DeliveryCharges;
import thegroceryshop.com.modal.Order;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.utils.DeviceUtil;


public class AppUtil {

    private static boolean isLoadingVisible;
    private static ProgressDialog mProgressDialog;
    private static AppDialogSingleAction mSingleActionAlertDialog;
    private static AppDialogSingleAction mErrorDialog;
    /**
     * Chrome Custom Tab
     */

    static CustomTabsClient mCustomTabsClient;
    static CustomTabsSession mCustomTabsSession;
    static CustomTabsServiceConnection mCustomTabsServiceConnection;
    static CustomTabsIntent mCustomTabsIntent;
    final static String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";


    /**
     * Method for checking all required permissions for app
     *
     * @return Activity current activity
     */
    public static boolean checkAllPermission(Activity activity) {

        if (DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.M) {
            return checkInternetPermission(activity);
        }
        return true;
    }

    /**
     * Method for checking internet permission for app
     *
     * @return Activity current activity
     */
    public static boolean checkInternetPermission(Activity activity) {

        if (DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                return true;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, AppConstants.PERMISSION_INTERNET);
            return false;
        }
        return true;
    }

    /**
     * Method for checking internet permission for app
     *
     * @return Activity current activity
     */
    public static boolean checkReadPhoneStatePermission(Activity activity) {

        if (DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                return true;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, AppConstants.PERMISSION_INTERNET);
            return false;
        }
        return true;
    }

    /**
     * This method is used for setting the fullscreen for splash activity
     */
    public static void setFullScreen(Activity activity) {

        /**
         * set flag to full screen of window
         */
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window window = activity.getWindow();
        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    /**
     * to show a alert dialog with single action.
     *
     * @param context     context
     * @param title       title of the dialog
     * @param description description to use in dialog
     * @param actionLabel label on the action
     */
    public static void displaySingleActionAlert(Context context, String title, String description, String actionLabel, boolean isOverWriteNewOne) {

        try{
            if (isOverWriteNewOne) {
                if (mSingleActionAlertDialog != null && mSingleActionAlertDialog.isShowing()) {
                    mSingleActionAlertDialog.dismiss();
                }

                mSingleActionAlertDialog = new AppDialogSingleAction(context, context.getResources().getString(R.string.app_name), description, actionLabel);
                mSingleActionAlertDialog.show();
                mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        mSingleActionAlertDialog.dismiss();
                    }
                });
            } else {
                if (mSingleActionAlertDialog == null || !mSingleActionAlertDialog.isShowing()) {
                    mSingleActionAlertDialog = new AppDialogSingleAction(context, context.getResources().getString(R.string.app_name), description, actionLabel);
                    mSingleActionAlertDialog.show();
                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                        @Override
                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                            mSingleActionAlertDialog.dismiss();
                        }
                    });
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show a alert dialog with single action.
     *
     * @param context     context
     * @param title       title of the dialog
     * @param description description to use in dialog
     * @param actionLabel label on the action
     */
    public static void displaySingleActionAlert(Context context, String title, String description, String actionLabel) {

        try{
            if (mSingleActionAlertDialog != null && mSingleActionAlertDialog.isShowing()) {
                mSingleActionAlertDialog.dismiss();
            }

            mSingleActionAlertDialog = new AppDialogSingleAction(context, context.getResources().getString(R.string.app_name), description, actionLabel);
            mSingleActionAlertDialog.show();
            mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                @Override
                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                    mSingleActionAlertDialog.dismiss();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show a alert dialog with single action.
     *
     * @param context                context
     * @param title                  title of the dialog
     * @param description            description to use in dialog
     * @param actionLabel            label on the action
     * @param onSingleActionListener click listener
     */
    public static void displaySingleActionAlert(Context context, String title, String description, String actionLabel, OnSingleActionListener onSingleActionListener) {

        try {
            if (mSingleActionAlertDialog != null && mSingleActionAlertDialog.isShowing()) {
                mSingleActionAlertDialog.dismiss();
            }

            mSingleActionAlertDialog = new AppDialogSingleAction(context, context.getResources().getString(R.string.app_name), description, actionLabel);
            mSingleActionAlertDialog.show();
            mSingleActionAlertDialog.setOnSingleActionListener(onSingleActionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * hide the softkeyboard
     *
     * @param mActivity
     */
    public static void hideSoftKeyboard(Activity mActivity, Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager.isAcceptingText()) {
            //writeToLog("Software Keyboard was shown");

            View view = mActivity.getCurrentFocus();
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * hide the softkeyboard
     *
     * @param context
     */
    public static void hideSoftKeyboard(Context context, View view) {
        Activity activity = (Activity)context;
        try {
            if(activity.getCurrentFocus()!=null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Show soft keyboard
     *
     * @param mActivity
     */
    public static void showSoftKeyboard(Activity mActivity) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * ( metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * method to show Progress.
     *
     * @param mContext Context to get resources and device specific display metrics
     */
    public static void showProgress(Context mContext) {

        if (isLoadingVisible) {
            hideProgress();
        }
        isLoadingVisible = true;
        mProgressDialog = new ProgressDialog(mContext);
        //mProgressDialog.setTitle("Wolero.One");
        mProgressDialog.setMessage(mContext.getString(R.string.loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        if (DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = new ProgressBar(mContext).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            mProgressDialog.setIndeterminateDrawable(drawable);
        }

        mProgressDialog.show();
    }

    public static void showProgress(Context mContext, String loadertext) {

        if (isLoadingVisible) {
            hideProgress();
        }
        isLoadingVisible = true;
        mProgressDialog = new ProgressDialog(mContext);
        //mProgressDialog.setTitle("Wolero.One");
        mProgressDialog.setMessage(loadertext);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        if (DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = new ProgressBar(mContext).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            mProgressDialog.setIndeterminateDrawable(drawable);
        }

        mProgressDialog.show();
    }

    /**
     * method to hide progress
     */
    public static void hideProgress() {
        try{
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                isLoadingVisible = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showErrorDialog(Context mContext, String msg) {
        displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, msg, mContext.getString(R.string.ok), true);
    }

    public static void showErrorDialog2(Context mContext, String msg) {
        displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, msg, mContext.getString(R.string.ok), false);
    }

    public static void showErrorDialogWithListener(Context mContext, String msg, OnSingleActionListener onSingleActionListener) {

        if (mSingleActionAlertDialog != null && mSingleActionAlertDialog.isShowing()) {
            mSingleActionAlertDialog.dismiss();
        }

        mSingleActionAlertDialog = new AppDialogSingleAction(mContext, AppConstants.ERROR_TAG, msg, mContext.getString(R.string.ok));
        mSingleActionAlertDialog.show();
        mSingleActionAlertDialog.setOnSingleActionListener(onSingleActionListener);
    }


    public static void showDialogPopUpPermission(final Activity currentActivity, String messageStr) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentActivity);

        // set title
        alertDialogBuilder.setTitle(currentActivity.getString(R.string.app_name));

        // set dialog message
        alertDialogBuilder.setMessage(messageStr)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", currentActivity.getPackageName(), null);
                        intent.setData(uri);
                        currentActivity.startActivity(intent);
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set flag to check popup will again show
                dialog.dismiss();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        TextView textView = alertDialog.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, currentActivity.getResources().getDisplayMetrics()), 1.0f);
        }
    }


    public static boolean mobileNumberValidationByCountryWiseAndroid(int countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode);
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        if (phoneNumber != null) {
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            if (isValid) {
                String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isValidPhone(Context context, String phNumber)
    {
        boolean valid = true;
        if (phNumber.isEmpty())
        {
            valid =false;
            AppUtil.showErrorDialog(context,context.getResources().getString(R.string.error_blank_phone));
        }else if (phNumber.length() < 6 || phNumber.length() > 15)
        {
            valid = false;
            AppUtil.showErrorDialog(context,context.getResources().getString(R.string.error_invalid_international_number));

        }
        return valid;
    }

    public static boolean isValidPhone(Context context, String phNumber, boolean shouldShowErrorDialog)
    {
        boolean valid = true;
        if (phNumber.isEmpty())
        {
            valid =false;
            if(shouldShowErrorDialog){
                AppUtil.showErrorDialog(context,context.getResources().getString(R.string.error_blank_phone));
            }
        }else if (phNumber.length() < 6 || phNumber.length() > 15)
        {
            valid = false;
            if(shouldShowErrorDialog){
                AppUtil.showErrorDialog(context,context.getResources().getString(R.string.error_invalid_international_number));
            }

        }
        return valid;
    }

    /**
     * Set Expand Animation
     *
     * @param v
     */
    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /**
     * Set Expand Animation
     *
     * @param v
     */
    public static void expand1(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        v.getLayoutParams().height = targetHeight;
        v.requestLayout();
        /*Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);*/
    }

    /**
     * Set Collapse Animation
     *
     * @param v
     */
    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /**
     * Set Collapse Animation
     *
     * @param v
     */
    public static void collapse1(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        v.setVisibility(View.GONE);
    }

    public static CartItem getCartObject(Product product) {

        if (product == null) {
            return null;
        }

        CartItem cartItem = new CartItem();
        cartItem.setId(product.getId());
        cartItem.setEnglishLabel(product.getEnglishLabel());
        cartItem.setArabicLabel(product.getArabicLabel());
        cartItem.setImage(product.getImage());
        cartItem.setOfferedPrice(product.getOfferedPrice());
        if (product.isOffer()) {
            cartItem.setActualPrice(product.getOfferedPrice());
            cartItem.setSavedPrice(
                    (Float.parseFloat(product.getActualPrice().replace(",", AppConstants.BLANK_STRING)) - Float.parseFloat(product.getOfferedPrice().replace(",", AppConstants.BLANK_STRING)))
                            + AppConstants.BLANK_STRING);
        } else {
            cartItem.setSavedPrice("0.00");
            cartItem.setActualPrice(product.getActualPrice());
        }
        //cartItem.setTaxCharges(product.getTaxCharges());
        cartItem.setEnglishQuantity(product.getEnglishQuantity());
        cartItem.setArabicQuantity(product.getArabicQuantity());
        cartItem.setMaxQuantity(product.getMaxQuantity());
        cartItem.setOffer(product.isOffer());
        cartItem.setSoldOut(product.isSoldOut());
        cartItem.setSelectedQuantity(product.getSelectedQuantity());
        cartItem.setCurrency(product.getCurrency());
        cartItem.setMarkSoldQuantity(product.getMarkSoldQuantity());
        cartItem.setEnglishBrandName(product.getEnglishBrandName());
        cartItem.setArabicBrandName(product.getArabicBrandName());
        cartItem.setDoorStepDelivery(product.isDoorStepDelivery());
        cartItem.setShippingThirdParty(product.isShippingThirdParty());
        cartItem.setAddedToWishList(product.isAddedToWishList());
        if (product.isShippingThirdParty()) {
            if(!ValidationUtil.isNullOrBlank(product.getShippingHours())){
                cartItem.setShippingHours(Integer.parseInt(product.getShippingHours()));
            }else{
                cartItem.setShippingHours(Integer.parseInt("0"));
            }
        } else {
            cartItem.setShippingHours(0);
        }

        return cartItem;
    }

    public static float getDeliveryCharges(float subtotal, String deliveryType){

        ArrayList<DeliveryCharges> list_charges = new ArrayList<>();
        if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())){
            try {
                JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                if(chargesArray != null){
                    list_charges.clear();
                    for(int i=0; i<chargesArray.length(); i++){
                        JSONObject object = chargesArray.optJSONObject(i);
                        if(object != null){

                            DeliveryCharges deliveryCharges = new DeliveryCharges();
                            deliveryCharges.setId(object.optString("id"));
                            deliveryCharges.setType(object.optString("type"));
                            deliveryCharges.setStart_amount((float)(object.optDouble("start_amt", 0.0)));
                            deliveryCharges.setEnd_amount((float)(object.optDouble("end_amt", 0.0)));
                            deliveryCharges.setCharges((float)(object.optDouble("d_charge", 0.0)));
                            list_charges.add(deliveryCharges);
                        }
                    }

                    if(list_charges != null){
                        for(int i=0; i<list_charges.size(); i++){
                            if(subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && deliveryType.equalsIgnoreCase(list_charges.get(i).getType())){
                                return list_charges.get(i).getCharges();
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{

        }
        return 0.0f;
    }

    public static JSONObject getOrderObj(Context context, Order order, String app_version) {

        if (order == null) {
            return null;
        }

        float subtotal = 0.0f; float savings = 0.0f; float tax_charges = 0.0f; float total_amont = 0.0f; float deliveryCharge = 0.0f;
        if (OnlineMartApplication.getCartList() != null && OnlineMartApplication.getCartList().size() > 0) {
            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {

                CartItem cartItem = OnlineMartApplication.getCartList().get(i);

                if (!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())) {
                    subtotal = subtotal + (Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                }

                if (cartItem.isOffer()) {
                    savings = savings + (Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                }
                //tax_charges = tax_charges + (cartItem.getTaxCharges() * cartItem.getSelectedQuantity());
                total_amont = subtotal + tax_charges;

            }

            tax_charges = getDeliveryCharges(subtotal, order.getDeliveyType());
            total_amont = subtotal + tax_charges;

            try {
                JSONObject orderObj = new JSONObject();

                orderObj.put("customer_id", OnlineMartApplication.mLocalStore.getUserId());
                orderObj.put("customer_name", OnlineMartApplication.mLocalStore.getFirstName() + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getLastName());

                //Added by Naresh
                orderObj.put("area_id", order.getAddress().getArea_id());
                orderObj.put("shipping_address_id", order.getAddress().getShip_id());
                orderObj.put("delivery_country_code", order.getCountryCode().replace("+", AppConstants.BLANK_STRING));
                orderObj.put("delivery_phone", order.getContactNo());
                orderObj.put("delivery_type", order.getDeliveyType());
                orderObj.put("delivery_date", order.getDeliveruDate());
                orderObj.put("payment_type", order.getPaymentMode());
                orderObj.put("doorstep", order.getPaymentMode().equalsIgnoreCase("1") ? "0" : (order.isDoorStep() ? "1" : "0"));
                orderObj.put("delivery_time_slot_id", order.getTimeSlot().getId());
                orderObj.put("card_id", order.getCardId());
                orderObj.put("status", "1");
                orderObj.put("payment_by_coupons", AppConstants.BLANK_STRING);
                orderObj.put("payment_by_points", AppConstants.BLANK_STRING);
                orderObj.put("payment_by_credits", AppUtil.mSetRoundUpPrice("" + order.getAmountByTgsCredits()));
                orderObj.put("payment_by_user", AppUtil.mSetRoundUpPrice("" + order.getAmountByUser()));
                orderObj.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                orderObj.put("app_version", app_version);
                orderObj.put("device_type", AppConstants.DEVICE_TYPE);
                orderObj.put("os_version", DeviceUtil.getOsVersion());
                orderObj.put("customer_country_code", OnlineMartApplication.mLocalStore.getUserCountryCode());
                orderObj.put("customer_mobile", OnlineMartApplication.mLocalStore.getUserPhone());
                orderObj.put("customer_type", OnlineMartApplication.mLocalStore.getUserType());
                orderObj.put("instruction", order.getInstructions());

                JSONObject chargesObj = new JSONObject();
                chargesObj.put("subtotal", AppUtil.mSetRoundUpPrice(subtotal + ""));
                chargesObj.put("delivery_charges", AppUtil.mSetRoundUpPrice(tax_charges + ""));
                chargesObj.put("tax", AppUtil.mSetRoundUpPrice(order.getTax()+""));
                chargesObj.put("total_savings", AppUtil.mSetRoundUpPrice(savings + ""));
                chargesObj.put("total_amount", AppUtil.mSetRoundUpPrice(total_amont + ""));
                chargesObj.put("payment_by_credits", AppUtil.mSetRoundUpPrice("" + order.getAmountByTgsCredits()));
                chargesObj.put("payment_by_user", AppUtil.mSetRoundUpPrice("" + order.getAmountByUser()));

                order.setTotalAmountToPay(total_amont);
                order.setSubTotal(subtotal);
                order.setDeliveryCharges(tax_charges);
                order.setTotalSavings(savings);

                JSONArray orderArray = new JSONArray();

                if (order.getList_cart() != null && order.getList_cart().size() > 0) {

                    for (int i = 0; i < order.getList_cart().size(); i++) {

                        CartItem cartItem = order.getList_cart().get(i);
                        if(cartItem.getSelectedQuantity() > 0){
                            JSONObject productObj = new JSONObject();
                            productObj.put("product_id", cartItem.getId());
                            productObj.put("name", cartItem.getLabel());
                            productObj.put("ordered_qty", cartItem.getSelectedQuantity());
                            productObj.put("doorstep", cartItem.isDoorStepDelivery() ? 1 : 0);
                            productObj.put("price", AppUtil.mSetRoundUpPrice(Float.parseFloat(cartItem.getActualPrice().replace(",", "")) + ""));
                            productObj.put("tax_per_qty", AppUtil.mSetRoundUpPrice(cartItem.getTaxCharges()+""));
                            productObj.put("saving", AppUtil.mSetRoundUpPrice(cartItem.getSavedPrice() + ""));

                            orderArray.put(orderArray.length(), productObj);
                        }
                    }
                }

                orderObj.put("order_product", orderArray);
                order.setNoOfItems(orderArray.length());
                chargesObj.put("total_item", order.getNoOfItems());
                orderObj.put("order_charges", chargesObj);
                orderObj.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                return orderObj;

            } catch (Exception e) {
                e.printStackTrace();
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getString(R.string.error_msg) + "(Err-747)", context.getString(R.string.ok));
                return null;
            }
        }

        return null;

    }

    public static JSONObject getOrderObj(Context context, Order order, String app_version, ArrayList<CartItem> list_item) {

        if (order == null) {
            return null;
        }

        float subtotal = 0.0f; float savings = 0.0f; float tax_charges = 0.0f; float total_amont = 0.0f; float deliveryCharge = 0.0f;
        if (list_item != null && list_item.size() > 0) {
            for (int i = 0; i < list_item.size(); i++) {

                CartItem cartItem = list_item.get(i);

                if (!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())) {
                    subtotal = subtotal + (Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                }

                if (cartItem.isOffer()) {
                    savings = savings + (Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                }
                //tax_charges = tax_charges + (cartItem.getTaxCharges() * cartItem.getSelectedQuantity());
                total_amont = subtotal + tax_charges;

            }

            tax_charges = getDeliveryCharges(subtotal, order.getDeliveyType());
            total_amont = subtotal + tax_charges;

            try {
                JSONObject orderObj = new JSONObject();

                orderObj.put("customer_id", OnlineMartApplication.mLocalStore.getUserId());
                orderObj.put("customer_name", OnlineMartApplication.mLocalStore.getFirstName() + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getLastName());
                orderObj.put("shipping_address_id", order.getAddress().getShip_id());
                orderObj.put("delivery_country_code", order.getCountryCode().replace("+", AppConstants.BLANK_STRING));
                orderObj.put("delivery_phone", order.getContactNo());
                orderObj.put("delivery_type", order.getDeliveyType());
                orderObj.put("delivery_date", order.getDeliveruDate());
                orderObj.put("payment_type", order.getPaymentMode());
                orderObj.put("doorstep", order.getPaymentMode().equalsIgnoreCase("1") ? "0" : (order.isDoorStep() ? "1" : "0"));
                orderObj.put("delivery_time_slot_id", order.getTimeSlot().getId());
                orderObj.put("card_id", order.getCardId());
                orderObj.put("status", "1");
                orderObj.put("payment_by_coupons", AppConstants.BLANK_STRING);
                orderObj.put("payment_by_points", AppConstants.BLANK_STRING);
                orderObj.put("payment_by_credits", AppUtil.mSetRoundUpPrice("" + order.getAmountByTgsCredits()));
                orderObj.put("payment_by_user", AppUtil.mSetRoundUpPrice("" + order.getAmountByUser()));
                orderObj.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                orderObj.put("app_version", app_version);
                orderObj.put("device_type", AppConstants.DEVICE_TYPE);
                orderObj.put("os_version", DeviceUtil.getOsVersion());
                orderObj.put("customer_country_code", OnlineMartApplication.mLocalStore.getUserCountryCode());
                orderObj.put("customer_mobile", OnlineMartApplication.mLocalStore.getUserPhone());
                orderObj.put("customer_type", OnlineMartApplication.mLocalStore.getUserType());
                orderObj.put("instruction", order.getInstructions());

                JSONObject chargesObj = new JSONObject();
                chargesObj.put("total_item", list_item.size());
                chargesObj.put("subtotal", AppUtil.mSetRoundUpPrice(subtotal + ""));
                chargesObj.put("delivery_charges", AppUtil.mSetRoundUpPrice(tax_charges + ""));
                chargesObj.put("tax", AppUtil.mSetRoundUpPrice(order.getTax()+""));
                chargesObj.put("total_savings", AppUtil.mSetRoundUpPrice(savings + ""));
                chargesObj.put("total_amount", AppUtil.mSetRoundUpPrice(total_amont + ""));
                chargesObj.put("payment_by_credits", AppUtil.mSetRoundUpPrice("" + order.getAmountByTgsCredits()));
                chargesObj.put("payment_by_user", AppUtil.mSetRoundUpPrice("" + order.getAmountByUser()));

                order.setTotalAmountToPay(total_amont);
                order.setSubTotal(subtotal);
                order.setDeliveryCharges(tax_charges);
                order.setTotalSavings(savings);

                JSONArray orderArray = new JSONArray();

                if (list_item != null && list_item.size() > 0) {

                    for (int i = 0; i < list_item.size(); i++) {

                        CartItem cartItem = list_item.get(i);
                        if(cartItem.getSelectedQuantity() > 0){
                            JSONObject productObj = new JSONObject();
                            productObj.put("product_id", cartItem.getId());
                            productObj.put("name", cartItem.getLabel());
                            productObj.put("ordered_qty", cartItem.getSelectedQuantity());
                            productObj.put("doorstep", cartItem.isDoorStepDelivery() ? 1 : 0);
                            productObj.put("price", AppUtil.mSetRoundUpPrice(Float.parseFloat(cartItem.getActualPrice().replace(",", "")) + ""));
                            productObj.put("tax_per_qty", AppUtil.mSetRoundUpPrice(cartItem.getTaxCharges()+""));
                            productObj.put("saving", AppUtil.mSetRoundUpPrice(cartItem.getSavedPrice() + ""));

                            orderArray.put(orderArray.length(), productObj);
                        }
                    }
                }

                orderObj.put("order_product", orderArray);
                order.setNoOfItems(orderArray.length());
                chargesObj.put("total_item", order.getNoOfItems());
                orderObj.put("order_charges", chargesObj);
                orderObj.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                return orderObj;

            } catch (Exception e) {
                e.printStackTrace();
                AppUtil.displaySingleActionAlert(context, AppConstants.BLANK_STRING, context.getString(R.string.error_msg) + "(Err-748)", context.getString(R.string.ok));
                return null;
            }
        }

        return null;

    }

    public static AppDialogDoubleAction requestLogin(final Context mContext) {

        final AppDialogDoubleAction loginAlertDialog = new AppDialogDoubleAction(
                mContext,
                mContext.getString(R.string.app_name),
                mContext.getString(R.string.do_you_want_to_login_into_OnlineMart),
                mContext.getString(R.string.cancel),
                mContext.getString(R.string.login));

        loginAlertDialog.show();
        loginAlertDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
            @Override
            public void onLeftActionClick(View view) {
                loginAlertDialog.dismiss();
            }

            @Override
            public void onRightActionClick(View view) {
                loginAlertDialog.dismiss();
                mContext.startActivity(new Intent(mContext, LoginModuleActivity.class));
            }
        });
        return loginAlertDialog;
    }

    public static Bitmap getBitmapFromPath(String filepath) {
        return loadBitmap(filepath, getCameraPhotoOrientation(filepath));
    }

    public static int getCameraPhotoOrientation(String imagePath) {

        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Log.v("ORIENTATION", "Exif orientation: " + orientation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap loadBitmap(String path, int orientation) {

        Bitmap bitmap = null;

        try {

            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(path);

            long size = bitmap.getRowBytes() * bitmap.getHeight();

            if (size < 1000000) {
                options.inSampleSize = 1;
            } else if (size < 3000000) {
                options.inSampleSize = 2;
            } else if (size < 5000000) {
                options.inSampleSize = 3;
            } else if (size < 7000000) {
                options.inSampleSize = 4;
            } else if (size < 9000000) {
                options.inSampleSize = 5;
            } else if (size < 1200000) {
                options.inSampleSize = 6;
            } else if (size > 1500000) {
                options.inSampleSize = 5;
            } else if (size > 3000000) {
                options.inSampleSize = 14;
            } else if (size > 6000000) {
                options.inSampleSize = 16;
            }

            bitmap = BitmapFactory.decodeFile(path, options);

            // Rotate the bitmap if required
            if (orientation > 0) {

                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("In LoadBitmap ", e.toString());
        }
        return bitmap;
    }

    public static void setKeyboardVisibilityListener(Activity activity, final KeyboardVisibilityListener keyboardVisibilityListener) {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    keyboardVisibilityListener.onKeyboardVisibilityChanged(true);
                } else {
                    // keyboard is closed
                    keyboardVisibilityListener.onKeyboardVisibilityChanged(false);
                }
            }
        });
    }

    public static boolean isAppRunning(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals("octal.com.onlinemart")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static BigDecimal mSetRoundUpPrice(String price)
    {
        BigDecimal bdTest = new BigDecimal(price);
        bdTest = bdTest.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bdTest;
    }

    public static int showDBErrorDialog(Context mContext, String msg) {

        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            return 0;
        }

        mErrorDialog = new AppDialogSingleAction(mContext, mContext.getString(R.string.app_name), msg, mContext.getString(R.string.ok));
        mErrorDialog.show();
        mErrorDialog.setOnSingleActionListener(new OnSingleActionListener() {
            @Override
            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                Runtime.getRuntime().exit(0);
            }
        });
        return 1;
    }

    public static String getDecimalForamte(int number){
        DecimalFormat decimalFormat = (((DecimalFormat)(NumberFormat.getNumberInstance(new Locale("en")))));
        decimalFormat.applyPattern("00");
        return decimalFormat.format(number);
    }
}