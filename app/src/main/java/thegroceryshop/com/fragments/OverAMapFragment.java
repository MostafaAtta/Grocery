package thegroceryshop.com.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.AddNewAddressActivity;
import thegroceryshop.com.adapter.PlacesAutoCompleteAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.dialog.AddressDialog;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.Item;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static android.app.Activity.RESULT_OK;
import static thegroceryshop.com.R.id.map;
import static thegroceryshop.com.webservices.ApiClient.getRequestQueue;

@SuppressLint("ValidFragment")
public class OverAMapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    AppCompatActivity appCompatActivity;
    private GoogleMap googleMap;
    private Activity mActivity;
    private double latitude = 30.119798655098183;
    private double longitude = 31.5370003;
    public AppCompatAutoCompleteTextView edtTextAddress;
    private MarkerOptions markerOptions;
    private ShippingAddress shippingAddress;
    private Item selectedArea;
    private Address selected_address;
    private String address_reference = "";
    private TextView no_area_txt;
    private boolean  isLoaded;
    private Handler  mapHandler;
    private Runnable mapRunnable;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final  int REQUEST_CHECK_SETTINGS = 1000;
    private ImageView img_close;
    private boolean isinitial;
    private AddressDialog addressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_over_a_map, container, false);
        if(getArguments() != null)
        {
            shippingAddress = getArguments().getParcelable("address");
            if(shippingAddress != null){
                latitude = shippingAddress.getLatitude();
                longitude = shippingAddress.getLongitude();
            }
        }

        mActivity = getActivity();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        appCompatActivity = (AppCompatActivity) getActivity();
        MapFragment mapFragment = (MapFragment) appCompatActivity.getFragmentManager().findFragmentById(map);

        ImageView btnMyLocation = view.findViewById(R.id.myLocation);
        img_close = view.findViewById(R.id.img_close);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTextAddress.setHint(getString(R.string.search_address));
                edtTextAddress.setText(AppConstants.BLANK_STRING);
            }
        });

        edtTextAddress = view.findViewById(R.id.edtTextAddress);
        no_area_txt = view.findViewById(R.id.no_area_txt);

        mapFragment.getMapAsync(this);
        edtTextAddress.bringToFront();

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (shouldAskPermissions()) {
                    askPermissions();
                }else {
                    settingsrequest();
                }

            }
        });

        mapHandler = new Handler();
        mapRunnable = new Runnable() {
            @Override
            public void run() {
                if(no_area_txt != null){
                    no_area_txt.setVisibility(View.GONE);
                }
            }
        };

        if(getActivity() != null){
            ((AddNewAddressActivity) getActivity()).setAddEnable(false);

            if(shippingAddress == null){
                isinitial = false;
                if (shouldAskPermissions()) {
                    askPermissions();
                }else {
                    mGoogleApiClient.connect();
                    settingsrequest();
                }
            }else{
                isinitial = true;
                settingsrequest();
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        appCompatActivity = (AppCompatActivity) getActivity();
        if(mGoogleApiClient != null){
            if(!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
                mGoogleApiClient.connect();
            }
        }else{
            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        if(addressDialog != null && addressDialog.isShowing()){
            addressDialog.dismiss();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mapHandler != null){
            mapHandler.removeCallbacks(mapRunnable);
        }

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ));
        // Add a marker in Sydney and move the camera
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
            }else{

            }
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
        }

        final LatLng cairoLatLng;
        markerOptions = new MarkerOptions();
        if(shippingAddress != null){
            cairoLatLng = new LatLng(shippingAddress.getLatitude(), shippingAddress.getLongitude());
        }else{
            if(((AddNewAddressActivity) mActivity).currentLatitude == 0.0 && ((AddNewAddressActivity) mActivity).currentLongitude == 0.0){
                cairoLatLng = new LatLng(latitude, longitude);
            }else{
                cairoLatLng = new LatLng(((AddNewAddressActivity) mActivity).currentLatitude, ((AddNewAddressActivity) mActivity).currentLongitude);
            }
        }

        if(shippingAddress == null){
            if(googleMap.getCameraPosition().zoom != 17.0){
                googleMap.moveCamera(CameraUpdateFactory.zoomTo( 17.0f ));
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(cairoLatLng));
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(googleMap.getCameraPosition().zoom != 17.0){
                        googleMap.moveCamera(CameraUpdateFactory.zoomTo( 17.0f ));
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(cairoLatLng));
                }
            }, 500);
        }

        loadArea(cairoLatLng.latitude, cairoLatLng.longitude);
        getAddressFromApi(((AddNewAddressActivity) mActivity).currentLatitude, ((AddNewAddressActivity) mActivity).currentLongitude);

        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(getActivity() != null){
                    ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                }
                System.out.println("GoogleMap Camera move started");
            }
        });

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                System.out.println("GoogleMap Camera change");

                googleMap.clear();
                try {

                    if (cameraPosition.target.latitude !=0.0) {
                        getAddressFromApi(cameraPosition.target.latitude, cameraPosition.target.longitude);
                        latitude = cameraPosition.target.latitude;
                        longitude = cameraPosition.target.longitude;
                        loadArea(latitude, longitude);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        if(shippingAddress != null){
            edtTextAddress.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_row, new LatLng(shippingAddress.getLatitude(), shippingAddress.getLongitude()), edtTextAddress.getText().toString()));
        }else{
            edtTextAddress.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_row, new LatLng(latitude, longitude), edtTextAddress.getText().toString()));
        }

        edtTextAddress.setOnItemClickListener(autoCompleteListener);
    }

    private void loadArea(double latitude, double longitude) {

        selectedArea = null;
        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put("lat", latitude);
            requestObject.put("lng", longitude);
            requestObject.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());

            if(shippingAddress != null && shippingAddress.getRegionId() != null){
                requestObject.put("warehouse_id", shippingAddress.getRegionId());
            }else{
                requestObject.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());
            }


            if (NetworkUtil.networkStatus(getActivity())) {

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, getActivity());
                Call<ResponseBody> call = apiService.getArea((new ConvertJsonToMap().jsonToMap(requestObject)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                if(responseObj.length() > 0){
                                    JSONObject jsonObjectresponse = responseObj.optJSONObject("response");
                                    if(jsonObjectresponse != null){

                                        int statusCode = jsonObjectresponse.optInt("status_code", 0);
                                        String errorMsg = jsonObjectresponse.optString("error_message");

                                        if(statusCode == 200){

                                            JSONObject dataObject = jsonObjectresponse.optJSONObject("data");
                                            if(dataObject != null){

                                                Item area = new Item(dataObject.optString("id"), AppConstants.BLANK_STRING, false);
                                                selectedArea = area;

                                                if(getActivity() != null){
                                                    ((AddNewAddressActivity) getActivity()).setAddEnable(true);
                                                }


                                            }else{
                                                if(no_area_txt.getVisibility() == View.VISIBLE){
                                                    mapHandler.removeCallbacks(mapRunnable);
                                                }
                                                no_area_txt.setVisibility(View.VISIBLE);
                                                mapHandler.postDelayed(mapRunnable, 5000);
                                                if(getActivity() != null){
                                                    ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                                                }
                                            }

                                        }else{
                                            if(no_area_txt.getVisibility() == View.VISIBLE){
                                                mapHandler.removeCallbacks(mapRunnable);
                                            }
                                            no_area_txt.setVisibility(View.VISIBLE);
                                            mapHandler.postDelayed(mapRunnable, 5000);
                                            if(getActivity() != null) {
                                                ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                                            }
                                        }

                                    }else{
                                        if(no_area_txt.getVisibility() == View.VISIBLE){
                                            mapHandler.removeCallbacks(mapRunnable);
                                        }
                                        no_area_txt.setVisibility(View.VISIBLE);
                                        mapHandler.postDelayed(mapRunnable, 5000);
                                        if(getActivity() != null){
                                            ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                                        }
                                    }

                                }else{
                                    if(no_area_txt.getVisibility() == View.VISIBLE){
                                        mapHandler.removeCallbacks(mapRunnable);
                                    }
                                    no_area_txt.setVisibility(View.VISIBLE);
                                    mapHandler.postDelayed(mapRunnable, 5000);
                                    if(getActivity() != null){
                                        ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                                    }
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                if(no_area_txt.getVisibility() == View.VISIBLE){
                                    mapHandler.removeCallbacks(mapRunnable);
                                }
                                no_area_txt.setVisibility(View.VISIBLE);
                                mapHandler.postDelayed(mapRunnable, 5000);
                                if(getActivity() != null){
                                    ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                                }
                            }

                        }else{
                            if(no_area_txt.getVisibility() == View.VISIBLE){
                                mapHandler.removeCallbacks(mapRunnable);
                            }
                            no_area_txt.setVisibility(View.VISIBLE);
                            mapHandler.postDelayed(mapRunnable, 5000);
                            if(getActivity() != null){
                                ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if(no_area_txt.getVisibility() == View.VISIBLE){
                            mapHandler.removeCallbacks(mapRunnable);
                        }
                        no_area_txt.setVisibility(View.VISIBLE);
                        mapHandler.postDelayed(mapRunnable, 5000);
                        if(getActivity() != null){
                            ((AddNewAddressActivity) getActivity()).setAddEnable(false);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
            if(no_area_txt.getVisibility() == View.VISIBLE){
                mapHandler.removeCallbacks(mapRunnable);
            }
            no_area_txt.setVisibility(View.VISIBLE);
            mapHandler.postDelayed(mapRunnable, 5000);
            if(getActivity() != null){
                ((AddNewAddressActivity) getActivity()).setAddEnable(false);
            }

        }

    }

    private AdapterView.OnItemClickListener autoCompleteListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String locationNameTxt = (String) parent.getItemAtPosition(position);
            AppUtil.hideSoftKeyboard(mActivity,getContext());
            new LatLngFromApiTask(locationNameTxt).execute();
        }
    };

    private void getAddressFromApi(final double latitude, final double longitude) {
        if (NetworkUtil.networkStatus(mActivity)) {
            edtTextAddress.setHint(getString(R.string.getting_location_data));
            edtTextAddress.setText(AppConstants.BLANK_STRING);

            ApiInterface apiService = getRequestQueue("https://maps.googleapis.com/maps/api/").create(ApiInterface.class);
            Call<ResponseBody> call = apiService.getAddress("" + latitude + "," + longitude, true,  AppConstants.GOOGLE_API_KEY);
            APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                //call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //AppUtil.hideProgress();
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                        if(resultJsonArray != null && resultJsonArray.length()>0){
                            JSONObject addressObj = resultJsonArray.getJSONObject(0);

                            ArrayList<Address> addressList = new ArrayList<>();
                            Address address = new Address(Locale.ENGLISH);
                            address.setAddressLine(0, addressObj.getString("formatted_address"));
                            addressList.add(address);

                            if (addressList.size() > 0)
                            {
                                if (googleMap != null && markerOptions != null) {
                                    markerOptions.snippet(addressList.get(0).getAddressLine(0));
                                }

                                edtTextAddress.setText(addressList.get(0).getAddressLine(0));
                                edtTextAddress.setHint(getString(R.string.search_address));
                                Log.e("Address is : ", "" + addressList.get(0).getAddressLine(0));

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        edtTextAddress.setHint(AppConstants.BLANK_STRING);
                        edtTextAddress.setText(AppConstants.BLANK_STRING);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //AppUtil.hideProgress();
                    edtTextAddress.setHint(AppConstants.BLANK_STRING);
                    edtTextAddress.setText(AppConstants.BLANK_STRING);
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getArguments() != null)
        {
            shippingAddress = getArguments().getParcelable("address");
            if(shippingAddress != null){
                latitude = shippingAddress.getLatitude();
                longitude = shippingAddress.getLongitude();
            }
        }

        if(shippingAddress == null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(googleMap != null){
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ));
                    }else{
                        new Handler().postDelayed(this, 300);
                    }
                }
            }, 700);
        }
    }

    class LatLngFromApiTask extends AsyncTask<Void, Void, Void>{

        String LOG_TAG = "AutoApp";
        String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode";
        String OUT_JSON = "/json";
        String API_KEY = "AIzaSyAz16e7lCk9niP5BP4k18a3Tv4GHiZvZWo";
        StringBuilder jsonResults = new StringBuilder();
        String address;

        public LatLngFromApiTask(String address){
            this.address = address;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection conn = null;

            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE  + OUT_JSON);
                sb.append("?address=" + URLEncoder.encode(address,  "UTF-8"));
                sb.append("&sensor=true&key=" + API_KEY);
                sb.append("&components=country:eg");

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (Exception e) {
                Log.e("PlaceAPI", "Error processing Places API URL", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                System.out.println("Print the Server Response :" + jsonResults.toString());
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray resultsJsonArray = jsonObj.getJSONArray("results");
                JSONObject location = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                latitude = location.optDouble("lat");
                longitude = location.optDouble("lng");

                if (googleMap!=null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                }
                // Extract the Place descriptions from the results
            } catch (Exception e) {
                Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
        }
    }

    public void addUserAddress() {

        if(appCompatActivity != null){
            addressDialog = new AddressDialog(appCompatActivity, R.style.AddressDialog, shippingAddress);
            addressDialog.show();
            addressDialog.setOnAddAddressClickLisener(new AddressDialog.OnAddAddressClickLisener() {
                @Override
                public void onAddAddressSubmit(String firstname, String lastName, String floor, String unit, String address_name, String building_name, String address_instrution, String street) {
                    AppUtil.hideSoftKeyboard(appCompatActivity, appCompatActivity);

                    try {
                        // hides the soft keyboard when the drawer opens
                        InputMethodManager inputManager = (InputMethodManager)appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(addressDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        addressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        addressDialog.dismiss();
                    }

                    addUpdateUserAddressWebService(firstname, lastName, floor, unit, address_name, building_name, address_instrution, street);
                }
            });
            addressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    AppUtil.hideSoftKeyboard(appCompatActivity, appCompatActivity);
                }
            });
        }
    }

    private void addUpdateUserAddressWebService(String firstname, String lastName, String floor, String unit, String address_name, String building_name, String address_instrution, String street) {
        if (NetworkUtil.networkStatus(mActivity)) {
            if(selectedArea != null){
                AppUtil.showProgress(mActivity);
                try {
                    JSONObject request_data = new JSONObject();
                    if (shippingAddress != null) {
                        request_data.put("shipping_address_id", shippingAddress.getShip_id());
                    }
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                    request_data.put("floor_number", floor);
                    request_data.put("unit_number", unit);
                    request_data.put("building_name", building_name);
                    request_data.put("area_id", selectedArea.getItemId());
                    request_data.put("street", street);
                    request_data.put("city_id", "2");
                    request_data.put("country_id", "62");
                    request_data.put("address_insturuction", address_instrution);
                    request_data.put("first_name", firstname);
                    request_data.put("last_name", lastName);
                    request_data.put("address_reference", address_name);
                    request_data.put("latitude", "" + latitude);
                    request_data.put("longitude", "" + longitude);
                    request_data.put("input_from", "map");

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
            }else{
                if(shippingAddress != null){
                    AppUtil.showProgress(mActivity);
                    try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("shipping_address_id", shippingAddress.getShip_id());
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("floor_number", floor);
                        request_data.put("unit_number", unit);
                        request_data.put("building_name", building_name);
                        request_data.put("area_id", shippingAddress.getArea_id());
                        request_data.put("city_id", "2");
                        request_data.put("country_id", "62");
                        request_data.put("address_insturuction", address_instrution);
                        request_data.put("first_name", firstname);
                        request_data.put("last_name", lastName);
                        request_data.put("address_reference", address_name);
                        request_data.put("latitude", "" + latitude);
                        request_data.put("longitude", "" + longitude);
                        request_data.put("input_from", "map");

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
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location.getLatitude() !=0.0) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (googleMap!=null && shippingAddress == null) {
                if(googleMap.getCameraPosition().zoom == 2.0){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
                }else{
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                }
                getAddressFromApi(latitude, longitude);
            }else{
                if(shippingAddress != null){
                    if(shippingAddress.getLatitude() == latitude && shippingAddress.getLongitude() == longitude){

                    }else{
                        if(googleMap.getCameraPosition().zoom == 2.0){
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
                        }else{
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                        }
                        getAddressFromApi(latitude, longitude);
                    }
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(shippingAddress == null){
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                //If everything went fine lets get latitude and longitude
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                onLocationChanged(location);
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            }
        }else{
            if(shippingAddress.getLongitude() == longitude && shippingAddress.getLongitude() == latitude){

            }else{
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                } else {
                    //If everything went fine lets get latitude and longitude
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    onLocationChanged(location);
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                }
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void settingsrequest() {

        int finelocationPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (finelocationPermission != PackageManager.PERMISSION_GRANTED || coarsePermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if(!isinitial){
                requestPermissions(PERMISSIONS_LOCATION, REQUEST_EXTERNAL_LOATION);
            }else{
                isinitial = false;
            }
        } else {

            if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }else{
                    onLocationChanged(location);
                }
            }else{
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:

                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_LOATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @TargetApi(23)
    protected void askPermissions() {
        int finelocationPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (finelocationPermission != PackageManager.PERMISSION_GRANTED || coarsePermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            requestPermissions(PERMISSIONS_LOCATION, REQUEST_EXTERNAL_LOATION);
        } else {
            settingsrequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_LOATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // save file
               settingsrequest();
            } else {
                if (googleMap!=null && shippingAddress == null)
                {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
                    getAddressFromApi(latitude, longitude);
                }
            }
        }
    }
}
