package thegroceryshop.com.loginModule;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.CountriesListActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.country_list.CountriesBaseFragment;
import thegroceryshop.com.country_list.model.CountryPhoneCode;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/16/2015.
 */
public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    // for operations
    private Context mContext;
    private LoginModuleActivity activity;
    private static final int PERMISSION_CALLBACK_CONSTANT = 500;
    private String[] permissionsRequired = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    private LinearLayout header;
    private TextView textView2;
    private AppCompatTextView txtViewCountryCode;
    private AppCompatEditText txtViewNumber;
    private RippleButton rippleButton;
    private LinearLayout footer;
    private CountryPhoneCode selected_country;
    private int mCountryCode;
    private JSONObject mCheckMobileData = new JSONObject();

    String otp;
    String userId;

    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
        return forgotPasswordFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.frag_forgot_password, container, false);
        mContext = getActivity();
        activity = (LoginModuleActivity) getActivity();

        initView(view);
        setOnClickListener();

        new AsyncPhoneInitTask(mContext).execute();

        return view;
    }

    private void initView(View view) {
        header = view.findViewById(R.id.header);
        textView2 = view.findViewById(R.id.textView2);
        txtViewCountryCode = view.findViewById(R.id.txtViewCountryCode);
        txtViewNumber = view.findViewById(R.id.txtViewNumber);
        rippleButton = view.findViewById(R.id.rippleButton);
        footer = view.findViewById(R.id.mCheckoutBtn);
    }

    private void setOnClickListener(){
        rippleButton.setOnClickListener(this);
        txtViewCountryCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.rippleButton:
                AppUtil.hideSoftKeyboard(getActivity(),mContext);
                if (!txtViewNumber.getText().toString().isEmpty()) {

                        try {
                            mCheckMobileData.put("country_code", "" + mCountryCode);
                            mCheckMobileData.put("mobile_number", txtViewNumber.getText().toString());
                            mCheckMobileData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                            mCheckMobileNo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                } else {
                    AppUtil.displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, mContext.getResources().getString(R.string.error_blank_phone), mContext.getResources().getString(R.string.ok), false);
                }
                break;

            case R.id.txtViewCountryCode:

                Intent intentCountry = new Intent(mContext, CountriesListActivity.class);
                startActivityForResult(intentCountry, AppConstants.REQUEST_PICK_COUNTRY_SELF);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.REQUEST_PICK_COUNTRY_SELF) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getParcelableExtra(CountriesBaseFragment.result_country) != null) {
                    selected_country = data.getParcelableExtra(CountriesBaseFragment.result_country);
                    if (selected_country != null) {
                        txtViewCountryCode.setText(selected_country.getCountryCodeStr());
                        mCountryCode = selected_country.getCountryCode();
                    }
                }
            }
        }
    }

    // load  the countries list from loacal to set the device's default information
    public class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<CountryPhoneCode>> {

        private Context mContext;
        private ArrayList<CountryPhoneCode> mCountry = new ArrayList<>();
        private SparseArray<ArrayList<CountryPhoneCode>> mCountriesMap = new SparseArray<ArrayList<CountryPhoneCode>>();

        AsyncPhoneInitTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<CountryPhoneCode> doInBackground(Void... params) {

            /**
             *  Load RequestData From Text File
             * */
            ArrayList<CountryPhoneCode> data = new ArrayList<CountryPhoneCode>(233);
            mCountry = new ArrayList<CountryPhoneCode>(233);
            BufferedReader reader = null;
            try {

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));
                }else{
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries_arabic.dat"), "UTF-8"));
                }

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    CountryPhoneCode c = new CountryPhoneCode(mContext, line, i);
                    data.add(c);
                    ArrayList<CountryPhoneCode> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<CountryPhoneCode>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<CountryPhoneCode> data) {
            mCountry = data;
//            for (int i = 0; i < mCountry.size(); i++) {
//                if (DeviceUtil.getDeviceCountryCode().equalsIgnoreCase(mCountry.get(i).getCountryISO().toUpperCase())) {
//                    selected_country = mCountry.get(i);
//                    txtViewCountryCode.setText(selected_country.getCountryCodeStr());
//                    mCountryCode = selected_country.getCountryCode();
//                    break;
//                }
//            }
//
//            if (selected_country.getCountryCode()==1)
//            {
                txtViewCountryCode.setText("+20");
                mCountryCode = 20;
//            }
        }
    }

    private void mCheckMobileNo() {
        if (NetworkUtil.networkStatus(mContext)) {
            try {
                AppUtil.showProgress(mContext);
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                Call<ResponseBody> call = apiService.mCheckMobileNoExistance((new ConvertJsonToMap().jsonToMap(mCheckMobileData)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AppUtil.hideProgress();

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

                                                otp = dataObject.optString("otp");
                                                userId = dataObject.optString("user_id");

                                                if (Build.VERSION.SDK_INT >= 23) {

                                                    ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("otp", otp);
                                                    bundle.putString("user_id", userId);
                                                    bundle.putString("phone_number", txtViewCountryCode.getText().toString() + " " + txtViewNumber.getText().toString());
                                                    activity.switchToFragment(VerifyUserFragment.newInstance(bundle), true);

                                                    /*if(!askForPermissions()){

                                                        requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);

                                                    }else{

                                                        ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();

                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("otp", otp);
                                                        bundle.putString("user_id", userId);
                                                        bundle.putString("phone_number", txtViewCountryCode.getText().toString() + " " + txtViewNumber.getText().toString());
                                                        activity.switchToFragment(VerifyUserFragment.newInstance(bundle), true);


                                                    }*/
                                                } else {

                                                    ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("otp", otp);
                                                    bundle.putString("user_id", userId);
                                                    bundle.putString("phone_number", txtViewCountryCode.getText().toString() + " " + txtViewNumber.getText().toString());
                                                    activity.switchToFragment(VerifyUserFragment.newInstance(bundle), true);
                                                }

                                            }else{
                                                AppUtil.displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, getString(R.string.error_msg) + "(ERR-604)", getResources().getString(R.string.ok), false);
                                            }

                                        }else{

                                            AppUtil.displaySingleActionAlert(mContext, AppConstants.ERROR_TAG, errorMsg, getResources().getString(R.string.ok), false);
                                        }

                                    }else{
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-605)");
                                    }

                                }else{
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-606)");
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-607)");
                            }

                        }else{
                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-608)");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-609)");
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-610)");
            }
        }
    }

    private boolean askForPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ((OnlineMartApplication)(getActivity().getApplicationContext())).registerSMSAutoReadReceiver();
            }

            Bundle bundle = new Bundle();
            bundle.putString("otp", otp);
            bundle.putString("user_id", userId);
            bundle.putString("phone_number", txtViewCountryCode.getText().toString() + " " + txtViewNumber.getText().toString());
            activity.switchToFragment(VerifyUserFragment.newInstance(bundle), true);
        }
    }

}
