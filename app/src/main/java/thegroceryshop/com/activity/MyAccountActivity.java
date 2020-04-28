package thegroceryshop.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.Pinview;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.LogOutDialog;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.loginModule.LoginModuleActivity;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/*
 * Created by umeshk on 21-Feb-17.
 */

public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    private TextView login_logout_btn;
    private LinearLayout lyt_my_savings, lyt_account_detail, lyt_payment_method, lyt_language, lyt_tgs_credit, lyt_tgs_points, lyt_my_coupons, lyt_manage_address,
            lyt_manage_cards, lyt_recurring_orders, lyt_faq, lyt_contact_us, lyt_system_notifications, lyt_about_us, lyt_login_logout;

    private Toolbar toolbar;
    private TextView txt_title;
    private LoaderLayout loader;
    private Pinview weightView;
    private MenuItem mSerachMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_account);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
        toolbar.setNavigationIcon(R.mipmap.top_back);
        txt_title.setText(getString(R.string.my_account).toUpperCase());

        initView();
        setOnClickListener();
        LoadUsualData.loadWishLists(this);
    }

    private void initView() {

        lyt_account_detail = findViewById(R.id.lyt_account_detail);
        lyt_payment_method = findViewById(R.id.lyt_payment_method);
        lyt_language = findViewById(R.id.lyt_language);
        lyt_tgs_credit = findViewById(R.id.lyt_tgs_credit);
        lyt_tgs_points = findViewById(R.id.lyt_tgs_points);
        lyt_manage_address = findViewById(R.id.lyt_manage_address);
        lyt_faq = findViewById(R.id.lyt_faq);
        lyt_contact_us = findViewById(R.id.lyt_contact_us);
        lyt_system_notifications = findViewById(R.id.lyt_system_notifications);
        lyt_about_us = findViewById(R.id.lyt_about_us);
        lyt_login_logout = findViewById(R.id.lyt_login_logout);
        lyt_my_coupons = findViewById(R.id.lyt_my_coupons);
        lyt_my_savings = findViewById(R.id.lyt_my_savings);
        
        login_logout_btn = findViewById(R.id.txtViewLogin);
        loader = findViewById(R.id.my_account_loader);
        weightView = findViewById(R.id.my_account_pinview);
        weightView.setSplitWidth(10);
        weightView.setEnabled(false);
    }

    private void setOnClickListener() {
        lyt_my_savings.setOnClickListener(this);
        lyt_account_detail.setOnClickListener(this);
        lyt_payment_method.setOnClickListener(this);
        lyt_language.setOnClickListener(this);
        lyt_tgs_credit.setOnClickListener(this);
        lyt_tgs_points.setOnClickListener(this);
        lyt_manage_address.setOnClickListener(this);
        lyt_faq.setOnClickListener(this);
        lyt_contact_us.setOnClickListener(this);
        lyt_system_notifications.setOnClickListener(this);
        lyt_about_us.setOnClickListener(this);
        lyt_login_logout.setOnClickListener(this);
        lyt_my_coupons.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.lyt_my_savings:
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentStore;
                    intentStore = new Intent(this, MySavingActivity.class);
                    intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentStore);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_account_detail:

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentStore;
                    intentStore = new Intent(this, AccountDetailsActivity.class);
                    intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentStore);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_payment_method:
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentPayment;
                    intentPayment = new Intent(this, PaymentMethodActivity.class);
                    intentPayment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentPayment);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_language:

                final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(MyAccountActivity.this, getString(R.string.language_confirmation), getString(R.string.language_selection), getString(R.string.language_arabic), getString(R.string.language_english));
                appDialogDoubleAction.show();
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    appDialogDoubleAction.setLeftLabelFont(ResourcesCompat.getFont(this, R.font.ge_ss_two_medium));
                }else{
                    appDialogDoubleAction.setRightLabelFont(ResourcesCompat.getFont(this, R.font.montserrat_regular));
                }
                appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                    @Override
                    public void onLeftActionClick(View view) {

                        appDialogDoubleAction.dismiss();
                        if(!OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")){
                            OnlineMartApplication.mLocalStore.saveLanguageSelected(true);
                            OnlineMartApplication.mLocalStore.setAppLanguage("ar", getApplicationContext());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    OnlineMartApplication.restartApplication(MyAccountActivity.this);
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onRightActionClick(View view) {
                        appDialogDoubleAction.dismiss();
                        if(!OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                            OnlineMartApplication.mLocalStore.saveLanguageSelected(true);
                            OnlineMartApplication.mLocalStore.setAppLanguage("en", getApplicationContext());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    OnlineMartApplication.restartApplication(MyAccountActivity.this);
                                }
                            }, 1000);
                        }

                    }
                });
                break;

            case R.id.lyt_tgs_credit:

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentPayment;
                    intentPayment = new Intent(this, TGSCreditsActivity.class);
                    startActivity(intentPayment);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_manage_address:
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentSelectYourAddress;
                    intentSelectYourAddress = new Intent(this, ManageShippingAddressActivity.class);
                    intentSelectYourAddress.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentSelectYourAddress);
                    break;
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_my_coupons:

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentPayment;
                    intentPayment = new Intent(this, MyCouponsActivity.class);
                    startActivity(intentPayment);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_faq:
                Intent intentFAQ;
                intentFAQ = new Intent(this, FAQActivity.class);
                intentFAQ.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentFAQ);
                break;

            case R.id.lyt_contact_us:
                Intent intentContactUs;
                intentContactUs = new Intent(this, ContactUsActivity.class);
                intentContactUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentContactUs);
                break;

            case R.id.lyt_system_notifications:

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentNotification;
                    intentNotification = new Intent(this, NotificationActivity.class);
                    intentNotification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentNotification);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;

            case R.id.lyt_about_us:
                Intent intentAbout;
                intentAbout = new Intent(this, AboutActivity.class);
                intentAbout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentAbout);
                break;

            case R.id.lyt_login_logout:
                if (ApiLocalStore.getInstance(MyAccountActivity.this).getUserId().length() > 0) {
                    LogOutDialog logOutDialog = new LogOutDialog(this);
                    logOutDialog.show();
                    logOutDialog.setOnLogOutAlertListener(new LogOutDialog.OnLogOutAlertListener() {
                        @Override
                        public void onCancelClick(View view) {

                        }

                        @Override
                        public void onLogoutClick(View view) {
                            logoutUser();
                        }
                    });
                } else {
                    Intent intent = new Intent(this, LoginModuleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;

            /*case R.id.textViewLanguage:
                Intent intentLang;
                intentLang = new Intent(this, SelectLanguageActivity.class);
                intentLang.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentLang);
                break;*/

            /*case R.id.textViewSavings:
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentSaving;
                    intentSaving = new Intent(this, MySavingActivity.class);
                    intentSaving.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentSaving);
                } else {
                    AppUtil.requestLogin(this);
                }
                break;*/

            /*case textViewMyCoupons:
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    Intent intentCoupons;
                    intentCoupons = new Intent(this, MyCouponsActivity.class);
                    intentCoupons.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentCoupons);

                } else {
                    AppUtil.requestLogin(this);
                }
                break;*/
        }
    }

    private void logoutUser() {

        try {

            AppUtil.showProgress(MyAccountActivity.this);
            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request.put("device_type", AppConstants.DEVICE_TYPE);
            request.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());

            ApiInterface apiService = ApiClient.createService(ApiInterface.class, this);
            Call<ResponseBody> call = apiService.logout((new ConvertJsonToMap().jsonToMap(request)));
            APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                //call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppUtil.hideProgress();
                    if (response.code() == 200) {
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            JSONObject resObj = obj.optJSONObject("response");
                            if (resObj.length() > 0) {

                                int statusCode = resObj.optInt("status_code", 0);
                                String status = resObj.optString("status");
                                String errorMsg = resObj.optString("error_message");
                                if (statusCode == 200 && status != null && status.equalsIgnoreCase("OK")) {

                                    OnlineMartApplication.mLocalStore.clearOnLogout();
                                    OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                    OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                    OnlineMartApplication.getCartList().clear();

                                    if (ApiLocalStore.getInstance(MyAccountActivity.this).getUserId().length() > 0) {
                                        login_logout_btn.setText(getString(R.string.logout));
                                    } else {
                                        login_logout_btn.setText(getString(R.string.login));
                                    }
                                    finish();

                                } else {
                                    AppUtil.showErrorDialog(MyAccountActivity.this, errorMsg);
                                }

                            } else {
                                AppUtil.showErrorDialog(MyAccountActivity.this, getString(R.string.error_msg) + "(Err-697)");
                            }

                        } catch (Exception e) {
                            AppUtil.showErrorDialog(MyAccountActivity.this, getString(R.string.error_msg) + "(Err-698)");
                            e.printStackTrace();
                        }
                    } else {
                        AppUtil.showErrorDialog(MyAccountActivity.this, getString(R.string.error_msg) + "(Err-699)");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppUtil.hideProgress();
                    AppUtil.showErrorDialog(MyAccountActivity.this, getString(R.string.error_msg) + "(Err-700)");
                }
            });

        } catch (JSONException e) {
            AppUtil.hideProgress();
            AppUtil.showErrorDialog(MyAccountActivity.this, getString(R.string.error_msg) + "(Err-701)");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        super.setPageTitle(getString(R.string.my_account));
        super.setSearchBarVisiblity(false);
        super.setToolbarTag(getString(R.string.back));

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            toolbar.setNavigationIcon(R.mipmap.top_back);
        }else{
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        if (ApiLocalStore.getInstance(MyAccountActivity.this).getUserId().length() > 0) {
            login_logout_btn.setText(getString(R.string.logout));
            loadTotalWeight();
            OnlineMartApplication.loadUserProfile(this);
        } else {
            loader.showContent();
            login_logout_btn.setText(getString(R.string.login));
            weightView.setPinLength(1);
            weightView.setValue("0");
        }
    }

    private void loadTotalWeight() {

        if (NetworkUtil.networkStatus(this)) {
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                loader.showProgress();
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, this);
                Call<ResponseBody> call = apiService.getTotalWeight((new ConvertJsonToMap().jsonToMap(request_data)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loader.showContent();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                if (resObject.getInt("status_code") == 200) {

                                    String data = resObject.optString("data");
                                    if(!ValidationUtil.isNullOrBlank(data)){
                                        try {
                                            if (data.contains("."))
                                            {
                                                String value [] = data.trim().split("\\.");
                                                weightView.setPinLength(value[0].length());
                                                weightView.setValue(value[0]);
                                            }else
                                            {
                                                weightView.setPinLength(data.length());
                                                weightView.setValue(data);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        if(data.length() == 0){
                                            weightView.setPinLength(1);
                                            weightView.setValue("0");
                                        }
                                    }else{
                                        weightView.setPinLength(1);
                                        weightView.setValue("0");
                                    }

                                    weightView.setEnabled(false);
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loader.showContent();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                loader.showContent();
            }
        }else{
            loader.showContent();
            weightView.setPinLength(1);
            weightView.setValue("0");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.serach_menu:
                Intent searchIntent = new Intent(MyAccountActivity.this, SearchingActivity.class);
                startActivity(searchIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSerachMenu  =  menu.findItem(R.id.serach_menu);
        mSerachMenu.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }
}
