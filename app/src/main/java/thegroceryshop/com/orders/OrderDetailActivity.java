package thegroceryshop.com.orders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.OrderProductListAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.OrderCaptain;
import thegroceryshop.com.modal.OrderData;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.modal.TimeSlot;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/*
 * Created by mohitd on 23-Feb-17.
 */

public class OrderDetailActivity extends AppCompatActivity {

    public static String ORDER_ID = "key_order_id";
    public static String AUTH_HASH = "key_auth_hash";
    private TextView txt_address_line1;
    private TextView txt_address_line2;
    private TextView txt_address_line3;
    private TextView txt_address_line4;
    private TextView txt_address_line5;
    private TextView txt_address_street;
    private TextView txt_delivery_time_line1;
    private TextView txt_delivery_time_line2;
    private TextView txt_payment_type;
    private TextView txt_region_name;
    private LinearLayout lyt_card_number;
    private TextView txt_card_no;
    private TextView txt_subtotal;
    private TextView txt_delivery_fee;
    private TextView txt_total_amount;
    private TextView txt_no_of_items;
    private TextView txt_order_status;
    private LoaderLayout loader;
    private RippleButton btn_track_order;
    private RippleButton btn_cancel;
    private TextView txt_order_captain_name;
    private TextView txt_order_captain_number;
    private LinearLayout lyt_order_captain;
    private TextView txt_call;
    private TextView txt_pay_by_card;
    private TextView txt_pay_by_credits, txt_paid_in_cash;

    private RecyclerView recyl_products;
    private Context context;
    private String order_id;
    private String auth_hash;
    private OrderData orderData;


    //Promo code
    private RelativeLayout promoLyt;
    private TextView txt_promo_amt_total;


    private ApiInterface apiService;
    private DateTimeFormatter formatter_time = DateTimeFormat.forPattern("HH:mm:ss");
   // private DateTimeFormatter formatter_time1 = DateTimeFormat.forPattern("h a");
    private DateTimeFormatter formatter_time1 = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter formatter_date = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTimeFormatter formatter_date1 = DateTimeFormat.forPattern("EEEE, d MMMM, yyyy");

