package thegroceryshop.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.orders.PaymentWebViewActivity;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by mohitd on 23-Feb-17.
 */

public class TGSCreditsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txt_title;

    private TextView txt_credit_amount;
    private TextView btn_add_amount;
    private EditText edt_amount;
    private LoaderLayout loader_credit;
    public static final int REQUEST_ADD_AMOUNT = 1212;
    public static final int RESULT_JUST_BACK = 1313;
    public static final String AMOUNT_TO_ADD = "amount_to_add";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_tgs_credits);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
        toolbar.setNavigationIcon(R.mipmap.top_back);
        txt_title.setText(getString(R.string.tgs_credit).toUpperCase());

        txt_credit_amount = findViewById(R.id.add_credit_txt_credit_amount);
        btn_add_amount = (Button) findViewById(R.id.add_credit_btn_add);
        edt_amount = findViewById(R.id.add_credit_edt_amount);
        loader_credit = findViewById(R.id.add_credit_loader_credits);
        loader_credit.setStatuText(getString(R.string.credit_amount_error));

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            toolbar.setNavigationIcon(R.mipmap.top_back);
        }else{
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        loader_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loader_credit.isStatusTextShowing()){
                    loadCreditsInWalltet();
                }
            }
        });

        btn_add_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ValidationUtil.isValidAmountDigits(TGSCreditsActivity.this, edt_amount.getText().toString())){
                    Intent intentPayment;
                    intentPayment = new Intent(TGSCreditsActivity.this, PaymentMethodActivity.class);
                    intentPayment.putExtra(PaymentMethodActivity.IS_IN_PAYMENT_MODE, true);
                    intentPayment.putExtra(AMOUNT_TO_ADD, edt_amount.getText().toString());
                    startActivityForResult(intentPayment, REQUEST_ADD_AMOUNT);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCreditsInWalltet();
    }

    private void loadCreditsInWalltet() {

        try {

            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            loader_credit.showProgress();
            ApiInterface apiService = ApiClient.createService(ApiInterface.class, this);
            Call<ResponseBody> call = apiService.getCredits((new ConvertJsonToMap().jsonToMap(request)));
            APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                //call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loader_credit.showContent();
                    if (response.code() == 200) {
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            JSONObject resObj = obj.optJSONObject("response");
                            if (resObj.length() > 0) {

                                int statusCode = resObj.optInt("status_code", 0);
                                String status = resObj.optString("status");
                                String errorMsg = resObj.optString("error_message");
                                if (statusCode == 200 && status != null && status.equalsIgnoreCase("OK")) {

                                    JSONObject dataObj = resObj.optJSONObject("data");
                                    if(dataObj != null){
                                        String amount = dataObj.optString("user_credit_points");
                                        OnlineMartApplication.mLocalStore.saveUserCredit(Float.parseFloat(amount));
                                        if(amount != null && amount.length()>0){
                                            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                                                txt_credit_amount.setText(getString(R.string.egp) + AppConstants.SPACE + amount);
                                            }else{
                                                txt_credit_amount.setText(amount + AppConstants.SPACE + getString(R.string.egp));
                                            }
                                        }else{
                                            loader_credit.showStatusText();
                                        }
                                    }else{
                                        loader_credit.showStatusText();
                                    }

                                } else {
                                    loader_credit.showStatusText();
                                }

                            } else {
                                loader_credit.showStatusText();
                            }

                        } catch (Exception e) {
                            loader_credit.showStatusText();
                            e.printStackTrace();
                        }
                    } else {
                        loader_credit.showStatusText();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppUtil.hideProgress();
                    loader_credit.showContent();
                    loader_credit.showStatusText();
                }
            });

        } catch (JSONException e) {
            AppUtil.hideProgress();
            loader_credit.showContent();
            loader_credit.showStatusText();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD_AMOUNT){

            if(resultCode == Activity.RESULT_OK){

                if(data != null){

                    String callback_url = data.getStringExtra(PaymentWebViewActivity.KEY_CALLBACK_URL);
                    if (callback_url != null) {
                        callback_url = callback_url.replace("#", "");
                        Uri uri = Uri.parse(callback_url);
                        String status = uri.getQueryParameter("success");
                        String message = uri.getQueryParameter("data.message").replace("+", " ");

                        JSONObject object = new JSONObject();
                        try {

                            object.put("response", callback_url);
                            if(!ValidationUtil.isNullOrBlank(status)){
                                if(status.equalsIgnoreCase("true")){

                                    loadCreditsInWalltet();
                                    AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(TGSCreditsActivity.this, getResources().getString(R.string.app_name), getString(R.string.amount_added_successfully), getString(R.string.ok));
                                    mSingleActionAlertDialog.show();
                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            edt_amount.setText(AppConstants.BLANK_STRING);
                                        }
                                    });

                                }else{
                                    if(!ValidationUtil.isNullOrBlank(message) && message.equalsIgnoreCase("Declined")){
                                        AppUtil.displaySingleActionAlert(TGSCreditsActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_declined), getString(R.string.ok));

                                    }else{
                                        AppUtil.displaySingleActionAlert(TGSCreditsActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                                    }
                                }
                            }else{
                                AppUtil.displaySingleActionAlert(TGSCreditsActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppUtil.displaySingleActionAlert(TGSCreditsActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                        }

                    }else{
                        AppUtil.displaySingleActionAlert(TGSCreditsActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                    }
                }

            } else {
                if(resultCode != RESULT_JUST_BACK){
                    AppUtil.displaySingleActionAlert(TGSCreditsActivity.this, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                }
            }

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
