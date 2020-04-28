package thegroceryshop.com.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.modal.sildemenu_model.CategoryItem;
import thegroceryshop.com.modal.sildemenu_model.NestedCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.PromotionCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.SequenceNumberSorter;
import thegroceryshop.com.service.LoadInitialData;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.service.UpdateCartService;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.utils.Utils;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class SplashActivity extends AppCompatActivity {
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
    };
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private boolean isUpdateAvailable;
    private boolean isForceUpdate;
    private boolean isDataLoaded;
    private boolean isHandlerExcauted;
    private int MIN_TIME_TO_DISPLAY = 3000;
    private Runnable splash_runnable;
    private Handler splash_hadler;
    private boolean isFromNotification;
    private boolean isUserActive;
    private boolean isSplashRunning;
    private AlertDialog alert;
    private int PERM_REQUEST_CODE_DRAW_OVERLAYS = 554;
    private boolean shouldAskForMorePermisiions;
    private int retryCount = 0;

    private AppDialogDoubleAction rateUsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(OnlineMartApplication.isSplashRunning){
            finish();
        }

        //new MaterialCalendarView(SplashActivity.this);

        LoadUsualData.loadShippingCharges(this);
        LoadUsualData.loadWishLists(this);
        if(!OnlineMartApplication.mLocalStore.isCartUpdated()){
            UpdateCartService.startUpdateCartService(SplashActivity.this);
        }

        //Utils.arabicToDecimal("٣");
        if(getIntent() != null){
            isFromNotification = (getIntent().getBooleanExtra("isFromNotification", false));
            if(OnlineMartApplication.nestedCategory == null || OnlineMartApplication.nestedCategory.size() == 0){
                setContentView(R.layout.layout_splash_screen);
                permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
            }else{
                navigateSplashTo();
            }
        }else{
            setContentView(R.layout.layout_splash_screen);
            permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        }

        splash_hadler = new Handler();
        splash_runnable = new Runnable() {
            @Override
            public void run() {
                isHandlerExcauted = true;
                if(isDataLoaded){
                    navigateSplashTo();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        isSplashRunning = true;
        OnlineMartApplication.isSplashRunning = true;

        if(OnlineMartApplication.mLocalStore.isLanguageSelected()){

            Configuration config = getBaseContext().getResources().getConfiguration();
            String lang = OnlineMartApplication.mLocalStore.getAppLanguage();
            String systemLocale = getSystemLocale(config).getLanguage();

            if (!systemLocale.startsWith(lang)) {
                LocaleHelper.setLocale(getBaseContext(), lang);
                proceedToSplashTask();
            }else{
                proceedToSplashTask();
            }

        }else{

            final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(SplashActivity.this, getString(R.string.language_confirmation), getString(R.string.language_selection), getString(R.string.language_arabic), getString(R.string.language_english));
            appDialogDoubleAction.show();
            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                appDialogDoubleAction.setLeftLabelFont(ResourcesCompat.getFont(this, R.font.ge_ss_two_medium));
            }else{
                appDialogDoubleAction.setRightLabelFont(ResourcesCompat.getFont(this, R.font.montserrat_regular));
            }

            appDialogDoubleAction.setCanceledOnTouchOutside(false);
            appDialogDoubleAction.setCancelable(false);
            appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                @Override
                public void onLeftActionClick(View view) {
                    appDialogDoubleAction.dismiss();
                    OnlineMartApplication.mLocalStore.saveLanguageSelected(true);
                    OnlineMartApplication.mLocalStore.setAppLanguage("ar", getApplicationContext());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OnlineMartApplication.restartApplication(SplashActivity.this);
                        }
                    }, 1000);

                }

                @Override
                public void onRightActionClick(View view) {
                    appDialogDoubleAction.dismiss();
                    OnlineMartApplication.mLocalStore.saveLanguageSelected(true);
                    OnlineMartApplication.mLocalStore.setAppLanguage("en", getApplicationContext());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OnlineMartApplication.restartApplication(SplashActivity.this);
                        }
                    }, 1000);

                }
            });

        }

        LoadInitialData.loadInitialData(this);
    }

    public void proceedToSplashTask(){

        if(!OnlineMartApplication.mLocalStore.isAlreadyRated()){
            if(OnlineMartApplication.mLocalStore.getAppRunCounter() >= 5){
                OnlineMartApplication.mLocalStore.saveAppRunCounter(0);

                if(rateUsDialog == null){
                    if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                        rateUsDialog = new AppDialogDoubleAction(SplashActivity.this, getString(R.string.app_name), "How are you doing with The Grocery Shop, Would you like to rate us on Google Play Store.", "LATER", "RATE US");
                        rateUsDialog.setFont(ResourcesCompat.getFont(this, R.font.montserrat_regular));
                    }else{
                        rateUsDialog = new AppDialogDoubleAction(SplashActivity.this, getString(R.string.app_name), "رأيك يهمنا , هل تريد تقييمنا علي متجر جوجل؟", "في وقت لاحق", "نعم");
                        rateUsDialog.setFont(ResourcesCompat.getFont(this, R.font.ge_ss_two_medium));
                    }
                }

                if(!rateUsDialog.isShowing()){
                    if (NetworkUtil.networkStatus(SplashActivity.this)) {
                        rateUsDialog.show();
                    }
                }
                rateUsDialog.setCanceledOnTouchOutside(false);
                rateUsDialog.setCancelable(false);
                rateUsDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                    @Override
                    public void onLeftActionClick(View view) {
                        rateUsDialog.dismiss();
                        if(splash_hadler != null){
                            splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                        }
                        loadCategoriesForSideMenu();

                    }

                    @Override
                    public void onRightActionClick(View view) {
                        rateUsDialog.dismiss();
                        OnlineMartApplication.mLocalStore.saveIsAlreadyRated(true);

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_APP_STORE)));
                        }

                    }
                });
            }else{
                if(splash_hadler != null){
                    splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                }
                loadCategoriesForSideMenu();
                /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    mCheckPermission();
                } else {
                    if(splash_hadler != null){
                        splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                    }
                    loadCategoriesForSideMenu();
                }*/
            }
        }else{
            if(splash_hadler != null){
                splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
            }
            loadCategoriesForSideMenu();
            /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                mCheckPermission();
            } else {
                if(splash_hadler != null){
                    splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                }
                loadCategoriesForSideMenu();
            }*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isSplashRunning = false;
        OnlineMartApplication.isSplashRunning = false;
        if(splash_hadler != null){
            splash_hadler.removeCallbacks(splash_runnable);
        }
    }

    private void holdSplashScreen() {
        Thread timerThread = new Thread() {

            public void run() {
                try {

                    //this method will be used for holding current activity for giving time
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

                /**
                 * Method calling for starting new activity
                 */
                navigateSplashTo();
            }
        };
        timerThread.start();
    }

    /**
     * This method is used for starting new activity using intent
     */
    private void navigateSplashTo() {

        Utils.setDeviceLanguage(SplashActivity.this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //***************Start App Introduction Activity *****************************//
                if(isUpdateAvailable){
                    if(!isForceUpdate){
                        final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(SplashActivity.this, getString(R.string.app_name), getString(R.string.app_update_message), getString(R.string.later).toUpperCase(), getString(R.string.update).toUpperCase());
                        appDialogDoubleAction.show();
                        appDialogDoubleAction.setCanceledOnTouchOutside(false);
                        appDialogDoubleAction.setCancelable(false);
                        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                            @Override
                            public void onLeftActionClick(View view) {
                                appDialogDoubleAction.dismiss();
                                if(isHandlerExcauted && OnlineMartApplication.getRegions().size() > 0){
                                    if (OnlineMartApplication.getSharedPreferences().getBoolean("appIntro", false)) {
                                        Intent intent;
                                        //intent = new Intent(SplashActivity.this, SildeMenuActivity.class);
                                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("isFromNotification", isFromNotification);
                                        intent.putExtra("isUserActive", isUserActive);
                                        startActivity(intent);
                                        //Finish current splash activity object
                                        finish();
                                    } else {
                                        Intent intent;
                                        intent = new Intent(SplashActivity.this, AppIntro_Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("isFromNotification", isFromNotification);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onRightActionClick(View view) {
                                appDialogDoubleAction.dismiss();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_APP_STORE)));
                                }
                            }
                        });
                    }
                }else{
                    if (OnlineMartApplication.getSharedPreferences().getBoolean("appIntro", false)) {
                        Intent intent;
                        //intent = new Intent(SplashActivity.this, SildeMenuActivity.class);
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("isFromNotification", isFromNotification);
                        intent.putExtra("isUserActive", isUserActive);
                        startActivity(intent);
                        //Finish current splash activity object
                        finish();
                    } else {
                        Intent intent;
                        intent = new Intent(SplashActivity.this, AppIntro_Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("isFromNotification", isFromNotification);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }

    /**
     * Check Run Time to access app
     */

    private void mCheckPermission() {
        //permissionToDrawOverlays();
        shouldAskForMorePermisiions = true;
        if(shouldAskForMorePermisiions){

            if (ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                /*|| ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED*/
                    ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[3])
                    /*|| ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[4])*/
                        ) {
                    //Show Information about why you need the permission

                    if(alert == null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                        builder.setTitle(getResources().getString(R.string.multiple_title));
                        builder.setMessage(getResources().getString(R.string.msg_txt));
                        builder.setPositiveButton(getResources().getString(R.string.grant_txt), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert = null;
                                dialog.cancel();
                                ActivityCompat.requestPermissions(SplashActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton(getResources().getString(R.string.cancel_txt), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert = null;
                                dialog.cancel();
                                finish();
                            }
                        });
                        alert = builder.show();
                    }

                } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission

                    if(alert == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                        builder.setTitle(getResources().getString(R.string.multiple_title));
                        builder.setMessage(getResources().getString(R.string.msg_txt));
                        builder.setPositiveButton(getResources().getString(R.string.grant_txt), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert = null;
                                dialog.cancel();
                                //sentToSettings = true;
                                ActivityCompat.requestPermissions(SplashActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton(getResources().getString(R.string.cancel_txt), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert = null;
                                dialog.cancel();
                                finish();
                            }
                        });
                        alert = builder.show();
                    }
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(SplashActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }


                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permissionsRequired[0], true);
                editor.commit();

            } else {

                if(splash_hadler != null){
                    splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                }

                loadCategoriesForSideMenu();

                //You already have the permission, just go ahead.
                //holdSplashScreen();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {

                if(splash_hadler != null){
                    splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                }

                loadCategoriesForSideMenu();

                //holdSplashScreen();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permissionsRequired[4])
                    ) {

                if(alert == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle(getResources().getString(R.string.multiple_title));
                    builder.setMessage(getResources().getString(R.string.msg_txt));
                    builder.setPositiveButton(getResources().getString(R.string.grant_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert = null;
                            dialog.cancel();
                            ActivityCompat.requestPermissions(SplashActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert = null;
                            dialog.cancel();
                            finish();
                        }
                    });
                    alert = builder.show();
                }
            } else {

                if(alert == null){

                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle(getResources().getString(R.string.multiple_title));
                    builder.setMessage(getResources().getString(R.string.msg_txt));
                    builder.setPositiveButton(getResources().getString(R.string.grant_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert = null;
                            dialog.cancel();

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, PERMISSION_CALLBACK_CONSTANT);

                            //ActivityCompat.requestPermissions(SplashActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert = null;
                            dialog.cancel();
                            finish();
                        }
                    });
                    alert = builder.show();
                }

                //Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                if(splash_hadler != null){
                    splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                }
                //holdSplashScreen();
            }
        }

        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {

                    shouldAskForMorePermisiions = false;

                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle(getResources().getString(R.string.multiple_title));
                    builder.setMessage(getResources().getString(R.string.msg_txt));
                    builder.setPositiveButton(getResources().getString(R.string.grant_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert = null;
                            dialog.cancel();
                            mCheckPermission();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert = null;
                            dialog.cancel();
                            finish();
                        }
                    });
                    alert = builder.show();
                }else{
                    shouldAskForMorePermisiions = true;
                    mCheckPermission();
                }
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                if(splash_hadler != null){
                    splash_hadler.postDelayed(splash_runnable, MIN_TIME_TO_DISPLAY);
                }
                //holdSplashScreen();
            }
        }
    }

    /**
     * Call webservice and get all course details
     */
    private void loadCategoriesForSideMenu() {

        retryCount = retryCount + 1;

        if(retryCount < 6){

            if(alert != null && alert.isShowing()){
                alert.dismiss();
            }

            if (NetworkUtil.networkStatus(SplashActivity.this)) {

                try {
                    JSONObject request_data = new JSONObject();
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("device_type", AppConstants.DEVICE_TYPE);
                    request_data.put("os_version", DeviceUtil.getOsVersion());
                    request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                    request_data.put("app_version", DeviceUtil.getVersionName(this));
                    request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                    ApiInterface apiService = ApiClient.createService(ApiInterface.class, SplashActivity.this);
                    Call<ResponseBody> call = apiService.mGetSildeMenuCategory((new ConvertJsonToMap().jsonToMap(request_data)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {

                                if(response != null){
                                    if(response.code() == 404){

                                        isDataLoaded = false;
                                        if(isSplashRunning){
                                            AppUtil.displaySingleActionAlert(SplashActivity.this, getString(R.string.app_name), getString(R.string.msg_404), getString(R.string.exit));
                                        }

                                    }else{
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        if (jsonObject.getJSONObject("response").getInt("status_code") == 200) {

                                            retryCount = 0;

                                            isUserActive = jsonObject.getJSONObject("response").optBoolean("user_active", true);
                                            OnlineMartApplication.mLocalStore.saveUserActive(isUserActive);

                                            isForceUpdate = jsonObject.getJSONObject("response").optBoolean("is_force_update", false);
                                            isUpdateAvailable = jsonObject.getJSONObject("response").optBoolean("is_update_available", false);
                                            isDataLoaded = true;

                                            if(isForceUpdate){
                                                final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(SplashActivity.this, getString(R.string.app_name), getString(R.string.app_force_update_message), getString(R.string.later).toUpperCase(), getString(R.string.update).toUpperCase());
                                                appDialogDoubleAction.show();
                                                appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                                appDialogDoubleAction.setCancelable(false);
                                                appDialogDoubleAction.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                });
                                                appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                    @Override
                                                    public void onLeftActionClick(View view) {
                                                        appDialogDoubleAction.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onRightActionClick(View view) {
                                                        appDialogDoubleAction.dismiss();
                                                        try {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                                        } catch (ActivityNotFoundException e) {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_APP_STORE)));
                                                        }

                                                    }
                                                });
                                            }else{

                                                OnlineMartApplication.promotionCategory.clear();

                                                JSONObject dataJsonObject = jsonObject.getJSONObject("response").getJSONObject("data");

                                                if (dataJsonObject.get("PromotionCategory") instanceof JSONArray) {
                                                    JSONArray promotionJsonArray = dataJsonObject.getJSONArray("PromotionCategory");

                                                    for (int promoIndex = 0; promoIndex < promotionJsonArray.length(); promoIndex++) {
                                                        JSONObject promoData = promotionJsonArray.getJSONObject(promoIndex);

                                                        PromotionCategoryItem promotionCategoryItem = new PromotionCategoryItem();
                                                        promotionCategoryItem.setId(promoData.optString("id"));
                                                        promotionCategoryItem.setName(promoData.optString("name"));
                                                        promotionCategoryItem.setImage(promoData.optString("image"));
                                                        promotionCategoryItem.setApp_ican_id(promoData.optInt("app_ican_id"));
                                                        OnlineMartApplication.promotionCategory.add(promotionCategoryItem);
                                                    }
                                                }

                                                OnlineMartApplication.nestedCategory.clear();

                                                if (dataJsonObject.get("NestedCategory") instanceof JSONArray) {

                                                    JSONArray nestedCatJsonArray = dataJsonObject.getJSONArray("NestedCategory");

                                                    for (int nestedIndex = 0; nestedIndex < nestedCatJsonArray.length(); nestedIndex++) {
                                                        JSONObject nestedData = nestedCatJsonArray.getJSONObject(nestedIndex);

                                                        NestedCategoryItem nestedCategoryItem = new NestedCategoryItem();
                                                        nestedCategoryItem.setCategoryId(nestedData.optString("category_id"));
                                                        nestedCategoryItem.setImage(nestedData.optString("image"));
                                                        nestedCategoryItem.setName(nestedData.optString("name"));
                                                        nestedCategoryItem.setLevel(nestedData.optString("level"));
                                                        nestedCategoryItem.setSequenceNumber(nestedData.optString("sequence_number"));
                                                        nestedCategoryItem.setAdminCategoryLevel(nestedData.optString("admin_category_level"));
                                                        nestedCategoryItem.setParentId( nestedData.optString("parent_id"));
                                                        nestedCategoryItem.setProductCount( nestedData.optString("product_count"));
                                                        nestedCategoryItem.setApp_ican_id(nestedData.optInt("app_ican_id"));

                                                        if (nestedData.has("Category")) {
                                                            if (nestedData.get("Category") instanceof JSONArray) {
                                                                JSONArray categoryJsonArray = nestedData.getJSONArray("Category");
                                                                List<CategoryItem> category = new ArrayList<>();

                                                                for (int catIndex = 0; catIndex < categoryJsonArray.length(); catIndex++) {
                                                                    JSONObject catData = categoryJsonArray.getJSONObject(catIndex);
                                                                    CategoryItem categoryItem = new CategoryItem();
                                                                    categoryItem.setName(catData.optString("name"));
                                                                    categoryItem.setImage(catData.optString("image"));
                                                                    categoryItem.setLevel(catData.optString("level"));
                                                                    categoryItem.setAdminCategoryLevel(catData.optString("admin_category_level"));
                                                                    categoryItem.setParentId(catData.optString("parent_id"));
                                                                    categoryItem.setProduct_count(catData.optString("product_count"));
                                                                    categoryItem.setCategoryId(catData.optString("category_id"));


                                                                    if (catData.has("Category")) {
                                                                        JSONArray subcategoryJsonArray = catData.getJSONArray("Category");
                                                                        List<CategoryItem> sub_category = new ArrayList<>();

                                                                        for (int subIndex = 0; subIndex < subcategoryJsonArray.length(); subIndex++) {
                                                                            JSONObject subcatData = subcategoryJsonArray.getJSONObject(subIndex);
                                                                            CategoryItem subcategoryItem = new CategoryItem();
                                                                            subcategoryItem.setName(subcatData.optString("name"));
                                                                            subcategoryItem.setImage(subcatData.optString("image"));
                                                                            subcategoryItem.setLevel(subcatData.optString("level"));
                                                                            subcategoryItem.setAdminCategoryLevel(subcatData.optString("admin_category_level"));
                                                                            subcategoryItem.setParentId(subcatData.optString("parent_id"));
                                                                            subcategoryItem.setProduct_count(subcatData.optString("product_count"));
                                                                            subcategoryItem.setCategoryId(subcatData.optString("category_id"));

                                                                            sub_category.add(subcategoryItem);
                                                                        }
                                                                        categoryItem.setCategory(sub_category);
                                                                    }

                                                                    category.add(categoryItem);
                                                                }
                                                                nestedCategoryItem.setCategory(category);
                                                            }
                                                        }

                                                        if(!nestedCategoryItem.getProduct_count().equalsIgnoreCase("0")){
                                                            OnlineMartApplication.nestedCategory.add(nestedCategoryItem);
                                                        }
                                                    }
                                                }

                                                Collections.sort(OnlineMartApplication.nestedCategory, new SequenceNumberSorter());

                                                isDataLoaded = true;
                                                if(isHandlerExcauted && OnlineMartApplication.getRegions().size() > 0){
                                                    navigateSplashTo();
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    isDataLoaded = false;
                                    loadCategoriesForSideMenu();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                isDataLoaded = false;
                                loadCategoriesForSideMenu();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            isDataLoaded = false;
                            if(!call.isCanceled()){
                                if(isSplashRunning){
                                    AppUtil.displaySingleActionAlert(SplashActivity.this, getString(R.string.app_name), getString(R.string.msg_404), getString(R.string.exit));
                                }
                            }else{
                                loadCategoriesForSideMenu();
                            }
                        }
                    });
                }catch (Exception e){
                    isDataLoaded = false;
                    loadCategoriesForSideMenu();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static Locale getSystemLocale(Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.getLocales().get(0);
        } else {
            return config.locale;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
