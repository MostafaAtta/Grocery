package thegroceryshop.com.checkoutProcess.checkoutfragment;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class TimeSlotService extends IntentService {

    private static final String ACTION_TIME_SLOT = "services.action.time_slot";
    private static final String KEY_REQUEST = "services.extra.key.request";
    private ApiInterface apiService;

    public TimeSlotService() {
        super("LoadInitialData");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void reserveOrReleaseTimeSlot(Context context, JSONObject requestObj) {

        Intent intent = new Intent(context, TimeSlotService.class);
        intent.setAction(ACTION_TIME_SLOT);
        intent.putExtra(KEY_REQUEST, requestObj.toString());
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            apiService = ApiClient.createService(ApiInterface.class, this);
            final String action = intent.getAction();
            try {
                if(ACTION_TIME_SLOT.equalsIgnoreCase(action)){
                    JSONObject request = new JSONObject(intent.getStringExtra(KEY_REQUEST));
                    reserveOrReleaseTimeSlot(request);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void reserveOrReleaseTimeSlot(JSONObject request_data) {

        try {
            Call<ResponseBody> call = apiService.reserveOrReleaseTimeSlot((new ConvertJsonToMap().jsonToMap(request_data)));
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
                                    stopSelf();

                                } else {
                                    //AppUtil.showErrorDialog(mContext, errorMsg);
                                }

                            } else {
                                //AppUtil.showErrorDialog(mContext, getString(R.string.some_error_occoured));
                            }

                        } catch (Exception e) {//(JSONException | IOException e) {

                            e.printStackTrace();
                            //AppUtil.showErrorDialog(mContext, response.message());
                        }
                    } else {
                        //AppUtil.showErrorDialog(mContext, response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}