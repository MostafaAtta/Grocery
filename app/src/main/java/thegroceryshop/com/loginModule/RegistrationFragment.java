package thegroceryshop.com.loginModule;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.CommonWebViewActivity;
import thegroceryshop.com.activity.CountriesListActivity;
import thegroceryshop.com.activity.HelpActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.country_list.CountriesBaseFragment;
import thegroceryshop.com.country_list.model.CountryPhoneCode;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.DateTimePickUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.R.id.txtViewCountryCode;

/**
 * Created by rohitg on 4/16/2015.
 */
public class RegistrationFragment extends Fragment {

    private static final int PERMISSION_CALLBACK_CONSTANT = 500;
    private String[] permissionsRequired = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };
    // for operations
    private Context mContext;
    private LoginModuleActivity activity;

    public static final String IS_SOCIAL_VERFICATION = "is_social_verfication";
    public static final String SOCIAL_FIRST_NAME = "social_first_name";
    public static final String SOCIAL_LAST_NAME = "social_last_name";
    public static final String SOCIAL_EMAIL = "social_email";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String SOCIAL_ID = "social_id";
    public static final String SOCIAL_TYPE = "social_type";
    public static final String COUNTRY_CODE = "country_code";
    public static final String DOB = "dob";
    public static final String MOBILE = "mobile";
    public static final String OTHER_REFFRRAKL_CODE = "other_referral_code";


    private boolean isSocialVerification;
    private String social_first_name, social_last_name, social_email, social_id, social_type;

    private ImageView img_back;
    private TextView textView2;
    private AppCompatEditText appCompatEditText;
    private AppCompatEditText appCompatEditText2;
    private AppCompatTextView txt_country_code;
    private AppCompatEditText txtViewNumber;
    private TextView textView;
    private RippleButton sugnUpbtn;
    private TextView textViewSignUp;
    private LinearLayout footer;
    private CountryPhoneCode selected_self_countru_code;
    private ScrollView scrollView;
    private AppCompatEditText appCompatEditTextFirstName;
    private AppCompatEditText appCompatEditTextLastName;
    private AppCompatEditText appCompatEditTextEmail;
    private AppCompatEditText appCompatEditTextPassword;
    private AppCompatEditText appCompatEditTextConfirmPassword;
    private AppCompatEditText appCompatEditTextReferralCode;
    private TextView txt_password, txt_confirm_password;
    private TextView textViewDob;
    private AppCompatTextView txtViewDate;
    private TextView textView3;
    private TextView textViewCheckOut;
    private TextView txt_need_help;
    private CheckBox term_condtn_checkBox;
    private ApiInterface apiService;
    private boolean isEmailAvailable;
    private boolean isMobileNoAvailable;

    private TextView txt_email_warning;
    private TextView txt_mobile_warning;

    private JSONObject requestData = new JSONObject();

    private JSONObject mOtpData = new JSONObject();
    private JSONObject mCheckMobileData = new JSONObject();
    private JSONObject mCheckMailData = new JSONObject();
    private int mCountryCode = 0;
    private DateTime selected;
    private String mDob_txt = "";
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
    private String other_referral_code;
    private boolean isEmailVerified;
    private boolean isLoaderVisible;
    private boolean isUserRegisteredAlready;

    public static RegistrationFragment newInstance() {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        return registrationFragment;
    }

    public static RegistrationFragment newInstance(boolean isSocialVerification, String social_type, String social_first_name, String social_last_name, String social_email, String social_id) {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_SOCIAL_VERFICATION, isSocialVerification);
        bundle.putString(SOCIAL_FIRST_NAME, social_first_name);
        bundle.putString(SOCIAL_LAST_NAME, social_last_name);
        bundle.putString(SOCIAL_EMAIL, social_email);
        bundle.putString(SOCIAL_ID, social_id);
        bundle.putString(SOCIAL_TYPE, social_type);
        registrationFragment.setArguments(bundle);

        return registrationFragment;
    }

    public static RegistrationFragment newInstance(Bundle bundle) {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        registrationFragment.setArguments(bundle);
        return registrationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.frag_register, container, false);
        mContext = getActivity();
        activity = (LoginModuleActivity) getActivity();

        apiService = ApiClient.createService(ApiInterface.class, mContext);

        scrollView = view.findViewById(R.id.scrollView);
        appCompatEditTextFirstName = view.findViewById(R.id.appCompatEditTextFirstName);
        appCompatEditTextLastName = view.findViewById(R.id.appCompatEditTextLastName);
        appCompatEditTextEmail = view.findViewById(R.id.appCompatEditTextEmail);
        appCompatEditTextPassword = view.findViewById(R.id.appCompatEditTextPassword);
        appCompatEditTextConfirmPassword = view.findViewById(R.id.appCompatEditTextConfirmPassword);
        txtViewNumber = view.findViewById(R.id.txtViewNumber);
        appCompatEditTextReferralCode = view.findViewById(R.id.appCompatEditTextReferralCode);
        textViewDob = view.findViewById(R.id.textViewDob);
        txtViewDate = view.findViewById(R.id.txtViewDate);
        textView3 = view.findViewById(R.id.textView3);
        textViewCheckOut = view.findViewById(R.id.textViewCheckOut);
        sugnUpbtn = view.findViewById(R.id.sugnUpbtn);
        txt_country_code = view.findViewById(txtViewCountryCode);
        term_condtn_checkBox = view.findViewById(R.id.term_condtn_checkBox);
        txt_need_help = view.findViewById(R.id.textViewNeedHelp);
        txt_password = view.findViewById(R.id.reg_txt_pasword);
        txt_confirm_password = view.findViewById(R.id.reg_txt_confirm_pasword);
        img_back = view.findViewById(R.id.img_back);

        txt_email_warning = view.findViewById(R.id.reg_email_warning);
        txt_mobile_warning = view.findViewById(R.id.reg_mobile_warning);

        Spannable wordtoSpan = new SpannableString(getContext().getString(R.string.i_agree_tnc));
        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {

            wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.yello_trm)), 11, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.yello_trm)), 25, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //For UnderLine
            wordtoSpan.setSpan(clickableSpan, 11, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(clickableSpan1, 25, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {

            wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.yello_trm)), 11, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.yello_trm)), 27, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //For UnderLine
            wordtoSpan.setSpan(clickableSpan, 11, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(clickableSpan1, 27, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        term_condtn_checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        term_condtn_checkBox.setText(wordtoSpan);

        txtViewDate.setText(mDob_txt);

        if (getArguments() != null) {
            Bundle socialDataArgs = getArguments();

            isSocialVerification = socialDataArgs.getBoolean(IS_SOCIAL_VERFICATION, false);

            country_code = socialDataArgs.getString(COUNTRY_CODE);
            mobile = socialDataArgs.getString(MOBILE);
            dob = socialDataArgs.getString(DOB);
            other_referral_code = socialDataArgs.getString(OTHER_REFFRRAKL_CODE);

            if (isSocialVerification) {

                social_first_name = socialDataArgs.getString(SOCIAL_FIRST_NAME);
                social_last_name = socialDataArgs.getString(SOCIAL_LAST_NAME);
                social_email = socialDataArgs.getString(SOCIAL_EMAIL);
                social_id = socialDataArgs.getString(SOCIAL_ID);
                social_type = socialDataArgs.getString(SOCIAL_TYPE);

                if (isSocialVerification) {
                    appCompatEditTextPassword.setVisibility(View.GONE);
                    appCompatEditTextConfirmPassword.setVisibility(View.GONE);
                    txt_password.setVisibility(View.GONE);
                    txt_confirm_password.setVisibility(View.GONE);
                    appCompatEditTextReferralCode.setVisibility(View.VISIBLE);

                    appCompatEditTextFirstName.setText(social_first_name);
                    if (!ValidationUtil.isNullOrBlank(social_last_name)) {
                        appCompatEditTextFirstName.setEnabled(false);
                    }

                    appCompatEditTextLastName.setText(social_last_name);
                    if (!ValidationUtil.isNullOrBlank(social_last_name)) {
                        appCompatEditTextLastName.setEnabled(false);
                    }

                    appCompatEditTextEmail.setText(social_email);
                    if (!ValidationUtil.isNullOrBlank(social_email)) {
                        appCompatEditTextEmail.setEnabled(false);
                    }

                    if (dob != null && dob.length() > 0) {
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                        selected = formatter.parseDateTime(dob);

                        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateTimePickUtil.SLASH_DATE);
                        String selected_date = dateTimeFormatter.print(selected);
                        txtViewDate.setText(selected_date);
                    }

                    if (mobile != null && mobile.length() > 0) {
                        txtViewNumber.setText(mobile);
                    }

                    if (other_referral_code != null && other_referral_code.trim().length() > 0) {
                        appCompatEditTextReferralCode.setText(other_referral_code);
                        //appCompatEditTextReferralCode.setHint(getString(R.string.referral_code));
                    } else {
                        //appCompatEditTextReferralCode.setHint(getString(R.string.referral_code));
                    }
                }

            } else {

                appCompatEditTextReferralCode.setVisibility(View.VISIBLE);

                appCompatEditTextFirstName.setText(socialDataArgs.getString(FIRST_NAME));
                appCompatEditTextLastName.setText(socialDataArgs.getString(LAST_NAME));
                appCompatEditTextEmail.setText(socialDataArgs.getString(EMAIL));
                if (dob != null && dob.length() > 0) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                    selected = formatter.parseDateTime(dob);

                    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateTimePickUtil.SLASH_DATE);
                    String selected_date = dateTimeFormatter.print(selected);
                    txtViewDate.setText(selected_date);
                }

                if (mobile != null && mobile.length() > 0) {
                    txtViewNumber.setText(mobile);
                }

                if (other_referral_code != null && other_referral_code.trim().length() > 0) {
                    appCompatEditTextReferralCode.setText(other_referral_code);
                    appCompatEditTextReferralCode.setHint(getString(R.string.referral_code));
                } else {
                    appCompatEditTextReferralCode.setHint(getString(R.string.referral_code));
                }


            }

            if (country_code == null) {
                isUserRegisteredAlready = true;
            }

        } else {
            isSocialVerification = false;
        }

        new AsyncPhoneInitTask(mContext).execute();

        appCompatEditTextReferralCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                /*if (AppUtil.isValidPhone(mContext, txtViewNumber.getText().toString())) {
                    try {
                        mCheckMobileData.put("country_code", selected_self_countru_code.getCountryCode() + AppConstants.BLANK_STRING);
                        mCheckMobileData.put("mobile_number", txtViewNumber.getText().toString());
                        mCheckMobileData.put("lang_id", "1");
                        mCheckMobileNo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity != null){
                    activity.onBackPressed();
                }
            }
        });

        txtViewNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txt_mobile_warning.setVisibility(View.GONE);
                }
            }
        });

        appCompatEditTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txt_email_warning.setVisibility(View.GONE);
                }
            }
        });

        appCompatEditTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!ValidationUtil.isNullOrBlank(mContext, appCompatEditTextEmail.getText().toString(), appCompatEditTextEmail, "Email")) {
                    if (ValidationUtil.isValidEmail(mContext, appCompatEditTextEmail)) {
                        try {
                            mCheckMailData.put("email", "" + appCompatEditTextEmail.getText().toString());
                            mCheckMailData.put("language", OnlineMartApplication.mLocalStore.getAppLangId());
                            mCheckMailData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                            mCheckEmailID(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        appCompatEditTextEmail.setActivated(true);
                        appCompatEditTextEmail.setSelected(true);
                    }
                } else {
                    appCompatEditTextEmail.setActivated(true);
                    appCompatEditTextEmail.setSelected(true);
                }

            }
        });

        txt_need_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                startActivity(new Intent(mContext, HelpActivity.class));
            }
        });

        sugnUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        txt_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);
                Intent intentCountry = new Intent(mContext, CountriesListActivity.class);
                startActivityForResult(intentCountry, AppConstants.REQUEST_PICK_COUNTRY_SELF);
            }
        });

        txtViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(getActivity(), mContext);

                String number = txtViewNumber.getText().toString();
                for (int i = 0; number.startsWith("0"); i++) {
                    number = number.replaceFirst("0", "");
                }

                if (AppUtil.isValidPhone(mContext, number, false)) {
                    try {
                        mCheckMobileData.put("country_code", selected_self_countru_code.getCountryCode() + AppConstants.BLANK_STRING);
                        mCheckMobileData.put("mobile_number", number);
                        mCheckMobileData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        mCheckMobileNo(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {

                    if (selected != null) {
                        DateTimePickUtil.showDateSelection(mContext, selected, DateTime.now().minusYears(13), new DateTimePickUtil.DateTimePickListener() {
                            @Override
                            public void onDateTimeSelected(DateTime selected) {

                                RegistrationFragment.this.selected = selected;
                                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateTimePickUtil.SLASH_DATE);
                                String selected_date = dateTimeFormatter.print(selected);
                                txtViewDate.setText(selected_date);


                            }
                        });
                    } else {
                        DateTimePickUtil.showDateSelection(mContext, DateTime.now().minusYears(13), DateTime.now().minusYears(13), new DateTimePickUtil.DateTimePickListener() {
                            @Override
                            public void onDateTimeSelected(DateTime selected) {

                                RegistrationFragment.this.selected = selected;
                                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateTimePickUtil.SLASH_DATE);
                                String selected_date = dateTimeFormatter.print(selected);
                                //  String a[]= new String[3];
                                //  a=selected_date.split("/");
                                txtViewDate.setText(selected_date);


                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    public void signUp() {
        AppUtil.hideSoftKeyboard(getActivity(), mContext);

        try {
            if (!ValidationUtil.isNullOrBlank(mContext, appCompatEditTextFirstName.getText().toString(), appCompatEditTextFirstName, "firstName")
                    && !ValidationUtil.isNullOrBlank(mContext, appCompatEditTextLastName.getText().toString(), appCompatEditTextLastName, "lastName")
                    && ValidationUtil.isValidEmails(mContext, appCompatEditTextEmail)
                    && (isSocialVerification || ValidationUtil.isValidPassword(mContext, appCompatEditTextPassword, false))
                    && (isSocialVerification || ValidationUtil.isValidConfirmPassword1(mContext, appCompatEditTextConfirmPassword, appCompatEditTextPassword.getText().toString()))
                    && AppUtil.isValidPhone(mContext, txtViewNumber.getText().toString())
                    /*&& ValidationUtil.isValidDateOfBirth(mContext, txtViewDate)*/) {
                /**
                 * Call webservice for register user and Set Request Data
                 */

                if (term_condtn_checkBox.isChecked()) {
                    mDob_txt = txtViewDate.getText().toString();
                    if (isEmailAvailable) {
                        if (isMobileNoAvailable) {

                            if (isSocialVerification) {
                                requestData.put("login_type", social_type + AppConstants.BLANK_STRING);
                                requestData.put("password", AppConstants.BLANK_STRING);
                                requestData.put("social_id", social_id);

                            } else {
                                requestData.put("login_type", "1");
                                requestData.put("password", appCompatEditTextPassword.getText().toString());
                                requestData.put("social_id", AppConstants.BLANK_STRING);
                            }

                            requestData.put("referral_code", appCompatEditTextReferralCode.getText().toString());
                            requestData.put("user_type", "0");
                            requestData.put("first_name", appCompatEditTextFirstName.getText().toString());
                            requestData.put("last_name", appCompatEditTextLastName.getText().toString());
                            requestData.put("email", appCompatEditTextEmail.getText().toString());
                            requestData.put("dob", txtViewDate.getText().toString());
                            requestData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                            requestData.put("country_code", selected_self_countru_code.getCountryCode() + AppConstants.BLANK_STRING);

                            String number = txtViewNumber.getText().toString();
                            for (int i = 0; number.startsWith("0"); i++) {
                                number = number.replaceFirst("0", "");
                            }

                            requestData.put("phone_num", number);
                            requestData.put("device_type", AppConstants.DEVICE_TYPE);
                            requestData.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());
                            requestData.put("os_version", DeviceUtil.getOsVersion());
                            requestData.put("device_id", DeviceUtil.getDeviceId(mContext));

                            mOtpData.put("country_code", txt_country_code.getText().toString().replace("+", ""));
                            mOtpData.put("mobile_num", number);
                            mOtpData.put("language", OnlineMartApplication.mLocalStore.getAppLangId());
                            mOtpData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                            mSnedOTP();

                        } else {
                            try {

                                String number = txtViewNumber.getText().toString();
                                for (int i = 0; number.startsWith("0"); i++) {
                                    number = number.replaceFirst("0", "");
                                }

                                mCheckMobileData.put("country_code", selected_self_countru_code.getCountryCode() + AppConstants.BLANK_STRING);
                                mCheckMobileData.put("mobile_number", number);
                                mCheckMobileData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                                mCheckMobileNo(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, getString(R.string.mobile_not_available), getString(R.string.ok));
                        }
                    } else {
                        try {
                            mCheckMailData.put("email", "" + appCompatEditTextEmail.getText().toString());
                            mCheckMailData.put("language", OnlineMartApplication.mLocalStore.getAppLangId());
                            mCheckMailData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                            mCheckEmailID(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, getString(R.string.email_not_available), getString(R.string.ok));
                    }

                } else {
                    AppUtil.showErrorDialog(mContext, getString(R.string.please_accept_terms));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AppUtil.hideSoftKeyboard(getActivity(), mContext);
        if (requestCode == AppConstants.REQUEST_PICK_COUNTRY_SELF) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getParcelableExtra(CountriesBaseFragment.result_country) != null) {
                    selected_self_countru_code = data.getParcelableExtra(CountriesBaseFragment.result_country);
                    if (selected_self_countru_code != null) {
                        txt_country_code.setText(selected_self_countru_code.getCountryCodeStr());
                        mCountryCode = selected_self_countru_code.getCountryCode();
                        //txtViewCountryName.setText(selected_self_countru_code.getName());
                    }
                }
            }
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
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
            }
            requestForOTPAndProceed();
        }
    }

    /**
     * Call webservice and get all course details
     */
    private void mSnedOTP() {
        AppUtil.hideSoftKeyboard(getActivity(), mContext);
        if (Build.VERSION.SDK_INT >= 23) {

            ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
            requestForOTPAndProceed();

            /*if(!askForPermissions()){
                requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }else{
                ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
                requestForOTPAndProceed();
            }*/

        } else {
            ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
            requestForOTPAndProceed();
        }
    }

    private void requestForOTPAndProceed(){
        if (NetworkUtil.networkStatus(mContext)) {
            try {
                if (!isLoaderVisible) {
                    AppUtil.showProgress(mContext);
                }

                requestData.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                Call<ResponseBody> call = apiService.getOtpRegistration((new ConvertJsonToMap().jsonToMap(requestData)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        isLoaderVisible = false;
                        AppUtil.hideProgress();

                        if (response.code() == 200) {
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                if (responseObj.length() > 0) {
                                    JSONObject jsonObjectresponse = responseObj.optJSONObject("response");
                                    if (jsonObjectresponse != null) {

                                        int statusCode = jsonObjectresponse.optInt("status_code", 0);
                                        String errorMsg = jsonObjectresponse.optString("error_message");

                                        if (statusCode == 201) {

                                            String responseMessage = jsonObjectresponse.optString("success_message");
                                            JSONObject dataObj = jsonObjectresponse.optJSONObject("data");
                                            if (dataObj != null && dataObj.length() > 0) {

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_id"))) {
                                                    userid = dataObj.optString("user_id");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("user_type"))) {
                                                    userType = dataObj.optString("user_type");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("referral_code"))) {
                                                    refrral = dataObj.optString("referral_code");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("first_name"))) {
                                                    firstNmae = dataObj.optString("first_name");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("last_name"))) {
                                                    lastName = dataObj.optString("last_name");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("email"))) {
                                                    email = dataObj.optString("email");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("login_type"))) {
                                                    login_type = dataObj.optString("login_type");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("dob")) && !dataObj.optString("dob").equalsIgnoreCase("0000-00-00")) {
                                                    dob = dataObj.optString("dob");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("country_code"))) {
                                                    country_code = dataObj.optString("country_code");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("mobile"))) {
                                                    mobile = dataObj.optString("mobile");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("image"))) {
                                                    image = dataObj.optString("image");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("otp"))) {
                                                    otp = dataObj.optString("otp");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("is_email_verified"))) {
                                                    isEmailVerified = false;
                                                    isEmailVerified = !dataObj.optString("is_email_verified").equalsIgnoreCase("0");
                                                }

                                                Bundle bundle = new Bundle();
                                                bundle.putString("otpNumber", otp);
                                                bundle.putString("userid", userid);
                                                bundle.putString("userType", userType);
                                                bundle.putString("firstNmae", firstNmae);
                                                bundle.putString("lastName", lastName);
                                                bundle.putString("email", email);
                                                bundle.putString("login_type", login_type);
                                                bundle.putString("dob", dob);
                                                bundle.putString("country_code", country_code);
                                                bundle.putString("mobile", mobile);
                                                bundle.putString("image", image);
                                                bundle.putBoolean("isEmailVerified", isEmailVerified);
                                                bundle.putString("userid", userid);
                                                bundle.putString("phone_no", "+" + selected_self_countru_code.getCountryCode() + " " + requestData.optString("phone_num"));

                                                activity.switchToFragment(VerifyUserFragment.newInstance(bundle), true);

                                            } else {
                                                AppUtil.displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, getString(R.string.error_msg) + "(Err-692)", getResources().getString(R.string.ok), false);
                                            }

                                        } else {

                                            AppUtil.showErrorDialog(mContext, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-693)");
                                    }

                                } else {
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-694)");
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-695)");
                            }

                        } else {
                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-696)");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isLoaderVisible = false;
                        AppUtil.hideProgress();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void mCheckMobileNo(final boolean isFromSignup) {
        if (isUserRegisteredAlready) {

            String number = txtViewNumber.getText().toString();
            for (int i = 0; number.startsWith("0"); i++) {
                number = number.replaceFirst("0", "");
            }

            if (mobile != null && mobile.equalsIgnoreCase(number) && country_code.equalsIgnoreCase(txt_country_code.getText().toString())) {
                txtViewNumber.setActivated(false);
                txtViewNumber.setSelected(false);
                txt_mobile_warning.setVisibility(View.GONE);
                isMobileNoAvailable = true;
                if (isFromSignup) {
                    if (!isLoaderVisible) {
                        isLoaderVisible = true;
                        AppUtil.showProgress(mContext);
                    }
                    signUp();
                }
            } else {
                if (NetworkUtil.networkStatus(mContext)) {
                    try {
                        ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                        Call<ResponseBody> call = apiService.mCheckMobileUnique((new ConvertJsonToMap().jsonToMap(mCheckMobileData)));
                        if (isFromSignup) {
                            if (!isLoaderVisible) {
                                isLoaderVisible = true;
                                AppUtil.showProgress(mContext);
                            }
                        }
                        APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                            //call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.body() != null) {
                                        JSONObject jsonObjectresponse = new JSONObject(response.body().string());
                                        JSONObject jsonObjectresponse1 = jsonObjectresponse.getJSONObject("response");
                                        if (jsonObjectresponse1.optString("status").equalsIgnoreCase("FAIL")) {
                                            AppUtil.hideProgress();
                                            isLoaderVisible = false;
                                            txtViewNumber.setActivated(true);
                                            txtViewNumber.setSelected(true);

                                            txt_mobile_warning.setText(jsonObjectresponse1.optString("error_message"));
                                            txt_mobile_warning.setVisibility(View.VISIBLE);

                                        } else {
                                            txtViewNumber.setActivated(false);
                                            txtViewNumber.setSelected(false);
                                            txt_mobile_warning.setVisibility(View.GONE);
                                            isMobileNoAvailable = true;
                                            if (isFromSignup) {
                                                signUp();
                                            }
                                        }
                                    } else {
                                        AppUtil.hideProgress();
                                        isLoaderVisible = false;
                                        if (isFromSignup) {
                                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-616)");
                                        }
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.hideProgress();
                                    isLoaderVisible = false;
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                AppUtil.hideProgress();
                                isLoaderVisible = false;
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppUtil.hideProgress();
                        isLoaderVisible = false;
                    }

                }
            }
        } else {
            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                    Call<ResponseBody> call = apiService.mCheckMobileUnique((new ConvertJsonToMap().jsonToMap(mCheckMobileData)));
                    if (isFromSignup) {
                        if (!isLoaderVisible) {
                            isLoaderVisible = true;
                            AppUtil.showProgress(mContext);
                        }
                    }
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObjectresponse = new JSONObject(response.body().string());
                                    JSONObject jsonObjectresponse1 = jsonObjectresponse.getJSONObject("response");
                                    if (jsonObjectresponse1.optString("status").equalsIgnoreCase("FAIL")) {
                                        AppUtil.hideProgress();
                                        isLoaderVisible = false;
                                        txtViewNumber.setActivated(true);
                                        txtViewNumber.setSelected(true);

                                        txt_mobile_warning.setText(jsonObjectresponse1.optString("error_message"));
                                        txt_mobile_warning.setVisibility(View.VISIBLE);

                                    } else {
                                        txtViewNumber.setActivated(false);
                                        txtViewNumber.setSelected(false);
                                        txt_mobile_warning.setVisibility(View.GONE);
                                        isMobileNoAvailable = true;
                                        if (isFromSignup) {
                                            signUp();
                                        }
                                    }
                                } else {
                                    AppUtil.hideProgress();
                                    isLoaderVisible = false;
                                    if (isFromSignup) {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-617)");
                                    }
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                isLoaderVisible = false;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
                            isLoaderVisible = false;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppUtil.hideProgress();
                    isLoaderVisible = false;
                }

            }
        }
    }

    private void mCheckEmailID(final boolean isFromSignUp) {
        if (NetworkUtil.networkStatus(mContext)) {
            try {
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                Call<ResponseBody> call = apiService.mCheckEmailExistance((new ConvertJsonToMap().jsonToMap(mCheckMailData)));
                if (isFromSignUp) {
                    isLoaderVisible = true;
                    AppUtil.showProgress(mContext);
                }
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null) {
                            try {
                                JSONObject jsonObjectresponse = new JSONObject(response.body().string());
                                JSONObject respnseData = jsonObjectresponse.getJSONObject("response");
                                if (respnseData.optString("status ").equalsIgnoreCase("FAIL")) {
                                    AppUtil.hideProgress();
                                    isLoaderVisible = false;
                                    appCompatEditTextEmail.setActivated(true);
                                    appCompatEditTextEmail.setSelected(true);

                                    txt_email_warning.setText(jsonObjectresponse.optJSONObject("response").optString("error_message"));
                                    txt_email_warning.setVisibility(View.VISIBLE);

                                } else {
                                    appCompatEditTextEmail.setActivated(false);
                                    appCompatEditTextEmail.setSelected(false);
                                    txt_email_warning.setVisibility(View.GONE);
                                    isEmailAvailable = true;
                                    if (isFromSignUp) {
                                        signUp();
                                    }
                                }


                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                isLoaderVisible = false;
                            }
                        } else {
                            AppUtil.hideProgress();
                            isLoaderVisible = false;
                            if (isFromSignUp) {
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-618)");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        isLoaderVisible = false;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                isLoaderVisible = false;
            }

        }
    }

    // load  the countries list from loacal to set the device's default information
    public class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<CountryPhoneCode>> {

        private Context mContext;
        private ArrayList<CountryPhoneCode> mCountry = new ArrayList<>();
        private SparseArray<ArrayList<CountryPhoneCode>> mCountriesMap = new SparseArray<ArrayList<CountryPhoneCode>>();

        AsyncPhoneInitTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<CountryPhoneCode> doInBackground(Void... params) {

            /**
             *  Load RequestData From Text File
             * */
            ArrayList<CountryPhoneCode> data = new ArrayList<CountryPhoneCode>(233);
            mCountry = new ArrayList<CountryPhoneCode>(233);
            BufferedReader reader = null;
            try {

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));
                } else {
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries_arabic.dat"), "UTF-8"));
                }

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    CountryPhoneCode c = new CountryPhoneCode(mContext, line, i);
                    data.add(c);
                    ArrayList<CountryPhoneCode> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<CountryPhoneCode>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<CountryPhoneCode> data) {
            mCountry = data;
            if (selected_self_countru_code == null) {
                for (int i = 0; i < mCountry.size(); i++) {
                    if (country_code != null) {
                        if (country_code.equalsIgnoreCase(mCountry.get(i).getCountryCode() + AppConstants.BLANK_STRING)) {
                            selected_self_countru_code = mCountry.get(i);
                            txt_country_code.setText(selected_self_countru_code.getCountryCodeStr());
                            mCountryCode = selected_self_countru_code.getCountryCode();
                            break;
                        }
                    } else {
                        if ("eg" .equalsIgnoreCase(mCountry.get(i).getCountryISO().toUpperCase())) {
                            selected_self_countru_code = mCountry.get(i);
                            txt_country_code.setText(selected_self_countru_code.getCountryCodeStr());
                            mCountryCode = selected_self_countru_code.getCountryCode();
                            break;
                        }
                    }
                }

                if (selected_self_countru_code == null) {
                    for (int i = 0; i < mCountry.size(); i++) {
                        if ("eg" .equalsIgnoreCase(mCountry.get(i).getCountryISO().toUpperCase())) {
                            selected_self_countru_code = mCountry.get(i);
                            txt_country_code.setText(selected_self_countru_code.getCountryCodeStr());
                            mCountryCode = selected_self_countru_code.getCountryCode();
                            break;
                        }
                    }
                }
            } else {
                txt_country_code.setText(selected_self_countru_code.getCountryCodeStr());
                mCountryCode = selected_self_countru_code.getCountryCode();
            }
            /*if (selected_country.getCountryCode() ==1)
            {
                txt_country_code.setText("+20");
                mCountryCode = 20;
            }*/
        }
    }

    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
            Intent intentAbout = new Intent(mContext, CommonWebViewActivity.class);
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_TERMS_OF_USE + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.terms_of_use));
            startActivity(intentAbout);
        }
    };

    ClickableSpan clickableSpan1 = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
//            ToastUtil.show(getContext(),"Clicked Smile ");
            Intent intentAbout = new Intent(mContext, CommonWebViewActivity.class);
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_PRIVACY_POLICY + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.privacy_policy));
            startActivity(intentAbout);
        }
    };
}
