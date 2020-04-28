package thegroceryshop.com.loginModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.HelpActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.KeyboardVisibilityListener;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.utils.FacebookUtil;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/16/2015.
 */
public class LoginFragment extends Fragment {

    // for operations
    private Context mContext;
    private LoginModuleActivity activity;

    private AppCompatEditText edt_username;
    private AppCompatEditText edt_password;
    private TextView txt_forgot_password;
    private TextView txt_need_help;
    private RippleButton btn_login;
    private ImageView img_google;
    private ImageView img_facebook;
    private TextView txt_signUp;

    private CallbackManager facebook_login_callback;
    private ApiInterface apiService;
    public String social_type, social_id, social_first_name, social_last_name, social_email, social_img;
    private LinearLayout login_lyt_footer;

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.frag_login, container, false);
        mContext = getActivity();
        activity = (LoginModuleActivity) getActivity();

        apiService = ApiClient.createService(ApiInterface.class, mContext);

        edt_username = view.findViewById(R.id.login_edt_username);
        edt_password = view.findViewById(R.id.login_edt_password);
        txt_forgot_password = view.findViewById(R.id.login_txt_forgot_password);
        txt_need_help = view.findViewById(R.id.login_txt_need_help);
        btn_login = view.findViewById(R.id.login_btn_login);
        img_google = view.findViewById(R.id.login_img_google);
        img_facebook = view.findViewById(R.id.login_img_facebook);
        txt_signUp = view.findViewById(R.id.login_txt_sign_up);
        login_lyt_footer = view.findViewById(R.id.login_lyt_footer);

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            img_google.setImageResource(R.mipmap.gplus);
            img_facebook.setImageResource(R.mipmap.facebook);
            edt_password.setGravity(Gravity.START);
        } else {
            img_google.setImageResource(R.mipmap.gplus_ar);
            img_facebook.setImageResource(R.mipmap.facebook_ar);
            edt_password.setGravity(Gravity.END);
        }

        facebook_login_callback = CallbackManager.Factory.create();

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                activity.switchToFragment(ForgotPasswordFragment.newInstance(), true);
            }
        });

        txt_need_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                activity.startActivity(new Intent(activity, HelpActivity.class));
            }
        });

        txt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.switchToFragment(RegistrationFragment.newInstance(), true);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                if (ValidationUtil.isAllFieldsBlank(mContext, new View[]{edt_username, edt_password})
                        && ValidationUtil.isValidUsername(mContext, edt_username)
                        && ValidationUtil.isValidPassword(mContext, edt_password, true)) {

                    sendLoginRequest();
                }
            }
        });

        img_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                if (NetworkUtil.networkStatus(mContext)) {
                    social_type = AppConstants.TYPE_SOCIAL_GOOGLE_PLUS;

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(activity.mGoogleApiClient);
                    activity.startActivityForResult(signInIntent, AppConstants.REQUEST_GOOGLE_SIGN_IN);
                }
            }
        });

        img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                if (NetworkUtil.networkStatus(mContext)) {
                    social_type = AppConstants.TYPE_SOCIAL_FACEBOOK;
                    FacebookUtil.loginWithFacebook(LoginFragment.this, facebook_login_callback);
                }
            }
        });

        AppUtil.setKeyboardVisibilityListener(getActivity(), new KeyboardVisibilityListener() {
            @Override
            public void onKeyboardVisibilityChanged(boolean keyboardVisible) {
                if (keyboardVisible) {
                    login_lyt_footer.setVisibility(View.GONE);
                } else {
                    login_lyt_footer.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppConstants.REQUEST_FACEBOOK_SIGN_IN) {
            facebook_login_callback.onActivityResult(requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == AppConstants.REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            social_id = acct.getId();
            social_type = AppConstants.TYPE_SOCIAL_GOOGLE_PLUS;

            if (acct.getDisplayName() != null) {
                String[] nameArray = acct.getDisplayName().split(" ");
                if (nameArray != null && nameArray.length > 1) {
                    social_first_name = nameArray[0];
                    social_last_name = nameArray[1];
                } else {
                    social_first_name = acct.getDisplayName();
                    social_last_name = AppConstants.BLANK_STRING;
                }
            } else {
                social_first_name = AppConstants.BLANK_STRING;
                social_last_name = AppConstants.BLANK_STRING;
            }

            social_email = acct.getEmail();
            if (acct.getPhotoUrl() != null) {
                social_img = acct.getPhotoUrl().toString();
            }

            socialLogin(acct.getId(), AppConstants.TYPE_SOCIAL_GOOGLE_PLUS);
            activity.signOutGoogle();
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    /**
     * Web Service Calling for Login Request
     */
    private void sendLoginRequest() {
        try {
            JSONObject login_request_data = new JSONObject();
            login_request_data.put("login_type", AppConstants.TYPE_LOGIN_EMAIL);
            login_request_data.put("username", edt_username.getText().toString().trim());
            login_request_data.put("password", edt_password.getText().toString());
            login_request_data.put("language", OnlineMartApplication.mLocalStore.getAppLangId());
            login_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            login_request_data.put("social_id", AppConstants.BLANK_STRING);
            login_request_data.put("device_type", AppConstants.DEVICE_TYPE);
            login_request_data.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());
            login_request_data.put("os_version", DeviceUtil.getOsVersion());
            login_request_data.put("device_id", DeviceUtil.getDeviceId(mContext));
            login_request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(getActivity())) {
                try {
                    AppUtil.showProgress(getActivity());
                    Call<ResponseBody> call = apiService.loginRequest((new ConvertJsonToMap().jsonToMap(login_request_data)));
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
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            LoadUsualData.loadWishLists(getActivity());
                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if (dataObj != null && dataObj.length() > 0) {

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_id"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserId(dataObj.optString("user_id"));
                                                    OnlineMartApplication.mLocalStore.saveUserActive(true);
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_type"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserType(dataObj.optString("user_type"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("referral_code"))) {
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

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("image"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserImage(dataObj.optString("image"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("is_email_verified"))) {
                                                    if (dataObj.optString("is_email_verified").equalsIgnoreCase("0")) {
                                                        OnlineMartApplication.mLocalStore.saveIsEmailVerified(false);
                                                    } else {
                                                        OnlineMartApplication.mLocalStore.saveIsEmailVerified(true);
                                                    }
                                                }

                                                if (activity != null) {
                                                    activity.finish();
                                                }


                                            } else {
                                                AppUtil.showErrorDialog(getActivity(), errorMsg);
                                            }

                                        } else if (statusCode == 210) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if (dataObj != null && dataObj.length() > 0) {

                                                String country_code = null, mobile = null, dob = null, first_name = null, last_name = null, other_referral_code = AppConstants.BLANK_STRING, email = null;

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("first_name"))) {
                                                    first_name = dataObj.optString("first_name");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("last_name"))) {
                                                    last_name = dataObj.optString("last_name");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("other_referral_code"))) {
                                                    other_referral_code = dataObj.optString("other_referral_code");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("email"))) {
                                                    email = dataObj.optString("email");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("country_code"))) {
                                                    country_code = dataObj.optString("country_code");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("mobile"))) {
                                                    mobile = dataObj.optString("mobile");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("dob"))) {
                                                    dob = dataObj.optString("dob");
                                                }

                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(RegistrationFragment.IS_SOCIAL_VERFICATION, false);
                                                bundle.putString(RegistrationFragment.FIRST_NAME, first_name);
                                                bundle.putString(RegistrationFragment.LAST_NAME, last_name);
                                                bundle.putString(RegistrationFragment.EMAIL, email);

                                                bundle.putString(RegistrationFragment.COUNTRY_CODE, country_code);
                                                bundle.putString(RegistrationFragment.DOB, dob);
                                                bundle.putString(RegistrationFragment.MOBILE, mobile);
                                                bundle.putString(RegistrationFragment.OTHER_REFFRRAKL_CODE, other_referral_code);

                                                activity.switchToFragment(RegistrationFragment.newInstance(bundle), true);
                                            }

                                        } else {
                                            AppUtil.showErrorDialog(getActivity(), errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(getActivity(), getString(R.string.error_msg) + "(ERR-622)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(getActivity(), getString(R.string.error_msg) + "(ERR-623)");
                                }
                            } else {
                                AppUtil.showErrorDialog(getActivity(), getString(R.string.error_msg) + "(ERR-624)");
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

            } else {
                AppUtil.hideProgress();
            }
        } catch (JSONException e) {
            AppUtil.hideProgress();
            e.printStackTrace();
        }
    }

    public void socialLogin(final String social_id, final String social_type) {

        try {
            JSONObject social_request_data = new JSONObject();
            social_request_data.put("login_type", social_type);
            social_request_data.put("social_id", social_id);
            social_request_data.put("username", social_email);
            social_request_data.put("password", AppConstants.BLANK_STRING);
            social_request_data.put("language", OnlineMartApplication.mLocalStore.getAppLangId());
            social_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            social_request_data.put("device_type", AppConstants.DEVICE_TYPE);
            social_request_data.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());
            social_request_data.put("os_version", DeviceUtil.getOsVersion());
            social_request_data.put("device_id", DeviceUtil.getDeviceId(mContext));
            social_request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            /*if (social_type.equalsIgnoreCase("3")) {
                social_request_data.put("social_id", "1668670553144039");
                social_request_data.put("username", "seif.hussam@icloud.com");
            }*/

            /*if(social_type.equalsIgnoreCase("3")){
                social_request_data.put("social_id", "10155086187380042");
                social_request_data.put("username", "mohammed.mohsen@gmail.com");
            }*/

            /*if(social_type.equalsIgnoreCase("2")){
                social_request_data.put("social_id", "116158527565520982529");
                social_request_data.put("username", "haniosamaamr@gmail.com");
            }*/

            /*if(social_type.equalsIgnoreCase("2")) {
                social_request_data.put("social_id", "100267078817983430508");
                social_request_data.put("username", "whassan67@gmail.com");
            }*/

            if (NetworkUtil.networkStatus(getActivity())) {
                try {
                    AppUtil.showProgress(getActivity());
                    Call<ResponseBody> call = apiService.loginRequest((new ConvertJsonToMap().jsonToMap(social_request_data)));
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
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if (dataObj != null && dataObj.length() > 0) {

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_id"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserId(dataObj.optString("user_id"));
                                                    OnlineMartApplication.mLocalStore.saveUserActive(true);
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_type"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserType(dataObj.optString("user_type"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("referral_code"))) {
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

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("image"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserImage(dataObj.optString("image"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("is_email_verified"))) {
                                                    if (dataObj.optString("is_email_verified").equalsIgnoreCase("0")) {
                                                        OnlineMartApplication.mLocalStore.saveIsEmailVerified(false);
                                                    } else {
                                                        OnlineMartApplication.mLocalStore.saveIsEmailVerified(true);
                                                    }
                                                }

                                                if (activity != null) {
                                                    activity.finish();
                                                }

                                            } else {
                                                AppUtil.showErrorDialog(mContext, errorMsg);
                                            }

                                        } else if (statusCode == 205) {

                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(RegistrationFragment.IS_SOCIAL_VERFICATION, true);
                                            bundle.putString(RegistrationFragment.SOCIAL_FIRST_NAME, social_first_name);
                                            bundle.putString(RegistrationFragment.SOCIAL_LAST_NAME, social_last_name);
                                            bundle.putString(RegistrationFragment.SOCIAL_EMAIL, social_email);
                                            bundle.putString(RegistrationFragment.SOCIAL_ID, social_id);
                                            bundle.putString(RegistrationFragment.SOCIAL_TYPE, social_type);

                                            activity.switchToFragment(RegistrationFragment.newInstance(bundle), true);


                                        } else if (statusCode == 210) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if (dataObj != null && dataObj.length() > 0) {

                                                String userid = null, country_code = null, mobile = null, dob = null, first_name = null, last_name = null, other_referral_code = AppConstants.BLANK_STRING, email = null;

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_id"))) {
                                                    userid = dataObj.optString("user_id");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("first_name"))) {
                                                    first_name = dataObj.optString("first_name");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("last_name"))) {
                                                    last_name = dataObj.optString("last_name");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("other_referral_code"))) {
                                                    other_referral_code = dataObj.optString("other_referral_code");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("email"))) {
                                                    email = dataObj.optString("email");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("country_code"))) {
                                                    country_code = dataObj.optString("country_code");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("mobile"))) {
                                                    mobile = dataObj.optString("mobile");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("dob"))) {
                                                    dob = dataObj.optString("dob");
                                                }

                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(RegistrationFragment.IS_SOCIAL_VERFICATION, true);
                                                bundle.putString(RegistrationFragment.SOCIAL_FIRST_NAME, first_name);
                                                bundle.putString(RegistrationFragment.SOCIAL_LAST_NAME, last_name);
                                                bundle.putString(RegistrationFragment.SOCIAL_EMAIL, email);
                                                bundle.putString(RegistrationFragment.SOCIAL_ID, social_id);
                                                bundle.putString(RegistrationFragment.SOCIAL_TYPE, social_type);

                                                bundle.putString(RegistrationFragment.COUNTRY_CODE, country_code);
                                                bundle.putString(RegistrationFragment.DOB, dob);
                                                bundle.putString(RegistrationFragment.MOBILE, mobile);
                                                bundle.putString(RegistrationFragment.OTHER_REFFRRAKL_CODE, other_referral_code);

                                                activity.switchToFragment(RegistrationFragment.newInstance(bundle), true);
                                            }

                                        } else {
                                            AppUtil.showErrorDialog(getActivity(), errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(getActivity(), getString(R.string.error_msg) + "(ERR-625)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(getActivity(), getString(R.string.error_msg) + "(ERR-626)");
                                }
                            } else {
                                AppUtil.showErrorDialog(getActivity(), getString(R.string.error_msg) + "(ERR-627)");
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

            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
