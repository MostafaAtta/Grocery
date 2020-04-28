package thegroceryshop.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.MySelectorDecorator;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.TimeSlotListAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.GridSpacingItemDecoration;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.TimeSlot;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.utils.Utils;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by mohitd on 24-Feb-17.
 */

public class ScheduledDeliveryActivity extends AppCompatActivity implements OnDateSelectedListener {

    private static DateTimeFormatter FORMATTER;
    private MaterialCalendarView calendarView;
    private boolean isSlotsLoading;
    private ApiInterface apiService;
    private Call<ResponseBody> slotsCall;
    private LoaderLayout loader_slots;
    private RecyclerView recyl_slots;
    private ArrayList<TimeSlot> list_slots = new ArrayList<>();
    private DateTimeFormatter formatter_time;
    private DateTimeFormatter timeFormatter;
    private Context mContext;
    private RippleButton btn_done, btn_cancel;
    private TimeSlot selectedTimeSlot;
    private int max_shipping_hours;
    private String area_id;
    private NestedScrollView nestedScrollview;

    public static final String KEY_SELECTED_TIME_SLOT = "key_selected_time_slot";
    public static final String KEY_SELECTED_DATE = "key_seleted_date";
    public static final String KEY_SELECTED_TIME_FORMAT = "key_seleted_time_format";
    public static final String KEY_MAX_SHIPPING_HOURS = "key_max_shipping_hours";
    public static final String KEY_AREA_ID = "key_area_id";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scheduled_delivery);

        apiService = ApiClient.createService(ApiInterface.class, this);
        mContext = this;
        calendarView = findViewById(R.id.calendar_view);
        calendarView.setOnDateChangedListener(this);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            calendarView.setLeftArrowMask(ContextCompat.getDrawable(this, R.mipmap.top_back));
            calendarView.setRightArrowMask(ContextCompat.getDrawable(this, R.mipmap.previous));
        }else{
            calendarView.setLeftArrowMask(ContextCompat.getDrawable(this, R.mipmap.previous));
            calendarView.setRightArrowMask(ContextCompat.getDrawable(this, R.mipmap.top_back));
        }

        calendarView.setArrowColor(Color.WHITE);
        calendarView.setHeaderTextAppearance(R.style.TextAppearance_AppCompat_Large);
        calendarView.setDateTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        calendarView.setWeekDayTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);

        loader_slots = findViewById(R.id.loader_slots);
        recyl_slots = findViewById(R.id.slots_grid);
        recyl_slots.addItemDecoration(new GridSpacingItemDecoration(3, 50, false));
        btn_cancel = findViewById(R.id.slots_btn_cancel);
        btn_done = findViewById(R.id.slots_btn_done);
        nestedScrollview = findViewById(R.id.nestedScrollview);
        loader_slots.setStatuText(getString(R.string.pls_select_delivery_date_for_time_slots));
        loader_slots.showStatusText();
        loader_slots.setHideContentOnLoad(true);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            formatter_time = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("en"));
            FORMATTER = (DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).withLocale(new Locale("en"));
            //timeFormatter = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("en"));
            timeFormatter = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("en"));
        }else{
            formatter_time = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("ar"));
            FORMATTER = (DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")).withLocale(new Locale("ar"));
            //timeFormatter = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("ar"));
            timeFormatter = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("ar"));
        }

        if(getIntent() != null){
            if(getIntent().hasExtra(KEY_MAX_SHIPPING_HOURS)){
                max_shipping_hours = getIntent().getIntExtra(KEY_MAX_SHIPPING_HOURS, 0);
            }

            if(getIntent().hasExtra(KEY_AREA_ID)){
                area_id = getIntent().getStringExtra(KEY_AREA_ID);
            }

        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedTimeSlot != null){
                    Intent intent = new Intent();

                    intent.putExtra(KEY_SELECTED_TIME_SLOT, selectedTimeSlot);

                    DateTime startTime = selectedTimeSlot.getStartTime();
                    DateTime endTime = selectedTimeSlot.getEndTime();

                    String time_string = String.format(
                            getString(R.string.time_slot_format),
                            timeFormatter.print(startTime),
                            timeFormatter.print(endTime));

                    intent.putExtra(KEY_SELECTED_TIME_FORMAT, time_string);
                    intent.putExtra(KEY_SELECTED_DATE, Utils.arabicToDecimal(getSelectedDatesString().split(" ")[0]));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, getString(R.string.pls_select_a_delivery_time_slot), getString(R.string.ok));
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyl_slots.setLayoutManager(gridLayoutManager);
        recyl_slots.setFocusable(false);

        Calendar cal = CalendarUtils.getInstance();
        cal.set(CalendarUtils.getYear(cal), CalendarUtils.getMonth(cal) + 2, CalendarUtils.getDay(cal));

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(CalendarDay.from(cal))
                .commit();

        calendarView.addDecorators(new MySelectorDecorator(this));

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        loadDeliverySlots();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                nestedScrollview.smoothScrollTo(0, nestedScrollview.getBottom());
            }
        });
    }

    private void loadDeliverySlots() {
        if(isSlotsLoading){
            slotsCall.cancel();
        }

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("date", /*Utils.arabicToDecimal(getSelectedDatesString())*/ getSelectedDatesString());
            request_data.put("max_shipment_hours", max_shipping_hours);

            //Added by Naresh
            request_data.put("area_id", area_id);
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(this)) {
                try {
                    //AppUtil.showProgress(mContext);
                    slotsCall = apiService.loadScheduleTimeSlot((new ConvertJsonToMap().jsonToMap(request_data)));
                    isSlotsLoading = true;
                    loader_slots.showProgress();
                    APIHelper.enqueueWithRetry(slotsCall, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //AppUtil.hideProgress();
                            loader_slots.showContent();
                            isSlotsLoading = false;
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONArray dataArr = resObj.optJSONArray("data");
                                            if (dataArr != null && dataArr.length() > 0) {

                                                list_slots.clear();
                                                for(int i=0; i<dataArr.length(); i++){
                                                    JSONObject slotObj = dataArr.optJSONObject(i);
                                                    if(slotObj != null){
                                                        TimeSlot timeSlot = new TimeSlot();
                                                        timeSlot.setId(slotObj.optString("id"));
                                                        timeSlot.setSlotAvailable(slotObj.optBoolean("order_limit"));

                                                        if(!ValidationUtil.isNullOrBlank(slotObj.optString("start_time"))){
                                                            if(slotObj.optString("start_time").equalsIgnoreCase("24:00:00")){
                                                                DateTime startTime = formatter_time.parseDateTime("00:00:00");
                                                                timeSlot.setStartTime(startTime);
                                                            }else{
                                                                DateTime startTime = formatter_time.parseDateTime(slotObj.optString("start_time"));
                                                                timeSlot.setStartTime(startTime);
                                                            }
                                                        }

                                                        if(!ValidationUtil.isNullOrBlank(slotObj.optString("end_time"))){

                                                            if(slotObj.optString("end_time").equalsIgnoreCase("24:00:00")){
                                                                DateTime endTime = formatter_time.parseDateTime("00:00:00");
                                                                timeSlot.setEndTime(endTime);
                                                            }else{
                                                                DateTime endTime = formatter_time.parseDateTime(slotObj.optString("end_time"));
                                                                timeSlot.setEndTime(endTime);
                                                            }
                                                        }

                                                        list_slots.add(timeSlot);
                                                    }
                                                }

                                                if(list_slots.size() == 0){
                                                    loader_slots.setStatuText(getString(R.string.no_slots_avalilable));
                                                    loader_slots.showStatusText();
                                                    selectedTimeSlot = null;
                                                }else{
                                                    final TimeSlotListAdapter timeSlotListAdapter = new TimeSlotListAdapter(mContext, list_slots);
                                                    recyl_slots.setAdapter(timeSlotListAdapter);
                                                    timeSlotListAdapter.setOnTimeSlotClickLIstener(new TimeSlotListAdapter.OnTimeSlotClickLIstener() {
                                                        @Override
                                                        public void onTimeSotClick(int position) {
                                                            for(int i=0; i<list_slots.size(); i++){
                                                                if(list_slots.get(i).isSlotAvailable()){
                                                                    list_slots.get(i).setSlotSelected(i == position);
                                                                    if(list_slots.get(i).isSlotSelected()){
                                                                        selectedTimeSlot = list_slots.get(i);
                                                                    }
                                                                }
                                                            }
                                                            timeSlotListAdapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }

                                            } else {
                                                AppUtil.showErrorDialog(mContext, errorMsg);
                                            }

                                        } else {
                                            AppUtil.showErrorDialog(mContext, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-642)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    isSlotsLoading = false;
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-643)");
                                }
                            } else {
                                loader_slots.showContent();
                                isSlotsLoading = false;
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-644)");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loader_slots.showContent();
                            isSlotsLoading = false;
                            AppUtil.hideProgress();
                        }
                    });
                } catch (JSONException e) {
                    loader_slots.showContent();
                    isSlotsLoading = false;
                    e.printStackTrace();
                }

            } else {
                loader_slots.showContent();
                isSlotsLoading = false;
            }
        } catch (JSONException e) {
            loader_slots.showContent();
            e.printStackTrace();
            isSlotsLoading = false;
        }
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.print(date.getDate().getTime());
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}