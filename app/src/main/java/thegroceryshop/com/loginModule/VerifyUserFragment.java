package thegroceryshop.com.loginModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/16/2015.
 */
public class VerifyUserFragment extends Fragment implements View.OnClickListener {

    // for operations
    private Context mContext;
    private LoginModuleActivity activity;
    private static final int PERMISSION_CALLBACK_CONSTANT = 500;
    private String[] permissionsRequired = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    private TextView textViewOTP;
    private TextView timerText;
    private RippleButton rippleButton, resendOtpBtn;
    private LinearLayout footer;
    private int size = 1;
    private String mOtpNumber = "", mSignupRequest = "", mforgotPassword_Otp;
    public static Pinview pinview;
    private int timee = 45;
    private Timer timer;
    private String phone_num = "", user_id = "";
    private LinearLayout changePasswordLayout;
    private AppCompatEditText editTextNewPassword;
    private AppCompatEditText editTextRetypeNewPassword;
    private RippleButton btnChangePassword;
    private LinearLayout optLayout;
    private RelativeLayout main_layout;
    private boolean isForgotPassowrd;

    private String userid;
    private String userType;
    private String refrral;
    private String firstNmae;
    private String lastName;
    private String email;
    private String login_type;
    private String country_code;
    private String mobile;
    private String image;
    private String otp;
    private String dob;
    private boolean isEmailVerified;
    private boolean isMobileVerification;


    public static VerifyUserFragment newInstance(Bundle bundle) {
        VerifyUserFragment verifyUserFragment = new VerifyUserFragment();
        verifyUserFragment.setArguments(bundle);
        return verifyUserFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.layout_otp_screen, container, false);
        mContext = getActivity();
        activity = (LoginModuleActivity) getActivity();
        AppUtil.hideSoftKeyboard(activity, mContext);
        if (getArguments() != null) {
            if (getArguments().getString("otpNumber") != null) {
                isForgotPassowrd = false;
                mOtpNumber = getArguments().getString("otpNumber");

                isMobileVerification = mOtpNumber.equalsIgnoreCase(AppConstants.BLANK_STRING);

                mSignupRequest = getArguments().getString("signUpdata");
                phone_num = getArguments().getString("phone_no");
                //Toast.makeText(mContext, "Your OTP no is " + mOtpNumber, Toast.LENGTH_LONG).show();

                userid = getArguments().getString("userid");
                userType = getArguments().getString("userType");
                refrral = getArguments().getString("refrral");
                firstNmae = getArguments().getString("firstNmae");
                lastName = getArguments().getString("lastName");
                email = getArguments().getString("email");
                country_code = getArguments().getString("country_code");
                login_type = getArguments().getString("login_type");
                mobile = getArguments().getString("mobile");
                image = getArguments().getString("image");
                dob = getArguments().getString("dob");
                isEmailVerified = getArguments().getBoolean("isEmailVerified", false);

                //Toast.makeText(mContext, "Your OTP no is " + mOtpNumber, Toast.LENGTH_LONG).show();

            } else {
                isForgotPassowrd = true;
                mforgotPassword_Otp = getArguments().getString("otp");
                user_id = getArguments().getString("user_id");
                phone_num = getArguments().getString("phone_number");

                //Toast.makeText(mContext, "Your OTP no is " + mforgotPassword_Otp, Toast.LENGTH_LONG).show();
            }
        }

        initView(view);
        setOnClickListener();

