package thegroceryshop.com.activity;

/*
 * Created by umeshk on 16-03-2018.
 */

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.MySavingAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.Pinview;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.MySavingBean;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class MySavingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTitle;
    private LoaderLayout loaderLayout;
    private Context context;
    private String list_id;
    private TextView txt_update;
    private LoaderLayout myAccountLoader;
    private Pinview myAccountPinview;
    private TextView textViewUnit;
    private RecyclerView recyclerViewSaving;
    private LoaderLayout loader_saving;
    private ArrayList<MySavingBean> mySavingBeanArrayList = new ArrayList<>();

    private MySavingAdapter mySavedListAdapter;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_saving);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById(R.id.txt_title);
        loaderLayout = findViewById(R.id.my_saving_loader);
        recyclerViewSaving = findViewById(R.id.recyclerWishListDetail);
        txt_update = findViewById(R.id.txt_update);
        loader_saving = findViewById(R.id.my_saving_loader);

        myAccountLoader = findViewById(R.id.loaderLayout);
        myAccountPinview = findViewById(R.id.my_account_pinview);
        textViewUnit = findViewById(R.id.textViewUnit);
        recyclerViewSaving = findViewById(R.id.recyclerViewSaving);

        context = this;
        loaderLayout.showContent();
        loaderLayout.setHideContentOnLoad(true);
        myAccountLoader.setHideContentOnLoad(true);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }
            txtTitle.setText(getString(R.string.savings).toUpperCase());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewSaving.setLayoutManager(linearLayoutManager);
        recyclerViewSaving.setHasFixedSize(true);
        myAccountPinview.setSplitWidth(10);
        myAccountPinview.setEnabled(false);
        loadSaving();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSaving() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                loaderLayout.showProgress();
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                Call<ResponseBody> call = apiService.getMySaving((new ConvertJsonToMap().jsonToMap(request)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        loaderLayout.showContent();
                        if (response.code() == 200) {
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                JSONObject resObj = obj.optJSONObject("response");
                                if (resObj.length() > 0) {

                                    int statusCode = resObj.optInt("status_code", 0);
                                    String errorMsg = resObj.optString("error_message");
                                    if (statusCode == 200) {
                                        mySavingBeanArrayList.clear();
                                        //String responseMessage = resObj.optString("success_message");
                                        JSONObject jsonObject = resObj.optJSONObject("data");

                                        String totalSaving = jsonObject.optString("total_savings", "0");
                                        if (totalSaving != null) {
                                            //myAccountPinview.setValue(totalSaving);
                                            //myAccountPinview.setPinLength(totalSaving.length());
                                            if (totalSaving.contains(".")) {
                                                String value[] = totalSaving.trim().split("\\.");
                                                myAccountPinview.setPinLength(value[0].length());
                                                myAccountPinview.setValue(value[0]);
                                            } else {
                                                myAccountPinview.setPinLength(totalSaving.length());
                                                myAccountPinview.setValue(totalSaving);
                                            }
                                        }

                                        if (jsonObject.optJSONArray("list_savings") != null) {
                                            JSONArray listSavingArray = jsonObject.optJSONArray("list_savings");
                                            for (int i = 0; i < listSavingArray.length(); i++) {
                                                JSONObject listSavingObject = listSavingArray.optJSONObject(i);
                                                MySavingBean mySavingBean = new MySavingBean();
                                                mySavingBean.setDate(listSavingObject.optString("date"));
                                                mySavingBean.setStartTime(listSavingObject.optString("start_time"));
                                                mySavingBean.setEndTime(listSavingObject.optString("end_time"));
                                                mySavingBean.setOrderId(listSavingObject.optString("order_id"));
                                                mySavingBean.setSaving(listSavingObject.optString("saving"));
                                                mySavingBeanArrayList.add(mySavingBean);
                                            }

                                            if(mySavingBeanArrayList != null && mySavingBeanArrayList.size()>0){
                                                mySavedListAdapter = new MySavingAdapter(context, mySavingBeanArrayList);
                                                recyclerViewSaving.setAdapter(mySavedListAdapter);
                                                loaderLayout.showContent();
                                                loader_saving.showContent();
                                            }else{
                                                myAccountLoader.setStatuText(getString(R.string.no_saving_text));
                                                myAccountLoader.showStatusText();
                                            }

                                        }
                                    } else {
                                        loaderLayout.showStatusText();
                                        loader_saving.showContent();
                                    }

                                } else {
                                    loaderLayout.showStatusText();
                                    myAccountLoader.showContent();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                myAccountPinview.setPinLength("0".length());
                                myAccountPinview.setValue("0");
                                myAccountLoader.showContent();
                                loaderLayout.showContent();
                            }
                        } else {
                            myAccountPinview.setPinLength("0".length());
                            myAccountPinview.setValue("0");
                            myAccountLoader.showContent();
                            loaderLayout.showStatusText();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        myAccountPinview.setPinLength("0".length());
                        myAccountPinview.setValue("0");
                        myAccountLoader.showContent();
                        loaderLayout.showStatusText();
                    }
                });

            } catch (JSONException e) {
                myAccountPinview.setPinLength("0".length());
                myAccountPinview.setValue("0");
                e.printStackTrace();
            }
        } else {
            myAccountPinview.setPinLength("0".length());
            myAccountPinview.setValue("0");
            AppUtil.requestLogin(context);
        }
    }
}
