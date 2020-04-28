package thegroceryshop.com.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txt_title;
    private boolean isUpdate = false;
    private Context mContext;
    private ApiInterface apiService;

    private SwitchButton btn_allow_notifications;
    private SwitchButton btn_out_for_delivery;
    private SwitchButton btn_delivered;
    private SwitchButton btn_offers;
    private SwitchButton btn_notify_me;
    private LoaderLayout loader;

    private boolean isAllowNotifications;
    private boolean isOutForDelivery;
    private boolean isDelivered;
    private boolean isOffers;
    private boolean isNotifyMe;

    private CompoundButton.OnCheckedChangeListener onAllowNotificationChangeListener, onChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_title = findViewById(R.id.base_txt_title);
        txt_title.setText(getString(R.string.system_notifications).toUpperCase());

        mContext = this;
        apiService = ApiClient.createService(ApiInterface.class, mContext);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(AppConstants.BLANK_STRING);
            //actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setShowHideAnimationEnabled(true);
            //toolbar.setNavigationIcon(R.mipmap.top_back);
            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                toolbar.setNavigationIcon(R.mipmap.top_back);
            }else{
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

        }

        onAllowNotificationChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){
                    isAllowNotifications = false;
                    isOutForDelivery = false;
                    isDelivered = false;
                    isOffers = false;
                    isNotifyMe = false;

                    btn_delivered.setChecked(isDelivered);
                    btn_offers.setChecked(isOffers);
                    btn_notify_me.setChecked(isNotifyMe);
                    btn_out_for_delivery.setChecked(isOutForDelivery);

                    btn_delivered.setEnabled(isDelivered);
                    btn_offers.setEnabled(isOffers);
                    btn_notify_me.setEnabled(isNotifyMe);
                    btn_out_for_delivery.setEnabled(isOutForDelivery);

                }else{
                    isAllowNotifications = true;
                    isOutForDelivery = true;
                    isDelivered = true;
                    isOffers = true;
                    isNotifyMe = true;

                    btn_delivered.setEnabled(isDelivered);
                    btn_offers.setEnabled(isOffers);
                    btn_notify_me.setEnabled(isNotifyMe);
                    btn_out_for_delivery.setEnabled(isOutForDelivery);

                    btn_delivered.setChecked(isDelivered);
                    btn_offers.setChecked(isOffers);
                    btn_notify_me.setChecked(isNotifyMe);
                    btn_out_for_delivery.setChecked(isOutForDelivery);
                }

                loadUserPrefrences();
            }
        };

        onChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.getId() == R.id.notifications_btn_delivered) {
                    isDelivered = isChecked;
                } else if (buttonView.getId() == R.id.notifications_btn_out_for_delivery) {
                    isOutForDelivery = isChecked;
                } else if (buttonView.getId() == R.id.notifications_btn_offers) {
                    isOffers = isChecked;
                } else if (buttonView.getId() == R.id.notifications_btn_notify_me) {
                    isNotifyMe = isChecked;
                }

                if (isChecked) {
                    isAllowNotifications = true;
                }else{
                    if(!isDelivered && !isOutForDelivery && !isOffers && !isNotifyMe){
                        isAllowNotifications = false;
                    }
                }

                loadUserPrefrences();
            }
        };

        initView();
        loadUserPrefrences();
    }

    private void loadUserPrefrences() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("isUpdate", isUpdate ? "1" : "0");
            request_data.put("isNotifications", isAllowNotifications ? "1" : "0");
            request_data.put("isOutForDelivery", isOutForDelivery ? "1" : "0");
            request_data.put("isDelivered", isDelivered ? "1" : "0");
            request_data.put("isOffers", isOffers ? "1" : "0");
            request_data.put("isNotifyMe", isNotifyMe ? "1" : "0");

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    isUpdate = true;
                    loader.showProgress();
                    Call<ResponseBody> call = apiService.updateNotificationPrefrences((new ConvertJsonToMap().jsonToMap(request_data)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            loader.showContent();
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject object = resObj.optJSONObject("data");
                                            if (object != null) {

                                                if(!ValidationUtil.isNullOrBlank(object.optString("isNotifications"))){
                                                    isAllowNotifications = object.optString("isNotifications").equalsIgnoreCase("1");

                                                    if(!ValidationUtil.isNullOrBlank(object.optString("isOutForDelivery"))){
                                                        isOutForDelivery = object.optString("isOutForDelivery").equalsIgnoreCase("1");
                                                    }else{
                                                        isOutForDelivery = false;
                                                    }

                                                    if(!ValidationUtil.isNullOrBlank(object.optString("isDelivered"))){
                                                        isDelivered = object.optString("isDelivered").equalsIgnoreCase("1");
                                                    }else{
                                                        isDelivered = false;
                                                    }

                                                    if(!ValidationUtil.isNullOrBlank(object.optString("isOffers"))){
                                                        isOffers = object.optString("isOffers").equalsIgnoreCase("1");
                                                    }else{
                                                        isOffers = false;
                                                    }

                                                    if(!ValidationUtil.isNullOrBlank(object.optString("isNotifyMe"))){
                                                        isNotifyMe = object.optString("isNotifyMe").equalsIgnoreCase("1");
                                                    }else{
                                                        isNotifyMe = false;
                                                    }

                                                }else{
                                                    isAllowNotifications = false;
                                                    isOutForDelivery = false;
                                                    isDelivered = false;
                                                    isOffers = false;
                                                    isNotifyMe = false;
                                                }

                                                setData();

                                            } else {
                                                //AppUtil.showErrorDialog(mContext, errorMsg);
                                                isAllowNotifications = true;
                                                isOutForDelivery = true;
                                                isDelivered = true;
                                                isOffers = true;
                                                isNotifyMe = true;
                                            }

                                        } else {
                                            //AppUtil.showErrorDialog(mContext, errorMsg);
                                            isAllowNotifications = true;
                                            isOutForDelivery = true;
                                            isDelivered = true;
                                            isOffers = true;
                                            isNotifyMe = true;
                                        }

                                    } else {
                                        //AppUtil.showErrorDialog(mContext, getString(R.string.some_error_occoured));
                                        isAllowNotifications = true;
                                        isOutForDelivery = true;
                                        isDelivered = true;
                                        isOffers = true;
                                        isNotifyMe = true;
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    isAllowNotifications = true;
                                    isOutForDelivery = true;
                                    isDelivered = true;
                                    isOffers = true;
                                    isNotifyMe = true;
                                    //AppUtil.showErrorDialog(mContext, response.message());
                                }
                            } else {
                                //AppUtil.showErrorDialog(mContext, response.message());
                                isUpdate = true;
                                isAllowNotifications = true;
                                isOutForDelivery = true;
                                isDelivered = true;
                                isOffers = true;
                                isNotifyMe = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //loader_root.setStatuText(getString(R.string.no_product_found));
                            //loader_root.showStatusText();
                            loader.showContent();
                            isAllowNotifications = true;
                            isOutForDelivery = true;
                            isDelivered = true;
                            isOffers = true;
                            isNotifyMe = true;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //Snackbar.make(term_condtn_checkBox, getResources().getString(R.string.error_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.try_again), networkCallBack1).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setData() {

        btn_out_for_delivery.setOnCheckedChangeListener(null);
        btn_delivered.setOnCheckedChangeListener(null);
        btn_offers.setOnCheckedChangeListener(null);
        btn_notify_me.setOnCheckedChangeListener(null);
        btn_allow_notifications.setOnCheckedChangeListener(null);

        btn_allow_notifications.setChecked(isAllowNotifications);
        btn_out_for_delivery.setChecked(isAllowNotifications ? isOutForDelivery : isAllowNotifications);
        btn_delivered.setChecked(isAllowNotifications ? isDelivered : isAllowNotifications);
        btn_offers.setChecked(isAllowNotifications ? isOffers : isAllowNotifications);
        btn_notify_me.setChecked(isAllowNotifications ? isNotifyMe : isAllowNotifications);

        btn_out_for_delivery.setOnCheckedChangeListener(onChangeListener);
        btn_delivered.setOnCheckedChangeListener(onChangeListener);
        btn_offers.setOnCheckedChangeListener(onChangeListener);
        btn_notify_me.setOnCheckedChangeListener(onChangeListener);
        btn_allow_notifications.setOnCheckedChangeListener(onAllowNotificationChangeListener);

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

    private void initView() {

        loader = findViewById(R.id.notifications_lyt_loader);
        btn_allow_notifications = findViewById(R.id.notifications_btn_allow_notifications);
        btn_out_for_delivery = findViewById(R.id.notifications_btn_out_for_delivery);
        btn_delivered = findViewById(R.id.notifications_btn_delivered);
        btn_offers = findViewById(R.id.notifications_btn_offers);
        btn_notify_me = findViewById(R.id.notifications_btn_notify_me);

        /*btn_allow_notifications.setOnCheckedChangeListener();

        btn_out_for_delivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    isAllowNotifications = true;
                    isOutForDelivery = true;
                }else{
                    isOutForDelivery = false;
                }

                if(isUpdate)
                    loadUserPrefrences();
            }
        });

        btn_delivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    isAllowNotifications = true;
                    isDelivered = true;
                }else{
                    isDelivered = false;
                }

                if(isUpdate)
                    loadUserPrefrences();
            }
        });

        btn_offers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    isAllowNotifications = true;
                    isOffers = true;
                }else{
                    isOffers = false;
                }

                if(isUpdate)
                    loadUserPrefrences();
            }
        });

        btn_notify_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    isAllowNotifications = true;
                    isNotifyMe = true;
                }else{
                    isNotifyMe = false;
                }

                if(isUpdate)
                    loadUserPrefrences();
            }
        });*/


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