        return view;
    }

    private void initView(View view) {
        textViewOTP = view.findViewById(R.id.textViewOTP);
        timerText = view.findViewById(R.id.timerText);

        rippleButton = view.findViewById(R.id.rippleButton);
        resendOtpBtn = view.findViewById(R.id.resendOtpBtn);

        footer = view.findViewById(R.id.mCheckoutBtn);
        pinview = view.findViewById(R.id.opt_pinView);
        pinview.requestFocus();
        changePasswordLayout = view.findViewById(R.id.change_password_layout);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextRetypeNewPassword = view.findViewById(R.id.editTextRetypeNewPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        optLayout = view.findViewById(R.id.opt_layout);
        main_layout = view.findViewById(R.id.main_layout);

        timer = new Timer();
        mSetTimer();

        textViewOTP.setText(phone_num);

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyForOTP();
            }
        });

        if(isMobileVerification){
            timerText.setVisibility(View.GONE);
            applyForOTP();
            //resendOtpBtn.performClick();
        }
    }

    private void applyForOTP(){
        try {
            AppUtil.hideSoftKeyboard(activity, mContext);

            JSONObject mOtpData = new JSONObject();
            mOtpData.put("country_code", phone_num.split(" ")[0].replace("+", ""));
            mOtpData.put("mobile_num", phone_num.split(" ")[1]);
            mOtpData.put("language", OnlineMartApplication.mLocalStore.getAppLangId());
            mOtpData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

            if (NetworkUtil.networkStatus(mContext)) {
                AppUtil.showProgress(mContext);
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                Call<ResponseBody> call;
                if(isForgotPassowrd){
                    call = apiService.mCheckMobileNoExistance((new ConvertJsonToMap().jsonToMap(mOtpData)));
                }else{
                    mOtpData.put("user_id", userid);
                    mOtpData.put("user_status", false);
                    call = apiService.mSendOtpno((new ConvertJsonToMap().jsonToMap(mOtpData)));
                }

                //mOtpNumber = /*dataObject.optString("otp")*/"1234";
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AppUtil.hideProgress();

                        if(response.code() == 200){
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                if(responseObj.length() > 0){
                                    JSONObject jsonObjectresponse = responseObj.optJSONObject("response");
                                    if(jsonObjectresponse != null){

                                        int statusCode = jsonObjectresponse.optInt("status_code", 0);
                                        String errorMsg = jsonObjectresponse.optString("error_message");

                                        if(statusCode == 200){

                                            JSONObject dataObject = jsonObjectresponse.optJSONObject("data");
                                            if(dataObject != null){

                                                resendOtpBtn.setVisibility(View.GONE);
                                                rippleButton.setVisibility(View.VISIBLE);

                                                mOtpNumber = dataObject.optString("otp");

                                                timee = 45;
                                                timer = new Timer();
                                                timerText.setVisibility(View.VISIBLE);
                                                mSetTimer();

                                            }else{
                                                AppUtil.displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, getString(R.string.error_msg) + "(ERR-611)", getResources().getString(R.string.ok), false);
                                            }

                                        }else{
                                            AppUtil.displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, errorMsg, getResources().getString(R.string.ok), false);
                                        }

                                    }else{
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-612)");
                                    }

                                }else{
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-613)");
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-614)");
                            }

                        }else{
                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-615)");
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListener() {
        rippleButton.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rippleButton:
                AppUtil.hideSoftKeyboard(getActivity(), mContext);

                if(!isForgotPassowrd){
                    if(!ValidationUtil.isNullOrBlank(pinview.getValue().toString())){
                        if (mOtpNumber.equalsIgnoreCase(pinview.getValue().toString())) {
                            AppUtil.hideSoftKeyboard(getActivity(), mContext);
                            mUserRegister();
                        }else{
                            Toast.makeText(mContext, getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(mContext, getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(!ValidationUtil.isNullOrBlank(pinview.getValue().toString())){
                        if (mforgotPassword_Otp.equalsIgnoreCase(pinview.getValue().toString())) {
                            main_layout.setBackground(null);
                            optLayout.setVisibility(View.GONE);
                            changePasswordLayout.setVisibility(View.VISIBLE);

                            //Toast.makeText(mContext, getString(R.string.otp_done), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(mContext, getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                    }
                }


                /*if (mOtpNumber.equalsIgnoreCase(pinview.getValue().toString())) {
                    mUserRegister();
                } else {
                    if (mforgotPassword_Otp.equalsIgnoreCase(pinview.getValue().toString())) {
                        main_layout.setBackground(null);
                        optLayout.setVisibility(View.GONE);
                        changePasswordLayout.setVisibility(View.VISIBLE);

                        Toast.makeText(mContext, getString(R.string.otp_done), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                    }

                }*/
                break;
            case R.id.btnChangePassword:
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                if (!editTextNewPassword.getText().toString().isEmpty()) {
                    if (!editTextRetypeNewPassword.getText().toString().isEmpty()) {
                        if (editTextNewPassword.getText().toString().equalsIgnoreCase(editTextRetypeNewPassword.getText().toString())) {
                            mChangePassword();
                        } else {
                            AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, mContext.getResources().getString(R.string.err_cnfm_password_not_match), mContext.getString(R.string.ok), false);
                        }
                    } else {
                        AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, mContext.getResources().getString(R.string.retype_password_error), mContext.getString(R.string.ok), false);
                    }
                } else {
                    AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, mContext.getResources().getString(R.string.new_password_error), mContext.getString(R.string.ok), false);
                }
                break;
        }
    }

    /**
     * Call webservice and get all course details
     */
    private void mUserRegister() {
        AppUtil.hideSoftKeyboard(activity, mContext);
        if (NetworkUtil.networkStatus(mContext)) {
            try {
                AppUtil.hideSoftKeyboard(activity, mContext);
                AppUtil.showProgress(mContext);
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);

                JSONObject mRegRequest = new JSONObject();
                mRegRequest.put("user_id", userid);
                mRegRequest.put("user_status", true);
                mRegRequest.put("isUserData", "1");
                mRegRequest.put("os_version", DeviceUtil.getOsVersion());
                mRegRequest.put("device_id", DeviceUtil.getDeviceId(mContext));
                mRegRequest.put("device_type", AppConstants.DEVICE_TYPE);
                mRegRequest.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());
                /*if(isMobileVerification){
                    mRegRequest.put("isUserData", "1");
                    mRegRequest.put("os_version", DeviceUtil.getOsVersion());
                    mRegRequest.put("device_id", DeviceUtil.getDeviceId(mContext));
                    mRegRequest.put("device_type", AppConstants.DEVICE_TYPE);
                    mRegRequest.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());
                }else{
                    mRegRequest.put("isUserData", "1");
                }*/
                Call<ResponseBody> call = apiService.mSendOtpno((new ConvertJsonToMap().jsonToMap(mRegRequest)));

                //Call<ResponseBody> call = apiService.mRegisterRequest((new ConvertJsonToMap().jsonToMap(new JSONObject(mSignupRequest))));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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

                                            LoadUsualData.loadWishLists(getActivity());

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_id"))) {
                                                OnlineMartApplication.mLocalStore.saveUserId(dataObj.optString("user_id"));
                                                OnlineMartApplication.mLocalStore.saveUserActive(true);
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_type"))) {
                                                OnlineMartApplication.mLocalStore.saveUserType(dataObj.optString("user_type"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("referral_code"))){
                                                OnlineMartApplication.mLocalStore.saveReferralCOde(dataObj.optString("referral_code"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("first_name"))) {
                                                OnlineMartApplication.mLocalStore.saveFirstName(dataObj.optString("first_name"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("last_name"))) {
                                                OnlineMartApplication.mLocalStore.saveLastName(dataObj.optString("last_name"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("email"))) {
                                                OnlineMartApplication.mLocalStore.saveUserEmail(dataObj.optString("email"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("login_type"))) {
                                                OnlineMartApplication.mLocalStore.saveLoginType(dataObj.optString("login_type"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("dob")) && !dataObj.optString("dob").equalsIgnoreCase("0000-00-00")) {
                                                OnlineMartApplication.mLocalStore.saveUserDob(dataObj.optString("dob"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("country_code"))) {
                                                OnlineMartApplication.mLocalStore.saveUserCountryCode(dataObj.optString("country_code"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("mobile"))) {
                                                OnlineMartApplication.mLocalStore.saveUserPhone(dataObj.optString("mobile"));
                                            }

                                            if(!ValidationUtil.isNullOrBlank(dataObj.optString("image"))){
                                                OnlineMartApplication.mLocalStore.saveUserImage(dataObj.optString("image"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(dataObj.optString("is_email_verified"))) {
                                                if (dataObj.optString("is_email_verified").equalsIgnoreCase("0")) {
                                                    OnlineMartApplication.mLocalStore.saveIsEmailVerified(false);
                                                } else {
                                                    OnlineMartApplication.mLocalStore.saveIsEmailVerified(true);
                                                }
                                            }

                                            if(isMobileVerification){
                                                AppUtil.hideSoftKeyboard(activity, mContext);
                                                activity.finish();
                                            }else{
                                                AppUtil.displaySingleActionAlert(mContext, getString(R.string.app_name), responseMessage, getString(R.string.ok), new OnSingleActionListener() {
                                                    @Override
                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                        AppUtil.hideSoftKeyboard(activity, mContext);
                                                        appDialogSingleAction.dismiss();
                                                        activity.finish();
                                                    }
                                                });
                                            }


                                        } else {
                                            AppUtil.showErrorDialog(mContext, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, errorMsg);
                                    }

                                } else {
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-619)");
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-620)");
                            }
                        } else {
                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-621)");
                        }


                        AppUtil.hideProgress();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
        }
    }

    private boolean askForPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED;
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

            if(allgranted){
                ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
                //LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("otp"));
            }
        }
    }

    View.OnClickListener networkCallBack1 = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mUserRegister();
        }
    };


    @Override
    public void onResume() {
        /*if (ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED
                ){

            ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
            //LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("otp"));
        }*/
        super.onResume();

    }

    @Override
    public void onDestroy() {
        ((OnlineMartApplication)(getActivity().getApplicationContext())).releaseSMSAutoReadReceiver();
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("otp");
                //Do whatever you want with the code here
                pinview.setValue(message);
            }
        }
    };

    private void mSetTimer() {

        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void run() {
                final int i = timee--;
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(timer != null){
                            timerText.setText(String.format("%d seconds left", i));
                            if (i <= 0) {
                                timerText.setText(AppConstants.BLANK_STRING);
                                resendOtpBtn.setVisibility(View.VISIBLE);

                                if(timer != null){
                                    timer.cancel();
                                    timer.purge();
                                    timer = null;
                                }
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    private void mChangePassword() {
        if (NetworkUtil.networkStatus(mContext)) {
            AppUtil.showProgress(mContext);
            try {
                JSONObject request_data = new JSONObject();
                request_data.put("user_id", user_id);
                request_data.put("password", editTextNewPassword.getText().toString());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                Call<ResponseBody> call = apiService.mChangePassword((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                if (resObject.getString("status_code").equalsIgnoreCase("200")) {
                                    AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, resObject.getString("success_message"), "OK", new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            startActivity(new Intent(mContext, LoginModuleActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }
                                    });
                                } else {
                                    AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, resObject.getString("error_message"), "OK");
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(activity, mContext);
    }
}
