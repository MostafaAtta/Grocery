package thegroceryshop.com.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Region;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class LoadInitialData extends IntentService {

    private static final String ACTION_LOAD = "services.action.load";
    private ApiInterface apiService;
    private int count_free_shipping_amount;
    public LoadInitialData() {
        super("LoadInitialData");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void loadInitialData(Context context) {

        Intent intent = new Intent(context, LoadInitialData.class);
        intent.setAction(ACTION_LOAD);
        if(!OnlineMartApplication.isApplicationInBackground){
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            apiService = ApiClient.createService(ApiInterface.class, this);
            final String action = intent.getAction();
            if (ACTION_LOAD.equalsIgnoreCase(action)) {
                count_free_shipping_amount = 0;
                loadInitialData();
            }
        }
    }

    private void loadInitialData() {

        try{

            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request.put("device_type", AppConstants.DEVICE_TYPE);
            request.put("app_version", DeviceUtil.getVersionName(this));
            request.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());

            if(count_free_shipping_amount < 6){
                count_free_shipping_amount++;
                Call<ResponseBody> call = apiService.loadInitialData((new ConvertJsonToMap().jsonToMap(request)));
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
                                        if (resObj.optJSONObject("data") != null) {
                                            JSONObject object = resObj.optJSONObject("data");

                                            String warehouse_id = resObj.optString("warehouse_id");

                                            if (object != null) {
                                                OnlineMartApplication.mLocalStore.saveFreeShippingAmount((float) object.optDouble("delivary_charge"));
                                                OnlineMartApplication.mLocalStore.saveShowCaseText(object.optString("welcome_message"));

                                                JSONArray arr_regions = resObj.optJSONArray("warehouse");
                                                OnlineMartApplication.mLocalStore.saveRegionList(arr_regions.toString());

                                                if(arr_regions != null && arr_regions.length()>0){

                                                    OnlineMartApplication.getRegions().clear();

                                                    for(int i=0; i<arr_regions.length(); i++){

                                                        JSONObject regionObj = (JSONObject) arr_regions.get(i);

                                                        if(regionObj != null){

                                                            Region region = new Region();
                                                            region.setRegionId(regionObj.optString("id"));
                                                            region.setRegionNameEnglish(regionObj.optString("name"));
                                                            region.setRegionNameArabic(regionObj.optString("name_ar"));

                                                            if(!ValidationUtil.isNullOrBlank(warehouse_id) && region.getRegionId().equalsIgnoreCase(warehouse_id)){
                                                                OnlineMartApplication.mLocalStore.setSelectedRegion(region);
                                                                region.setSelected(true);
                                                            }else{
                                                               if(region.getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId())){
                                                                   region.setSelected(true);
                                                               }
                                                            }

                                                            OnlineMartApplication.getRegions().add(region);
                                                        }
                                                    }

                                                }

                                                stopSelf();
                                            }else{
                                                loadInitialData();
                                            }
                                        }else{
                                            loadInitialData();
                                        }

                                    } else {
                                        loadInitialData();
                                    }

                                } else {
                                    loadInitialData();
                                }

                            } catch (Exception e) {
                                loadInitialData();
                                e.printStackTrace();
                                //AppUtil.showErrorDialog(mContext, response.message());
                            }
                        } else {
                            loadInitialData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loadInitialData();
                    }
                });
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}