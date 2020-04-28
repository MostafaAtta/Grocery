package thegroceryshop.com.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.AddNewAddressActivity;
import thegroceryshop.com.adapter.AddressNamesAdapter;
import thegroceryshop.com.adapter.CountryListAdapter;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.Item;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.R.id.editTextSplIns;

/*
 * Created by umeshk on 2/23/2017.
 */
public class Manually_Address_Fragment extends Fragment {

    AppCompatActivity appCompatActivity;
    private AppCompatSpinner spinnerCountry;
    private String addressName;
    private String floor;
    private String unit;
    private String firstName;
    private String lastName;
    private String address;
    private String buildingName;
    private String street;

    private AppCompatSpinner spinnerCity;
    private AppCompatSpinner spinnerArea;
    private Activity mActivity;
    private ArrayList<Item> countryList = new ArrayList<>();
    private ArrayList<Item> cityList = new ArrayList<>();
    private ArrayList<Item> addressNamesList = new ArrayList<>();
    private String country_id = "0";
    private String city_id = "0";
    private String area_id = "0";

    //public AppCompatEditText edtTextPostalCode;
    public AppCompatSpinner spinnerAddressName;
    public AppCompatEditText edtTextFloor;
    public AppCompatEditText edtTextUnit;
    public AppCompatEditText edtTextBuildingName;
    public AppCompatEditText edtTextAddress;
    public AppCompatEditText edtTextFirstName;
    public AppCompatEditText edtTextLastName;
    public AppCompatEditText edtTextStreet;
    public TextView txtViewAddressSpecific;
    public TextView txtViewBuildingName;
    private String user_id = "";
    private ShippingAddress shippingAddress;
    private CountryListAdapter countryListAdapter;
    private CountryListAdapter cityListAdapter;
    private CountryListAdapter areaListAdapter;
    private AddressNamesAdapter addressNamesAdapter;

