package thegroceryshop.com.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.NotificationListAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.NotificationItem;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/*
 * Created by umeshk on 23-Mar-17.
 */

public class NotificationListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txt_title;

    private LoaderLayout loader;
    private RecyclerView recyl_notifications;

    private ArrayList<NotificationItem> list_notifications = new ArrayList<>();
    private ApiInterface apiService;
    private NotificationListAdapter notificationListAdapter;

    DateTimeFormatter serverFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
    DateTimeFormatter displayFormatter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification_list);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            displayFormatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm a").withLocale(new Locale("en"));
        }else{
            displayFormatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm a").withLocale(new Locale("ar"));
        }

        apiService = ApiClient.createService(ApiInterface.class, NotificationListActivity.this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                toolbar.setNavigationIcon(R.mipmap.top_back);
            }else{
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            txt_title.setText(getResources().getString(R.string.notifications));
        }

        initView();
    }

    private void initView() {
        loader = findViewById(R.id.notification_list_loader);
        recyl_notifications = findViewById(R.id.notification_list_recyl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyl_notifications.setLayoutManager(linearLayoutManager);
        loader.setStatuText(getString(R.string.no_notification_available_for_you));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationsList();
    }

    private void loadNotificationsList() {

        if (NetworkUtil.networkStatus(NotificationListActivity.this)) {
            try {

                loader.showProgress();
                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

                Call<ResponseBody> call = apiService.getNotificationsList(new ConvertJsonToMap().jsonToMap(request_data));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loader.showContent();
                        if (response.code() == 200) {
                            try {

                                list_notifications.clear();

                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                if (resObject.getString("status_code").equalsIgnoreCase("200")) {

                                    JSONArray dataArray = resObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {

                                        JSONObject dataObject = dataArray.getJSONObject(i);

                                        NotificationItem notificationItem = new NotificationItem();
                                        notificationItem.setNotificationId(dataObject.getString("id"));
                                        notificationItem.setMessage(dataObject.getString("message"));

                                        DateTime dateTime = serverFormatter.parseDateTime(dataObject.getString("date_time"));

                                        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")){
                                            String time = displayFormatter.print(dateTime);
                                            if(time != null){
                                                if(time.contains("ص")){
                                                    time = time.replace("ص", getString(R.string.am));
                                                }else{
                                                    time = time.replace("م", getString(R.string.pm));
                                                }
                                                notificationItem.setDatetime(time);
                                            }
                                        }else{
                                            notificationItem.setDatetime(displayFormatter.print(dateTime));
                                        }

                                        list_notifications.add(notificationItem);

                                    }

                                    if (list_notifications.size() == 0) {
                                        loader.showStatusText();
                                    } else {

                                        notificationListAdapter = new NotificationListAdapter(NotificationListActivity.this, list_notifications);
                                        recyl_notifications.setAdapter(notificationListAdapter);
                                        notificationListAdapter.setOnNotificationRemoveListener(new NotificationListAdapter.OnNotificationRemoveListener() {
                                            @Override
                                            public void onNotificationRemove(int position) {
                                                if (list_notifications != null && list_notifications.size() > position) {
                                                    removeNotification(list_notifications.get(position).getNotificationId(), position);
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    loader.showStatusText();
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                loader.showStatusText();
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

    private void removeNotification(String notificationId, final int position) {

        try {
            JSONObject login_request_data = new JSONObject();
            login_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            login_request_data.put("notification_id", notificationId);

            if (NetworkUtil.networkStatus(this)) {
                try {
                    AppUtil.showProgress(NotificationListActivity.this);
                    Call<ResponseBody> call = apiService.removeNotification((new ConvertJsonToMap().jsonToMap(login_request_data)));
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

                                            if (position < list_notifications.size()) {
                                                list_notifications.remove(position);
                                                if (notificationListAdapter != null) {
                                                    notificationListAdapter.notifyDataSetChanged();
                                                }

                                                if (list_notifications.size() == 0) {
                                                    loader.showStatusText();
                                                }
                                            }

                                        } else {
                                            AppUtil.showErrorDialog(NotificationListActivity.this, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(NotificationListActivity.this, getString(R.string.error_msg) + "(Err-689)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(NotificationListActivity.this, getString(R.string.error_msg) + "(Err-690)");
                                }
                            } else {
                                AppUtil.showErrorDialog(NotificationListActivity.this, getString(R.string.error_msg) + "(Err-691)");
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
                break;
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
