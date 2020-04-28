package thegroceryshop.com.orders;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.modal.OrderCaptain;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/27/2017.
 */

public class TrackOrderCaptainActivity extends AppCompatActivity {

    private RippleButton btn_done;
    private OrderCaptain orderCaptain;
    private Handler locationHandler;
    private Runnable locationRunnable;
    private ApiInterface apiService;
    private GoogleMap googleMap;
    private Marker currentMarker;

    private Context mContext;
    private Toolbar toolbar;
    private TextView txt_title;
    private ImageView img_call;
    private static final  int REQUEST_CHECK_SETTINGS = 1000;
    public static final String KEY_ORDER_CAPTAIN = "key_order_captain";
    public final int MIN_INTERVAL_FOR_LOCATION_UPDATE = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_captain);

        mContext = this;
        if(getIntent() != null && getIntent().hasExtra(KEY_ORDER_CAPTAIN)){
            orderCaptain = getIntent().getParcelableExtra(KEY_ORDER_CAPTAIN);
        }

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        img_call = findViewById(R.id.icon_call);
        setSupportActionBar(toolbar);

        if(orderCaptain == null){
            img_call.setVisibility(View.GONE);
        }else{
            img_call.setVisibility(View.VISIBLE);
        }

        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                toolbar.setNavigationIcon(R.mipmap.top_back);
            }else{
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            txt_title.setText(getResources().getString(R.string.track_order).toUpperCase());
        }

        apiService = ApiClient.createService(ApiInterface.class, mContext);
        btn_done = findViewById(R.id.track_btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(orderCaptain != null){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orderCaptain.getMobileNo(), null));
                    startActivity(intent);
                }
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.track_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                TrackOrderCaptainActivity.this.googleMap = googleMap;
                LatLng cairoLatLng = null;

                MarkerOptions markerOptions = new MarkerOptions();

                if(orderCaptain != null){
                    if(orderCaptain.getLatitude() == 0.0 && orderCaptain.getLatitude() == 0.0 ){
                        cairoLatLng = new LatLng(30.058659, 31.235407);
                    }else{
                        cairoLatLng = new LatLng(orderCaptain.getLatitude(), orderCaptain.getLongitude());
                    }
                    markerOptions.title(orderCaptain.getName());
                }else{
                    cairoLatLng = new LatLng(30.058659, 31.235407);
                }

                markerOptions.position(cairoLatLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));
                currentMarker = googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cairoLatLng, 17.0f));

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (shouldAskPermissions())
//        {
//            askPermissions();
//        }
        if(locationRunnable == null){
            locationRunnable = new Runnable() {
                @Override
                public void run() {
                    if(orderCaptain != null){
                        loadCurrentPosition(orderCaptain.getId());
                        locationHandler.postDelayed(locationRunnable, MIN_INTERVAL_FOR_LOCATION_UPDATE);
                    }
                }
            };
        }

        if(locationHandler == null){
            locationHandler = new Handler();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                locationHandler.postDelayed(locationRunnable, MIN_INTERVAL_FOR_LOCATION_UPDATE);
            }
        }, MIN_INTERVAL_FOR_LOCATION_UPDATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationHandler != null){
            locationHandler.removeCallbacks(locationRunnable);
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

    private void loadCurrentPosition(String id) {

        try {
            JSONObject login_request_data = new JSONObject();
            login_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            login_request_data.put("order_captain_id", id);

            if (NetworkUtil.networkStatus(mContext)) {
                try {

                    Call<ResponseBody> call = apiService.updateCaptainLocation((new ConvertJsonToMap().jsonToMap(login_request_data)));
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

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if(dataObj != null){

                                                double lat = Double.parseDouble(dataObj.optString("lagtitude"));
                                                double lon = Double.parseDouble(dataObj.optString("longitude"));

                                                if(orderCaptain != null && googleMap != null){
                                                    if (currentMarker != null) {
                                                        currentMarker.remove();
                                                    }

                                                    LatLng cairoLatLng = new LatLng(lat, lon);
                                                    MarkerOptions markerOptions = new MarkerOptions();
                                                    markerOptions.title(orderCaptain.getName());
                                                    markerOptions.position(cairoLatLng);
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));
                                                    currentMarker = googleMap.addMarker(markerOptions);
                                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(cairoLatLng));
                                                }
                                            }

                                        } else {
                                        }

                                    } else {
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
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
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_CALL = 1;
    private static String[] PERMISSIONS_CALL = {
            Manifest.permission.CALL_PHONE
    };

    @TargetApi(23)
    protected void askPermissions() {
        int mCallPermission = ActivityCompat.checkSelfPermission(TrackOrderCaptainActivity.this, Manifest.permission.CALL_PHONE);

        if (mCallPermission != PackageManager.PERMISSION_GRANTED ) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(TrackOrderCaptainActivity.this, PERMISSIONS_CALL, REQUEST_EXTERNAL_CALL
            );
        }else
        {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_CALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // save file
            } else {
                Toast.makeText(TrackOrderCaptainActivity.this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