    private Toolbar toolbar;
    private TextView txt_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_review_order);

        context = this;
        apiService = ApiClient.createService(ApiInterface.class, this);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            txt_title.setText(getResources().getString(R.string.order_detail).toUpperCase());
        }

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            formatter_time = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("en"));
           // formatter_time1 = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("en"));
            formatter_time1 = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("en"));
            formatter_date = (DateTimeFormat.forPattern("yyyy-MM-dd")).withLocale(new Locale("en"));
            formatter_date1 = (DateTimeFormat.forPattern("EEEE, d MMMM, yyyy")).withLocale(new Locale("en"));
        } else {
            formatter_time = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("ar"));
            //formatter_time1 = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("ar"));
            formatter_time1 = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("ar"));
            formatter_date = (DateTimeFormat.forPattern("yyyy-MM-dd")).withLocale(new Locale("ar"));
            formatter_date1 = (DateTimeFormat.forPattern("EEEE, d MMMM, yyyy")).withLocale(new Locale("ar"));
        }

        txt_address_line1 = findViewById(R.id.order_detail_txt_address_line1);
        txt_address_line2 = findViewById(R.id.order_detail_txt_address_line2);
        txt_address_line3 = findViewById(R.id.order_detail_txt_address_line3);
        txt_address_line4 = findViewById(R.id.order_detail_txt_address_line4);
        txt_address_line5 = findViewById(R.id.order_detail_txt_address_line5);
        txt_address_street = findViewById(R.id.order_detail_txt_address_street);
        txt_delivery_time_line1 = findViewById(R.id.order_detail_txt_delivery_time_line1);
        txt_delivery_time_line2 = findViewById(R.id.order_detail_txt_delivery_time_line2);
        txt_payment_type = findViewById(R.id.order_detail_txt_payment_type);
        txt_region_name = findViewById(R.id.order_detail_txt_region_name);
        lyt_card_number = findViewById(R.id.order_detail_lyt_card_number);
        txt_card_no = findViewById(R.id.order_detail_txt_card_no);
        txt_subtotal = findViewById(R.id.order_detail_txt_subtotal);

        //promo
        txt_promo_amt_total = findViewById(R.id.summary_txt_coupon_total);
        promoLyt = findViewById(R.id.promo_lyt);
        promoLyt.setVisibility(View.GONE);


        txt_delivery_fee = findViewById(R.id.order_detail_txt_delivery_fee);
        txt_total_amount = findViewById(R.id.order_detail_txt_total_amount);
        txt_no_of_items = findViewById(R.id.order_detail_txt_no_items);
        txt_order_status = findViewById(R.id.order_detail_txt_order_status);
        loader = findViewById(R.id.order_detail_loader);
        btn_track_order = findViewById(R.id.order_detail_btn_track_order);
        btn_cancel = findViewById(R.id.order_detail_btn_cancel);
        txt_order_captain_name = findViewById(R.id.order_detail_txt_order_captain_name);
        txt_order_captain_number = findViewById(R.id.order_detail_txt_order_captain_no);
        lyt_order_captain = findViewById(R.id.order_detail_lyt_order_captain);
        txt_call = findViewById(R.id.order_detail_txt_call);
        txt_pay_by_card = findViewById(R.id.order_detail_txt_pay_by_card);
        txt_pay_by_credits = findViewById(R.id.order_detail_txt_pay_by_credits);
        txt_paid_in_cash = findViewById(R.id.order_detail_txt_paid_in_cash);

        recyl_products = findViewById(R.id.order_detail_recyl_products);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyl_products.setLayoutManager(linearLayoutManager);
        recyl_products.setNestedScrollingEnabled(false);

        //recyclerViewAdapter = new CartListRecyclerViewAdapter(OrderDetailActivity.this, null);
        //recyclerView.setAdapter(recyclerViewAdapter);

        loader.setStatuText(getString(R.string.no_order_information_available));

        if (getIntent() != null && getIntent().hasExtra(AUTH_HASH)) {
            auth_hash = getIntent().getStringExtra(AUTH_HASH);
        }

        if (getIntent() != null && getIntent().hasExtra(ORDER_ID)) {
            order_id = getIntent().getStringExtra(ORDER_ID);
            if (!ValidationUtil.isNullOrBlank(order_id)) {
                loadOrderDetail();
            } else {
                loader.showStatusText();
            }
        } else {
            loader.showStatusText();
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderData != null) {
                    final AppDialogDoubleAction confirmationDialog = new AppDialogDoubleAction(context, getString(R.string.app_name), getString(R.string.order_cancel_confrmation), getString(R.string.cancel), getString(R.string.ok));
                    confirmationDialog.show();
                    confirmationDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                        @Override
                        public void onLeftActionClick(View view) {
                            confirmationDialog.dismiss();
                        }

                        @Override
                        public void onRightActionClick(View view) {
                            confirmationDialog.dismiss();
                            cancelOrder();
                        }
                    });
                }
            }
        });

        btn_track_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orderData != null && orderData.getOrderCaptain() != null) {
                    Intent trackOrderIntent = new Intent(context, TrackOrderCaptainActivity.class);
                    trackOrderIntent.putExtra(TrackOrderCaptainActivity.KEY_ORDER_CAPTAIN, orderData.getOrderCaptain());
                    startActivity(trackOrderIntent);
                }
            }
        });

        txt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderData != null && orderData.getOrderCaptain() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orderData.getOrderCaptain().getMobileNo(), null));
                    startActivity(intent);
                }
            }
        });

    }

    private void cancelOrder() {

        try {
            JSONObject cancel_order_request_data = new JSONObject();
            cancel_order_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            cancel_order_request_data.put("order_id", order_id);
            cancel_order_request_data.put("auth_hash", auth_hash);
            cancel_order_request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            if (NetworkUtil.networkStatus(this)) {
                try {
                    loader.showProgress();
                    Call<ResponseBody> call = apiService.cancelOrder((new ConvertJsonToMap().jsonToMap(cancel_order_request_data)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loader.showContent();

                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(context, AppConstants.BLANK_STRING, responseMessage, getString(R.string.ok));
                                            appDialogSingleAction.show();
                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                    finish();
                                                }
                                            });

                                        } else {
                                            AppUtil.showErrorDialog(context, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(context, getString(R.string.error_msg) + "(Err-676)");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(context, getString(R.string.error_msg) + "(Err-677)");
                                }
                            } else {
                                AppUtil.showErrorDialog(context, getString(R.string.error_msg) + "(Err-678)");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
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

    private void loadOrderDetail() {

        try {
            JSONObject load_order_request_data = new JSONObject();
            load_order_request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            load_order_request_data.put("order_id", order_id);
            load_order_request_data.put("auth_hash", auth_hash);
            load_order_request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            if (NetworkUtil.networkStatus(this)) {
                try {
                    loader.showProgress();
                    Call<ResponseBody> call = apiService.loadOrderDetail((new ConvertJsonToMap().jsonToMap(load_order_request_data)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loader.showContent();

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
                                            if (dataObj != null) {
                                                orderData = new OrderData();

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("delivery_date"))) {
                                                    DateTime date = formatter_date.parseDateTime(dataObj.optString("delivery_date"));
                                                    orderData.setDeliveryDate(formatter_date1.print(date));
                                                } else {
                                                    orderData.setDeliveryDate(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("order_mobile"))) {
                                                    orderData.setOrderMobileNo(dataObj.optString("order_mobile"));
                                                } else {
                                                    orderData.setOrderMobileNo(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("order_country_code"))) {
                                                    orderData.setOrderCountryCode(dataObj.optString("order_country_code"));
                                                } else {
                                                    orderData.setOrderCountryCode(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("paid_by_user"))) {
                                                    orderData.setAmountPaidByUser(dataObj.optString("paid_by_user"));
                                                } else {
                                                    orderData.setAmountPaidByUser(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("paid_by_credits"))) {
                                                    orderData.setAmountPaidByCredits(dataObj.optString("paid_by_credits"));
                                                } else {
                                                    orderData.setAmountPaidByCredits(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("warehouse_name"))) {
                                                    orderData.setRegionName(dataObj.optString("warehouse_name"));
                                                } else {
                                                    orderData.setRegionName(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("order_payment_type"))) {
                                                    if (dataObj.optString("order_payment_type").equalsIgnoreCase("1")) {
                                                        orderData.setPaymentType(getString(R.string.cod).toUpperCase());
                                                    } else if (dataObj.optString("order_payment_type").equalsIgnoreCase("2")) {
                                                        orderData.setPaymentType(getString(R.string.card).toUpperCase());
                                                    } else if (dataObj.optString("order_payment_type").equalsIgnoreCase("3")) {
                                                        orderData.setPaymentType(getString(R.string.tgs_credit).toUpperCase());
                                                    } else if (dataObj.optString("order_payment_type").equalsIgnoreCase("4")) {
                                                        orderData.setPaymentType(getString(R.string.card_and_credits).toUpperCase());
                                                    } else {
                                                        orderData.setPaymentType(getString(R.string.cash_and_credits).toUpperCase());
                                                    }
                                                } else {
                                                    orderData.setPaymentType(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("order_card_masked_pan"))) {
                                                    orderData.setCardNo(dataObj.optString("order_card_masked_pan"));
                                                } else {
                                                    orderData.setCardNo(AppConstants.BLANK_STRING);
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("order_total_item"))) {
                                                    orderData.setNoOfItems(dataObj.optString("order_total_item"));
                                                } else {
                                                    orderData.setNoOfItems(getString(R.string.na));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObj.optString("order_status"))) {
                                                    if (dataObj.optString("order_status").equalsIgnoreCase("1")) {

                                                        orderData.setOrderStatus(getString(R.string.acknowledge).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("2")) {

                                                        orderData.setOrderStatus(getString(R.string.picking).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("3")) {

                                                        orderData.setOrderStatus(getString(R.string.picked).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("4")) {

                                                        orderData.setOrderStatus(getString(R.string.packing).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("5")) {

                                                        orderData.setOrderStatus(getString(R.string.packed).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("6")) {

                                                        orderData.setOrderStatus(getString(R.string.out_for_delivery).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("7")) {

                                                        orderData.setOrderStatus(getString(R.string.delivered).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("9")) {

                                                        orderData.setOrderStatus(getString(R.string.picking).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("10")) {

                                                        orderData.setOrderStatus(getString(R.string.cancelled).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("12")) {

                                                        orderData.setOrderStatus(getString(R.string.packing).toUpperCase());

                                                    } else if (dataObj.optString("order_status").equalsIgnoreCase("13")) {

                                                        orderData.setOrderStatus(getString(R.string.returned).toUpperCase());
                                                    }
                                                } else {
                                                    orderData.setNoOfItems(getString(R.string.na));
                                                }

                                                if (dataObj.optJSONObject("order_charge_info") != null) {

                                                    JSONObject chargeObj = dataObj.optJSONObject("order_charge_info");

                                                    if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("subtotal"))) {
                                                            orderData.setOrderSubTotal(getString(R.string.egp) + AppConstants.SPACE + chargeObj.optString("subtotal"));
                                                        } else {
                                                            orderData.setOrderSubTotal(getString(R.string.na));
                                                        }


                                                        //"coupon_amount":"16.25",
                                                        //"coupon_code":"Bakery10"
                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("coupon_amount"))) {
                                                            orderData.setOrderPromoDiscount(getString(R.string.egp) + AppConstants.SPACE + chargeObj.optString("coupon_amount"));
                                                        } else {
                                                            orderData.setOrderPromoDiscount(getString(R.string.na));
                                                        }


                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("coupon_code"))) {
                                                            orderData.setOrderCouponCode(getString(R.string.egp) + AppConstants.SPACE + chargeObj.optString("coupon_code"));
                                                        } else {
                                                            orderData.setOrderCouponCode(getString(R.string.na));
                                                        }


                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("delivery_fee"))) {
                                                            orderData.setOrderDeliveryFee(getString(R.string.egp) + AppConstants.SPACE + chargeObj.optString("delivery_fee"));
                                                        } else {
                                                            orderData.setOrderDeliveryFee(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("total_amount"))) {
                                                            orderData.setOrderAmount(getString(R.string.egp) + AppConstants.SPACE + chargeObj.optString("total_amount"));
                                                        } else {
                                                            orderData.setOrderAmount(getString(R.string.na));
                                                        }
                                                    } else {
                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("subtotal"))) {
                                                            orderData.setOrderSubTotal(chargeObj.optString("subtotal") + AppConstants.SPACE + getString(R.string.egp));
                                                        } else {
                                                            orderData.setOrderSubTotal(getString(R.string.na));
                                                        }

                                                        //"coupon_amount":"16.25",
                                                        //"coupon_code":"Bakery10"
                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("coupon_amount"))) {
                                                            orderData.setOrderPromoDiscount(chargeObj.optString("coupon_amount") + AppConstants.SPACE + getString(R.string.egp));
                                                        } else {
                                                            orderData.setOrderPromoDiscount(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("coupon_code"))) {
                                                            orderData.setOrderCouponCode(chargeObj.optString("coupon_code") + AppConstants.SPACE + getString(R.string.egp));
                                                        } else {
                                                            orderData.setOrderCouponCode(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("delivery_fee"))) {
                                                            orderData.setOrderDeliveryFee(chargeObj.optString("delivery_fee") + AppConstants.SPACE + getString(R.string.egp));
                                                        } else {
                                                            orderData.setOrderDeliveryFee(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(chargeObj.optString("total_amount"))) {
                                                            orderData.setOrderAmount(chargeObj.optString("total_amount") + AppConstants.SPACE + getString(R.string.egp));
                                                        } else {
                                                            orderData.setOrderAmount(getString(R.string.na));
                                                        }
                                                    }
                                                }

                                                if (dataObj.optJSONObject("delivery_time_slot") != null) {

                                                    JSONObject timeSlotObj = dataObj.optJSONObject("delivery_time_slot");
                                                    if (timeSlotObj != null) {

                                                        TimeSlot timeSlot = new TimeSlot();
                                                        timeSlot.setId(timeSlotObj.optString("id"));

                                                        if (!ValidationUtil.isNullOrBlank(timeSlotObj.optString("start_time"))) {

                                                            if (timeSlotObj.optString("start_time").equalsIgnoreCase("24:00:00")) {
                                                                DateTime startTime = formatter_time.parseDateTime("00:00:00");
                                                                timeSlot.setStartTime(startTime);
                                                            } else {
                                                                DateTime startTime = formatter_time.parseDateTime(timeSlotObj.optString("start_time"));
                                                                timeSlot.setStartTime(startTime);
                                                            }
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(timeSlotObj.optString("end_time"))) {

                                                            if (timeSlotObj.optString("end_time").equalsIgnoreCase("24:00:00")) {
                                                                DateTime endTime = formatter_time.parseDateTime("00:00:00");
                                                                timeSlot.setEndTime(endTime);
                                                            } else {
                                                                DateTime endTime = formatter_time.parseDateTime(timeSlotObj.optString("end_time"));
                                                                timeSlot.setEndTime(endTime);
                                                            }
                                                        }

                                                        orderData.setDeliveryTimeSlot(timeSlot);

                                                        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")) {

                                                            String start = formatter_time1.print(timeSlot.getStartTime());
                                                            if (start.contains("ص")) {
                                                                start = start.replace("ص", getString(R.string.am));
                                                            } else if (start.contains("م")) {
                                                                start = start.replace("م", getString(R.string.pm));
                                                            }

                                                            String end = formatter_time1.print(timeSlot.getEndTime());
                                                            if (end.contains("ص")) {
                                                                end = end.replace("ص", getString(R.string.am));
                                                            } else if (end.contains("م")) {
                                                                end = end.replace("م", getString(R.string.pm));
                                                            }

                                                            orderData.setDeliveryTimeInString(String.format(
                                                                    getString(R.string.time_slot_format),
                                                                    start, end));
                                                        } else {
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

                                                if (dataObj.optJSONObject("order_captain_info") != null) {

                                                    JSONObject orderCaptainObj = dataObj.optJSONObject("order_captain_info");
                                                    if (orderCaptainObj != null) {

                                                        OrderCaptain orderCaptain = new OrderCaptain();

                                                        if (!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("id"))) {
                                                            orderCaptain.setId(orderCaptainObj.optString("id"));
                                                        } else {
                                                            orderCaptain.setId(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("name"))) {
                                                            orderCaptain.setName(orderCaptainObj.optString("name"));
                                                        } else {
                                                            orderCaptain.setName(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("mobile"))) {
                                                            orderCaptain.setMobileNo(orderCaptainObj.optString("mobile"));
                                                        } else {
                                                            orderCaptain.setMobileNo(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("country_code"))) {
                                                            orderCaptain.setCountryCode(orderCaptainObj.optString("country_code"));
                                                            if (orderCaptain.getCountryCode().startsWith("+")) {
                                                                orderCaptain.setCountryCode(orderCaptain.getCountryCode().replaceAll("\\+", ""));
                                                            }
                                                        } else {
                                                            orderCaptain.setCountryCode(getString(R.string.na));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("order_captain_lat"))) {
                                                            orderCaptain.setLatitude(orderCaptainObj.optDouble("order_captain_lat", 0.0f));
                                                        } else {
                                                            orderCaptain.setLatitude(0.0f);
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(orderCaptainObj.optString("order_captain_long"))) {
                                                            orderCaptain.setLongitude(orderCaptainObj.optDouble("order_captain_long", 0.0f));
                                                        } else {
                                                            orderCaptain.setLongitude(0.0f);
                                                        }

                                                        orderData.setOrderCaptain(orderCaptain);

                                                    } else {
                                                        orderData.setOrderCaptain(null);
                                                    }

                                                } else {
                                                    orderData.setOrderCaptain(null);
                                                }

                                                if (dataObj.optJSONObject("delivery_address") != null) {

                                                    JSONObject addressObj = dataObj.optJSONObject("delivery_address");
                                                    if (addressObj != null) {

                                                        ShippingAddress shippingAddress = new ShippingAddress();

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("floor_number"))) {
                                                            shippingAddress.setFloor_number(AppConstants.SPACE + addressObj.optString("floor_number"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("unit_number"))) {
                                                            shippingAddress.setUnit_number(addressObj.optString("unit_number"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("building_name"))) {
                                                            shippingAddress.setBuilding_name(addressObj.optString("building_name"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("area"))) {
                                                            shippingAddress.setArea(addressObj.optString("area"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("street"))) {
                                                            shippingAddress.setStreetName(addressObj.optString("street"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("city"))) {
                                                            shippingAddress.setCity(addressObj.optString("city"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("country"))) {
                                                            shippingAddress.setCountry(addressObj.optString("country"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("first_name"))) {
                                                            shippingAddress.setFirst_name(addressObj.optString("first_name"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("last_name"))) {
                                                            shippingAddress.setLast_name(addressObj.optString("last_name"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optString("address_reference"))) {
                                                            shippingAddress.setAddress_name(addressObj.optString("address_reference"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optDouble("latitude", 0.0f))) {
                                                            shippingAddress.setLatitude(addressObj.optDouble("latitude", 0.0f));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(addressObj.optDouble("longitude", 0.0f))) {
                                                            shippingAddress.setLongitude(addressObj.optDouble("longitude", 0.0f));
                                                        }

                                                        orderData.setShippingAddress(shippingAddress);

                                                    } else {
                                                        orderData.setShippingAddress(null);
                                                    }

                                                } else {
                                                    orderData.setShippingAddress(null);
                                                }

                                                ArrayList<Product> list_products = new ArrayList<>();
                                                if (dataObj.optJSONArray("product_list") != null) {
                                                    JSONArray productsArray = dataObj.optJSONArray("product_list");
                                                    if (productsArray != null && productsArray.length() > 0) {

                                                        for (int i = 0; i < productsArray.length(); i++) {
                                                            JSONObject productObj = productsArray.optJSONObject(i);
                                                            if (productObj != null) {
                                                                Product product = new Product();

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("english_product_name"))) {
                                                                    product.setEnglishName(productObj.optString("english_product_name"));
                                                                } else {
                                                                    product.setEnglishName(productObj.optString("name"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("arabic_product_name"))) {
                                                                    product.setArabicName(productObj.optString("arabic_product_name"));
                                                                } else {
                                                                    product.setArabicName(productObj.optString("name"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("english_product_size"))) {
                                                                    product.setEnglishQuantity(productObj.optString("english_product_size"));
                                                                } else {
                                                                    product.setEnglishQuantity(productObj.optString("product_size"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("arabic_product_size"))) {
                                                                    product.setArabicQuantity(productObj.optString("arabic_product_size"));
                                                                } else {
                                                                    product.setArabicQuantity(productObj.optString("product_size"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("english_brand_name"))) {
                                                                    product.setEnglishBrandName(productObj.optString("english_brand_name"));
                                                                } else {
                                                                    product.setEnglishBrandName(productObj.optString("product_brand_name"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("arabic_brand_name"))) {
                                                                    product.setArabicBrandName(productObj.optString("arabic_brand_name"));
                                                                } else {
                                                                    product.setArabicBrandName(productObj.optString("product_brand_name"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(productObj.optString("product_added_qty"))) {
                                                                    product.setSelectedQuantity(Integer.parseInt(productObj.optString("product_added_qty")));
                                                                } else {
                                                                    product.setSelectedQuantity(0);
                                                                }

                                                                product.setActualPrice(productObj.optString("product_price"));
                                                                product.setImage(productObj.optString("product_image"));
                                                                product.setId(productObj.optString("product_id"));

                                                                if (product.getSelectedQuantity() != 0) {
                                                                    list_products.add(product);
                                                                }
                                                            }
                                                        }

                                                        orderData.setListProducts(list_products);
                                                    }
                                                }

                                                setData(orderData);

                                            } else {
                                                loader.setStatuText(getString(R.string.no_order_information_available));
                                                loader.showStatusText();
                                                AppUtil.showErrorDialog(context, errorMsg);
                                            }

                                        } else {
                                            loader.setStatuText(getString(R.string.no_order_information_available));
                                            loader.showStatusText();
                                            AppUtil.showErrorDialog(context, errorMsg);
                                        }

                                    } else {
                                        loader.setStatuText(getString(R.string.no_order_information_available));
                                        loader.showStatusText();
                                        AppUtil.showErrorDialog(context, getString(R.string.error_msg) + "(Err-673)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    loader.setStatuText(getString(R.string.no_order_information_available));
                                    loader.showStatusText();
                                    AppUtil.showErrorDialog(context, getString(R.string.error_msg) + "(Err-674)");
                                }
                            } else {
                                loader.setStatuText(getString(R.string.no_order_information_available));
                                loader.showStatusText();
                                AppUtil.showErrorDialog(context, getString(R.string.error_msg) + "(Err-675)");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loader.showContent();
                            loader.setStatuText(getString(R.string.no_order_information_available));
                            loader.showStatusText();
                            AppUtil.hideProgress();
                        }
                    });
                } catch (JSONException e) {
                    loader.showContent();
                    loader.setStatuText(getString(R.string.no_order_information_available));
                    loader.showStatusText();
                    e.printStackTrace();
                }

            } else {
                loader.showContent();
                loader.setStatuText(getString(R.string.no_order_information_available));
                loader.showStatusText();
            }
        } catch (JSONException e) {
            loader.showContent();
            loader.setStatuText(getString(R.string.no_order_information_available));
            loader.showStatusText();
            e.printStackTrace();
        }
    }

    private void setData(OrderData orderData) {

        if (orderData != null) {

            if (orderData.getShippingAddress() != null) {

                String address_line2 = AppConstants.BLANK_STRING;
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getAddress_name())) {
                    address_line2 = orderData.getShippingAddress().getAddress_name();
                }

                if (!ValidationUtil.isNullOrBlank(address_line2)) {
                    txt_address_line1.setVisibility(View.VISIBLE);
                    txt_address_line1.setText(address_line2);
                } else {
                    txt_address_line1.setVisibility(View.GONE);
                }

                String address_line3 = AppConstants.BLANK_STRING;
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getFloor_number())) {
                    address_line3 = getString(R.string.floor) + AppConstants.SPACE + "# " + orderData.getShippingAddress().getFloor_number();
                }
                if (!ValidationUtil.isNullOrBlank(address_line3)) {
                    txt_address_line2.setVisibility(View.VISIBLE);
                    txt_address_line2.setText(address_line3);
                } else {
                    txt_address_line2.setVisibility(View.GONE);
                }

                String address_line4 = AppConstants.BLANK_STRING;
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getUnit_number())) {
                    address_line4 = getString(R.string.unit) + AppConstants.SPACE + "# " + orderData.getShippingAddress().getUnit_number();
                }
                if (!ValidationUtil.isNullOrBlank(address_line4)) {
                    txt_address_line3.setVisibility(View.VISIBLE);
                    txt_address_line3.setText(address_line4);
                } else {
                    txt_address_line3.setVisibility(View.GONE);
                }

                String address_line = AppConstants.BLANK_STRING;
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getBuilding_name())) {
                    address_line = orderData.getShippingAddress().getBuilding_name();
                }

                if (!ValidationUtil.isNullOrBlank(address_line)) {
                    txt_address_line4.setVisibility(View.VISIBLE);
                    txt_address_line4.setText(getString(R.string.building_name_num) + " " + address_line);
                } else {
                    txt_address_line4.setVisibility(View.GONE);
                }

                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getStreetName())) {
                    txt_address_street.setVisibility(View.VISIBLE);
                    txt_address_street.setText(getString(R.string.street) + " : " + orderData.getShippingAddress().getStreetName());
                } else {
                    txt_address_street.setVisibility(View.GONE);
                }

                String address_line5 = AppConstants.BLANK_STRING;
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getArea())) {
                    address_line5 = orderData.getShippingAddress().getArea();
                }
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getCity())) {
                    if (address_line5.length() == 0) {
                        address_line5 = orderData.getShippingAddress().getCity();
                    } else {
                        address_line5 = address_line5 + ", " + orderData.getShippingAddress().getCity();
                    }
                }
                if (!ValidationUtil.isNullOrBlank(orderData.getShippingAddress().getCountry())) {
                    if (address_line5.length() == 0) {
                        address_line5 = orderData.getShippingAddress().getCountry();
                    } else {
                        address_line5 = address_line5 + ", " + orderData.getShippingAddress().getCountry();
                    }
                }
                if (!ValidationUtil.isNullOrBlank(orderData.getOrderCountryCode())) {
                    if (address_line5.length() == 0) {
                        address_line5 = "+" + (orderData.getOrderCountryCode().replace("+", ""));
                    } else {
                        address_line5 = address_line5 + "\n+" + (orderData.getOrderCountryCode().replace("+", ""));
                    }
                }
                if (!ValidationUtil.isNullOrBlank(orderData.getOrderMobileNo())) {
                    if (address_line5.length() == 0) {
                        address_line5 = orderData.getOrderMobileNo();
                    } else {
                        address_line5 = address_line5 + " " + orderData.getOrderMobileNo();
                    }
                }

                if (!ValidationUtil.isNullOrBlank(address_line5)) {
                    txt_address_line5.setVisibility(View.VISIBLE);
                    txt_address_line5.setText(address_line5);
                } else {
                    txt_address_line5.setVisibility(View.GONE);
                }

            } else {
                txt_address_line1.setVisibility(View.GONE);
            }

            txt_delivery_time_line1.setText(orderData.getDeliveryDate());
            txt_delivery_time_line2.setText(orderData.getDeliveryTimeInString());

            txt_region_name.setText(orderData.getRegionName());

            txt_payment_type.setText(orderData.getPaymentType());
            if (orderData.getPaymentType().equalsIgnoreCase(getString(R.string.card))) {
                txt_card_no.setVisibility(View.VISIBLE);
                lyt_card_number.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(orderData.getCardNo())) {
                    txt_card_no.setVisibility(View.VISIBLE);
                    lyt_card_number.setVisibility(View.VISIBLE);
                    if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                        txt_card_no.setText(" : " + orderData.getCardNo());
                    } else {
                        txt_card_no.setText(orderData.getCardNo() + " : ");
                    }
                } else {
                    txt_card_no.setVisibility(View.GONE);
                    lyt_card_number.setVisibility(View.GONE);
                    txt_card_no.setText(orderData.getCardNo());
                }
                txt_pay_by_card.setVisibility(View.GONE);
                txt_pay_by_credits.setVisibility(View.GONE);
                txt_paid_in_cash.setVisibility(View.GONE);
            } else if (orderData.getPaymentType().equalsIgnoreCase(getString(R.string.card_and_credits))) {
                txt_card_no.setVisibility(View.VISIBLE);
                lyt_card_number.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(orderData.getCardNo())) {
                    txt_card_no.setVisibility(View.VISIBLE);
                    lyt_card_number.setVisibility(View.VISIBLE);
                    if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                        txt_card_no.setText(" : " + orderData.getCardNo());
                    } else {
                        txt_card_no.setText(orderData.getCardNo() + " : ");
                    }
                } else {
                    txt_card_no.setVisibility(View.GONE);
                    lyt_card_number.setVisibility(View.GONE);
                    txt_card_no.setText(orderData.getCardNo());
                }
                txt_pay_by_card.setVisibility(View.VISIBLE);
                txt_paid_in_cash.setVisibility(View.GONE);
                txt_pay_by_credits.setVisibility(View.VISIBLE);

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    txt_pay_by_card.setText(getString(R.string.paid_by_card) + " : " + getString(R.string.egp) + " " + orderData.getAmountPaidByUser());
                    txt_pay_by_credits.setText(getString(R.string.paid_by_credits) + " : " + getString(R.string.egp) + " " + orderData.getAmountPaidByCredits());
                } else {
                    txt_pay_by_card.setText(getString(R.string.paid_by_card) + " : " + orderData.getAmountPaidByUser() + AppConstants.SPACE + getString(R.string.egp));
                    txt_pay_by_credits.setText(getString(R.string.paid_by_credits) + " : " + orderData.getAmountPaidByCredits() + AppConstants.SPACE + getString(R.string.egp));
                }

            } else if (orderData.getPaymentType().equalsIgnoreCase(getString(R.string.cash_and_credits))) {
                txt_card_no.setVisibility(View.GONE);
                txt_pay_by_card.setVisibility(View.GONE);
                txt_paid_in_cash.setVisibility(View.VISIBLE);
                txt_pay_by_credits.setVisibility(View.VISIBLE);

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    txt_paid_in_cash.setText(getString(R.string.paid_in_cash) + " : " + getString(R.string.egp) + " " + orderData.getAmountPaidByUser());
                    txt_pay_by_credits.setText(getString(R.string.paid_by_credits) + " : " + getString(R.string.egp) + " " + orderData.getAmountPaidByCredits());
                } else {
                    txt_paid_in_cash.setText(getString(R.string.paid_in_cash) + " : " + orderData.getAmountPaidByUser() + AppConstants.SPACE + getString(R.string.egp));
                    txt_pay_by_credits.setText(getString(R.string.paid_by_credits) + " : " + orderData.getAmountPaidByCredits() + AppConstants.SPACE + getString(R.string.egp));
                }

            } else {
                txt_card_no.setVisibility(View.GONE);
                lyt_card_number.setVisibility(View.GONE);
                txt_pay_by_card.setVisibility(View.GONE);
                txt_pay_by_credits.setVisibility(View.GONE);
                txt_paid_in_cash.setVisibility(View.GONE);
            }

            txt_subtotal.setText(orderData.getOrderSubTotal());

            if (!ValidationUtil.isNullOrBlank(orderData.getOrderCouponCode())  && !orderData.getOrderCouponCode().equalsIgnoreCase(getString(R.string.na)))
                promoLyt.setVisibility(View.VISIBLE);
            else
                promoLyt.setVisibility(View.GONE);


            //Promo discount
            txt_promo_amt_total.setText(orderData.getOrderPromoDiscount());

            txt_delivery_fee.setText(orderData.getOrderDeliveryFee());
            txt_total_amount.setText(orderData.getOrderAmount());
            txt_no_of_items.setText(orderData.getNoOfItems());
            txt_order_status.setText(orderData.getOrderStatus());

            if (orderData.getOrderStatus().equalsIgnoreCase(getString(R.string.out_for_delivery))
                    && orderData.getOrderCaptain() != null) {

                lyt_order_captain.setVisibility(View.VISIBLE);
                txt_order_captain_name.setText(orderData.getOrderCaptain().getName());
                txt_order_captain_number.setText("+" + orderData.getOrderCaptain().getCountryCode() + AppConstants.SPACE + orderData.getOrderCaptain().getMobileNo());
                btn_track_order.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.GONE);

            } else {

                lyt_order_captain.setVisibility(View.GONE);
                if (orderData.getOrderStatus() != null && orderData.getOrderStatus().equalsIgnoreCase(getString(R.string.acknowledge))) {
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_track_order.setVisibility(View.GONE);
                } else {
                    btn_track_order.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.GONE);
                }
            }

            if (orderData.getListProducts() != null && orderData.getListProducts().size() > 0) {
                OrderProductListAdapter orderProductListAdapter = new OrderProductListAdapter(context, orderData.getListProducts());
                recyl_products.setAdapter(orderProductListAdapter);
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
