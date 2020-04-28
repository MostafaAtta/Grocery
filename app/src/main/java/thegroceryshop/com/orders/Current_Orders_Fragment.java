package thegroceryshop.com.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.OrderCaptain;
import thegroceryshop.com.modal.OrderData;
import thegroceryshop.com.modal.TimeSlot;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by jaikishang on 2/23/2017.
 */
@SuppressLint("ValidFragment")
public class Current_Orders_Fragment extends Fragment {

    private LoaderLayout loader_orders;
    private RecyclerView recyl_orders;
    private MyOrdersActivity activity;

    private ApiInterface apiService;
    private ArrayList<OrderData> list_orders = new ArrayList<>();
    private DateTimeFormatter formatter_time = DateTimeFormat.forPattern("HH:mm:ss");
   // private DateTimeFormatter formatter_time1 = DateTimeFormat.forPattern("h a");
    private DateTimeFormatter formatter_time1 = DateTimeFormat.forPattern("HH:mm");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_my_orders_item, container, false);

        activity = (MyOrdersActivity) getActivity();
        apiService = ApiClient.createService(ApiInterface.class, activity);

        loader_orders = view.findViewById(R.id.current_orders_loader);
        recyl_orders = view.findViewById(R.id.current_orders_recyl_orders);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyl_orders.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentOrders();
    }

    private void loadCurrentOrders() {

        try {
            JSONObject order_request_data = new JSONObject();
            order_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            order_request_data.put("customer_id", OnlineMartApplication.mLocalStore.getUserId());
            order_request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(activity)) {
                try {
                    loader_orders.showProgress();
                    Call<ResponseBody> call = apiService.loadCurrentOrders((new ConvertJsonToMap().jsonToMap(order_request_data)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loader_orders.showContent();

                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONArray dataArray = resObj.optJSONArray("data");
                                            if (dataArray != null && dataArray.length() > 0) {
                                                list_orders.clear();
                                                for (int i = 0; i < dataArray.length(); i++) {
                                                    JSONObject orderObj = dataArray.optJSONObject(i);

                                                    if (orderObj != null) {
                                                        OrderData orderData = new OrderData();

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("order_id"))) {
                                                            orderData.setOrderId(orderObj.optString("order_id"));
                                                        } else {
                                                            orderData.setOrderId(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("auth_hash"))) {
                                                            orderData.setAuth_hash(orderObj.optString("auth_hash"));
                                                        } else {
                                                            orderData.setAuth_hash(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("delivery_date"))) {
                                                            orderData.setDeliveryDate(orderObj.optString("delivery_date"));
                                                        } else {
                                                            orderData.setDeliveryDate(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("order_placing_date"))) {
                                                            orderData.setOrderPlaceDate(orderObj.optString("order_placing_date"));
                                                        } else {
                                                            orderData.setOrderPlaceDate(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("order_total_item"))) {
                                                            orderData.setNoOfItems(orderObj.optString("order_total_item"));
                                                        } else {
                                                            orderData.setNoOfItems(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("warehouse_name"))) {
                                                            orderData.setRegionName(orderObj.optString("warehouse_name"));
                                                        } else {
                                                            orderData.setRegionName(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("order_type"))) {
                                                            if (orderObj.optString("order_type").equalsIgnoreCase("1")) {
                                                                orderData.setOrderType(getString(R.string.express));
                                                            } else {
                                                                orderData.setOrderType(getString(R.string.scheduled));
                                                            }
                                                        } else {
                                                            orderData.setOrderType(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("total_amount"))) {
                                                            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                                                                orderData.setOrderAmount(getString(R.string.egp) + AppConstants.SPACE + orderObj.optString("total_amount"));
                                                            }else{
                                                                orderData.setOrderAmount(orderObj.optString("total_amount") + AppConstants.SPACE + getString(R.string.egp));
                                                            }
                                                        } else {
                                                            orderData.setOrderAmount(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("order_payment_type"))) {
                                                            if (orderObj.optString("order_payment_type").equalsIgnoreCase("1")) {
                                                                orderData.setPaymentType(getString(R.string.cod).toUpperCase());
                                                            } else if (orderObj.optString("order_payment_type").equalsIgnoreCase("2")) {
                                                                orderData.setPaymentType(getString(R.string.card).toUpperCase());
                                                            } else if (orderObj.optString("order_payment_type").equalsIgnoreCase("3")) {
                                                                orderData.setPaymentType(getString(R.string.tgs_credit).toUpperCase());
                                                            } else if (orderObj.optString("order_payment_type").equalsIgnoreCase("4")) {
                                                                orderData.setPaymentType(getString(R.string.card_and_credits).toUpperCase());
                                                            } else {
                                                                orderData.setPaymentType(getString(R.string.cash_and_credits).toUpperCase());
                                                            }
                                                        } else {
                                                            orderData.setPaymentType(getString(R.string.na));
                                                        }

                                                        if(orderObj.optJSONObject("order_captain_info") != null){

                                                            JSONObject orderCaptainObj = orderObj.optJSONObject("order_captain_info");
                                                            if(orderCaptainObj != null){

                                                                OrderCaptain orderCaptain = new OrderCaptain();

                                                                if(!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("id"))){
                                                                    orderCaptain.setId(orderCaptainObj.optString("id"));
                                                                }else{
                                                                    orderCaptain.setId(getString(R.string.na));
                                                                }

                                                                if(!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("name"))){
                                                                    orderCaptain.setName(orderCaptainObj.optString("name"));
                                                                }else{
                                                                    orderCaptain.setName(getString(R.string.na));
                                                                }

                                                                if(!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("mobile"))){
                                                                    orderCaptain.setMobileNo(orderCaptainObj.optString("mobile"));
                                                                }else{
                                                                    orderCaptain.setMobileNo(getString(R.string.na));
                                                                }

                                                                if(!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("country_code"))){
                                                                    orderCaptain.setCountryCode(orderCaptainObj.optString("country_code"));
                                                                }else{
                                                                    orderCaptain.setCountryCode(getString(R.string.na));
                                                                }

                                                                if(!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("order_captain_lat"))){
                                                                    orderCaptain.setLatitude(orderCaptainObj.optDouble("order_captain_lat", 0.0f));
                                                                }else{
                                                                    orderCaptain.setLatitude(0.0f);
                                                                }

                                                                if(!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("order_captain_long"))){
                                                                    orderCaptain.setLongitude(orderCaptainObj.optDouble("order_captain_long", 0.0f));
                                                                }else{
                                                                    orderCaptain.setLongitude(0.0f);
                                                                }

                                                                orderData.setOrderCaptain(orderCaptain);

                                                            }else{
                                                                orderData.setOrderCaptain(null);
                                                            }

                                                        }else{
                                                            orderData.setOrderCaptain(null);
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderObj.optString("order_status"))) {

                                                            if (orderObj.optString("order_status").equalsIgnoreCase("1")) {

                                                                orderData.setOrderStatus(getString(R.string.acknowledge).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("2")) {

                                                                orderData.setOrderStatus(getString(R.string.picking).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("3")) {

                                                                orderData.setOrderStatus(getString(R.string.picked).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("4")) {

                                                                orderData.setOrderStatus(getString(R.string.packing).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("5")) {

                                                                orderData.setOrderStatus(getString(R.string.packed).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("6")) {

                                                                orderData.setOrderStatus(getString(R.string.out_for_delivery).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("7")) {

                                                                orderData.setOrderStatus(getString(R.string.delivered).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("9")) {

                                                                orderData.setOrderStatus(getString(R.string.picking).toUpperCase());

                                                            } else if (orderObj.optString("order_status").equalsIgnoreCase("10")) {

                                                                orderData.setOrderStatus(getString(R.string.cancelled).toUpperCase());

                                                            } else if(orderObj.optString("order_status").equalsIgnoreCase("12")){

                                                                orderData.setOrderStatus(getString(R.string.packing).toUpperCase());

                                                            } else if(orderObj.optString("order_status").equalsIgnoreCase("13")){

                                                                orderData.setOrderStatus(getString(R.string.returned).toUpperCase());
                                                            }
                                                        } else {

                                                            orderData.setOrderStatus(getString(R.string.na));
                                                        }

                                                        if (orderObj.optJSONObject("delivery_time_slot") != null) {

                                                            JSONObject timeSlotObj = orderObj.optJSONObject("delivery_time_slot");
                                                            if (timeSlotObj != null) {

                                                                TimeSlot timeSlot = new TimeSlot();
                                                                timeSlot.setId(timeSlotObj.optString("id"));

                                                                if (!ValidationUtil.isNullOrBlank(timeSlotObj.optString("start_time"))) {

                                                                    if(timeSlotObj.optString("start_time").equalsIgnoreCase("24:00:00")){
                                                                        DateTime startTime = formatter_time.parseDateTime("00:00:00");
                                                                        timeSlot.setStartTime(startTime);
                                                                    }else{
                                                                        DateTime startTime = formatter_time.parseDateTime(timeSlotObj.optString("start_time"));
                                                                        timeSlot.setStartTime(startTime);
                                                                    }
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(timeSlotObj.optString("end_time"))) {

                                                                    if(timeSlotObj.optString("end_time").equalsIgnoreCase("24:00:00")){
                                                                        DateTime endTime = formatter_time.parseDateTime("00:00:00");
                                                                        timeSlot.setEndTime(endTime);
                                                                    }else{
                                                                        DateTime endTime = formatter_time.parseDateTime(timeSlotObj.optString("end_time"));
                                                                        timeSlot.setEndTime(endTime);
                                                                    }
                                                                }

                                                                orderData.setDeliveryTimeSlot(timeSlot);

                                                                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")){

                                                                    String start = formatter_time1.print(timeSlot.getStartTime());
                                                                    if(start.contains("ص")){
                                                                        start = start.replace("ص", getString(R.string.am));
                                                                    }else if(start.contains("م")){
                                                                        start = start.replace("م", getString(R.string.pm));
                                                                    }

                                                                    String end = formatter_time1.print(timeSlot.getEndTime());
                                                                    if(end.contains("ص")){
                                                                        end = end.replace("ص", getString(R.string.am));
                                                                    }else if(end.contains("م")){
                                                                        end = end.replace("م", getString(R.string.pm));
                                                                    }

                                                                    orderData.setDeliveryTimeInString(String.format(
                                                                            getString(R.string.time_slot_format),
                                                                            start, end));
                                                                }else{
                                                                    orderData.setDeliveryTimeInString(String.format(
                                                                            getString(R.string.time_slot_format),
                                                                            formatter_time1.print(timeSlot.getStartTime()),
                                                                            formatter_time1.print(timeSlot.getEndTime())));
                                                                }

                                                            } else {
                                                                orderData.setDeliveryTimeSlot(null);
                                                            }

                                                        } else {
                                                            orderData.setDeliveryTimeSlot(null);
                                                        }

                                                        list_orders.add(orderData);
                                                    }
                                                }

                                                OrderListAdapter orderListAdapter = new OrderListAdapter(activity, list_orders);
                                                recyl_orders.setAdapter(orderListAdapter);

                                                orderListAdapter.setOnOrderClickListener(new OrderListAdapter.OnOrderClickListener() {
                                                    @Override
                                                    public void onOrderClick(int position) {

                                                        if(list_orders != null && list_orders.size() > position){
                                                            Intent orderDetailIntent = new Intent(activity, OrderDetailActivity.class);
                                                            orderDetailIntent.putExtra(OrderDetailActivity.ORDER_ID, list_orders.get(position).getOrderId());
                                                            orderDetailIntent.putExtra(OrderDetailActivity.AUTH_HASH, list_orders.get(position).getAuth_hash());
                                                            startActivity(orderDetailIntent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onTrackOrder(int position) {
                                                        if(list_orders != null && list_orders.size() > position){
                                                            Intent trackOrderIntent = new Intent(activity, TrackOrderCaptainActivity.class);
                                                            trackOrderIntent.putExtra(TrackOrderCaptainActivity.KEY_ORDER_CAPTAIN, list_orders.get(position).getOrderCaptain());
                                                            startActivity(trackOrderIntent);
                                                        }
                                                    }
                                                });


                                            } else {
                                                if(loader_orders != null && isAdded()){
                                                    loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                                                    loader_orders.showStatusText();
                                                }
                                                //AppUtil.showErrorDialog(activity, errorMsg);
                                            }

                                        } else {
                                            if(loader_orders != null && isAdded()){
                                                loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                                                loader_orders.showStatusText();

                                            }
                                            //AppUtil.showErrorDialog(activity, errorMsg);
                                        }

                                    } else {
                                        if(loader_orders != null && isAdded()){
                                            loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                                            loader_orders.showStatusText();
                                        }
                                        //AppUtil.showErrorDialog(activity, getString(R.string.some_error_occoured));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    if(loader_orders != null && isAdded()) {
                                        loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                                        loader_orders.showStatusText();
                                    }
                                    //AppUtil.showErrorDialog(activity, response.message());
                                }
                            } else {
                                if(loader_orders != null && isAdded()) {
                                    loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                                    loader_orders.showStatusText();
                                }
                                //AppUtil.showErrorDialog(activity, response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if(loader_orders != null && isAdded()) {
                                loader_orders.showContent();
                                loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                                loader_orders.showStatusText();
                            }
                            //AppUtil.hideProgress();
                        }
                    });
                } catch (JSONException e) {
                    if(loader_orders != null && isAdded()) {

                        loader_orders.showContent();
                        loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                        loader_orders.showStatusText();
                    }
                    e.printStackTrace();
                }

            } else {
                if(loader_orders != null && isAdded()) {

                    loader_orders.showContent();
                    loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                    loader_orders.showStatusText();
                }
            }
        } catch (JSONException e) {
            if(loader_orders != null && isAdded()) {

                loader_orders.showContent();
                loader_orders.setStatuText(getString(R.string.no_current_orders_available));
                loader_orders.showStatusText();
            }
            //AppUtil.hideProgress();
            e.printStackTrace();
        }
    }
}
