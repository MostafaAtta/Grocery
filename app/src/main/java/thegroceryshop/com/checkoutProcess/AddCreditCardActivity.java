package thegroceryshop.com.checkoutProcess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.CommonWebViewActivity;
import thegroceryshop.com.adapter.SpinnerItemAdapter;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.Item;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;


/*
 * Created by umeshk on 12/19/2016.
 */
public class AddCreditCardActivity extends AppCompatActivity implements View.OnClickListener {

    // for operations
    private AddCreditCardActivity mActivity;
    private EditText mEditTextName,
            mEditTextCreditCardNumber,
            mEditTextMM,
            mEditTextYY,
            mEditTextCVV;
    private RippleButton mRippleButtonAdd;
    private CheckBox check_terms;
    private TextView text_check;
    private String mName,
            mCreditCardNumbers,
            mTextMM,
            mTextYY,
            mTextCVV;
    private TextView mTextViewTerms;
    private TextView textViewTitle;
    private LinearLayout lyt_check;
    private String paymentToken = "";
    private String userId;
    private String databaseId = "";
    private String cardTokenForPayment = "";
    private String month, year;

    private Toolbar toolbar;
    private TextView txt_title;

    private ArrayList<Item> list_months = new ArrayList<>(), list_years = new ArrayList<>();
    private Spinner spinner_months, spinner_years;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_credit_card);
        mActivity = this;

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                toolbar.setNavigationIcon(R.mipmap.top_back);
            }else{
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            txt_title.setText(getResources().getString(R.string.add_card).toUpperCase());
        }

        initializeViews();
        setOnClickListener();
    }

    private void initializeViews() {

        mTextViewTerms = findViewById(R.id.mtextViewTerms);
        mEditTextCreditCardNumber = findViewById(R.id.meditTextCreditCardNumber);
        mEditTextName = findViewById(R.id.meditTextName);
        mEditTextMM = findViewById(R.id.meditTextMM);
        mEditTextYY = findViewById(R.id.meditTextYY);
        mEditTextCVV = findViewById(R.id.mEditTextCVV);
        mRippleButtonAdd = findViewById(R.id.mRippleButtonAdd);
        lyt_check = findViewById(R.id.lyt_check);
        check_terms = findViewById(R.id.add_credit_check_terms);
        text_check = findViewById(R.id.add_credit_text_terms);
        textViewTitle = findViewById(R.id.textViewTitle);
        spinner_months = findViewById(R.id.add_card_spinner_month);
        spinner_years = findViewById(R.id.add_card_spinner_year);
        mEditTextCVV.setLongClickable(false);

        spinner_months.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AppUtil.hideSoftKeyboard(AddCreditCardActivity.this, AddCreditCardActivity.this);
                mEditTextCreditCardNumber.clearFocus();
                mEditTextName.clearFocus();
                return false;
            }
        });

        spinner_years.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AppUtil.hideSoftKeyboard(AddCreditCardActivity.this, AddCreditCardActivity.this);
                mEditTextCreditCardNumber.clearFocus();
                mEditTextName.clearFocus();
                return false;
            }
        });

        list_months.clear();
        for(int i=1; i<13; i++){
            Item item = new Item(AppUtil.getDecimalForamte(i) +AppConstants.BLANK_STRING, i + AppConstants.BLANK_STRING, false);
            list_months.add(item);
        }

        list_years.clear();
        for(int i = 2018; i<2029; i++){
            if(i == 2020){
                Item item = new Item("20", "2020", false);
                list_years.add(item);
            }else{
                Item item = new Item(
                        ( i + AppConstants.BLANK_STRING).replace("20", AppConstants.BLANK_STRING),
                        i + AppConstants.BLANK_STRING,
                        false);
                list_years.add(item);
            }
        }

        SpinnerItemAdapter monthsAdapter = new SpinnerItemAdapter(AddCreditCardActivity.this, list_months, "MM");
        spinner_months.setAdapter(monthsAdapter);

        spinner_months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    month = AppConstants.BLANK_STRING;
                }else{
                    month = list_months.get(position).getItemId();
                }
                mEditTextMM.setText(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerItemAdapter yearsAdapter = new SpinnerItemAdapter(AddCreditCardActivity.this, list_years, "YYYY");
        spinner_years.setAdapter(yearsAdapter);

        spinner_years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    year = AppConstants.BLANK_STRING;
                }else{
                    year = list_years.get(position).getItemId();
                }
                mEditTextYY.setText(year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spannable wordtoSpan = new SpannableString(getResources().getString(R.string.checkbox_text));
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.yello_trm)), 51, 63, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(clickableSpan, 51, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(privacy_policy_span, 66, 80, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_check.setMovementMethod(LinkMovementMethod.getInstance());
        text_check.setText(wordtoSpan);

        check_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lyt_check.setSelected(false);
                }
            }
        });

        userId = ApiLocalStore.getInstance(mActivity).getUserId();


        mEditTextCreditCardNumber.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrecntString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // chech that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });
    }

    /**
     * click event on views
     */
    private void setOnClickListener() {

        mRippleButtonAdd.setOnClickListener(this);
        mTextViewTerms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.mRippleButtonAdd:

                AppUtil.hideSoftKeyboard(AddCreditCardActivity.this, AddCreditCardActivity.this);
                if (ValidationUtil.validateCreditCardForm(mActivity,
                        mEditTextName,
                        mEditTextCreditCardNumber,
                        mEditTextMM,
                        mEditTextYY,
                        mEditTextCVV)
                        ) {

                    if (check_terms.isChecked()) {
                        mCreditCardNumbers = mEditTextCreditCardNumber.getText().toString().replaceAll("-","");
                        mName = mEditTextName.getText().toString();
                        mTextMM = mEditTextMM.getText().toString();
                        mTextYY = mEditTextYY.getText().toString();
                        mTextCVV = mEditTextCVV.getText().toString();

                        if (mTextMM.length() == 1 && Integer.parseInt(mTextMM) > 0 && Integer.parseInt(mTextMM) <= 9) {
                            mTextMM = "0" + mTextMM;
                            mEditTextMM.setText(mTextMM);
                        }
                        firstPaymentWebService();
                    } else if (!check_terms.isChecked()) {
                        lyt_check.setSelected(true);
                        final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(this, getString(R.string.app_name), getString(R.string.please_accept_terms), getString(R.string.cancel_caps), getString(R.string.i_agree));
                        appDialogDoubleAction.setCanceledOnTouchOutside(false);
                        appDialogDoubleAction.setCancelable(false);
                        appDialogDoubleAction.show();
                        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                            @Override
                            public void onLeftActionClick(View view) {
                                appDialogDoubleAction.dismiss();
                            }

                            @Override
                            public void onRightActionClick(View view) {
                                appDialogDoubleAction.dismiss();
                                check_terms.setChecked(true);
                            }
                        });
                        //AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, getResources().getString(R.string.please_accept_terms), getString(R.string.ok));
                    }
                }
                break;
            case R.id.mtextViewTerms:
                /*Intent intent = new Intent(getActivity(), CommonWebViewActivity.class);
                intent.putExtra("commonUrl", AppConstants.URL_TERMS_OF_USE);
                intent.putExtra("textHeader", getString(R.string.terms_of_use));
                startActivity(intent);*/
                break;

        }

    }

    private void firstPaymentWebService() {
        if (NetworkUtil.networkStatus(mActivity)) {
            AppUtil.showProgress(mActivity);
            try {

                final JSONObject request_data = new JSONObject();
                request_data.put("user_id", userId);
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.startCardProcess((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                if (resObject.getInt("status_code") == 200) {
                                    JSONObject dataObject = resObject.getJSONObject("data");
                                    databaseId = dataObject.getString("database_id");
                                    paymentToken = dataObject.getString("payment_token");
                                    acceptPayMobAPI(paymentToken);
                                }else if (resObject.getInt("status_code") == 205) {
                                    AppUtil.hideProgress();
                                    if (resObject.has("error_message")) {
                                        AppUtil.showErrorDialog(mActivity,resObject.optString("error_message"));
                                    }
                                }

                            } catch (JSONException | IOException e) {
                                AppUtil.hideProgress();
                                AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-645)");
                                e.printStackTrace();
                            }
                        }else{
                            AppUtil.hideProgress();
                            AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-646)");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-647)");
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-648)");
            }
        }
    }

    private void acceptPayMobAPI(String paymentToken) {
        if (NetworkUtil.networkStatus(mActivity)) {
            //AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("pan", mEditTextCreditCardNumber.getText().toString().replaceAll("-",""));
                request_data.put("cardholder_name", mName);
                request_data.put("expiry_month", mTextMM);
                request_data.put("expiry_year", mTextYY);
                request_data.put("cvn", mTextCVV);

                ApiInterface apiService = ApiClient.getRequestQueue("https://accept.paymobsolutions.com/api/acceptance/").create(ApiInterface.class);
                Call<ResponseBody> call = apiService.acceptCardFromPayMob(paymentToken, (new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.code() == 201) {
                            try {
                                String JSONString = response.body().string();
                                JSONObject jsonObject = new JSONObject(JSONString);
                                saveCardAPI(jsonObject, JSONString);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                saveCardAPI(null, null);
                                AppUtil.hideProgress();
                            }
                        } else {
                            try {
                                if(response.errorBody() != null) {
                                    saveCardAPI(null, response.errorBody().string());
                                    AppUtil.hideProgress();
                                }else {
                                    saveCardAPI(null, null);
                                    AppUtil.hideProgress();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                saveCardAPI(null, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppUtil.hideProgress();
                        AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-649)");
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                AppUtil.hideProgress();
                AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-650)");
            }
        }
    }

    private void saveCardAPI(JSONObject cardObject, String response) {
        if (NetworkUtil.networkStatus(mActivity)) {
            //AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();

                if (cardObject != null) {
                    cardTokenForPayment = cardObject.getString("token");
                    request_data.put("database_id", databaseId);
                    request_data.put("user_id", userId);
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("id", cardObject.getString("id"));
                    request_data.put("token", cardTokenForPayment);
                    request_data.put("card_name", mName);
                    request_data.put("card_month", mTextMM);
                    request_data.put("card_year", mTextYY);
                    request_data.put("merchant_id", cardObject.getString("merchant_id"));
                    request_data.put("masked_pan", cardObject.getString("masked_pan"));
                    request_data.put("card_subtype", cardObject.getString("card_subtype"));
                    request_data.put("created_at", cardObject.getString("created_at"));
                } else {
                    request_data.put("database_id", databaseId);
                    request_data.put("user_id", userId);
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    if(!ValidationUtil.isNullOrBlank(response) && response.equalsIgnoreCase("[\"Invalid Card Data\"]")){
                        request_data.put("error", "2");
                    }else{
                        request_data.put("error", "1");
                    }
                }

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.saveCardAPI((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                if (resObject.optString("status_code") != null && resObject.optString("status_code").equalsIgnoreCase("200")) {
                                    AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mActivity, getString(R.string.app_name), resObject.getString("success_message"), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            finish();
                                            //startPaymentProcessAPI();
                                        }
                                    });
                                }else{
                                    AppUtil.hideProgress();
                                    AppUtil.displaySingleActionAlert(AddCreditCardActivity.this, AppConstants.ERROR_TAG, resObject.optString("error_message"), getString(R.string.ok));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                AppUtil.displaySingleActionAlert(AddCreditCardActivity.this, AppConstants.ERROR_TAG, getString(R.string.error_msg) + "(ERR-654)", getString(R.string.ok));
                            }
                        }else{
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(AddCreditCardActivity.this, AppConstants.ERROR_TAG, getString(R.string.error_msg) + "(ERR-653)", getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-651)");
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                AppUtil.showErrorDialog(mActivity,getString(R.string.error_msg) + "(ERR-652)");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
//            ToastUtil.show(getContext(),"Clicked Smile ");
            AppUtil.hideSoftKeyboard(AddCreditCardActivity.this, AddCreditCardActivity.this);
            Intent intentAbout = new Intent(AddCreditCardActivity.this, CommonWebViewActivity.class);
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_TERMS_OF_USE + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.terms_of_use));
            startActivity(intentAbout);
        }
    };

    ClickableSpan privacy_policy_span = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
//            ToastUtil.show(getContext(),"Clicked Smile ");
            AppUtil.hideSoftKeyboard(AddCreditCardActivity.this, AddCreditCardActivity.this);
            Intent intentAbout = new Intent(AddCreditCardActivity.this, CommonWebViewActivity.class);
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_PRIVACY_POLICY + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.privacy_policy));
            startActivity(intentAbout);
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
