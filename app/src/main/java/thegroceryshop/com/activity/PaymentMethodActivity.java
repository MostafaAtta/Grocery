package thegroceryshop.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.PaymentMethodAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.AddCreditCardActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.TwoActionAlertDialogWithEditText;
import thegroceryshop.com.modal.PaymentCard;
import thegroceryshop.com.orders.PaymentWebViewActivity;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;


/**
 * Created by mohitd on 01-Mar-17.
 */

public class PaymentMethodActivity extends AppCompatActivity {

    public static final String IS_IN_PAYMENT_MODE = "is_in_payment_mode";
    private Context mContext;
    private Toolbar toolbar;
    private TextView txt_title;

    private LinearLayout lyt_done;
    private RecyclerView recyl_cards;
    private TextView txt_add_amount;
    private LoaderLayout loader;

    private ArrayList<PaymentCard> list_cards = new ArrayList<>();
    private ApiInterface apiService;
    private PaymentMethodAdapter paymentMethodAdapter;
    private boolean isInPaymentMode;
    private String amountToAdd;
    private PaymentCard selectedCard;
    private String paymentToken;
    private String cardCVV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment_method);

        mContext = this;
        apiService = ApiClient.createService(ApiInterface.class, mContext);
        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);

        lyt_done = findViewById(R.id.payment_method_lyt_done);
        recyl_cards = findViewById(R.id.payment_method_recyl_cards);
        loader = findViewById(R.id.payment_method_lyt_loader);
        txt_add_amount = findViewById(R.id.payment_method_txt_add_amount);
        loader.setStatuText(getString(R.string.no_saved_cards_available));

        if(getIntent() != null){
            isInPaymentMode = getIntent().getBooleanExtra(IS_IN_PAYMENT_MODE, false);
            if(isInPaymentMode){
                amountToAdd = getIntent().getStringExtra(TGSCreditsActivity.AMOUNT_TO_ADD);
            }
        }

        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                toolbar.setNavigationIcon(R.mipmap.top_back);
            }else{
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            if(isInPaymentMode){
                txt_title.setText(getResources().getString(R.string.select_a_card).toUpperCase());
                txt_add_amount.setVisibility(View.VISIBLE);
            }else{
                txt_title.setText(getResources().getString(R.string.payment_method).toUpperCase());
                txt_add_amount.setVisibility(View.GONE);
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyl_cards.setLayoutManager(linearLayoutManager);

        txt_add_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedCard != null){

                    final TwoActionAlertDialogWithEditText withEditText = new TwoActionAlertDialogWithEditText(
                            PaymentMethodActivity.this,
                            getString(R.string.enter_cvv),
                            getString(R.string.cvv_note),
                            getString(R.string.cancel_caps),
                            getString(R.string.ok));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        withEditText.edt_input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
                    }

                    withEditText.setOnTwoActionsListener(new TwoActionAlertDialogWithEditText.OnTwoActionAlertListener() {
                        @Override
                        public void onLeftActionClick(View view, String input) {
                            withEditText.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            try {
                                // hides the soft keyboard when the drawer opens
                                InputMethodManager inputManager = (InputMethodManager) PaymentMethodActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(withEditText.edt_input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                withEditText.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                                withEditText.dismiss();
                            }
                        }

                        @Override
                        public void onRightActionClick(View view, String input) {

                            try {
                                // hides the soft keyboard when the drawer opens
                                InputMethodManager inputManager = (InputMethodManager) PaymentMethodActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(withEditText.edt_input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                withEditText.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                                withEditText.dismiss();
                            }

                            cardCVV = input;
                            startAddAmountProcess();
                        }
                    });
                    withEditText.show();

                }else{
                    AppUtil.showErrorDialog(PaymentMethodActivity.this, getString(R.string.select_a_card));
                }
            }
        });

    }

    private void startAddAmountProcess() {

        if (NetworkUtil.networkStatus(PaymentMethodActivity.this)) {
            AppUtil.hideSoftKeyboard(PaymentMethodActivity.this, PaymentMethodActivity.this);
            AppUtil.showProgress(PaymentMethodActivity.this, getString(R.string.processing_payment));
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request_data.put("card_id", selectedCard.getCardId());
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                if(ApiClient.getPageURL().contains("prod")){
                    request_data.put("amount_cents", ((int)(Float.parseFloat(amountToAdd) * 100)) + AppConstants.BLANK_STRING);
                }else{
                    if((((int)(Float.parseFloat(amountToAdd) * 100))+ AppConstants.BLANK_STRING).endsWith("00")){
                        request_data.put("amount_cents", ((int)(Float.parseFloat(amountToAdd) * 100)) + AppConstants.BLANK_STRING);
                    }else{
                        request_data.put("amount_cents", ((int)(Float.parseFloat(amountToAdd) * 100))+ "00");
                    }
                }

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, PaymentMethodActivity.this);
                Call<ResponseBody> call = apiService.startAddCreditProcess((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");

                                if (resObject.optString("status_code") != null && resObject.optString("status_code").equalsIgnoreCase("200")) {
                                    JSONObject dataObject = resObject.getJSONObject("data");
                                    paymentToken = dataObject.getString("payment_token");
                                    lastPaymentProcessAPI(paymentToken);
                                } else {
                                    AppUtil.hideProgress();
                                    AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.ERROR_TAG, resObject.optString("error_message"), getString(R.string.ok));
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-702)", getString(R.string.ok));
                            }
                        } else {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-703)", getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-704)", getString(R.string.ok));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-705)", getString(R.string.ok));
            }
        }
    }

    private void lastPaymentProcessAPI(String paymentToken) {
        if (NetworkUtil.networkStatus(PaymentMethodActivity.this)) {
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("payment_token", paymentToken);
                JSONObject sourceObject = new JSONObject();
                sourceObject.put("identifier", selectedCard.getCard_token());
                sourceObject.put("sourceholder_name", OnlineMartApplication.mLocalStore.getFirstName() + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getLastName());
                sourceObject.put("subtype", "TOKEN");
                sourceObject.put("expiry_month", selectedCard.getCard_month());
                sourceObject.put("expiry_year", selectedCard.getCard_year());
                sourceObject.put("cvn", cardCVV);

                JSONObject billingObject = new JSONObject();
                billingObject.put("first_name", OnlineMartApplication.mLocalStore.getFirstName());
                billingObject.put("last_name", OnlineMartApplication.mLocalStore.getLastName());
                billingObject.put("street", "TheGroceryShop");
                billingObject.put("building", "TheGroceryShop");
                billingObject.put("floor", "TheGroceryShop");
                billingObject.put("apartment", "TheGroceryShop");
                billingObject.put("city", "Cairo");
                billingObject.put("state", "Cairo");
                billingObject.put("country", "Egypt");
                billingObject.put("email", OnlineMartApplication.mLocalStore.getUserEmail());
                billingObject.put("phone_number", OnlineMartApplication.mLocalStore.getUserPhone());
                billingObject.put("postal_code", "TheGroceryShop");

                request_data.put("source", sourceObject);
                request_data.put("billing", billingObject);

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, PaymentMethodActivity.this);
                Call<ResponseBody> call = apiService.lastPaymentProcess((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.code() == 200) {
                            try {
                                JSONObject json = new JSONObject(response.body().string());

                                JSONObject paymobJson = json.optJSONObject("response");
                                if(paymobJson != null){

                                    JSONObject jsonObject = paymobJson.optJSONObject("paymob_response");
                                    if(jsonObject != null){
                                        boolean is_3d_secure = jsonObject.optBoolean("is_3d_secure", false);
                                        if(is_3d_secure){
                                            String redirect_url = jsonObject.optString("redirect_url_3d_secure");
                                            if(!ValidationUtil.isNullOrBlank(redirect_url)){

                                                AppUtil.hideProgress();
                                                Intent intent = new Intent(PaymentMethodActivity.this, PaymentWebViewActivity.class);
                                                intent.putExtra(CommonWebViewActivity.KEY_URL, redirect_url);
                                                startActivityForResult(intent, TGSCreditsActivity.REQUEST_ADD_AMOUNT);

                                            }else{
                                                AppUtil.hideProgress();
                                                AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-658)", getString(R.string.ok));
                                            }

                                        }else{
                                            if(jsonObject.getString("success").equalsIgnoreCase("true")){

                                                OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                                OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                                OnlineMartApplication.getCartList().clear();
                                                boolean isTransactionDeclined = false;

                                                AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(PaymentMethodActivity.this, getResources().getString(R.string.app_name), getString(R.string.amount_added_successfully), getString(R.string.ok));
                                                mSingleActionAlertDialog.show();
                                                mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                                    @Override
                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                        appDialogSingleAction.dismiss();
                                                        setResult(Activity.RESULT_OK);
                                                        PaymentMethodActivity.this.finish();
                                                    }
                                                });

                                            }else{
                                                AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                                            }
                                        }
                                    }else {
                                        AppUtil.hideProgress();
                                        AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-659)", getString(R.string.ok));
                                    }
                                }else {
                                    AppUtil.hideProgress();
                                    AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-660)", getString(R.string.ok));
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-661)", getString(R.string.ok));
                            }
                        } else {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(PaymentMethodActivity.this, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-662)", getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(PaymentMethodActivity.this, getResources().getString(R.string.app_name), getString(R.string.payment_failed), getString(R.string.ok));
                        mSingleActionAlertDialog.show();
                        mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                            @Override
                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                appDialogSingleAction.dismiss();
                            }
                        });
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(PaymentMethodActivity.this, getResources().getString(R.string.app_name), getString(R.string.payment_failed), getString(R.string.ok));
                mSingleActionAlertDialog.show();
                mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        appDialogSingleAction.dismiss();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedCard = null;
        loadCardList();
    }

    private void loadCardList() {

        if (NetworkUtil.networkStatus(mContext)) {
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                loader.showProgress();
                Call<ResponseBody> call = apiService.getCardList((new ConvertJsonToMap().jsonToMap(request_data)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loader.showContent();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.optJSONObject("response");
                                list_cards.clear();
                                if (resObject.getInt("status_code") == 200) {
                                    JSONArray jsonArray = resObject.optJSONArray("data");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject cardObject = jsonArray.optJSONObject(i);
                                        PaymentCard paymentCard = new PaymentCard();
                                        paymentCard.setCardId(cardObject.optString("id"));
                                        paymentCard.setCard_token(cardObject.optString("token"));
                                        paymentCard.setCard_name(cardObject.optString("card_name"));
                                        paymentCard.setCard_number(cardObject.optString("masked_pan"));
                                        paymentCard.setMerchant_id(cardObject.optString("merchant_id"));
                                        paymentCard.setCard_subType(cardObject.optString("card_subtype"));
                                        paymentCard.setCard_month(cardObject.optString("card_month"));
                                        paymentCard.setCard_year(cardObject.optString("card_year"));
                                        list_cards.add(paymentCard);
                                    }
                                }

                                PaymentCard newPaymentCard = new PaymentCard();
                                list_cards.add(newPaymentCard);

                                paymentMethodAdapter = new PaymentMethodAdapter(mContext, list_cards, isInPaymentMode);
                                recyl_cards.setAdapter(paymentMethodAdapter);
                                paymentMethodAdapter.setOnCardSelectListener(new PaymentMethodAdapter.OnCardSelectListener() {
                                    @Override
                                    public void onCardSelectListener(final int position) {

                                        if(position < list_cards.size()){

                                            if(isInPaymentMode){

                                                PaymentCard paymentCard = list_cards.get(position);

                                                for (int i = 0; i < list_cards.size(); i++) {
                                                    if (paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId())) {
                                                        list_cards.get(position).setSelected(true);
                                                        cardCVV = null;
                                                        selectedCard = list_cards.get(position);
                                                    } else {
                                                        list_cards.get(position).setSelected(false);
                                                    }
                                                }

                                                paymentMethodAdapter.updateList(list_cards);

                                            } else {

                                                cardCVV = null;
                                                selectedCard = null;
                                                final AppDialogDoubleAction confirmationDialog = new AppDialogDoubleAction(mContext, getString(R.string.delete), getString(R.string.card_delete_confrmation), getString(R.string.cancel), getString(R.string.ok));
                                                confirmationDialog.show();
                                                confirmationDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                    @Override
                                                    public void onLeftActionClick(View view) {
                                                        confirmationDialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onRightActionClick(View view) {
                                                        confirmationDialog.dismiss();
                                                        deleteCard(list_cards.get(position).getCardId(), position);
                                                    }
                                                });
                                            }
                                        }

                                        if (list_cards != null && list_cards.size()-1 > position) {
                                            PaymentCard paymentCard = list_cards.get(position);

                                            for (int i = 0; i < list_cards.size()-1; i++) {
                                                list_cards.get(i).setSelected(paymentCard.getCardId().equalsIgnoreCase(list_cards.get(i).getCardId()));
                                            }

                                            paymentMethodAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onAddNewCard() {
                                        selectedCard = null;
                                        cardCVV = null;
                                        Intent intentMyCoupons;
                                        intentMyCoupons = new Intent(mContext, AddCreditCardActivity.class);
                                        startActivity(intentMyCoupons);
                                    }
                                });

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loader.showStatusText();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                loader.showStatusText();
            }
        }
    }

    private void deleteCard(String cardId, final int position) {

        try {
            JSONObject login_request_data = new JSONObject();
            login_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            login_request_data.put("card_id", cardId);
            login_request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            if (NetworkUtil.networkStatus(this)) {
                try {
                    AppUtil.showProgress(this);
                    Call<ResponseBody> call = apiService.deleteCard((new ConvertJsonToMap().jsonToMap(login_request_data)));
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
                                            AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, responseMessage, getString(R.string.ok), new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                    loadCardList();
                                                }
                                            });

                                        } else {
                                            AppUtil.showErrorDialog(mContext, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-655)");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-656)");
                                }
                            } else {
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-657)");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TGSCreditsActivity.REQUEST_ADD_AMOUNT){
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(isInPaymentMode){
            setResult(TGSCreditsActivity.RESULT_JUST_BACK);
            finish();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
