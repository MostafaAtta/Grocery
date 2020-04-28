package thegroceryshop.com.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadUsualData extends IntentService {

    private static final String ACTION_LOAD = "services.action.loadshippingcharges";
    private static final String ACTION_LOAD_WISHLIST = "services.action.loadwishlists";
    private ApiInterface apiService;
    private int count_shipping_charges, count_wish_list;
    public LoadUsualData() {
        super("LoadUsualData");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void loadShippingCharges(Context context) {

        Intent intent = new Intent(context, LoadUsualData.class);
        intent.setAction(ACTION_LOAD);
        try{
            if(!OnlineMartApplication.isApplicationInBackground){
                context.startService(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void loadWishLists(Context context) {

        Intent intent = new Intent(context, LoadUsualData.class);
        intent.setAction(ACTION_LOAD_WISHLIST);
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
                count_shipping_charges = 0;
                loadCharges();
            }
            if (ACTION_LOAD_WISHLIST.equalsIgnoreCase(action)) {
                count_wish_list = 0;
                loadWishLists();
            }
        }
    }

    private void loadCharges() {

        try {
            JSONObject request = new JSONObject();
            //Added by Naresh
            request.put("area_id", OnlineMartApplication.mLocalStore.getDefaultShippingAreaId());

            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if(count_shipping_charges < 6){
                count_shipping_charges++;
                Call<ResponseBody> call = apiService.getShippingCharges((new ConvertJsonToMap().jsonToMap(request)));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                JSONObject resObj = obj.optJSONObject("response");
                                if (resObj.length() > 0) {

                                    int statusCode = resObj.optInt("status_code", 0);
                                    //String errorMsg = resObj.optString("error_message");
                                    if (statusCode == 200) {
                                        //String responseMessage = resObj.optString("success_message");
                                        if (resObj.optJSONArray("data") != null) {
                                            JSONArray array = resObj.optJSONArray("data");
                                            if (array != null) {
                                                OnlineMartApplication.mLocalStore.saveShippingCharges(array.toString());
                                            } else {
                                                loadCharges();
                                            }
                                        } else {
                                            loadCharges();
                                        }

                                    } else {
                                        loadCharges();
                                    }

                                } else {
                                    loadCharges();
                                }

                            } catch (Exception e) {
                                loadCharges();
                                e.printStackTrace();
                            }
                        } else {
                            loadCharges();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loadCharges();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadWishLists() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                if(count_wish_list < 6){
                    count_wish_list++;
                    Call<ResponseBody> call = apiService.getSavedWishLists((new ConvertJsonToMap().jsonToMap(request)));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        if (statusCode == 200) {
                                            if (resObj.optJSONArray("data") != null) {
                                                JSONArray array = resObj.optJSONArray("data");
                                                if (array != null) {
                                                    OnlineMartApplication.mLocalStore.setMyWishListNames(array.toString());
                                                } else {
                                                    loadWishLists();
                                                }
                                            } else {
                                                loadWishLists();
                                            }

                                        } else if(statusCode == 201) {

                                        } else {
                                            loadWishLists();
                                        }

                                    } else {
                                        loadWishLists();
                                    }

                                } catch (Exception e) {
                                    loadWishLists();
                                    e.printStackTrace();
                                }
                            } else {
                                loadWishLists();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loadWishLists();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}