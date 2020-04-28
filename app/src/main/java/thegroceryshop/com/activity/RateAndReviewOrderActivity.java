package thegroceryshop.com.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

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
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;


/*
 * Created by mohitd on 23-Feb-17.
 */

public class RateAndReviewOrderActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView txt_title;
    private String order_id;
    private SimpleRatingBar rateAndReviewRating;
    private AppCompatEditText rateAndReviewEdt;
    private TextView textViewCheckOut;

    private Activity mContext;
    private int rating;
    private String comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_order_completed);

        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            /*if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }*/

            txt_title.setText(getResources().getString(R.string.order_completed_caps));
        }

        if (getIntent() != null) {
            order_id = getIntent().getStringExtra("order_id");
        }

        initView();

    }

    private void initView() {

        rateAndReviewRating = findViewById(R.id.rate_and_review_rating);
        rateAndReviewEdt = findViewById(R.id.rate_and_review_edt);
        textViewCheckOut = findViewById(R.id.textViewCheckOut);

        textViewCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rating = (int) rateAndReviewRating.getRating();
                comment = rateAndReviewEdt.getText().toString();

                if (rating <= 0) {
                    AppUtil.showErrorDialog(RateAndReviewOrderActivity.this, getString(R.string.please_enter_rating));
                } else {
                    submitRateAndReview();
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
    public void onBackPressed() {

    }

    private void submitRateAndReview() {

        try {
            //loaderLayout.showProgress();
            JSONObject request = new JSONObject();
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("order_id", order_id);
            request.put("rate", "" + rating);
            request.put("comment", comment);

            ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
            Call<ResponseBody> call = apiService.submitReviewRating((new ConvertJsonToMap().jsonToMap(request)));
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
                                    //String responseMessage = resObj.optString("success_message");
                                    JSONObject jsonObject = resObj.optJSONObject("data");


                                } else {
                                    //loaderLayout.showStatusText();
                                }

                            } else {
                                //loaderLayout.showStatusText();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            //loaderLayout.showStatusText();
                        }
                    } else {
                        //loaderLayout.showStatusText();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //loaderLayout.showStatusText();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
