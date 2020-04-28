package thegroceryshop.com.checkoutProcess.checkoutfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.BuildConfig;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.CommonWebViewActivity;
import thegroceryshop.com.activity.HomeActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.TwoActionAlertDialogWithEditText;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.DeliveryCharges;
import thegroceryshop.com.orders.PaymentWebViewActivity;
import thegroceryshop.com.orders.ReviewCartActivity;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by jaikishang on 2/23/2017.
 */
@SuppressLint("ValidFragment")
public class OrdersSummaryFragment extends Fragment {
    private CheckOutProcessActivity checkOutProcessActivity;
    private TextView txt_total_items;
    private TextView txt_total_subtotal;
    private TextView txt_delivery_fee;
    private TextView txt_tax_charges;
    private TextView txt_total_amount;
    private TextView txt_edit_delivery_address;
    private TextView txt_address_name;
    private TextView txt_address_line1;
    private TextView txt_address_line2;
    private TextView txt_address_line3;
    private TextView txt_address_line4;
    private TextView txt_address_street;
    private TextView txt_address_contact;
    private TextView txt_edit_delivery_time;
    private TextView txt_time_line1;
    private TextView txt_time_line2;
    private TextView txt_door_step;
    private RippleButton btn_review_shopping_cart;
    private RippleButton btn_change_payment_method;
    private RippleButton btn_place_order;
    private TextView txt_payment_type;
    private RelativeLayout lyt_pay_by_card;
    private TextView txt_pay_by_card;
    private RelativeLayout lyt_pay_by_credits;
    private TextView txt_pay_by_credits;

    //Apply Coupon
    private Button applyCoupon;
    private EditText editCoupon;
    private RelativeLayout promoLyt;
    private TextView txt_promo_amt_total;

    private String promoCouponCode = "";
    private String promoCouponCodeId = "0";
    private String promoAmt = "0";
    private String promoCodeApplyOn = "";//1 Promo Code, 2 Free Delivery Code.


    private String paymentToken = "";
    private String databaseId = "";
    private JSONObject order;
    private boolean is_3d_secure = false;
    private boolean is_pending = false;

    DateTimeFormatter dateformatter = DateTimeFormat.forPattern("EEEE, dd MMMM, yyyy");
    //DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h a");
    DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    private ApiInterface apiService;
    private boolean isDeliveryChargesLoaded = false;
    private String cardCVV;
    private boolean isTransactionDeclined = false;
    private ArrayList<DeliveryCharges> list_charges = new ArrayList<>();
    private ArrayList<CartItem> updated_cart_list;

    private boolean isPlacingOrder = false;

    public OrdersSummaryFragment(CheckOutProcessActivity checkOutProcessActivity) {
        this.checkOutProcessActivity = checkOutProcessActivity;
    }

