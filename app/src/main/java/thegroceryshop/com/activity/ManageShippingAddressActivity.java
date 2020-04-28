package thegroceryshop.com.activity;

/*
 * Created by umeshk on 4/12/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import thegroceryshop.com.adapter.ManageAddressAdapter;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class ManageShippingAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerViewManageAddress;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private List<ShippingAddress> list_address = new ArrayList<>();
    private ManageAddressAdapter manageAddressAdapter;
    private Activity mActivity;
    private String user_id = "";
    private LinearLayout lyt_add_new_address;
    private LoaderLayout loader;

    private Toolbar toolbar;
    private TextView txt_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_shipping_address);
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

            txt_title.setText(getResources().getString(R.string.manage_shipping_address).toUpperCase());
        }

        loader = findViewById(R.id.manage_address_loader);
        //loader.setStatuText(getString(R.string.no_shipping_address_added_yet));
        loader.setStatuText(AppConstants.BLANK_STRING);
        lyt_add_new_address = findViewById(R.id.addNewAddress);
        recyclerViewManageAddress = findViewById(R.id.manageAddressRecyclerView);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewManageAddress.setLayoutManager(recyclerViewLayoutManager);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShippingAddressWebService();
    }

    private void initView() {
        user_id = ApiLocalStore.getInstance(mActivity).getUserId();
        lyt_add_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageShippingAddressActivity.this, AddNewAddressActivity.class));
            }
        });
    }

    private void getShippingAddressWebService() {
        if (NetworkUtil.networkStatus(mActivity)) {
            try {

                loader.showProgress();
                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("user_id", user_id);

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.getShippingAddress((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loader.showContent();
                        if (response.code() == 200) {
                            try {
                                list_address.clear();
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                if (resObject.getString("status_code").equalsIgnoreCase("200")) {

                                    JSONArray dataArray = resObject.getJSONArray("data");
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

                                        shippingAddress.setArea(dataObject.getString("area"));
                                        shippingAddress.setCity(dataObject.getString("city"));
                                        shippingAddress.setCountry(dataObject.getString("country"));

                                        shippingAddress.setAddress_instruction(dataObject.getString("address_insturuction"));
                                        shippingAddress.setFirst_name(dataObject.getString("first_name"));
                                        shippingAddress.setLast_name(dataObject.getString("last_name"));
                                        shippingAddress.setInputFrom(dataObject.getString("input_from"));
                                        shippingAddress.setAddress_name(dataObject.getString("address_reference"));
                                        shippingAddress.setLatitude(dataObject.optDouble("latitude", 0.0f));
                                        shippingAddress.setLongitude(dataObject.optDouble("longitude", 0.0f));
                                        shippingAddress.setRegionId(dataObject.optString("warehouse_id"));
                                        shippingAddress.setRegionName(dataObject.optString("warehouse_name"));
                                        shippingAddress.setEnable(
                                                shippingAddress.getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId()));

                                        list_address.add(shippingAddress);

                                    }


                                    if(list_address.size() == 0){
                                        loader.showStatusText();
                                    }else{
                                        manageAddressAdapter = new ManageAddressAdapter(mActivity, list_address);
                                        recyclerViewManageAddress.setAdapter(manageAddressAdapter);
                                        manageAddressAdapter.setOnAddressSelectListener(new ManageAddressAdapter.OnAddressSelectListener() {
                                            @Override
                                            public void onAddressSelectListener(final int position, String type) {
                                                if (type.equalsIgnoreCase("edit")) {

                                                    Intent intentAddNewAddress = new Intent(mActivity, AddNewAddressActivity.class);

                                                    if(!ValidationUtil.isNullOrBlank(list_address.get(position).getInputFrom())){
                                                        if(!list_address.get(position).getInputFrom().equalsIgnoreCase("map")){
                                                            intentAddNewAddress.putExtra("input_from", "form");
                                                        }else{
                                                            intentAddNewAddress.putExtra("input_from", "map");
                                                        }
                                                    }else{
                                                        intentAddNewAddress.putExtra("input_from", "form");
                                                    }

                                                    intentAddNewAddress.putExtra("shipping_address", list_address.get(position));
                                                    startActivityForResult(intentAddNewAddress, 300);

                                                } else if (type.equalsIgnoreCase("delete"))
                                                {
                                                    final AppDialogDoubleAction loginAlertDialog = new AppDialogDoubleAction(
                                                            ManageShippingAddressActivity.this,
                                                            getString(R.string.app_name),
                                                            getString(R.string.address_delete_msg),
                                                            getString(R.string.no),
                                                            getString(R.string.yes));

                                                    loginAlertDialog.show();
                                                    loginAlertDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                        @Override
                                                        public void onLeftActionClick(View view) {
                                                            loginAlertDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onRightActionClick(View view) {
                                                            loginAlertDialog.dismiss();
                                                            deleteAddressWebService(position);
                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    }
                                } else if (resObject.getString("status_code").equalsIgnoreCase("202")) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 300 && resultCode == RESULT_OK) {
            if(data.getBooleanExtra("isAddressAdded", false)) {
                getShippingAddressWebService();
            }
        }
    }

    private void deleteAddressWebService(final int position) {
        if (NetworkUtil.networkStatus(mActivity)) {
            AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("shipping_address_id", list_address.get(position).getShip_id());
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.deleteShippingAddress((new ConvertJsonToMap().jsonToMap(request_data)));

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
                                    AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, resObject.getString("success_message"), mActivity.getString(R.string.ok), new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            list_address.remove(position);
                                            manageAddressAdapter.notifyDataSetChanged();
                                            if (list_address.size() == 0) {
                                                finish();
                                            }
                                        }
                                    });
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
