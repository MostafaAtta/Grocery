package thegroceryshop.com.activity;

/*
 * Created by umeshk on 13-03-2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class CreateWishListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnCancel;
    private Button btnDone;
    private AppCompatEditText editTextListName;
    private EditText editTextDescription;
    private Toolbar toolbar;
    private TextView txtTitle;
    private ApiInterface apiService;
    private String product_id;

    private String list_id, list_name, list_description;
    private boolean isInEditMode;
    private JSONArray prodArray;

    public static final String KEY_WISHLIST_ID = "key_wishlist_id";
    public static final String KEY_WISHLIST_NAME = "key_wishlist_name";
    public static final String KEY_WISHLIST_DESCRIPTION = "key_wishlist_description";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wishlist);
        initView();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }
        }

        if(getIntent() != null && getIntent().getStringExtra("product_id") != null){
            product_id = getIntent().getStringExtra("product_id");
        }

        if(getIntent() != null && getIntent().getStringExtra(KEY_WISHLIST_ID) != null){
            list_id = getIntent().getStringExtra(KEY_WISHLIST_ID);
            isInEditMode = true;
        }else{
            isInEditMode = false;
        }

        if(getIntent() != null && getIntent().getStringExtra(KEY_WISHLIST_NAME) != null){
            list_name = getIntent().getStringExtra(KEY_WISHLIST_NAME);
        }

        if(getIntent() != null && getIntent().getStringExtra(KEY_WISHLIST_DESCRIPTION) != null){
            list_description = getIntent().getStringExtra(KEY_WISHLIST_DESCRIPTION);
        }

        if(isInEditMode){
            txtTitle.setText(getResources().getString(R.string.update));
            btnDone.setText(getResources().getString(R.string.update));
        }else{
            txtTitle.setText(getResources().getString(R.string.create_a_new_list));
        }
    }

    private void initView() {
        btnCancel = findViewById(R.id.btn_cancel);
        btnDone = findViewById(R.id.btn_done);
        editTextListName = findViewById(R.id.editTextListName);
        editTextDescription = findViewById(R.id.editTextDescription);
        //editTextDescription.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        editTextDescription.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.editTextSplIns) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);
        apiService = ApiClient.createService(ApiInterface.class, this);
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
    public void onClick(View view) {

        if (view == btnCancel) {
            finish();
        } else if (view == btnDone) {
            if (editTextListName.getText().toString().trim().isEmpty()) {
                AppUtil.displaySingleActionAlert(this, getString(R.string.title), getString(R.string.enter_list_name), getString(R.string.ok));
            } else if (editTextDescription.getText().toString().trim().isEmpty()) {
                AppUtil.displaySingleActionAlert(this, getString(R.string.title), getString(R.string.enter_list_description), getString(R.string.ok));
            } else {
                if(isInEditMode){
                    //updateWishListInfo();
                    new UpdateWishListsTask(ApiClient.getAPIURL() + "wishlist_info").execute();
                }else{
                    createWishList();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(CreateWishListActivity.this,CreateWishListActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ValidationUtil.isNullOrBlank(list_id)){
            loadWishListDetail();
        }
    }

    private void loadWishListDetail() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                AppUtil.showProgress(CreateWishListActivity.this);
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("list_id", list_id);
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                Call<ResponseBody> call = apiService.wishListInfo((new ConvertJsonToMap().jsonToMap(request)));
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
                                    prodArray = new JSONArray();
                                    if (statusCode == 200) {

                                        AppUtil.hideProgress();

                                        JSONObject dataObject = resObj.optJSONObject("data");
                                        JSONArray productsArray = dataObject.optJSONArray("products");
                                        if (productsArray != null && productsArray.length() > 0) {

                                            for (int i = 0; i < productsArray.length(); i++) {

                                                JSONObject object = productsArray.getJSONObject(i);
                                                JSONObject obj1 = new JSONObject();

                                                obj1.put("product_id", object.optString("product_id"));
                                                obj1.put("qty", object.optInt("wishlist_qty", 1));
                                                obj1.put("isRemoved", false);

                                                prodArray.put(prodArray.length(), obj1);
                                            }

                                        } else {
                                            AppUtil.hideProgress();
                                        }

                                        editTextListName.setText(list_name);
                                        editTextDescription.setText(list_description);

                                    } else {
                                        AppUtil.hideProgress();
                                    }

                                } else {
                                    AppUtil.hideProgress();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                            }
                        } else {
                            AppUtil.hideProgress();
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
            AppUtil.requestLogin(CreateWishListActivity.this);
        }
    }

    private void updateWishListInfo() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {

                AppUtil.showProgress(CreateWishListActivity.this);

                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("list_id", list_id);
                request.put("list_name", editTextListName.getText());
                request.put("list_description", editTextDescription.getText());
                request.put("products", prodArray);
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                Call<ResponseBody> call = apiService.wishListInfo((new ConvertJsonToMap().jsonToMap(request)));
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
                                    String successMessage = resObj.optString("success_message");
                                    if (statusCode == 200) {

                                        AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.app_name), successMessage, getString(R.string.ok), new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();
                                                Intent intent = new Intent();
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }
                                        });

                                    } else {
                                        AppUtil.hideProgress();
                                        AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), errorMsg, getString(R.string.ok));
                                    }

                                } else {
                                    AppUtil.hideProgress();
                                    AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-709)", getString(R.string.ok));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-710)", getString(R.string.ok));
                            }
                        } else {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-711)", getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-712)", getString(R.string.ok));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AppUtil.requestLogin(CreateWishListActivity.this);
        }

    }

    private void createWishList() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("list_name", editTextListName.getText().toString());
            request_data.put("list_description", editTextDescription.getText().toString());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(this)) {
                try {
                    Call<ResponseBody> call = apiService.createWishList((new ConvertJsonToMap().jsonToMap(request_data)));
                    AppUtil.showProgress(this);

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
                                            AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), responseMessage, getString(R.string.ok), new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                    Intent intent = new Intent();
                                                    intent.putExtra("product_id", product_id);
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            });

                                        } else {
                                            AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), errorMsg, getString(R.string.ok), new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();

                                                }
                                            });
                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-706)", getString(R.string.ok), new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();

                                            }
                                        });
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-707)", getString(R.string.ok), new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();

                                        }
                                    });
                                }
                            } else {
                                LoadUsualData.loadWishLists(CreateWishListActivity.this);
                                AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.app_name), getString(R.string.error_msg) + "(Err-708)", getString(R.string.ok), new OnSingleActionListener() {
                                    @Override
                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                        appDialogSingleAction.dismiss();

                                    }
                                });
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

            } else {
                AppUtil.hideProgress();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppUtil.hideProgress();
        }
    }

    private class UpdateWishListsTask extends AsyncTask<Void, Void, Void> {

        private String URL;
        private okhttp3.Response response;
        private String response_body;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppUtil.showProgress(CreateWishListActivity.this);

        }

        public UpdateWishListsTask(String URL) {
            this.URL = URL;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try{

                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("list_id", list_id);
                request.put("list_name", editTextListName.getText());
                request.put("list_description", editTextDescription.getText());
                request.put("products", prodArray);

                OkHttpClient client = new OkHttpClient();

                Log.d("wishlist_info API URL",  URL);
                Log.d("wishlist_info Request",  request.toString());

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, request.toString());
                Request api_request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();

                response = client.newCall(api_request).execute();
                response_body = response.body().string();

                Log.d("wishlist_info Response",  response_body);

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            AppUtil.hideProgress();

            if (response.code() == 200) {
                try {
                    JSONObject obj = new JSONObject(response_body);
                    JSONObject resObj = obj.optJSONObject("response");
                    if (resObj.length() > 0) {
                        int statusCode = resObj.optInt("status_code", 0);

                        String errorMsg = resObj.optString("error_message");
                        String successMessage = resObj.optString("success_message");
                        if (statusCode == 200) {

                            AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.app_name), successMessage, getString(R.string.ok), new OnSingleActionListener() {
                                @Override
                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                    appDialogSingleAction.dismiss();
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });

                        } else {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), errorMsg, getString(R.string.ok));
                        }

                    } else {
                        AppUtil.hideProgress();
                        AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg), getString(R.string.ok));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtil.hideProgress();
                    AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg), getString(R.string.ok));
                }
            } else {
                AppUtil.hideProgress();
                AppUtil.displaySingleActionAlert(CreateWishListActivity.this, getString(R.string.title), getString(R.string.error_msg), getString(R.string.ok));
            }
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