    public OrdersSummaryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, true);

        View view = inflater.inflate(R.layout.layout_order_summary, container, false);
        checkOutProcessActivity = (CheckOutProcessActivity) getActivity();

        apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
        txt_total_items = view.findViewById(R.id.summary_txt_total_items);
        txt_total_subtotal = view.findViewById(R.id.summary_txt_subtotal);
        txt_delivery_fee = view.findViewById(R.id.summary_txt_delivery_fee);
        txt_tax_charges = view.findViewById(R.id.summary_txt_tax_charges);
        txt_total_amount = view.findViewById(R.id.summary_txt_total_amount);
        txt_edit_delivery_address = view.findViewById(R.id.summary_txt_edit_address);
        txt_address_name = view.findViewById(R.id.summary_txt_address_name);
        txt_address_line1 = view.findViewById(R.id.summary_txt_address_line1);
        txt_address_line2 = view.findViewById(R.id.summary_txt_address_line2);
        txt_address_line3 = view.findViewById(R.id.summary_txt_address_line3);
        txt_address_line4 = view.findViewById(R.id.summary_txt_address_line4);
        txt_address_street = view.findViewById(R.id.summary_txt_address_street);
        txt_address_contact = view.findViewById(R.id.summary_txt_address_contact);
        txt_edit_delivery_time = view.findViewById(R.id.summary_txt_edt_delivery_time);
        txt_time_line1 = view.findViewById(R.id.summary_txt_time_line1);
        txt_time_line2 = view.findViewById(R.id.summary_txt_time_line2);
        btn_review_shopping_cart = view.findViewById(R.id.summary_btn_review_shopping_cart);
        btn_change_payment_method = view.findViewById(R.id.summary_btn_change_payment_method);
        btn_place_order = view.findViewById(R.id.summary_btn_place_order);
        txt_door_step = view.findViewById(R.id.summary_txt_door_step);
        txt_payment_type = view.findViewById(R.id.summary_txt_payment_type);
        lyt_pay_by_card = view.findViewById(R.id.summary_lyt_pay_by_card);
        txt_pay_by_card = view.findViewById(R.id.summary_txt_pay_by_card);
        lyt_pay_by_credits = view.findViewById(R.id.summary_lyt_pay_by_credits);
        txt_pay_by_credits = view.findViewById(R.id.summary_txt_pay_by_credits);


        //promo
        txt_promo_amt_total = view.findViewById(R.id.summary_txt_coupon_total);
        promoLyt = view.findViewById(R.id.promo_lyt);
        promoLyt.setVisibility(View.GONE);


        applyCoupon = view.findViewById(R.id.apply);
        editCoupon = view.findViewById(R.id.txtCoupon);
        //
        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            dateformatter = (DateTimeFormat.forPattern("EEEE, d MMMM, yyyy")).withLocale(new Locale("en"));
            //timeFormatter = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("en"));
            timeFormatter = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("en"));
        } else {
            dateformatter = (DateTimeFormat.forPattern("EEEE, d MMMM, yyyy")).withLocale(new Locale("ar"));
            //timeFormatter = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("ar"));
            timeFormatter = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("ar"));
        }

        loadSummary();

        txt_edit_delivery_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject request_data = new JSONObject();
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                    request_data.put("time_slot_id", checkOutProcessActivity.getOrder().getTimeSlot().getId());
                    request_data.put("date", checkOutProcessActivity.getOrder().getDeliveruDate().split(" ")[0]);
                    request_data.put("is_solt_book", "");

                    TimeSlotService.reserveOrReleaseTimeSlot(checkOutProcessActivity, request_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                checkOutProcessActivity.navigateTOAddress();

            }
        });

        txt_edit_delivery_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOutProcessActivity.navigateTODeliveryType();
            }
        });

        btn_review_shopping_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent reviewCartIntent = new Intent(checkOutProcessActivity, ReviewCartActivity.class);
                reviewCartIntent.putExtra(ReviewCartActivity.DELIVERY_TYPE, checkOutProcessActivity.getOrder().getDeliveyType());
                reviewCartIntent.putExtra(ReviewCartActivity.IS_USE_CREDITS, checkOutProcessActivity.getOrder().isUseTGSCredits());
                reviewCartIntent.putExtra(ReviewCartActivity.CREDITS_USED_FOR_ORDER, checkOutProcessActivity.getOrder().getAmountByTgsCredits());
                startActivityForResult(reviewCartIntent, CheckOutProcessActivity.REQUEST_REVIEW_CART);

            }
        });

        btn_change_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOutProcessActivity.navigateTOPayment();
            }
        });

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDeliveryChargesLoaded) {

                    if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("2") || checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")) {
                        final TwoActionAlertDialogWithEditText withEditText = new TwoActionAlertDialogWithEditText(
                                checkOutProcessActivity,
                                getString(R.string.enter_cvv),
                                getString(R.string.cvv_note),
                                getString(R.string.cancel_caps),
                                getString(R.string.ok));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            withEditText.edt_input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
                        }

                        withEditText.setOnTwoActionsListener(new TwoActionAlertDialogWithEditText.OnTwoActionAlertListener() {
                            @Override
                            public void onLeftActionClick(View view, String input) {
                                withEditText.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                try {
                                    // hides the soft keyboard when the drawer opens
                                    InputMethodManager inputManager = (InputMethodManager) checkOutProcessActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(withEditText.edt_input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                    withEditText.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    withEditText.dismiss();
                                }
                            }

                            @Override
                            public void onRightActionClick(View view, String input) {

                                try {
                                    // hides the soft keyboard when the drawer opens
                                    InputMethodManager inputManager = (InputMethodManager) checkOutProcessActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(withEditText.edt_input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                    withEditText.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    withEditText.dismiss();
                                }

                                cardCVV = input;
                                startPaymentProcessAPI();
                            }
                        });
                        withEditText.show();
                    } else {
                        orderCashOnDelivery(AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity())));
                    }
                }
            }
        });


        //Apply Coupon code
        applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPromoApi();
            }
        });


        editCoupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 1)
                    applyCoupon.setVisibility(View.VISIBLE);
                else
                    applyCoupon.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void callPromoApi() {

        String promoCode = editCoupon.getText().toString();
        if (!promoCode.isEmpty()) {
            setApplyCoupon(AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity())), promoCode);
        } else {
            promoCouponCodeId = "";
            promoCouponCode = "";
            promoAmt = "0";
            promoCodeApplyOn="";
            promoLyt.setVisibility(View.GONE);

            setPromoAmount();
        }
    }


    private void setPromoAmount() {
        try {


            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                //Sub total
                txt_total_subtotal.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getSubTotal()));

                //Promo Amt
                txt_promo_amt_total.setText("- " + getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + Float.parseFloat(promoAmt)));

                //total
                if(promoCodeApplyOn.equalsIgnoreCase("1")) {
                    txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay() - Float.parseFloat(promoAmt))));
                }else if(promoCodeApplyOn.equalsIgnoreCase("2")) {
                    txt_delivery_fee.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getDeliveryCharges()- Float.parseFloat(promoAmt))));
                    txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay())));
                }else{
                    txt_delivery_fee.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getDeliveryCharges()));
                    txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay())));
                }

            } else {
                //Sub total
                txt_total_subtotal.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getSubTotal()) + AppConstants.SPACE + getString(R.string.egp));

                //Promo Amt
                txt_promo_amt_total.setText("- " + AppUtil.mSetRoundUpPrice("" + Float.parseFloat(promoAmt)) + AppConstants.SPACE + getString(R.string.egp));

                //total
                //txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay() - Float.parseFloat(promoAmt))) + AppConstants.SPACE + getString(R.string.egp));

                //total
                if(promoCodeApplyOn.equalsIgnoreCase("1"))//Promo Discount
                     {
                    txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay() - Float.parseFloat(promoAmt)))+ AppConstants.SPACE + getString(R.string.egp));
                }else if(promoCodeApplyOn.equalsIgnoreCase("2")) //Delivery Discount
                {
                    txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getDeliveryCharges()- Float.parseFloat(promoAmt)))+ AppConstants.SPACE + getString(R.string.egp));
                    txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay()))+ AppConstants.SPACE + getString(R.string.egp));
                }else{
                    txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getDeliveryCharges()) + AppConstants.SPACE + getString(R.string.egp));
                    txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + (checkOutProcessActivity.getOrder().getTotalAmountToPay()))+ AppConstants.SPACE + getString(R.string.egp));
                }
            }
            // hide promo view
            if (Float.parseFloat(promoAmt) <= 0) {
                promoLyt.setVisibility(View.GONE);
            } else
                promoLyt.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (isPlacingOrder) {
            AppUtil.showProgress(getActivity());
        }
    }

    public ArrayList<CartItem> cloneList(ArrayList<CartItem> list) {
        if (list != null && list.size() > 0) {
            updated_cart_list = new ArrayList<>(list.size());
            for (CartItem cartItem : list)
                try {
                    updated_cart_list.add((CartItem) cartItem.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            return updated_cart_list;
        }
        return null;
    }

    private void setApplyCoupon(JSONObject orderObject, final String promoCode) {

        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {

            if (orderObject != null) {
                try {
                    JSONObject order_charges = orderObject.optJSONObject("order_charges");
                    JSONArray order_products = orderObject.optJSONArray("order_product");
                    orderObject.put("coupon_code", promoCode);
                    float subtotal = 0.0f, total_amount = 0.0f, payment_by_user = 0.0f;
                    int total_item = 0;

                    if (order_charges.optString("subtotal") != null) {
                        subtotal = (order_charges.optString("subtotal").length() > 0 ? Float.parseFloat(order_charges.getString("subtotal")) : 0.0f);
                    }

                    if (order_charges.optString("total_amount") != null) {
                        total_amount = (order_charges.optString("total_amount").length() > 0 ? Float.parseFloat(order_charges.getString("total_amount")) : 0.0f);
                    }

                    if (order_charges.optString("payment_by_user") != null) {
                        payment_by_user = (order_charges.optString("payment_by_user").length() > 0 ? Float.parseFloat(order_charges.getString("payment_by_user")) : 0.0f);
                    }

                    if (order_charges.optString("total_item") != null) {
                        total_item = (order_charges.getString("total_item").length() > 0 ? Integer.parseInt(order_charges.getString("total_item")) : 0);
                    }

                    if (subtotal == 0.0f
                            || total_amount == 0.0f
                            || payment_by_user == 0.0f
                            || total_item == 0
                            || order_products == null
                            || order_products.length() == 0) {

                        checkOutProcessActivity.getOrder().getList_cart().clear();
                        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, true);
                    }

                    AppUtil.showProgress(checkOutProcessActivity);
                    Call<ResponseBody> call = apiService.applyCoupon((new ConvertJsonToMap().jsonToMap(orderObject)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            AppUtil.hideProgress();
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        // {"response":{"data":{"coupon_code":"Bakery10","coupon_amount":"11.03"},
                                        // "status_code":200,"status":"OK",
                                        // "success_message":"Coupon Code applied successfully"}}


                                        //{"response":{"data":{"coupon_code":"Bakery10","coupon_amount":"11.03"},
                                        // "status_code":200,"status":"OK","success_message":"Coupon Code applied successfully"}}


                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject res = resObj.getJSONObject("data");
                                            promoCouponCode = res.optString("coupon_code");
                                            promoAmt = res.optString("coupon_amount");
                                            promoCouponCodeId = res.optString("coupon_code_id");
                                            promoCodeApplyOn = res.optString("promo_code_apply_on");
                                            //Set Amount
                                            setPromoAmount();
                                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, responseMessage, getString(R.string.ok));
                                        } else if (statusCode == 1001) {


                                        } else if (statusCode == 1002) {


                                        } else if (statusCode == 1003) {


                                        } else if (statusCode == 305) {
                                            promoCouponCode = "";
                                            promoCouponCodeId = "0";
                                            promoAmt = "0";
                                            promoCodeApplyOn = "";
                                            promoLyt.setVisibility(View.GONE);
                                            setPromoAmount();
                                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, errorMsg, getString(R.string.ok));

                                        } else {

                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-667)", getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-668)", getString(R.string.ok));
                                }
                            } else {

                                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-669)", getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            isPlacingOrder = false;
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-670)", getString(R.string.ok));
                        }
                    });
                } catch (JSONException e) {

                    AppUtil.hideProgress();
                    e.printStackTrace();
                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-671)", getString(R.string.ok));
                }

            } else {
                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-743)", getString(R.string.ok));
            }

        } else {
            AppUtil.hideProgress();
            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-672)", getString(R.string.ok));
        }

    }


    private void orderCashOnDelivery(JSONObject orderObject) {

        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {

            if (orderObject != null) {

                try {
                    isDeliveryChargesLoaded = false;
                    isPlacingOrder = true;

                    JSONObject order_charges = orderObject.optJSONObject("order_charges");
                    JSONArray order_products = orderObject.optJSONArray("order_product");
                    float subtotal = 0.0f, total_amount = 0.0f, payment_by_user = 0.0f;
                    int total_item = 0;

                    if (order_charges.optString("subtotal") != null) {
                        subtotal = (order_charges.optString("subtotal").length() > 0 ? Float.parseFloat(order_charges.getString("subtotal")) : 0.0f);
                    }

                    if (order_charges.optString("total_amount") != null) {
                        total_amount = (order_charges.optString("total_amount").length() > 0 ? Float.parseFloat(order_charges.getString("total_amount")) : 0.0f);
                    }

                    if (order_charges.optString("payment_by_user") != null) {
                        payment_by_user = (order_charges.optString("payment_by_user").length() > 0 ? Float.parseFloat(order_charges.getString("payment_by_user")) : 0.0f);
                    }

                    if (order_charges.optString("total_item") != null) {
                        total_item = (order_charges.getString("total_item").length() > 0 ? Integer.parseInt(order_charges.getString("total_item")) : 0);
                    }

                    if (subtotal == 0.0f
                            || total_amount == 0.0f
                            || payment_by_user == 0.0f
                            || total_item == 0
                            || order_products == null
                            || order_products.length() == 0) {

                        checkOutProcessActivity.getOrder().getList_cart().clear();
                        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, true);
                    }

                    AppUtil.showProgress(checkOutProcessActivity);

                    //Add Coupon details
                    orderObject.put("promo_code_apply_on", promoCodeApplyOn);
                    orderObject.put("coupon_code_id", promoCouponCodeId);
                    orderObject.put("coupon_code", promoCouponCode);
                    orderObject.put("coupon_amount", promoAmt);

                    Call<ResponseBody> call = apiService.placeOrder((new ConvertJsonToMap().jsonToMap(orderObject)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            isPlacingOrder = false;
                            OnlineMartApplication.loadCreditsInWalltet();
                            AppUtil.hideProgress();
                            isDeliveryChargesLoaded = true;
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            loadCreditsInWalltet();
                                            OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                            OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                            OnlineMartApplication.getCartList().clear();

                                            String responseMessage = resObj.optString("success_message");

                                            AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), responseMessage, getString(R.string.ok));
                                            mSingleActionAlertDialog.show();
                                            mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    Intent intentStore = new Intent(checkOutProcessActivity, HomeActivity.class);
                                                    intentStore.putExtra("isFromInternal", true);
                                                    intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intentStore);

                                                    checkOutProcessActivity.finish();
                                                }
                                            });

                                        } else if (statusCode == 1001) {

                                            JSONArray productsArray = (JSONArray) resObj.optJSONArray("produts");

                                            if (productsArray != null && productsArray.length() > 0) {
                                                for (int i = 0; i < productsArray.length(); i++) {
                                                    JSONObject object = (JSONObject) productsArray.get(i);
                                                    for (int j = 0; j < OnlineMartApplication.getCartList().size(); j++) {
                                                        if (OnlineMartApplication.getCartList().get(j).getId().equalsIgnoreCase(object.optString("product_id"))) {
                                                            if (object.optInt("availableQty") == 0) {
                                                                OnlineMartApplication.getCartList().get(j).setRemovedFromCart(true);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            updated_cart_list = cloneList(OnlineMartApplication.getCartList());
                                            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                                if (OnlineMartApplication.getCartList().get(i).shouldRemovedFromCart()) {
                                                    for (int j = 0; j < updated_cart_list.size(); j++) {
                                                        if (OnlineMartApplication.getCartList().get(i).getId().equalsIgnoreCase(updated_cart_list.get(j).getId())) {
                                                            updated_cart_list.remove(j);
                                                        }
                                                    }
                                                }
                                            }

                                            if (productsArray != null && productsArray.length() > 0) {
                                                for (int i = 0; i < productsArray.length(); i++) {
                                                    JSONObject object = (JSONObject) productsArray.get(i);
                                                    for (int j = 0; j < updated_cart_list.size(); j++) {
                                                        if (updated_cart_list.get(j).getId().equalsIgnoreCase(object.optString("product_id"))) {
                                                            updated_cart_list.get(j).setMaxQuantity(object.optInt("availableQty", 0));
                                                            updated_cart_list.get(j).setSelectedQuantity(object.optInt("availableQty", 0));
                                                        }
                                                    }
                                                }
                                            }

                                            final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.review_cart), getString(R.string.proceed));
                                            appDialogDoubleAction.show();
                                            appDialogDoubleAction.setCancelable(false);
                                            appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                            appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                @Override
                                                public void onLeftActionClick(View view) {
                                                    appDialogDoubleAction.dismiss();

                                                    Intent reviewCartIntent = new Intent(checkOutProcessActivity, ReviewCartActivity.class);
                                                    reviewCartIntent.putExtra(ReviewCartActivity.DELIVERY_TYPE, checkOutProcessActivity.getOrder().getDeliveyType());
                                                    reviewCartIntent.putExtra(ReviewCartActivity.IS_USE_CREDITS, checkOutProcessActivity.getOrder().isUseTGSCredits());
                                                    reviewCartIntent.putExtra(ReviewCartActivity.CREDITS_USED_FOR_ORDER, checkOutProcessActivity.getOrder().getAmountByTgsCredits());
                                                    startActivityForResult(reviewCartIntent, CheckOutProcessActivity.REQUEST_REVIEW_CART);
                                                }

                                                @Override
                                                public void onRightActionClick(View view) {
                                                    appDialogDoubleAction.dismiss();

                                                    float subtotal = 0.0f;
                                                    float savings = 0.0f;
                                                    float total_amount = 0.0f;
                                                    int shippingHours = 0;
                                                    boolean isShippingThirdParty = false;

                                                    boolean isDoorStepFinal = false;
                                                    if (updated_cart_list != null && updated_cart_list.size() > 0) {
                                                        for (int i = 0; i < updated_cart_list.size(); i++) {

                                                            CartItem cartItem = updated_cart_list.get(i);

                                                            if (!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())) {
                                                                subtotal = subtotal + (Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                                                            }

                                                            if (cartItem.isOffer()) {
                                                                savings = savings + (Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                                                            }

                                                            if (updated_cart_list.get(i).isShippingThirdParty()) {
                                                                isShippingThirdParty = true;
                                                            }

                                                            if (updated_cart_list.get(i).getShippingHours() > shippingHours) {
                                                                shippingHours = updated_cart_list.get(i).getShippingHours();
                                                            }

                                                            if (!updated_cart_list.get(i).isDoorStepDelivery()) {
                                                                checkOutProcessActivity.getOrder().setDoorStepAvailable(false);
                                                                isDoorStepFinal = true;
                                                            } else {
                                                                if (!isDoorStepFinal) {
                                                                    checkOutProcessActivity.getOrder().setDoorStepAvailable(true);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    checkOutProcessActivity.getOrder().setOrderShippingHours(shippingHours);
                                                    checkOutProcessActivity.getOrder().setShippinghirdParty(isShippingThirdParty);

                                                    float delivery_charges = getDeliveryCharges(subtotal);
                                                    total_amount = subtotal + delivery_charges;
                                                    checkOutProcessActivity.getOrder().setTotalAmountToPay(total_amount);
                                                    checkOutProcessActivity.getOrder().setTotalSavings(savings);
                                                    checkOutProcessActivity.getOrder().setDeliveryCharges(delivery_charges);
                                                    checkOutProcessActivity.getOrder().setAmountByUser(total_amount);

                                                    if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")
                                                            || checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("5")) {

                                                        if (total_amount <= checkOutProcessActivity.getOrder().getAmountByTgsCredits()) {
                                                            checkOutProcessActivity.getOrder().setPaymentMode("3");
                                                            checkOutProcessActivity.getOrder().setAmountByTgsCredits(total_amount);
                                                            checkOutProcessActivity.getOrder().setAmountByUser(0);
                                                        } else {
                                                            float amountPayByUser = checkOutProcessActivity.getOrder().getTotalAmountToPay() - checkOutProcessActivity.getOrder().getAmountByTgsCredits();
                                                            checkOutProcessActivity.getOrder().setAmountByTgsCredits(total_amount);
                                                            checkOutProcessActivity.getOrder().setAmountByUser(amountPayByUser);
                                                        }

                                                    }

                                                    orderCashOnDelivery(AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity()), updated_cart_list));
                                                }
                                            });

                                        } else if (statusCode == 1002) {

                                            final AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.ok));
                                            mSingleActionAlertDialog.show();
                                            mSingleActionAlertDialog.setCancelable(false);
                                            mSingleActionAlertDialog.setCanceledOnTouchOutside(false);
                                            mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    mSingleActionAlertDialog.dismiss();
                                                    checkOutProcessActivity.navigateTODeliveryType1();
                                                }
                                            });

                                        } else if (statusCode == 1003) {

                                            final AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.payment_setting));
                                            mSingleActionAlertDialog.show();
                                            mSingleActionAlertDialog.setCancelable(false);
                                            mSingleActionAlertDialog.setCanceledOnTouchOutside(false);
                                            mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    mSingleActionAlertDialog.dismiss();
                                                    checkOutProcessActivity.navigateTOPayment();
                                                }
                                            });

                                        } else if (statusCode == 305) {

                                            OnlineMartApplication.openRegionPicker(getActivity(), errorMsg);

                                        } else {
                                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, errorMsg, getString(R.string.ok));
                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-667)", getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-668)", getString(R.string.ok));
                                }
                            } else {
                                //AppUtil.showErrorDialog(mContext, response.message());
                                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-669)", getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            isPlacingOrder = false;
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-670)", getString(R.string.ok));
                        }
                    });
                } catch (JSONException e) {
                    isPlacingOrder = false;
                    AppUtil.hideProgress();
                    e.printStackTrace();
                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-671)", getString(R.string.ok));
                }

            } else {
                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-743)", getString(R.string.ok));
            }

        } else {
            isPlacingOrder = false;
            AppUtil.hideProgress();
            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-672)", getString(R.string.ok));
        }
    }

    private void loadSummary() {

        updateShippingCharges(checkOutProcessActivity.getOrder().getTotalAmountToPay());

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                String name = AppConstants.BLANK_STRING;

                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getAddress_name())) {
                    name = checkOutProcessActivity.getOrder().getAddress().getAddress_name();
                }

                if (!ValidationUtil.isNullOrBlank(name)) {
                    txt_address_name.setText(name);
                } else {
                    txt_address_name.setText(getString(R.string.na));
                }

                if (!ValidationUtil.isNullOrBlank("")) {
                    txt_address_line1.setVisibility(View.VISIBLE);
                    txt_address_line1.setText(checkOutProcessActivity.getOrder().getAddress().getAddress_instruction());
                } else {
                    txt_address_line1.setVisibility(View.GONE);
                }

                String address_line6 = getString(R.string.building_name_num);
                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getBuilding_name())) {
                    address_line6 = address_line6 + " " + checkOutProcessActivity.getOrder().getAddress().getBuilding_name();
                }
                if (!address_line6.equalsIgnoreCase(getString(R.string.building_name_num))) {
                    txt_address_line4.setVisibility(View.VISIBLE);
                    txt_address_line4.setText(address_line6);
                } else {
                    txt_address_line4.setVisibility(View.GONE);
                }

                String address_line3 = AppConstants.BLANK_STRING;
                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getFloor_number())) {
                    address_line3 = getString(R.string.floor) + " # " + checkOutProcessActivity.getOrder().getAddress().getFloor_number();
                }
                if (!ValidationUtil.isNullOrBlank(address_line3)) {
                    txt_address_line2.setVisibility(View.VISIBLE);
                    txt_address_line2.setText(address_line3);
                } else {
                    txt_address_line2.setVisibility(View.GONE);
                }

                String address_line4 = AppConstants.BLANK_STRING;
                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getUnit_number())) {
                    address_line4 = getString(R.string.unit) + " # " + checkOutProcessActivity.getOrder().getAddress().getUnit_number();
                }
                if (!ValidationUtil.isNullOrBlank(address_line4)) {
                    txt_address_line3.setVisibility(View.VISIBLE);
                    txt_address_line3.setText(address_line4);
                } else {
                    txt_address_line3.setVisibility(View.GONE);
                }

                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getStreetName())) {
                    txt_address_street.setVisibility(View.VISIBLE);
                    txt_address_street.setText(getString(R.string.street) + " : " + checkOutProcessActivity.getOrder().getAddress().getStreetName());
                } else {
                    txt_address_street.setVisibility(View.GONE);
                }

                String address_line5 = AppConstants.BLANK_STRING;
                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getArea())) {
                    address_line5 = checkOutProcessActivity.getOrder().getAddress().getArea();
                }
                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getCity())) {
                    if (address_line5.length() == 0) {
                        address_line5 = checkOutProcessActivity.getOrder().getAddress().getCity();
                    } else {
                        address_line5 = address_line5 + ", " + checkOutProcessActivity.getOrder().getAddress().getCity();
                    }
                }
                if (checkOutProcessActivity.getOrder().getAddress() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getCountry())) {
                    if (address_line5.length() == 0) {
                        address_line5 = checkOutProcessActivity.getOrder().getAddress().getCountry();
                    } else {
                        address_line5 = address_line5 + ", " + checkOutProcessActivity.getOrder().getAddress().getCountry();
                    }
                }
                if (checkOutProcessActivity.getOrder().getCountryCode() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getCountryCode())) {
                    if (address_line5.length() == 0) {
                        address_line5 = "+" + (checkOutProcessActivity.getOrder().getCountryCode().replace("+", ""));
                    } else {
                        address_line5 = address_line5 + "\n" + "+" + (checkOutProcessActivity.getOrder().getCountryCode().replace("+", ""));
                    }
                }
                if (checkOutProcessActivity.getOrder().getContactNo() != null && !ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getContactNo())) {
                    if (address_line5.length() == 0) {
                        address_line5 = checkOutProcessActivity.getOrder().getContactNo();
                    } else {
                        address_line5 = address_line5 + " " + checkOutProcessActivity.getOrder().getContactNo();
                    }
                }

                if (!ValidationUtil.isNullOrBlank(address_line5)) {
                    txt_address_contact.setVisibility(View.VISIBLE);
                    txt_address_contact.setText(address_line5);
                } else {
                    txt_address_contact.setVisibility(View.GONE);
                }

                if (checkOutProcessActivity != null && checkOutProcessActivity.getOrder() != null) {
                    txt_door_step.setText(checkOutProcessActivity.getOrder().isDoorStep() ? getString(R.string.yes) : getString(R.string.no));
                } else {
                    txt_door_step.setText(getString(R.string.no));
                }
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                DateTime date = checkOutProcessActivity.getOrder().getDate();
                DateTime startTime = checkOutProcessActivity.getOrder().getTimeSlot().getStartTime();
                DateTime endTime = checkOutProcessActivity.getOrder().getTimeSlot().getEndTime();

                String dateString = dateformatter.print(date);

                String timeString = null;

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")) {

                    String start = timeFormatter.print(startTime);
                    if (start.contains("ص")) {
                        start = start.replace("ص", getString(R.string.am));
                    } else if (start.contains("م")) {
                        start = start.replace("م", getString(R.string.pm));
                    }

                    String end = timeFormatter.print(endTime);
                    if (end.contains("ص")) {
                        end = end.replace("ص", getString(R.string.am));
                    } else if (end.contains("م")) {
                        end = end.replace("م", getString(R.string.pm));
                    }

                    timeString = String.format(getString(R.string.time_format_on_summary), start, end);
                } else {
                    timeString = String.format(getString(R.string.time_format_on_summary), timeFormatter.print(startTime), timeFormatter.print(endTime));
                }

                if (!ValidationUtil.isNullOrBlank(dateString)) {
                    txt_time_line1.setText(dateString);
                } else {
                    txt_time_line1.setVisibility(View.GONE);
                }

                if (!ValidationUtil.isNullOrBlank(timeString)) {
                    txt_time_line2.setText(timeString);
                } else {
                    txt_time_line2.setVisibility(View.GONE);
                }

                if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getPaymentMode())) {
                    if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("1")) {
                        txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.cod));
                        lyt_pay_by_card.setVisibility(View.GONE);
                        lyt_pay_by_credits.setVisibility(View.GONE);
                    } else if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("2")) {
                        txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.card));
                        lyt_pay_by_card.setVisibility(View.GONE);
                        lyt_pay_by_credits.setVisibility(View.GONE);
                    } else if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("3")) {
                        txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.tgs_credit));
                        lyt_pay_by_card.setVisibility(View.GONE);
                        lyt_pay_by_credits.setVisibility(View.GONE);
                    } else if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")) {
                        txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.card_and_credits));
                        lyt_pay_by_card.setVisibility(View.VISIBLE);
                        lyt_pay_by_credits.setVisibility(View.VISIBLE);
                    } else {
                        txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.cash_and_credits));
                        lyt_pay_by_card.setVisibility(View.VISIBLE);
                        lyt_pay_by_credits.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, true);
        if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getPaymentMode())) {

            if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("1")) {

                txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.cod));
                if (checkOutProcessActivity != null && checkOutProcessActivity.getOrder() != null) {
                    txt_door_step.setText(getString(R.string.no));
                }
                lyt_pay_by_card.setVisibility(View.GONE);
                lyt_pay_by_credits.setVisibility(View.GONE);

            } else if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("2")) {

                txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.card));
                if (checkOutProcessActivity != null && checkOutProcessActivity.getOrder() != null && checkOutProcessActivity.getOrder().isDoorStep()) {
                    txt_door_step.setText(getString(R.string.yes));
                } else {
                    txt_door_step.setText(getString(R.string.no));
                }
                lyt_pay_by_card.setVisibility(View.GONE);
                lyt_pay_by_credits.setVisibility(View.GONE);

            } else if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("3")) {

                txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.tgs_credit));
                if (checkOutProcessActivity != null && checkOutProcessActivity.getOrder() != null && checkOutProcessActivity.getOrder().isDoorStep()) {
                    txt_door_step.setText(getString(R.string.yes));
                } else {
                    txt_door_step.setText(getString(R.string.no));
                }
                lyt_pay_by_card.setVisibility(View.GONE);
                lyt_pay_by_credits.setVisibility(View.GONE);

            } else if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")) {

                txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.card_and_credits));
                if (checkOutProcessActivity != null && checkOutProcessActivity.getOrder() != null && checkOutProcessActivity.getOrder().isDoorStep()) {
                    txt_door_step.setText(getString(R.string.yes));
                } else {
                    txt_door_step.setText(getString(R.string.no));
                }
                lyt_pay_by_card.setVisibility(View.VISIBLE);
                lyt_pay_by_credits.setVisibility(View.VISIBLE);

            } else {

                txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.cash_and_credits));
                if (checkOutProcessActivity != null && checkOutProcessActivity.getOrder().isDoorStep()) {
                    txt_door_step.setText(getString(R.string.yes));
                } else {
                    txt_door_step.setText(getString(R.string.no));
                }
                lyt_pay_by_card.setVisibility(View.VISIBLE);
                lyt_pay_by_credits.setVisibility(View.VISIBLE);
            }

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                txt_pay_by_card.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByUser()));
                txt_pay_by_credits.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByTgsCredits()));
            } else {
                txt_pay_by_card.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByUser()) + AppConstants.SPACE + getString(R.string.egp));
                txt_pay_by_credits.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByTgsCredits()) + AppConstants.SPACE + getString(R.string.egp));
            }

        }
    }

    public float getDeliveryCharges(float subtotal) {

        if (list_charges != null) {
            if (list_charges.size() > 0) {
                for (int i = 0; i < list_charges.size(); i++) {
                    if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase(list_charges.get(i).getType())) {
                        return list_charges.get(i).getCharges();
                    }
                }
                return 0.0f;
            } else {
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())) {
                    try {
                        JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                        if (chargesArray != null) {
                            list_charges.clear();
                            for (int i = 0; i < chargesArray.length(); i++) {
                                JSONObject object = chargesArray.optJSONObject(i);
                                if (object != null) {

                                    DeliveryCharges deliveryCharges = new DeliveryCharges();
                                    deliveryCharges.setId(object.optString("id"));
                                    deliveryCharges.setType(object.optString("type"));
                                    deliveryCharges.setStart_amount((float) (object.optDouble("start_amt", 0.0)));
                                    deliveryCharges.setEnd_amount((float) (object.optDouble("end_amt", 0.0)));
                                    deliveryCharges.setCharges((float) (object.optDouble("d_charge", 0.0)));
                                    list_charges.add(deliveryCharges);
                                }
                            }

                            if (list_charges != null) {
                                for (int i = 0; i < list_charges.size(); i++) {
                                    if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase(list_charges.get(i).getType())) {
                                        return list_charges.get(i).getCharges();
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }
        } else {
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())) {
                try {
                    JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                    if (chargesArray != null) {
                        list_charges.clear();
                        for (int i = 0; i < chargesArray.length(); i++) {
                            JSONObject object = chargesArray.optJSONObject(i);
                            if (object != null) {

                                DeliveryCharges deliveryCharges = new DeliveryCharges();
                                deliveryCharges.setId(object.optString("id"));
                                deliveryCharges.setType(object.optString("type"));
                                deliveryCharges.setStart_amount((float) (object.optDouble("start_amt", 0.0)));
                                deliveryCharges.setEnd_amount((float) (object.optDouble("end_amt", 0.0)));
                                deliveryCharges.setCharges((float) (object.optDouble("d_charge", 0.0)));
                                list_charges.add(deliveryCharges);
                            }
                        }

                        if (list_charges != null) {
                            for (int i = 0; i < list_charges.size(); i++) {
                                if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase(list_charges.get(i).getType())) {
                                    return list_charges.get(i).getCharges();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        }
        return 0.0f;
    }

    private void updateShippingCharges(final float total_amount) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                float totalWithoutDeliveryCharges = checkOutProcessActivity.getOrder().getTotalAmountToPay() - checkOutProcessActivity.getOrder().getDeliveryCharges();
                float deliveryCharge = getDeliveryCharges(totalWithoutDeliveryCharges);
                checkOutProcessActivity.getOrder().setDeliveryCharges(deliveryCharge);
                checkOutProcessActivity.getOrder().setTotalAmountToPay(totalWithoutDeliveryCharges + deliveryCharge);

                txt_total_items.setText(checkOutProcessActivity.getOrder().getNoOfItems() + AppConstants.BLANK_STRING);

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    txt_total_subtotal.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getSubTotal()));
                    txt_delivery_fee.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getDeliveryCharges()));
                    txt_pay_by_card.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByUser()));
                    txt_pay_by_credits.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByTgsCredits()));
                    txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getTotalAmountToPay()));
                } else {
                    txt_total_subtotal.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getSubTotal()) + AppConstants.SPACE + getString(R.string.egp));
                    txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getDeliveryCharges()) + AppConstants.SPACE + getString(R.string.egp));
                    txt_pay_by_card.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByUser()) + AppConstants.SPACE + getString(R.string.egp));
                    txt_pay_by_credits.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getAmountByTgsCredits()) + AppConstants.SPACE + getString(R.string.egp));
                    txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + checkOutProcessActivity.getOrder().getTotalAmountToPay()) + AppConstants.SPACE + getString(R.string.egp));
                }

                isDeliveryChargesLoaded = true;
            }
        });
    }

    private void startPaymentProcessAPI() {
        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {
            AppUtil.hideSoftKeyboard(checkOutProcessActivity, checkOutProcessActivity);
            AppUtil.showProgress(checkOutProcessActivity, getString(R.string.processing_payment));
            try {

                JSONObject orderObj = AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity()));

                JSONObject request_data = new JSONObject();
                request_data.put("user_id", checkOutProcessActivity.getOrder().getUserId());
                request_data.put("order", orderObj);
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());


                //Add Coupon details
                request_data.put("promo_code_apply_on", promoCodeApplyOn);
                request_data.put("coupon_code_id", promoCouponCodeId);
                request_data.put("coupon_code", promoCouponCode);
                request_data.put("coupon_amount", promoAmt);


                if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
                    request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "");
                } else {
                    if (OnlineMartApplication.isLiveUrl) {
                        request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "");
                    } else {
                        if (((int) (checkOutProcessActivity.getOrder().getAmountByUser() * 100) + AppConstants.BLANK_STRING).endsWith("00")) {
                            request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "");
                        } else {
                            request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "00");
                        }
                    }
                }

                isPlacingOrder = true;

                JSONObject order_charges = orderObj.optJSONObject("order_charges");
                JSONArray order_products = orderObj.optJSONArray("order_product");
                float subtotal = 0.0f, total_amount = 0.0f, payment_by_user = 0.0f;
                int total_item = 0;

                if (order_charges.optString("subtotal") != null) {
                    subtotal = (order_charges.optString("subtotal").length() > 0 ? Float.parseFloat(order_charges.getString("subtotal")) : 0.0f);
                }

                if (order_charges.optString("total_amount") != null) {
                    total_amount = (order_charges.optString("total_amount").length() > 0 ? Float.parseFloat(order_charges.getString("total_amount")) : 0.0f);
                }

                if (order_charges.optString("payment_by_user") != null) {
                    payment_by_user = (order_charges.optString("payment_by_user").length() > 0 ? Float.parseFloat(order_charges.getString("payment_by_user")) : 0.0f);
                }

                if (order_charges.optString("total_item") != null) {
                    total_item = (order_charges.getString("total_item").length() > 0 ? Integer.parseInt(order_charges.getString("total_item")) : 0);
                }

                if (subtotal == 0.0f
                        || total_amount == 0.0f
                        || payment_by_user == 0.0f
                        || total_item == 0
                        || order_products == null
                        || order_products.length() == 0) {

                    checkOutProcessActivity.getOrder().getList_cart().clear();
                    OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, true);
                }

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
                Call<ResponseBody> call = apiService.startPaymentProcessNew((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //AppUtil.hideProgress();
                        isPlacingOrder = false;
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");

                                int statusCode = resObject.optInt("status_code", 0);
                                String errorMsg = resObject.optString("error_message");

                                if (resObject.optString("status_code") != null && resObject.optString("status_code").equalsIgnoreCase("200")) {
                                    JSONObject dataObject = resObject.getJSONObject("data");
                                    databaseId = dataObject.getString("database_id");
                                    paymentToken = dataObject.getString("payment_token");
                                    order = dataObject.getJSONObject("order");
                                    lastPaymentProcessAPI(paymentToken);

                                } else if (statusCode == 1001) {

                                    JSONArray productsArray = (JSONArray) resObject.optJSONArray("produts");

                                    if (productsArray != null && productsArray.length() > 0) {
                                        for (int i = 0; i < productsArray.length(); i++) {
                                            JSONObject object = (JSONObject) productsArray.get(i);
                                            for (int j = 0; j < OnlineMartApplication.getCartList().size(); j++) {
                                                if (OnlineMartApplication.getCartList().get(j).getId().equalsIgnoreCase(object.optString("product_id"))) {
                                                    if (object.optInt("availableQty") == 0) {
                                                        OnlineMartApplication.getCartList().get(j).setRemovedFromCart(true);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    updated_cart_list = cloneList(OnlineMartApplication.getCartList());
                                    for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                        if (OnlineMartApplication.getCartList().get(i).shouldRemovedFromCart()) {
                                            for (int j = 0; j < updated_cart_list.size(); j++) {
                                                if (OnlineMartApplication.getCartList().get(i).getId().equalsIgnoreCase(updated_cart_list.get(j).getId())) {
                                                    updated_cart_list.remove(j);
                                                }
                                            }
                                        }
                                    }

                                    if (productsArray != null && productsArray.length() > 0) {
                                        for (int i = 0; i < productsArray.length(); i++) {
                                            JSONObject object = (JSONObject) productsArray.get(i);
                                            for (int j = 0; j < updated_cart_list.size(); j++) {
                                                if (updated_cart_list.get(j).getId().equalsIgnoreCase(object.optString("product_id"))) {
                                                    updated_cart_list.get(j).setMaxQuantity(object.optInt("availableQty", 0));
                                                    updated_cart_list.get(j).setSelectedQuantity(object.optInt("availableQty", 0));
                                                }
                                            }
                                        }
                                    }

                                    AppUtil.hideProgress();
                                    final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.review_cart), getString(R.string.proceed));
                                    appDialogDoubleAction.show();
                                    appDialogDoubleAction.setCancelable(false);
                                    appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                    appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                        @Override
                                        public void onLeftActionClick(View view) {
                                            appDialogDoubleAction.dismiss();

                                            Intent reviewCartIntent = new Intent(checkOutProcessActivity, ReviewCartActivity.class);
                                            reviewCartIntent.putExtra(ReviewCartActivity.DELIVERY_TYPE, checkOutProcessActivity.getOrder().getDeliveyType());
                                            reviewCartIntent.putExtra(ReviewCartActivity.IS_USE_CREDITS, checkOutProcessActivity.getOrder().isUseTGSCredits());
                                            reviewCartIntent.putExtra(ReviewCartActivity.CREDITS_USED_FOR_ORDER, checkOutProcessActivity.getOrder().getAmountByTgsCredits());
                                            startActivityForResult(reviewCartIntent, CheckOutProcessActivity.REQUEST_REVIEW_CART);
                                        }

                                        @Override
                                        public void onRightActionClick(View view) {
                                            appDialogDoubleAction.dismiss();

                                            float subtotal = 0.0f;
                                            float savings = 0.0f;
                                            float total_amount = 0.0f;
                                            int shippingHours = 0;
                                            boolean isShippingThirdParty = false;

                                            boolean isDoorStepFinal = false;
                                            if (updated_cart_list != null && updated_cart_list.size() > 0) {
                                                for (int i = 0; i < updated_cart_list.size(); i++) {

                                                    CartItem cartItem = updated_cart_list.get(i);

                                                    if (!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())) {
                                                        subtotal = subtotal + (Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                                                    }

                                                    if (cartItem.isOffer()) {
                                                        savings = savings + (Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                                                    }

                                                    if (updated_cart_list.get(i).isShippingThirdParty()) {
                                                        isShippingThirdParty = true;
                                                    }

                                                    if (updated_cart_list.get(i).getShippingHours() > shippingHours) {
                                                        shippingHours = updated_cart_list.get(i).getShippingHours();
                                                    }

                                                    if (!updated_cart_list.get(i).isDoorStepDelivery()) {
                                                        checkOutProcessActivity.getOrder().setDoorStepAvailable(false);
                                                        isDoorStepFinal = true;
                                                    } else {
                                                        if (!isDoorStepFinal) {
                                                            checkOutProcessActivity.getOrder().setDoorStepAvailable(true);
                                                        }
                                                    }
                                                }
                                            }

                                            checkOutProcessActivity.getOrder().setOrderShippingHours(shippingHours);
                                            checkOutProcessActivity.getOrder().setShippinghirdParty(isShippingThirdParty);

                                            float delivery_charges = getDeliveryCharges(subtotal);
                                            total_amount = subtotal + delivery_charges;
                                            checkOutProcessActivity.getOrder().setTotalAmountToPay(total_amount);
                                            checkOutProcessActivity.getOrder().setTotalSavings(savings);
                                            checkOutProcessActivity.getOrder().setDeliveryCharges(delivery_charges);
                                            checkOutProcessActivity.getOrder().setAmountByUser(total_amount);

                                            if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")
                                                    || checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("5")) {

                                                if (total_amount <= checkOutProcessActivity.getOrder().getAmountByTgsCredits()) {
                                                    checkOutProcessActivity.getOrder().setPaymentMode("3");
                                                    checkOutProcessActivity.getOrder().setAmountByTgsCredits(total_amount);
                                                    checkOutProcessActivity.getOrder().setAmountByUser(0);
                                                } else {
                                                    float amountPayByUser = checkOutProcessActivity.getOrder().getTotalAmountToPay() - checkOutProcessActivity.getOrder().getAmountByTgsCredits();
                                                    checkOutProcessActivity.getOrder().setAmountByTgsCredits(total_amount);
                                                    checkOutProcessActivity.getOrder().setAmountByUser(amountPayByUser);
                                                }

                                            }

                                            startPaymentProcessAPI(AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity()), updated_cart_list));
                                        }
                                    });

                                } else if (statusCode == 1002) {

                                    AppUtil.hideProgress();
                                    final AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.ok));
                                    mSingleActionAlertDialog.show();
                                    mSingleActionAlertDialog.setCancelable(false);
                                    mSingleActionAlertDialog.setCanceledOnTouchOutside(false);
                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            mSingleActionAlertDialog.dismiss();
                                            checkOutProcessActivity.navigateTODeliveryType1();
                                        }
                                    });

                                } else if (statusCode == 1003) {

                                    AppUtil.hideProgress();
                                    final AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.payment_setting));
                                    mSingleActionAlertDialog.show();
                                    mSingleActionAlertDialog.setCancelable(false);
                                    mSingleActionAlertDialog.setCanceledOnTouchOutside(false);
                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            mSingleActionAlertDialog.dismiss();
                                            checkOutProcessActivity.navigateTOPayment();
                                        }
                                    });

                                } else if (statusCode == 305) {

                                    OnlineMartApplication.openRegionPicker(getActivity(), errorMsg);

                                } else {
                                    AppUtil.hideProgress();
                                    askForCODOption();
                                }

                            } catch (JSONException | IOException e) {
                                isPlacingOrder = false;
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                askForCODOption();
                            }
                        } else {
                            isPlacingOrder = false;
                            AppUtil.hideProgress();
                            askForCODOption();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isPlacingOrder = false;
                        AppUtil.hideProgress();
                        askForCODOption();
                    }
                });

            } catch (JSONException e) {
                isPlacingOrder = false;
                e.printStackTrace();
                AppUtil.hideProgress();
                askForCODOption();
            }
        }
    }

    private void startPaymentProcessAPI(JSONObject orderObj) {
        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {
            AppUtil.hideSoftKeyboard(checkOutProcessActivity, checkOutProcessActivity);
            AppUtil.showProgress(checkOutProcessActivity, getString(R.string.processing_payment));
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("user_id", checkOutProcessActivity.getOrder().getUserId());
                request_data.put("order", orderObj);
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                //Add Coupon details
                request_data.put("promo_code_apply_on", promoCodeApplyOn);
                request_data.put("coupon_code_id", promoCouponCodeId);
                request_data.put("coupon_code", promoCouponCode);
                request_data.put("coupon_amount", promoAmt);

                if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
                    request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "");
                } else {
                    if (OnlineMartApplication.isLiveUrl) {
                        request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "");
                    } else {
                        if (((int) (checkOutProcessActivity.getOrder().getAmountByUser() * 100) + AppConstants.BLANK_STRING).endsWith("00")) {
                            request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "");
                        } else {
                            request_data.put("amount_cents", (int) ((checkOutProcessActivity.getOrder().getAmountByUser() * 100)) + "00");
                        }
                    }
                }

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
                Call<ResponseBody> call = apiService.startPaymentProcessNew((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.getJSONObject("response");

                                int statusCode = resObject.optInt("status_code", 0);
                                String errorMsg = resObject.optString("error_message");

                                if (resObject.optString("status_code") != null && resObject.optString("status_code").equalsIgnoreCase("200")) {
                                    JSONObject dataObject = resObject.getJSONObject("data");
                                    databaseId = dataObject.getString("database_id");
                                    paymentToken = dataObject.getString("payment_token");
                                    order = dataObject.getJSONObject("order");
                                    lastPaymentProcessAPI(paymentToken);

                                } else if (statusCode == 1001) {

                                    JSONArray productsArray = (JSONArray) resObject.optJSONArray("produts");

                                    if (productsArray != null && productsArray.length() > 0) {
                                        for (int i = 0; i < productsArray.length(); i++) {
                                            JSONObject object = (JSONObject) productsArray.get(i);
                                            for (int j = 0; j < OnlineMartApplication.getCartList().size(); j++) {
                                                if (OnlineMartApplication.getCartList().get(j).getId().equalsIgnoreCase(object.optString("product_id"))) {
                                                    if (object.optInt("availableQty") == 0) {
                                                        OnlineMartApplication.getCartList().get(j).setRemovedFromCart(true);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    updated_cart_list = cloneList(OnlineMartApplication.getCartList());
                                    for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                        if (OnlineMartApplication.getCartList().get(i).shouldRemovedFromCart()) {
                                            for (int j = 0; j < updated_cart_list.size(); j++) {
                                                if (OnlineMartApplication.getCartList().get(i).getId().equalsIgnoreCase(updated_cart_list.get(j).getId())) {
                                                    updated_cart_list.remove(j);
                                                }
                                            }
                                        }
                                    }

                                    if (productsArray != null && productsArray.length() > 0) {
                                        for (int i = 0; i < productsArray.length(); i++) {
                                            JSONObject object = (JSONObject) productsArray.get(i);
                                            for (int j = 0; j < updated_cart_list.size(); j++) {
                                                if (updated_cart_list.get(j).getId().equalsIgnoreCase(object.optString("product_id"))) {
                                                    updated_cart_list.get(j).setMaxQuantity(object.optInt("availableQty", 0));
                                                    updated_cart_list.get(j).setSelectedQuantity(object.optInt("availableQty", 0));
                                                }
                                            }
                                        }
                                    }

                                    final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.review_cart), getString(R.string.proceed));
                                    appDialogDoubleAction.show();
                                    appDialogDoubleAction.setCancelable(false);
                                    appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                    appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                        @Override
                                        public void onLeftActionClick(View view) {
                                            appDialogDoubleAction.dismiss();

                                            Intent reviewCartIntent = new Intent(checkOutProcessActivity, ReviewCartActivity.class);
                                            reviewCartIntent.putExtra(ReviewCartActivity.DELIVERY_TYPE, checkOutProcessActivity.getOrder().getDeliveyType());
                                            reviewCartIntent.putExtra(ReviewCartActivity.IS_USE_CREDITS, checkOutProcessActivity.getOrder().isUseTGSCredits());
                                            reviewCartIntent.putExtra(ReviewCartActivity.CREDITS_USED_FOR_ORDER, checkOutProcessActivity.getOrder().getAmountByTgsCredits());
                                            startActivityForResult(reviewCartIntent, CheckOutProcessActivity.REQUEST_REVIEW_CART);
                                        }

                                        @Override
                                        public void onRightActionClick(View view) {
                                            appDialogDoubleAction.dismiss();

                                            float subtotal = 0.0f;
                                            float savings = 0.0f;
                                            float total_amount = 0.0f;
                                            int shippingHours = 0;
                                            boolean isShippingThirdParty = false;

                                            boolean isDoorStepFinal = false;
                                            if (updated_cart_list != null && updated_cart_list.size() > 0) {
                                                for (int i = 0; i < updated_cart_list.size(); i++) {

                                                    CartItem cartItem = updated_cart_list.get(i);

                                                    if (!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())) {
                                                        subtotal = subtotal + (Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                                                    }

                                                    if (cartItem.isOffer()) {
                                                        savings = savings + (Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                                                    }

                                                    if (updated_cart_list.get(i).isShippingThirdParty()) {
                                                        isShippingThirdParty = true;
                                                    }

                                                    if (updated_cart_list.get(i).getShippingHours() > shippingHours) {
                                                        shippingHours = updated_cart_list.get(i).getShippingHours();
                                                    }

                                                    if (!updated_cart_list.get(i).isDoorStepDelivery()) {
                                                        checkOutProcessActivity.getOrder().setDoorStepAvailable(false);
                                                        isDoorStepFinal = true;
                                                    } else {
                                                        if (!isDoorStepFinal) {
                                                            checkOutProcessActivity.getOrder().setDoorStepAvailable(true);
                                                        }
                                                    }
                                                }
                                            }

                                            checkOutProcessActivity.getOrder().setOrderShippingHours(shippingHours);
                                            checkOutProcessActivity.getOrder().setShippinghirdParty(isShippingThirdParty);

                                            float delivery_charges = getDeliveryCharges(subtotal);
                                            total_amount = subtotal + delivery_charges;
                                            checkOutProcessActivity.getOrder().setTotalAmountToPay(total_amount);
                                            checkOutProcessActivity.getOrder().setTotalSavings(savings);
                                            checkOutProcessActivity.getOrder().setDeliveryCharges(delivery_charges);
                                            checkOutProcessActivity.getOrder().setAmountByUser(total_amount);

                                            if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")
                                                    || checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("5")) {

                                                if (total_amount <= checkOutProcessActivity.getOrder().getAmountByTgsCredits()) {
                                                    checkOutProcessActivity.getOrder().setPaymentMode("3");
                                                    checkOutProcessActivity.getOrder().setAmountByTgsCredits(total_amount);
                                                    checkOutProcessActivity.getOrder().setAmountByUser(0);
                                                } else {
                                                    float amountPayByUser = checkOutProcessActivity.getOrder().getTotalAmountToPay() - checkOutProcessActivity.getOrder().getAmountByTgsCredits();
                                                    checkOutProcessActivity.getOrder().setAmountByTgsCredits(total_amount);
                                                    checkOutProcessActivity.getOrder().setAmountByUser(amountPayByUser);
                                                }

                                            }

                                            startPaymentProcessAPI(AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity()), updated_cart_list));
                                        }
                                    });

                                } else if (statusCode == 1002) {

                                    final AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.ok));
                                    mSingleActionAlertDialog.show();
                                    mSingleActionAlertDialog.setCancelable(false);
                                    mSingleActionAlertDialog.setCanceledOnTouchOutside(false);
                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            mSingleActionAlertDialog.dismiss();
                                            checkOutProcessActivity.navigateTODeliveryType1();
                                        }
                                    });

                                } else if (statusCode == 1003) {

                                    final AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), errorMsg, getString(R.string.payment_setting));
                                    mSingleActionAlertDialog.show();
                                    mSingleActionAlertDialog.setCancelable(false);
                                    mSingleActionAlertDialog.setCanceledOnTouchOutside(false);
                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            mSingleActionAlertDialog.dismiss();
                                            checkOutProcessActivity.navigateTOPayment();
                                        }
                                    });

                                } else {
                                    AppUtil.hideProgress();
                                    askForCODOption();
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                askForCODOption();
                            }
                        } else {
                            AppUtil.hideProgress();
                            askForCODOption();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                        askForCODOption();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtil.hideProgress();
                askForCODOption();
            }
        }
    }

    private void lastPaymentProcessAPI(String paymentToken) {
        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("payment_token", paymentToken);
                JSONObject sourceObject = new JSONObject();
                sourceObject.put("identifier", checkOutProcessActivity.getOrder().getSelectedCard().getCard_token());
                sourceObject.put("sourceholder_name", checkOutProcessActivity.getOrder().getSelectedCard().getCard_name());
                sourceObject.put("subtype", "TOKEN");
                sourceObject.put("expiry_month", checkOutProcessActivity.getOrder().getSelectedCard().getCard_month());
                sourceObject.put("expiry_year", checkOutProcessActivity.getOrder().getSelectedCard().getCard_year());
                sourceObject.put("cvn", cardCVV);

                JSONObject billingObject = new JSONObject();

                if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getFirst_name())) {
                    billingObject.put("first_name", checkOutProcessActivity.getOrder().getAddress().getFirst_name());
                } else {
                    billingObject.put("first_name", "first_name");
                }

                if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getLast_name())) {
                    billingObject.put("last_name", checkOutProcessActivity.getOrder().getAddress().getLast_name());
                } else {
                    billingObject.put("last_name", "last_name");
                }

                if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getBuilding_name())) {
                    billingObject.put("building", checkOutProcessActivity.getOrder().getAddress().getBuilding_name());
                } else {
                    billingObject.put("building", "building");
                }

                if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getStreetName())) {
                    billingObject.put("street", checkOutProcessActivity.getOrder().getAddress().getStreetName());
                } else {
                    billingObject.put("street", "street");
                }

                if (!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getAddress().getFloor_number())) {
                    billingObject.put("floor", checkOutProcessActivity.getOrder().getAddress().getFloor_number());
                } else {
                    billingObject.put("floor", "street");
                }

                billingObject.put("apartment", "apartment");
                billingObject.put("city", "Cairo");
                billingObject.put("state", "Cairo");
                billingObject.put("country", "Egypt");
                billingObject.put("postal_code", "postal_code");

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserEmail())) {
                    billingObject.put("email", OnlineMartApplication.mLocalStore.getUserEmail());
                } else {
                    billingObject.put("email", "email");
                }

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserPhone())) {
                    billingObject.put("phone_number", OnlineMartApplication.mLocalStore.getUserPhone());
                } else {
                    billingObject.put("phone_number", "phone_number");
                }

                request_data.put("source", sourceObject);
                request_data.put("billing", billingObject);

                //ApiInterface apiService = ApiClient.getRequestQueue("https://accept.paymobsolutions.com/api/acceptance/").create(ApiInterface.class);
                isPlacingOrder = true;
                ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
                Call<ResponseBody> call = apiService.lastPaymentProcess((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.code() == 200) {
                            isPlacingOrder = false;
                            try {
                                JSONObject json = new JSONObject(response.body().string());

                                if (json != null) {

                                    JSONObject jsonObject1 = json.optJSONObject("response");
                                    if (jsonObject1 != null) {

                                        JSONObject jsonObject = jsonObject1.optJSONObject("paymob_response");
                                        if (jsonObject != null) {
                                            is_pending = jsonObject.optBoolean("pending", false);
                                            if (is_pending) {
                                                is_3d_secure = jsonObject.optBoolean("is_3d_secure", false);
                                                if (is_3d_secure) {
                                                    String redirect_url = jsonObject.optString("redirect_url_3d_secure");
                                                    if (!ValidationUtil.isNullOrBlank(redirect_url)) {

                                                        AppUtil.hideProgress();
                                                        Intent intent = new Intent(checkOutProcessActivity, PaymentWebViewActivity.class);
                                                        intent.putExtra(CommonWebViewActivity.KEY_URL, redirect_url);
                                                        startActivityForResult(intent, CheckOutProcessActivity.REQUEST_PAYMENT);

                                                    } else {
                                                        AppUtil.hideProgress();
                                                        askForCODOption();
                                                        //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg), getString(R.string.ok));
                                                    }

                                                } else {
                                                    AppUtil.hideProgress();
                                                    if (jsonObject.optString("success").equalsIgnoreCase("true")) {

                                                        OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                                        OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                                        OnlineMartApplication.getCartList().clear();
                                                        isTransactionDeclined = false;

                                                        AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), getString(R.string.payment_success), getString(R.string.ok));
                                                        mSingleActionAlertDialog.show();
                                                        mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                                            @Override
                                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                appDialogSingleAction.dismiss();

                                                                Intent intentStore = new Intent(checkOutProcessActivity, HomeActivity.class);
                                                                intentStore.putExtra("isFromInternal", true);
                                                                intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intentStore);
                                                                checkOutProcessActivity.finish();
                                                            }
                                                        });

                                                    } else {
                                                        //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                                                        askForCODOption();
                                                    }

                                                    //paymentResponseAPI(jsonObject, jsonObject.getString("success").equalsIgnoreCase("false") ? false : true);
                                                }
                                            } else {
                                                AppUtil.hideProgress();
                                                if (jsonObject.optString("success").equalsIgnoreCase("true")) {

                                                    OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                                    OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                                    OnlineMartApplication.getCartList().clear();
                                                    isTransactionDeclined = false;

                                                    AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), getString(R.string.payment_success), getString(R.string.ok));
                                                    mSingleActionAlertDialog.show();
                                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                                        @Override
                                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                            appDialogSingleAction.dismiss();

                                                            Intent intentStore = new Intent(checkOutProcessActivity, HomeActivity.class);
                                                            intentStore.putExtra("isFromInternal", true);
                                                            intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intentStore);
                                                            checkOutProcessActivity.finish();
                                                        }
                                                    });

                                                } else {
                                                    //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                                                    askForCODOption();
                                                }
                                            }
                                        } else {
                                            AppUtil.hideProgress();
                                            //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg), getString(R.string.ok));
                                            askForCODOption();
                                        }
                                    } else {
                                        AppUtil.hideProgress();
                                        askForCODOption();
                                        //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg), getString(R.string.ok));
                                    }
                                } else {
                                    AppUtil.hideProgress();
                                    askForCODOption();
                                    //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg), getString(R.string.ok));
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                askForCODOption();
                                //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg), getString(R.string.ok));
                            }
                        } else {
                            AppUtil.hideProgress();
                            askForCODOption();
                            //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg), getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isPlacingOrder = false;
                        AppUtil.hideProgress();
                        askForCODOption();
                        /*AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), getString(R.string.payment_failed), getString(R.string.ok));
                        mSingleActionAlertDialog.show();
                        mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                            @Override
                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                appDialogSingleAction.dismiss();
                            }
                        });*/
                    }
                });

            } catch (JSONException e) {
                isPlacingOrder = false;
                e.printStackTrace();
                AppUtil.hideProgress();
                askForCODOption();
                /*AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), getString(R.string.payment_failed), getString(R.string.ok));
                mSingleActionAlertDialog.show();
                mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        appDialogSingleAction.dismiss();
                    }
                });*/
            }
        }
    }

    private void paymentResponseAPI(JSONObject paymentResponse, boolean isSuccess) {
        if (NetworkUtil.networkStatus(checkOutProcessActivity)) {
            if (is_3d_secure) {
                AppUtil.showProgress(checkOutProcessActivity);
                is_3d_secure = false;
            }
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("database_id", databaseId);
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                if (paymentResponse != null) {
                    if (isSuccess) {
                        request_data.put("success", "1");
                    } else {
                        request_data.put("success", "0");
                    }
                    request_data.put("responce_data", paymentResponse.toString());
                } else {
                    request_data.put("success", "0");
                    request_data.put("responce_data", AppConstants.BLANK_STRING);
                }
                request_data.put("order", order.toString());
                request_data.put("card_id", checkOutProcessActivity.getOrder().getSelectedCard().getCardId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
                Call<ResponseBody> call = apiService.paymentResponse((new ConvertJsonToMap().jsonToMap(request_data)));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AppUtil.hideProgress();
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject resObject = jsonObject.optJSONObject("response");
                                if (resObject.optString("status_code").equalsIgnoreCase("200")) {

                                    if (isTransactionDeclined) {
                                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_declined), getString(R.string.ok));
                                        isTransactionDeclined = false;
                                    } else {
                                        AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), resObject.getString("success_message"), getString(R.string.ok));
                                        mSingleActionAlertDialog.show();
                                        mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();
                                                OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                                OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                                OnlineMartApplication.getCartList().clear();
                                                isTransactionDeclined = false;

                                                Intent intentStore = new Intent(checkOutProcessActivity, HomeActivity.class);
                                                intentStore.putExtra("isFromInternal", true);
                                                intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intentStore);
                                                checkOutProcessActivity.finish();
                                            }
                                        });
                                    }

                                } else if (resObject.optString("status_code").equalsIgnoreCase("202")) {
                                    if (isTransactionDeclined) {
                                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_declined), getString(R.string.ok));
                                        isTransactionDeclined = false;
                                    } else {
                                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, resObject.getString("error_message"), getString(R.string.ok));
                                        isTransactionDeclined = false;
                                    }
                                } else {
                                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, resObject.getString("error_message"), getString(R.string.ok));
                                    isTransactionDeclined = false;
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                isTransactionDeclined = false;
                                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-663)", getString(R.string.ok));
                            }
                        } else {
                            isTransactionDeclined = false;
                            AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-664)", getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        isTransactionDeclined = false;
                        AppUtil.hideProgress();
                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-665)", getString(R.string.ok));
                    }
                });

            } catch (JSONException e) {

                isTransactionDeclined = false;
                e.printStackTrace();
                AppUtil.hideProgress();
                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.error_msg) + "(Err-666)", getString(R.string.ok));
            }
        }
    }

    private void loadCreditsInWalltet() {

        try {

            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            ApiInterface apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);
            Call<ResponseBody> call = apiService.getCredits((new ConvertJsonToMap().jsonToMap(request)));
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
                                String status = resObj.optString("status");
                                String errorMsg = resObj.optString("error_message");
                                if (statusCode == 200 && status != null && status.equalsIgnoreCase("OK")) {

                                    JSONObject dataObj = resObj.optJSONObject("data");
                                    if (dataObj != null) {
                                        String amount = dataObj.optString("user_credit_points");
                                        OnlineMartApplication.mLocalStore.saveUserCredit(Float.parseFloat(amount));
                                    }
                                }
                            }

                        } catch (Exception e) {
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
            AppUtil.hideProgress();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CheckOutProcessActivity.REQUEST_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {

                    String callback_url = data.getStringExtra(PaymentWebViewActivity.KEY_CALLBACK_URL);
                    if (callback_url != null) {
                        callback_url = callback_url.replace("#", "");
                        Uri uri = Uri.parse(callback_url);
                        String status = uri.getQueryParameter("success");
                        String message = uri.getQueryParameter("data.message").replace("+", " ");

                        JSONObject object = new JSONObject();
                        try {

                            object.put("response", callback_url);
                            if (!ValidationUtil.isNullOrBlank(status)) {
                                if (status.equalsIgnoreCase("true")) {

                                    loadCreditsInWalltet();
                                    OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                    OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                    OnlineMartApplication.getCartList().clear();

                                    AppDialogSingleAction mSingleActionAlertDialog = new AppDialogSingleAction(checkOutProcessActivity, checkOutProcessActivity.getResources().getString(R.string.app_name), getString(R.string.payment_success), getString(R.string.ok));
                                    mSingleActionAlertDialog.show();
                                    mSingleActionAlertDialog.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();

                                            Intent intentStore = new Intent(checkOutProcessActivity, HomeActivity.class);
                                            intentStore.putExtra("isFromInternal", true);
                                            intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intentStore);
                                            checkOutProcessActivity.finish();
                                        }
                                    });

                                } else {
                                    askForCODOption();
                                }
                            } else {
                                askForCODOption();
                                //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            askForCODOption();
                            //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                        }

                    } else {
                        askForCODOption();
                        //AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
                    }
                } else {
                    askForCODOption();
                }

            } else {
                AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.payment_failed), getString(R.string.ok));
            }

        } else {

            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    if (data.hasExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE)) {
                        checkOutProcessActivity.getOrder().setDeliveryCharges((float) data.getDoubleExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE, 0.0f));
                        isDeliveryChargesLoaded = true;
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT)) {
                        checkOutProcessActivity.getOrder().setTotalAmountToPay(data.getFloatExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT, 0.0f));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_SUBTOTAL)) {
                        checkOutProcessActivity.getOrder().setSubTotal(data.getFloatExtra(CheckOutProcessActivity.KEY_SUBTOTAL, 0.0f));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_TAX_CHARGES)) {
                        checkOutProcessActivity.getOrder().setTax(data.getFloatExtra(CheckOutProcessActivity.KEY_TAX_CHARGES, 0.0f));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS)) {
                        checkOutProcessActivity.getOrder().setNoOfItems(data.getIntExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS, 0));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS)) {
                        checkOutProcessActivity.getOrder().setTotalSavings(data.getFloatExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS, 0.0f));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_TOTAL_SHIPPING_HOURS)) {
                        checkOutProcessActivity.getOrder().setOrderShippingHours(data.getIntExtra(CheckOutProcessActivity.KEY_TOTAL_SHIPPING_HOURS, 0));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_IS_SHIPPING_TO_THIRD_PARTY)) {
                        checkOutProcessActivity.getOrder().setShippinghirdParty(data.getBooleanExtra(CheckOutProcessActivity.KEY_IS_SHIPPING_TO_THIRD_PARTY, false));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_IS_DOOR_STEP_DELIVERY_AVAILABLE)) {
                        checkOutProcessActivity.getOrder().setDoorStepAvailable(data.getBooleanExtra(CheckOutProcessActivity.KEY_IS_DOOR_STEP_DELIVERY_AVAILABLE, false));
                    }

                    if (data.hasExtra(CheckOutProcessActivity.KEY_CREDITS_CAN_USE)) {
                        float credits_can_use = data.getFloatExtra(CheckOutProcessActivity.KEY_CREDITS_CAN_USE, 0.0f);

                        if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("3")) {
                            if (credits_can_use > OnlineMartApplication.mLocalStore.getUserCredit()) {
                                AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(getActivity(), getString(R.string.app_name), getString(R.string.credits_not_enough), getString(R.string.payment_setting));
                                appDialogSingleAction.setCancelable(false);
                                appDialogSingleAction.setCanceledOnTouchOutside(false);
                                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                    @Override
                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                        btn_change_payment_method.performClick();
                                    }
                                });
                            }
                        }

                        if (checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("4")
                                || checkOutProcessActivity.getOrder().getPaymentMode().equalsIgnoreCase("5")) {

                            float amountPayByUser = checkOutProcessActivity.getOrder().getTotalAmountToPay() - credits_can_use;
                            if (credits_can_use > OnlineMartApplication.mLocalStore.getUserCredit()) {
                                checkOutProcessActivity.getOrder().setAmountByUser(amountPayByUser);
                            } else {
                                checkOutProcessActivity.getOrder().setPaymentMode("3");
                                checkOutProcessActivity.getOrder().setAmountByUser(0.0f);
                            }

                        }
                    }

                    checkOutProcessActivity.getOrder().setList_cart(OnlineMartApplication.getCartList());
                    checkOutProcessActivity.getOrder().setNoOfItems(OnlineMartApplication.getCartList().size());

                    if (checkOutProcessActivity.getOrder().getList_cart().size() == 0 || OnlineMartApplication.getCartList().size() == 0) {

                        try {
                            JSONObject request_data = new JSONObject();
                            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                            request_data.put("time_slot_id", checkOutProcessActivity.getOrder().getTimeSlot().getId());
                            request_data.put("date", checkOutProcessActivity.getOrder().getDeliveruDate().split(" ")[0]);
                            request_data.put("is_solt_book", "");

                            TimeSlotService.reserveOrReleaseTimeSlot(checkOutProcessActivity, request_data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        checkOutProcessActivity.finish();
                    } else {
                        loadSummary();
                    }


                    //Recheck promo discount
                    if (requestCode == CheckOutProcessActivity.REQUEST_REVIEW_CART)
                        callPromoApi();
                }
            } else {
                if (checkOutProcessActivity.getOrder().getList_cart().size() == 0 || OnlineMartApplication.getCartList().size() == 0) {

                    try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", checkOutProcessActivity.getOrder().getTimeSlot().getId());
                        request_data.put("date", checkOutProcessActivity.getOrder().getDeliveruDate().split(" ")[0]);
                        request_data.put("is_solt_book", "");

                        TimeSlotService.reserveOrReleaseTimeSlot(checkOutProcessActivity, request_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    checkOutProcessActivity.finish();
                }
            }

        }
    }

    public void askForCODOption() {
        final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(getActivity(), getString(R.string.app_name), getString(R.string.show_cod_option), getString(R.string.cancel), getString(R.string.cash));
        appDialogDoubleAction.setCanceledOnTouchOutside(false);
        appDialogDoubleAction.setCancelable(false);
        appDialogDoubleAction.show();
        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
            @Override
            public void onLeftActionClick(View view) {
                appDialogDoubleAction.dismiss();
            }

            @Override
            public void onRightActionClick(View view) {
                appDialogDoubleAction.dismiss();
                txt_payment_type.setText(checkOutProcessActivity.getResources().getString(R.string.cod));
                checkOutProcessActivity.getOrder().setSelectedCard(null);
                checkOutProcessActivity.getOrder().setCardId(AppConstants.BLANK_STRING);
                checkOutProcessActivity.getOrder().setPaymentMode("1");
                orderCashOnDelivery(AppUtil.getOrderObj(checkOutProcessActivity, checkOutProcessActivity.getOrder(), DeviceUtil.getVersionName(getActivity())));
            }
        });
    }
}
