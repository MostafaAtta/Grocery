package thegroceryshop.com.checkoutProcess.checkoutfragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
import thegroceryshop.com.activity.AddNewAddressActivity;
import thegroceryshop.com.adapter.ShippingAddressAdapter;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static android.app.Activity.RESULT_OK;
/*
 * Created by umeshk on 4/12/2017.
 */

public class SelectAddressFragment extends Fragment implements View.OnClickListener {

    private AppCompatActivity mActivity;
    private RecyclerView addressRecyclerView;
    private LinearLayout addNewAddress;
    private FrameLayout lyt_just_for_this_order;
    private String user_id = "";
    private List<ShippingAddress> listAddress = new ArrayList<>();
    public ShippingAddress selectedShippingAddress = null;
    private ShippingAddressAdapter shippingAddressAdapter;
    private CheckOutProcessActivity checkOutProcessActivity;
    private LoaderLayout loader;

    public static SelectAddressFragment newInstance(AppCompatActivity appCompatActivity) {
        SelectAddressFragment selectAddressFragment = new SelectAddressFragment();
        return selectAddressFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, false);

        View view = inflater.inflate(R.layout.layout_select_your_address, container, false);
        checkOutProcessActivity = (CheckOutProcessActivity)getActivity();
        mActivity = checkOutProcessActivity;
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = checkOutProcessActivity;
        getShippingAddressWebService();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isAdded()){
            if(listAddress != null){
                listAddress.clear();
                if(shippingAddressAdapter != null){
                    shippingAddressAdapter.notifyDataSetChanged();
                }
            }

            getShippingAddressWebService();
        }
    }

    private void initView(View view) {
        checkOutProcessActivity = (CheckOutProcessActivity) getActivity();
        addressRecyclerView = view.findViewById(R.id.addressRecyclerView);
        addressRecyclerView.setNestedScrollingEnabled(false);
        addNewAddress = view.findViewById(R.id.addNewAddress);
        loader = view.findViewById(R.id.select_address_loader);
        loader.setStatuText(getString(R.string.no_shipping_address_added_yet));
        lyt_just_for_this_order = view.findViewById(R.id.address_lyt_just_for_this_order);
        setOnClickListener();
        user_id = ApiLocalStore.getInstance(mActivity).getUserId();
    }

    private void setOnClickListener() {
        lyt_just_for_this_order.setOnClickListener(this);
        addNewAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.address_lyt_just_for_this_order) {

            if(selectedShippingAddress != null){
                OnlineMartApplication.mLocalStore.saveDefaultShippingAddress(selectedShippingAddress.getShip_id());

                //Added by Naresh
                OnlineMartApplication.mLocalStore.saveDefaultShippingAreaId(selectedShippingAddress.getArea_id());
                ((CheckOutProcessActivity) mActivity).getShippingCharges();


                checkOutProcessActivity.getOrder().setAddress(selectedShippingAddress);
                ((CheckOutProcessActivity) mActivity).viewpager.setCurrentItem(1);
            }else{
                AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(checkOutProcessActivity, AppConstants.ERROR_TAG, getString(R.string.pls_select_a_shipping_address_to_proceed), getString(R.string.ok));
                appDialogSingleAction.show();
                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        appDialogSingleAction.dismiss();
                    }
                });
            }
        } else if (v == addNewAddress) {
            Intent intentAddNewAddress;
            intentAddNewAddress = new Intent(mActivity, AddNewAddressActivity.class);
            intentAddNewAddress.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intentAddNewAddress, 200);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data.getBooleanExtra("isAddressAdded", false)) {
                getShippingAddressWebService();
            }
        }
    }

    private void getShippingAddressWebService() {
        if (NetworkUtil.networkStatus(mActivity)) {
            loader.showProgress();
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.getShippingAddress((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loader.showContent();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");
                                listAddress.clear();
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
                                        shippingAddress.setArea_id(dataObject.getString("area_id"));
                                        shippingAddress.setCity_id(dataObject.getString("city_id"));
                                        shippingAddress.setCountry_id(dataObject.getString("country_id"));
                                        shippingAddress.setStreetName(dataObject.getString("street"));
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

                                        if(checkOutProcessActivity.getOrder().getAddress() != null){
                                            if(checkOutProcessActivity.getOrder().getAddress().getShip_id().equalsIgnoreCase(shippingAddress.getShip_id())){
                                                shippingAddress.setSelected(true);
                                            }
                                        }

                                        listAddress.add(shippingAddress);
                                    }

                                    selectedShippingAddress = null;
                                    if(listAddress != null && listAddress.size()>0){

                                        if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getDefaultShippingAddress())){
                                            for(int i=0; i<listAddress.size(); i++){
                                                if(listAddress.get(i).getShip_id() != null && listAddress.get(i).getShip_id().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getDefaultShippingAddress())){
                                                    if(listAddress.get(i).getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId())){
                                                        listAddress.get(i).setSelected(true);
                                                        selectedShippingAddress = listAddress.get(i);
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if(selectedShippingAddress == null && listAddress.size() == 1){
                                            if(listAddress.get(0).getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId())){
                                                listAddress.get(0).setSelected(true);
                                                selectedShippingAddress = listAddress.get(0);
                                            }
                                        }

                                        shippingAddressAdapter = new ShippingAddressAdapter(mActivity, listAddress);
                                        addressRecyclerView.setAdapter(shippingAddressAdapter);
                                        addressRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

                                        shippingAddressAdapter.setOnAdressClickListener(new ShippingAddressAdapter.OnAdressClickListener() {
                                            @Override
                                            public void onAddressClick(int positon) {
                                                for(int i=0; i<listAddress.size(); i++){
                                                    if(i == positon){
                                                        listAddress.get(i).setSelected(true);
                                                        selectedShippingAddress = listAddress.get(i);
                                                    }else{
                                                        listAddress.get(i).setSelected(false);
                                                    }
                                                }
                                                shippingAddressAdapter.notifyDataSetChanged();
                                            }
                                        });

                                    }else{
                                        if(loader != null && isAdded()){
                                            loader.showStatusText();
                                        }
                                    }

                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                if(loader != null && isAdded()){
                                    loader.showStatusText();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        if(loader != null && isAdded()){
                            loader.showStatusText();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                if(loader != null && isAdded())
                    loader.showStatusText();
            }
        }
    }
}