    private int country_id_position = 0;
    private int city_id_position = 0;
    private int area_id_position = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manually_address, container, false);
        appCompatActivity = (AppCompatActivity) getActivity();

        if(getArguments() != null){
            shippingAddress = getArguments().getParcelable("address");
        }

        initView(view);
        return view;
    }


    private void initView(View view) {

        mActivity = getActivity();

        //edtTextPostalCode = view.findViewById(R.id.edtTextPostalCode);
        spinnerAddressName = view.findViewById(R.id.spinnerAddressName);
        edtTextFloor = view.findViewById(R.id.edtTextFloor);
        edtTextUnit = view.findViewById(R.id.edtTextUnit);
        edtTextBuildingName = view.findViewById(R.id.edtTextBuildingName);
        edtTextAddress = view.findViewById(R.id.edtTextAddress);
        edtTextFirstName = view.findViewById(R.id.edtTextFirstName);
        edtTextLastName = view.findViewById(R.id.edtTextLastName);
        txtViewAddressSpecific = view.findViewById(R.id.txtViewAddressSpecific);
        txtViewBuildingName = view.findViewById(R.id.txtViewBuildingName);
        edtTextStreet = view.findViewById(R.id.edtTextStreet);

        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        spinnerCity = view.findViewById(R.id.spinnerCity);
        spinnerArea = view.findViewById(R.id.spinnerArea);

        addressNamesList.add(new Item("0", getString(R.string.select_address_name), false));
        addressNamesList.add(new Item("1", getString(R.string.home), false));
        addressNamesList.add(new Item("2", getString(R.string.work), false));
        addressNamesList.add(new Item("3", getString(R.string.other), false));

        addressNamesAdapter = new AddressNamesAdapter(getActivity(), addressNamesList);
        spinnerAddressName.setAdapter(addressNamesAdapter);

        SpannableString ss1 = new SpannableString(txtViewAddressSpecific.getText().toString());
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            ss1.setSpan(new RelativeSizeSpan(.8f), 21, 31, 0); // set size
        }else{
            ss1.setSpan(new RelativeSizeSpan(.8f), 25, 34, 0);
        }
        txtViewAddressSpecific.setText(ss1);

        SpannableString ss2 = new SpannableString(txtViewBuildingName.getText().toString());
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            ss2.setSpan(new RelativeSizeSpan(.8f), 21, 32, 0); // set size
        }else{
            ss2.setSpan(new RelativeSizeSpan(.8f), 24, ss2.length()-1, 0); // set size
        }

        txtViewBuildingName.setText(ss2);

        ((AddNewAddressActivity)getActivity()).setAddEnable(true);

        edtTextAddress.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == editTextSplIns) {
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

        user_id = OnlineMartApplication.mLocalStore.getUserId();

        if (shippingAddress != null) {
            edtTextFirstName.setText(shippingAddress.getFirst_name());
            edtTextLastName.setText(shippingAddress.getLast_name());
            edtTextFloor.setText(shippingAddress.getFloor_number());
            edtTextUnit.setText(shippingAddress.getUnit_number());
            edtTextBuildingName.setText(shippingAddress.getBuilding_name());

            for(int i=0; i<addressNamesList.size(); i++){
                if(addressNamesList.get(i).getItemLabel().equalsIgnoreCase(shippingAddress.getAddress_name())){
                    if(addressNamesAdapter != null){
                        spinnerAddressName.setSelection(i);
                        break;
                    }
                }
            }

            //edtTextPostalCode.setText(shippingAddress.getAddress_name());
            edtTextAddress.setText(shippingAddress.getAddress_instruction());
            if(ValidationUtil.isNullOrBlank(shippingAddress.getStreetName())){
                edtTextStreet.setText(AppConstants.BLANK_STRING);
            }else{
                edtTextStreet.setText(shippingAddress.getStreetName());
            }
            country_id = shippingAddress.getCountry_id();
            city_id = shippingAddress.getCity_id();
            area_id = shippingAddress.getArea_id();

        } else {
            edtTextFirstName.setText(ApiLocalStore.getInstance(mActivity).getFirstName());
            edtTextLastName.setText(ApiLocalStore.getInstance(mActivity).getLastName());
        }

        edtTextAddress.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == editTextSplIns) {
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

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedItem = spinnerCountry.getSelectedItemPosition();
                if (selectedItem > 0) {
                    country_id = countryList.get(position).getItemId();
                    mCallAreaWebService(country_id, city_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedItem = spinnerCity.getSelectedItemPosition();
                if (selectedItem > 0) {
                    city_id = cityList.get(position).getItemId();
                    mCallAreaWebService(country_id, city_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedItem = spinnerArea.getSelectedItemPosition();
                if (selectedItem > 0) {
                    area_id = AddNewAddressActivity.areaList.get(position).getItemId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCallCountryWebService(false);
        mCallCountryWebService(true);
        AppUtil.hideSoftKeyboard(mActivity, mActivity);

    }

    public void addUserAddress() {

        if(spinnerAddressName != null && spinnerAddressName.getSelectedItem() != null && spinnerAddressName.getSelectedItemPosition() != 0 && ((Item)spinnerAddressName.getSelectedItem()).getItemLabel() != null){
            addressName = ((Item)spinnerAddressName.getSelectedItem()).getItemLabel();
        }else{
            addressName = AppConstants.BLANK_STRING;
        }

        /*if(edtTextPostalCode != null && edtTextPostalCode.getText() != null && edtTextPostalCode.getText().toString() != null){
            addressName = edtTextPostalCode.getText().toString();
        }else{
            addressName = AppConstants.BLANK_STRING;
        }*/

        if(edtTextFloor != null && edtTextFloor.getText() != null && edtTextFloor.getText().toString() != null){
            floor = edtTextFloor.getText().toString();
        }else{
            floor = AppConstants.BLANK_STRING;
        }

        if(edtTextUnit != null && edtTextUnit.getText() != null && edtTextUnit.getText().toString() != null){
            unit = edtTextUnit.getText().toString();
        }else{
            unit = AppConstants.BLANK_STRING;
        }

        if(edtTextBuildingName != null && edtTextBuildingName.getText() != null && edtTextBuildingName.getText().toString() != null){
            buildingName = edtTextBuildingName.getText().toString();
        }else{
            buildingName = AppConstants.BLANK_STRING;
        }

        if(edtTextAddress != null && edtTextAddress.getText() != null && edtTextAddress.getText().toString() != null){
            address = edtTextAddress.getText().toString();
        }else{
            address = AppConstants.BLANK_STRING;
        }

        if(edtTextStreet != null && edtTextStreet.getText() != null && edtTextStreet.getText().toString() != null){
            street = edtTextStreet.getText().toString();
        }else{
            street = AppConstants.BLANK_STRING;
        }

        if(edtTextFirstName != null && edtTextFirstName.getText() != null && edtTextFirstName.getText().toString() != null){
            firstName = edtTextFirstName.getText().toString();
        }else{
            firstName = AppConstants.BLANK_STRING;
        }

        if(edtTextLastName != null && edtTextLastName.getText() != null && edtTextLastName.getText().toString() != null){
            lastName = edtTextLastName.getText().toString();
        }else{
            lastName = AppConstants.BLANK_STRING;
        }

        if (addressName.isEmpty()) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_address_name), mActivity.getString(R.string.ok), false);
        } else if (floor.isEmpty()) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_floor_number), mActivity.getString(R.string.ok), false);
        } else if (unit.isEmpty()) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_apt_number), mActivity.getString(R.string.ok), false);
        } else if (street.isEmpty()) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_street_name), mActivity.getString(R.string.ok), false);
        } else if (area_id.equalsIgnoreCase("0")) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_area), mActivity.getString(R.string.ok), false);
        } else if (city_id.equalsIgnoreCase("0")) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_city), mActivity.getString(R.string.ok), false);
        } else if (country_id.equalsIgnoreCase("0")) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.pls_enter_country), mActivity.getString(R.string.ok), false);
        } else if (firstName.isEmpty()) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.error_blank_first_name_of_user), mActivity.getString(R.string.ok), false);
        } else if (lastName.isEmpty()) {
            AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, mActivity.getResources().getString(R.string.error_blank_last_name_of_user), mActivity.getString(R.string.ok), false);
        } else {
            addUpdateUserAddressWebService();
        }
    }

    private void mCallCountryWebService(final boolean isCountry) {
        if (NetworkUtil.networkStatus(mActivity)) {
            //AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call;
                if (isCountry) {
                    call = apiService.getCountryList((new ConvertJsonToMap().jsonToMap(request_data)));
                } else {
                    call = apiService.getCityList((new ConvertJsonToMap().jsonToMap(request_data)));
                }
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
                                    if (isCountry)
                                    {
                                        countryList.clear();
                                        country_id_position = 0;
                                        countryList.add(new Item("0", mActivity.getString(R.string.select_country), false));
                                    } else
                                    {
                                        cityList.clear();
                                        city_id_position = 0;
                                        cityList.add(new Item("0", mActivity.getString(R.string.select_city), false));
                                    }
                                    JSONArray dataArray = resObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        if (isCountry) {
                                            countryList.add(new Item(dataObject.getString("id"), dataObject.getString("name"), false));
                                        } else {
                                            cityList.add(new Item(dataObject.getString("city_id"), dataObject.getString("city_name"), false));
                                        }
                                    }
                                }
                                if (isCountry) {
                                    countryListAdapter = new CountryListAdapter(mActivity, countryList);
                                    spinnerCountry.setAdapter(countryListAdapter);

                                    for (int mIndex = 0; mIndex < countryList.size(); mIndex++)
                                    {
                                        if (country_id.equalsIgnoreCase("0"))
                                        {
                                            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                                                if ("Egypt".equalsIgnoreCase(countryList.get(mIndex).getItemLabel())) {
                                                    country_id_position = mIndex;
                                                }
                                            }else{
                                                if ("مصر".equalsIgnoreCase(countryList.get(mIndex).getItemLabel())) {
                                                    country_id_position = mIndex;
                                                }
                                            }
                                        }else
                                        {
                                            if (country_id.equalsIgnoreCase(countryList.get(mIndex).getItemId())) {
                                                country_id_position = mIndex;
                                            }
                                        }


                                    }

                                    spinnerCountry.setSelection(country_id_position);
                                    spinnerCountry.setEnabled(false);
                                } else {
                                    cityListAdapter = new CountryListAdapter(mActivity, cityList);
                                    spinnerCity.setAdapter(cityListAdapter);
                                    for (int mIndex = 0; mIndex < cityList.size(); mIndex++)
                                    {
                                        if (city_id.equalsIgnoreCase("0")) {
                                            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                                                if ("Cairo".equalsIgnoreCase(cityList.get(mIndex).getItemLabel())) {
                                                    city_id_position = mIndex;
                                                }
                                            }else{
                                                if ("القاهرة".equalsIgnoreCase(cityList.get(mIndex).getItemLabel())) {
                                                    city_id_position = mIndex;
                                                }
                                            }
                                        }else {
                                            if (city_id.equalsIgnoreCase(cityList.get(mIndex).getItemId())) {
                                                city_id_position = mIndex;
                                            }
                                        }
                                    }

                                    spinnerCity.setSelection(city_id_position);
                                    spinnerCity.setEnabled(false);
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

    private void mCallAreaWebService(String country_id, String city_id) {
        if (NetworkUtil.networkStatus(mActivity)) {
            //AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("city_id", "2");
                request_data.put("country_id", country_id);

                if(shippingAddress != null && shippingAddress.getRegionId() != null){
                    request_data.put("warehouse_id", shippingAddress.getRegionId());
                }else{
                    request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());
                }

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.getAreaList((new ConvertJsonToMap().jsonToMap(request_data)));

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
                                    area_id_position = 0;
                                    AddNewAddressActivity.areaList.clear();
                                    AddNewAddressActivity.areaList.add(new Item("0", getString(R.string.select_area), false));
                                    JSONArray dataArray = resObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        if (area_id.equalsIgnoreCase(dataObject.getString("area_id"))) {
                                            area_id_position = i + 1;
                                        }
                                        AddNewAddressActivity.areaList.add(new Item(dataObject.getString("area_id"), dataObject.getString("area_name"), false));
                                    }
                                }
                                areaListAdapter = new CountryListAdapter(mActivity, AddNewAddressActivity.areaList);
                                spinnerArea.setAdapter(areaListAdapter);
                                if (shippingAddress != null) {
                                    spinnerArea.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            spinnerArea.setSelection(area_id_position);
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

    private void addUpdateUserAddressWebService() {
        if (NetworkUtil.networkStatus(mActivity)) {
            AppUtil.showProgress(mActivity);
            try {
                JSONObject request_data = new JSONObject();
                if (shippingAddress != null) {
                    request_data.put("shipping_address_id", shippingAddress.getShip_id());
                }
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("user_id", user_id);
                request_data.put("floor_number", floor);
                request_data.put("unit_number", unit);
                request_data.put("building_name", buildingName);
                request_data.put("street", street);
                request_data.put("area_id", area_id);
                request_data.put("city_id", city_id);
                request_data.put("country_id", country_id);
                request_data.put("address_insturuction", address);
                request_data.put("first_name", firstName);
                request_data.put("last_name", lastName);
                request_data.put("address_reference", addressName);
                request_data.put("latitude", "" + ((AddNewAddressActivity) mActivity).currentLatitude);
                request_data.put("longitude", "" + ((AddNewAddressActivity) mActivity).currentLongitude);
                request_data.put("input_from", "form");

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call;
                if (shippingAddress != null) {
                    request_data.put("warehouse_id", shippingAddress.getRegionId());
                    call = apiService.editShippingAddress((new ConvertJsonToMap().jsonToMap(request_data)));
                } else {
                    request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());
                    call = apiService.addUserAddress((new ConvertJsonToMap().jsonToMap(request_data)));
                }

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");

                                int statusCode = resObject.optInt("status_code", 0);
                                String errorMsg = resObject.optString("error_message");

                                if (statusCode == 200) {
                                    AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, resObject.getString("success_message"), mActivity.getString(R.string.ok), new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            ((AddNewAddressActivity) mActivity).setResult();
                                        }
                                    });
                                } else if(statusCode == 305){

                                    OnlineMartApplication.openRegionPicker(getActivity(), errorMsg);

                                } else{
                                    AppUtil.displaySingleActionAlert(mActivity, AppConstants.ERROR_TAG, resObject.getString("error_message"), mActivity.getString(R.string.ok), new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
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
    public void onResume() {
        super.onResume();
        appCompatActivity = (AppCompatActivity) getActivity();
    }
}
