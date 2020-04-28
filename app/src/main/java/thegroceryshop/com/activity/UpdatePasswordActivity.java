package thegroceryshop.com.activity;

/*
 * Created by umeshk on 4/10/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;


public class UpdatePasswordActivity extends AppCompatActivity {

    private ImageView imgViewBack;
    private TextView txtViewHeaderTitle;
    private AppCompatEditText editTextCurrentPassword;
    private AppCompatEditText editTextNewPassword;
    private AppCompatEditText editTextRetypeNewPassword;
    private RippleButton btnUpdatePassword;
    private Activity mActivity;
    private String user_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password_screen);
        mActivity = this;
        initView();
    }

    private void initView() {
        imgViewBack = findViewById(R.id.imgViewBack);
        txtViewHeaderTitle = findViewById(R.id.txtViewHeaderTitle);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextRetypeNewPassword = findViewById(R.id.editTextRetypeNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            imgViewBack.setImageResource(R.mipmap.top_back);
        } else {
            imgViewBack.setImageResource(R.mipmap.top_back_arabic);
        }

        txtViewHeaderTitle.setText(getString(R.string.update_password));
        user_id = ApiLocalStore.getInstance(mActivity).getUserId();

        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editTextCurrentPassword.getText().toString().isEmpty()) {

                    if (ValidationUtil.isValidNewPassword(UpdatePasswordActivity.this, editTextNewPassword, false)) {

                        if (ValidationUtil.isValidConfirmPassword(UpdatePasswordActivity.this, editTextRetypeNewPassword, false)) {

                            if (ValidationUtil.isValidConfirmPassword(mActivity, editTextRetypeNewPassword, editTextNewPassword.getText().toString())) {
                                updatePasswordWebService();
                            }
                        }
                    }
                } else {
                    AppUtil.showErrorDialog(UpdatePasswordActivity.this, getResources().getString(R.string.current_password_error));
                }


            }
        });
    }

    private void updatePasswordWebService() {
        if (NetworkUtil.networkStatus(mActivity)) {
            AppUtil.showProgress(mActivity);
            try {
                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("user_id", user_id);
                request_data.put("old_password", editTextCurrentPassword.getText().toString());
                request_data.put("new_password", editTextNewPassword.getText().toString());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.updatePassword((new ConvertJsonToMap().jsonToMap(request_data)));

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
                                    AppUtil.displaySingleActionAlert(mActivity, AppConstants.BLANK_STRING, resObject.getString("success_message"), "OK", new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            finish();
                                        }
                                    });
                                } else {
                                    AppUtil.displaySingleActionAlert(mActivity, AppConstants.BLANK_STRING, resObject.getString("error_message"), "OK");
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
