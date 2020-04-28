package thegroceryshop.com.checkoutProcess.checkoutfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.ScheduledDeliveryActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.TimeSlot;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/*
 * Created by rohitg on 4/12/2017.
 */
public class DeliveryDateAndTimeFragment extends Fragment implements View.OnClickListener {

    private CheckOutProcessActivity checkOutProcessActivity;
    private RelativeLayout paymentMethodLayout;
    private FrameLayout normalLayout;
    private View normalView;
    private FrameLayout scheduledLayout;
    private View scheduledView;
    private FrameLayout expressLayout;
    private View expressView;
    private FrameLayout layoutDateTime;
    private TextView txtViewTimeSlot;
    private TextView textViewDateTime;
    private ApiInterface apiService;
    private LoaderLayout loader;
    private Call<ResponseBody> expresCall;
    private boolean isExpressLoading;
    private TextView txt_date;

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter formatter1 = DateTimeFormat.forPattern("yyyy-MM-dd");
    DateTimeFormatter formatter_time = DateTimeFormat.forPattern("HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("d\nMMM\nyyyy");
    //DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h a");
    DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    public TimeSlot selectedTimeSlot;
    private DateTime selected_date;
    public  String date;
    private String selected_time_string;

    public static DeliveryDateAndTimeFragment newInstance(CheckOutProcessActivity checkOutProcessActivity) {
        DeliveryDateAndTimeFragment deliveryDateAndTimeFragment = new DeliveryDateAndTimeFragment();
        deliveryDateAndTimeFragment.checkOutProcessActivity = checkOutProcessActivity;
        return deliveryDateAndTimeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, false);

        View view = inflater.inflate(R.layout.layout_delivery_date_and_time, container, false);
        checkOutProcessActivity = (CheckOutProcessActivity)getActivity();
        initView(view);
        apiService = ApiClient.createService(ApiInterface.class, checkOutProcessActivity);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            formatter = (DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).withLocale(new Locale("en"));
            formatter1 = (DateTimeFormat.forPattern("yyyy-MM-dd")).withLocale(new Locale("en"));
            formatter_time = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("en"));
            dateFormatter = (DateTimeFormat.forPattern("d\nMMM\nyyyy")).withLocale(new Locale("en"));
            //timeFormatter = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("en"));
            timeFormatter = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("en"));
        }else{
            formatter = (DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).withLocale(new Locale("ar"));
            formatter1 = (DateTimeFormat.forPattern("yyyy-MM-dd")).withLocale(new Locale("ar"));
            formatter_time = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("ar"));
            dateFormatter = (DateTimeFormat.forPattern("d\nMMM\nyyyy")).withLocale(new Locale("ar"));
            //timeFormatter = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("ar"));
            timeFormatter = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("ar"));
        }

        return view;
    }

    private void initView(View view) {
        paymentMethodLayout = view.findViewById(R.id.paymentMethodLayout);
        normalLayout = view.findViewById(R.id.normalLayout);
        normalView = view.findViewById(R.id.normalView);
        scheduledLayout = view.findViewById(R.id.scheduledLayout);
        scheduledView = view.findViewById(R.id.scheduledView);
        expressLayout = view.findViewById(R.id.expressLayout);
        expressView = view.findViewById(R.id.expressView);
        layoutDateTime = view.findViewById(R.id.layoutDateTime);
        txtViewTimeSlot = view.findViewById(R.id.txtViewTimeSlot);
        textViewDateTime = view.findViewById(R.id.textViewDateTime);
        loader = view.findViewById(R.id.loader_text);
        txt_date = view.findViewById(R.id.text_date);
        loader.showContent();

        textViewDateTime.setVisibility(View.VISIBLE);
        txtViewTimeSlot.setVisibility(View.INVISIBLE);
        layoutDateTime.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        if(selectedTimeSlot != null && selected_date != null){
            String time = String.format(
                    getString(R.string.time_slot_format),
                    timeFormatter.print(selectedTimeSlot.getStartTime()).replace("ص", getString(R.string.am)).replace("م", getString(R.string.pm)).toUpperCase(),
                    timeFormatter.print(selectedTimeSlot.getEndTime()).replace("ص", getString(R.string.am)).replace("م", getString(R.string.pm)).toUpperCase());

            txt_date.setText(dateFormatter.print(selected_date));
            txtViewTimeSlot.setText(time);

            txtViewTimeSlot.setVisibility(View.VISIBLE);
            layoutDateTime.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);

            if(checkOutProcessActivity.getOrder().getDeliveyType().equalsIgnoreCase("2")){
                scheduledView.setVisibility(View.VISIBLE);
                expressView.setVisibility(View.INVISIBLE);
            }else{
                scheduledView.setVisibility(View.INVISIBLE);
                expressView.setVisibility(View.VISIBLE);
            }
        }

        setOnClickListener();
    }

    public void updateUI(){
        if(checkOutProcessActivity.getOrder().getTimeSlot() == null){
            if(textViewDateTime != null){
                textViewDateTime.setVisibility(View.VISIBLE);
            }

            if(txtViewTimeSlot != null){
                txtViewTimeSlot.setVisibility(View.INVISIBLE);
            }

            if(layoutDateTime != null){
                layoutDateTime.setVisibility(View.GONE);
            }

            if(loader != null && isAdded()){
                loader.setVisibility(View.VISIBLE);
            }

            selectedTimeSlot = null;
            date = AppConstants.BLANK_STRING;

            if(scheduledView != null){
                scheduledView.setVisibility(View.INVISIBLE);
            }

            if(expressView != null){
                expressView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setOnClickListener() {
        paymentMethodLayout.setOnClickListener(this);
        scheduledLayout.setOnClickListener(this);
        expressLayout.setOnClickListener(this);
        normalLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.paymentMethodLayout:

                if(!ValidationUtil.isNullOrBlank(date) && !ValidationUtil.isNullOrBlank(selectedTimeSlot)){

                    try{
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", selectedTimeSlot.getId());
                        request_data.put("date", date.split(" ")[0]);
                        request_data.put("is_solt_book", "add");

                        TimeSlotService.reserveOrReleaseTimeSlot(checkOutProcessActivity, request_data);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    checkOutProcessActivity.getOrder().setTimeSlot(selectedTimeSlot);
                    checkOutProcessActivity.getOrder().setDeliveruDate(date.split(" ")[0]);
                    checkOutProcessActivity.getOrder().setDate(selected_date);
                    if(scheduledView.getVisibility() == View.VISIBLE){
                        checkOutProcessActivity.getOrder().setDeliveyType("2");
                    }else{
                        checkOutProcessActivity.getOrder().setDeliveyType("1");
                    }

                    checkOutProcessActivity.viewpager.setCurrentItem(3);

                    /*Intent intentMyCoupons;
                    intentMyCoupons = new Intent(checkOutProcessActivity, CardListActivity.class);//PaymentMethodActivity.class);
                    startActivity(intentMyCoupons);*/
                }else{
                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.pls_select_delivery_date_and_time_slot), getString(R.string.ok));
                }

                break;

            case R.id.scheduledLayout:

                if(isExpressLoading){
                    expresCall.cancel();
                    txt_date.setText(AppConstants.BLANK_STRING);
                    txtViewTimeSlot.setText(AppConstants.BLANK_STRING);
                    layoutDateTime.setVisibility(View.GONE);
                    loader.showContent();
                    loader.setVisibility(View.VISIBLE);
                }

                //normalView.setVisibility(View.INVISIBLE);
                scheduledView.setVisibility(View.VISIBLE);
                expressView.setVisibility(View.INVISIBLE);

                date = null; selectedTimeSlot = null;
                Intent intent = new Intent(checkOutProcessActivity, ScheduledDeliveryActivity.class);//PaymentMethodActivity.class);

                //Added by Naresh
                intent.putExtra(ScheduledDeliveryActivity.KEY_AREA_ID, checkOutProcessActivity.getOrder().getAddress().getArea_id());
                intent.putExtra(ScheduledDeliveryActivity.KEY_MAX_SHIPPING_HOURS, checkOutProcessActivity.getOrder().getOrderShippingHours());
                startActivityForResult(intent, CheckOutProcessActivity.REQUEST_DELIVERY_DATE_AND_TIME);
                break;

            case R.id.expressLayout:

                if(checkOutProcessActivity.getOrder().isShippinghirdParty()){
                    Toast.makeText(checkOutProcessActivity, getString(R.string.express_delivery_can_not_be_selected), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(checkOutProcessActivity, getString(R.string.express_delivery_note), Toast.LENGTH_LONG).show();
                    scheduledView.setVisibility(View.INVISIBLE);
                    expressView.setVisibility(View.VISIBLE);

                    txt_date.setText(AppConstants.BLANK_STRING);
                    txtViewTimeSlot.setText(AppConstants.BLANK_STRING);
                    date = null; selectedTimeSlot = null;
                    loadExpressDate();
                }


                /*final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(checkOutProcessActivity, getString(R.string.app_name), getString(R.string.note_exress_delivery), getString(R.string.cancel_caps), getString(R.string.ok));
                appDialogDoubleAction.setCancelable(false);
                appDialogDoubleAction.setCanceledOnTouchOutside(false);
                appDialogDoubleAction.show();
                appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                    @Override
                    public void onLeftActionClick(View view) {
                        appDialogDoubleAction.dismiss();
                    }

                    @Override
                    public void onRightActionClick(View view) {
                        appDialogDoubleAction.dismiss();
                        scheduledView.setVisibility(View.INVISIBLE);
                        expressView.setVisibility(View.VISIBLE);

                        txt_date.setText(AppConstants.BLANK_STRING);
                        txtViewTimeSlot.setText(AppConstants.BLANK_STRING);
                        date = null; selectedTimeSlot = null;
                        loadExpressDate();
                    }
                });*/

                //normalView.setVisibility(View.INVISIBLE);

                break;

            case R.id.normalLayout:
                scheduledView.setVisibility(View.INVISIBLE);
                //normalView.setVisibility(View.VISIBLE);
                expressView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void loadExpressDate() {

        if(isExpressLoading){
            expresCall.cancel();
        }

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("max_shipment_hours", checkOutProcessActivity.getOrder().getOrderShippingHours());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());
            request_data.put("area_id", checkOutProcessActivity.getOrder().getAddress().getArea_id());
            if (NetworkUtil.networkStatus(checkOutProcessActivity)) {
                try {
                    //AppUtil.showProgress(mContext);
                    expresCall = apiService.loadExpressdDate((new ConvertJsonToMap().jsonToMap(request_data)));
                    isExpressLoading = true;
                    layoutDateTime.setVisibility(View.GONE);
                    loader.showProgress();
                    loader.setVisibility(View.VISIBLE);
                    APIHelper.enqueueWithRetry(expresCall, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //AppUtil.hideProgress();

                            layoutDateTime.setVisibility(View.VISIBLE);
                            loader.showContent();
                            isExpressLoading = false;
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

                                                String start_time = dataObj.optString("start_time");
                                                String end_time = dataObj.optString("end_time");

                                                if(!ValidationUtil.isNullOrBlank(start_time) && start_time.equalsIgnoreCase("24:00:00")){
                                                    start_time = "00:00:00";
                                                }

                                                if(!ValidationUtil.isNullOrBlank(end_time) && end_time.equalsIgnoreCase("24:00:00")){
                                                    end_time = "00:00:00";
                                                }

                                                String slotDate = dataObj.optString("date_time").split(" ")[0];
                                                String serverDate = dataObj.optString("server_date").split(" ")[0];

                                                if(slotDate.equalsIgnoreCase(serverDate)){

                                                    if (checkOutProcessActivity.getOrder() != null && checkOutProcessActivity.getOrder().getTimeSlot() != null) {
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
                                                    }

                                                    DateTime startTime = formatter_time.parseDateTime(start_time);
                                                    DateTime endTime = formatter_time.parseDateTime(end_time);

                                                    selectedTimeSlot = new TimeSlot();
                                                    selectedTimeSlot.setId(dataObj.optString("id"));
                                                    selectedTimeSlot.setSlotAvailable(true);
                                                    selectedTimeSlot.setSlotSelected(true);
                                                    selectedTimeSlot.setStartTime(startTime);
                                                    selectedTimeSlot.setEndTime(endTime);

                                                    date = dataObj.optString("date_time");
                                                    selected_date = formatter.parseDateTime(date);

                                                    selected_time_string = String.format(
                                                            getString(R.string.time_slot_format),
                                                            timeFormatter.print(startTime).replace("ص", getString(R.string.am)).replace("م", getString(R.string.pm)).toUpperCase(),
                                                            timeFormatter.print(endTime).replace("ص", getString(R.string.am)).replace("م", getString(R.string.pm)).toUpperCase());

                                                    txt_date.setText(dateFormatter.print(selected_date));
                                                    txtViewTimeSlot.setText(selected_time_string);

                                                    txtViewTimeSlot.setVisibility(View.VISIBLE);
                                                    layoutDateTime.setVisibility(View.VISIBLE);
                                                    if(loader != null && isAdded())
                                                        loader.setVisibility(View.GONE);
                                                } else{
                                                    AppUtil.showErrorDialog(checkOutProcessActivity, getString(R.string.no_express_timeslot));
                                                    layoutDateTime.setVisibility(View.GONE);
                                                    updateUI();
                                                }

                                            } else {
                                                layoutDateTime.setVisibility(View.GONE);
                                                updateUI();
                                                AppUtil.showErrorDialog(checkOutProcessActivity, errorMsg);
                                            }

                                        } else {
                                            layoutDateTime.setVisibility(View.GONE);
                                            updateUI();
                                            AppUtil.showErrorDialog(checkOutProcessActivity, errorMsg);
                                        }

                                    } else {
                                        layoutDateTime.setVisibility(View.GONE);
                                        updateUI();
                                        AppUtil.showErrorDialog(checkOutProcessActivity, getString(R.string.error_msg) + "(ERR-639)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    if(loader != null && isAdded())
                                        loader.showContent();
                                    layoutDateTime.setVisibility(View.GONE);
                                    isExpressLoading = false;
                                    AppUtil.showErrorDialog(checkOutProcessActivity, getString(R.string.error_msg) + "(ERR-640)");
                                }
                            } else {
                                if(loader != null && isAdded())
                                    loader.showContent();
                                isExpressLoading = false;
                                AppUtil.showErrorDialog(checkOutProcessActivity, getString(R.string.error_msg) + "(ERR-641)");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if(loader != null && isAdded())
                                loader.showContent();
                            isExpressLoading = false;
                            AppUtil.hideProgress();
                        }
                    });
                } catch (JSONException e) {
                    if(loader != null && isAdded())
                        loader.showContent();
                    isExpressLoading = false;
                    e.printStackTrace();
                }

            } else {
                if(loader != null && isAdded())
                    loader.showContent();
                isExpressLoading = false;
            }
        } catch (JSONException e) {
            if(loader != null && isAdded())
                loader.showContent();
            e.printStackTrace();
            isExpressLoading = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            if(data != null){

                if(data.hasExtra(ScheduledDeliveryActivity.KEY_SELECTED_DATE)){
                    date = data.getStringExtra(ScheduledDeliveryActivity.KEY_SELECTED_DATE);
                    selected_date = formatter1.parseDateTime(date);
                    txt_date.setText(dateFormatter.print(selected_date));
                }

                if(data.hasExtra(ScheduledDeliveryActivity.KEY_SELECTED_TIME_SLOT)){
                    selectedTimeSlot = data.getParcelableExtra(ScheduledDeliveryActivity.KEY_SELECTED_TIME_SLOT);
                    String time_string = data.getStringExtra(ScheduledDeliveryActivity.KEY_SELECTED_TIME_FORMAT);

                    if(!ValidationUtil.isNullOrBlank(time_string)){
                        String start = time_string.split(getString(R.string.to))[0].trim();
                        String end = time_string.split(getString(R.string.to))[1].trim();

                        DateTime str_time = timeFormatter.parseDateTime(start);
                        DateTime end_time = timeFormatter.parseDateTime(end);

                        selectedTimeSlot.setStartTime(str_time);
                        selectedTimeSlot.setEndTime(end_time);

                        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")){
                            time_string = time_string.replace("ص", getString(R.string.am)).replace("م", getString(R.string.pm));
                        }
                    }

                    txtViewTimeSlot.setText(time_string);
                }

                txtViewTimeSlot.setVisibility(View.VISIBLE);
                layoutDateTime.setVisibility(View.VISIBLE);
                if(loader != null && isAdded())
                    loader.setVisibility(View.GONE);

            }else{
                textViewDateTime.setVisibility(View.VISIBLE);
                txtViewTimeSlot.setVisibility(View.INVISIBLE);
                layoutDateTime.setVisibility(View.GONE);
                if(loader != null && isAdded()){
                    loader.showContent();
                    loader.setVisibility(View.VISIBLE);
                }
                scheduledView.setVisibility(View.INVISIBLE);
                expressView.setVisibility(View.INVISIBLE);
            }

        }else{
            textViewDateTime.setVisibility(View.VISIBLE);
            txtViewTimeSlot.setVisibility(View.INVISIBLE);
            layoutDateTime.setVisibility(View.GONE);
            if(loader != null && isAdded()){
                loader.setVisibility(View.VISIBLE);
                loader.showContent();
            }
            scheduledView.setVisibility(View.INVISIBLE);
            expressView.setVisibility(View.INVISIBLE);
        }
    }
}
