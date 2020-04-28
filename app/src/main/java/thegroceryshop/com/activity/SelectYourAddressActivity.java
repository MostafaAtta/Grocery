package thegroceryshop.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.ShippingAddressAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/*
 * Created by mohitd on 28-Feb-17.
 */

public class SelectYourAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout addNewAddress;
    private TextView textViewAddNewAddress;
    private TextView textViewJustForThisOrder;
    private ImageView imageViewNext;
    private Activity mActivity;
    private RecyclerView addressRecyclerView;
    private List<ShippingAddress> listAddress = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_your_address);
        mActivity = this;
        initView();
        setOnClickListener();
    }

    private void initView() {
        addNewAddress = findViewById(R.id.addNewAddress);
        textViewAddNewAddress = findViewById(R.id.textViewAddNewAddress);
        textViewJustForThisOrder = findViewById(R.id.textViewJustForThisOrder);
        imageViewNext = findViewById(R.id.imageViewNext);
        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        getShippingAddressWebService();
    }

    private void setOnClickListener() {
        imageViewNext.setOnClickListener(this);
        textViewAddNewAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewNext:
                /*Intent intentJustForThisAddress;
                intentJustForThisAddress = new Intent(this, JustForThisAddressActivity.class);
                intentJustForThisAddress.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentJustForThisAddress);*/
                break;

            case R.id.textViewAddNewAddress:
                Intent intentAddNewAddress;
                intentAddNewAddress = new Intent(this, AddNewAddressActivity.class);
                startActivity(intentAddNewAddress);
                break;

        }
    }

    private void getShippingAddressWebService() {
        if (NetworkUtil.networkStatus(mActivity)) {
            AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("user_id", "1");

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.getShippingAddress((new ConvertJsonToMap().jsonToMap(request_data)));

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
                                    JSONArray dataArray = resObject.getJSONArray("data");
                                    listAddress.clear();
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        ShippingAddress shippingAddress = new ShippingAddress();
                                        shippingAddress.setShip_id(dataObject.getString("ship_id"));
                                        shippingAddress.setUser_id(dataObject.getString("user_id"));
                                        shippingAddress.setLang_id(dataObject.getString("lang_id"));
                                        shippingAddress.setFloor_number(dataObject.getString("floor_number"));
                                        shippingAddress.setUnit_number(dataObject.getString("unit_number"));
                                        shippingAddress.setBuilding_name(dataObject.getString("building_name"));
                                        shippingAddress.setStreetName(dataObject.getString("street"));
                                        shippingAddress.setArea_id(dataObject.getString("area_id"));
                                        shippingAddress.setCity_id(dataObject.getString("city_id"));
                                        shippingAddress.setCountry_id(dataObject.getString("country_id"));
                                        shippingAddress.setAddress_instruction(dataObject.getString("address_insturuction"));
                                        shippingAddress.setFirst_name(dataObject.getString("first_name"));
                                        shippingAddress.setLast_name(dataObject.getString("last_name"));
                                        shippingAddress.setAddress_name(dataObject.getString("address_reference"));
                                        shippingAddress.setLatitude(dataObject.optDouble("latitude", 0.0f));
                                        shippingAddress.setLongitude(dataObject.optDouble("longitude", 0.0f));
                                        shippingAddress.setRegionId(dataObject.optString("warehouse_id"));
                                        shippingAddress.setRegionName(dataObject.optString("warehouse_name"));
                                        listAddress.add(shippingAddress);
                                    }

                                    addressRecyclerView.setAdapter(new ShippingAddressAdapter(SelectYourAddressActivity.this, listAddress));
                                    addressRecyclerView.setLayoutManager(new LinearLayoutManager(SelectYourAddressActivity.this));
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
