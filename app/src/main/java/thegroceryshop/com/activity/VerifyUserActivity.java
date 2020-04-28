package thegroceryshop.com.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.Pinview;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by mohitd on 16-Feb-17.
 */

public class VerifyUserActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewOTP;
    private TextView textView;
    private RippleButton rippleButton;
    private LinearLayout footer;
    private int size=1;
    private String mOtpNumber = "", mSignupRequest = "";
    private Pinview pinview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_otp_screen);
        initView();

        if (getIntent() !=null)
        {
            if (getIntent().getExtras()!=null)
            {
                if (getIntent().getExtras().getString("otpNumber")!=null)
                {
                    mOtpNumber =  getIntent().getExtras().getString("otpNumber");
                    mSignupRequest = getIntent().getExtras().getString("signUpdata");
                }
            }
        }

        setOnClickListener();
    }

    private void initView()
    {
        textViewOTP = findViewById(R.id.textViewOTP);
        textView = findViewById(R.id.textView);
        rippleButton = findViewById(R.id.rippleButton);
        footer = findViewById(R.id.mCheckoutBtn);
        pinview = findViewById(R.id.opt_pinView);
    }

    private void setOnClickListener(){
        rippleButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rippleButton:
                AppUtil.hideSoftKeyboard(this, this);
                if (mOtpNumber.equalsIgnoreCase(pinview.getValue().toString()))
                {
                    mUserRegister();
                }else
                {
                    AppUtil.displaySingleActionAlert(VerifyUserActivity.this, getString(R.string.app_name),getString(R.string.otp_error), getString(R.string.ok));
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(this, this);
    }

    /**
     * Call webservice and get all course details
     */
    private void mUserRegister()
    {
        if (NetworkUtil.networkStatus(VerifyUserActivity.this))
        {
            try {
                AppUtil.showProgress(VerifyUserActivity.this);
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, VerifyUserActivity.this);
                Call<ResponseBody> call = apiService.mRegisterRequest((new ConvertJsonToMap().jsonToMap(new JSONObject(mSignupRequest))));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if (response.code() == 200) {
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                JSONObject resObj = obj.optJSONObject("response");
                                if (resObj.length() > 0) {

                                    int statusCode = resObj.optInt("status_code", 0);
                                    String errorMsg = resObj.optString("error_message");
                                    if (statusCode == 200) {

                                        String responseMessage = resObj.optString("success_message");
                                        JSONObject dataObj = resObj.optJSONObject("data");
                                        if (dataObj != null && dataObj.length() > 0) {

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("user_id"))){
                                                OnlineMartApplication.mLocalStore.saveUserId(dataObj.optString("user_id"));
                                                OnlineMartApplication.mLocalStore.saveUserActive(true);
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("user_type"))){
                                                OnlineMartApplication.mLocalStore.saveUserType(dataObj.optString("user_type"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("referral_code"))){
                                                OnlineMartApplication.mLocalStore.saveReferralCOde(dataObj.optString("referral_code"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("first_name"))){
                                                OnlineMartApplication.mLocalStore.saveFirstName(dataObj.optString("first_name"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("last_name"))){
                                                OnlineMartApplication.mLocalStore.saveLastName(dataObj.optString("last_name"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("email"))){
                                                OnlineMartApplication.mLocalStore.saveUserEmail(dataObj.optString("email"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("login_type"))){
                                                OnlineMartApplication.mLocalStore.saveLoginType(dataObj.optString("login_type"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("dob"))){
                                                OnlineMartApplication.mLocalStore.saveUserDob(dataObj.optString("dob"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("country_code"))){
                                                OnlineMartApplication.mLocalStore.saveUserCountryCode(dataObj.optString("country_code"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("phone_num"))){
                                                OnlineMartApplication.mLocalStore.saveUserPhone(dataObj.optString("phone_num"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("image"))){
                                                OnlineMartApplication.mLocalStore.saveUserImage(dataObj.optString("image"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("is_email_verified"))){
                                                if(dataObj.optString("is_email_verified").equalsIgnoreCase("0")){
                                                    OnlineMartApplication.mLocalStore.saveIsEmailVerified(false);
                                                }else{
                                                    OnlineMartApplication.mLocalStore.saveIsEmailVerified(true);
                                                }
                                            }

                                            AppUtil.displaySingleActionAlert(VerifyUserActivity.this, getString(R.string.app_name), responseMessage, getString(R.string.ok), new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    AppUtil.hideSoftKeyboard(VerifyUserActivity.this, VerifyUserActivity.this);
                                                    appDialogSingleAction.dismiss();
                                                    finish();
                                                }
                                            });

                                        } else {
                                            AppUtil.showErrorDialog(VerifyUserActivity.this, errorMsg);
                                            finish();
                                        }

                                    }  else {
                                        AppUtil.showErrorDialog(VerifyUserActivity.this, errorMsg);
                                        finish();
                                    }

                                } else {
                                    AppUtil.showErrorDialog(VerifyUserActivity.this, getString(R.string.error_msg) + "(ERR-601)");
                                    finish();
                                }

                            } catch (Exception e) {//(JSONException | IOException e) {

                                e.printStackTrace();
                                AppUtil.showErrorDialog(VerifyUserActivity.this, getString(R.string.error_msg) + "(ERR-602)");
                                finish();
                            }
                        } else {
                            AppUtil.showErrorDialog(VerifyUserActivity.this, getString(R.string.error_msg) + "(ERR-603)");
                            finish();
                        }




                        AppUtil.hideProgress();
                        try {
                            JSONObject otpresponse = new JSONObject(response.body().string());
                            if (otpresponse.getString("status").equalsIgnoreCase("OK"))
                            {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
        {
            //Snackbar.make(pinview, getResources().getString(R.string.error_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.try_again), networkCallBack1).show();
        }
    }

    View.OnClickListener networkCallBack1 = new View.OnClickListener() {

        @Override
        public void onClick(View view)
        {
            AppUtil.hideSoftKeyboard(VerifyUserActivity.this, VerifyUserActivity.this);
            mUserRegister();
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
