package thegroceryshop.com.custom;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import thegroceryshop.com.R;
import thegroceryshop.com.utils.DeviceUtil;

/**
 * Created by rohitg on 12/13/2016.
 */

public class DateTimePickUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm a";
    public static final String TIME_FORMAT = "hh:mm a dd MMM yyyy";
    public static final String SHORT_TIME_FORMAT = "HH:mm";
    public static final String SLASH_DATE = "dd/MM/yyyy";
    public static final String DISPLAY_DATE = "yyyy-MMM-dd";
    public static final String FULL_DATE_TIME = "MMMM dd, yyyy hh:mm a";
    public static final String DISPLAY_DATE_DESIRED = "yyyy-MMM-dd";
    public static final String COMPLETE_DATE = "dd MMMM yyyy";
    public static final String API_TIME = "HHmm";
    public static final String DATE_TIME = "dd/MM/yyyy hh:mm a";
    public static final String DATE_OF_BIRTH = "MMMM dd yyyy";
    public static final String TIME_ZONE = "00:00:00";
    public static final String NUMERIC_DATE_FORMAT = "dd-MM-yyyy";
    public static final String MONTH_DATE_FORMAT = "dd MMM yyyy";
    private static final String SERVER_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static DatePickerDialog dialog;

    /**
     * Display a date picker dialog.
     *
     * @param context  context.
     * @param initial  initial date.
     * @param listener pick callback listener.
     */
    public static void showDateSelection(Context context, DateTime initial, DateTime maxTime
            , final DateTimePickListener listener) {

        final DatePickerDialog.OnDateSetListener setListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                DateTime selected = DateTime.now()
                        .withYear(year)
                        .withMonthOfYear(month + 1)
                        .withDayOfMonth(day)
                        .withMillisOfDay(0);
                listener.onDateTimeSelected(selected);
            }
        };

        DateTime dateTime = initial == null ? DateTime.now() : initial;

        DatePickerDialog datePicker = new DatePickerDialog(
                context,
                //R.style.DatePickerDialog,
                //android.R.style.Theme_Holo_Light_Dialog,
                R.style.DialogTheme,
                setListener,
                dateTime.getYear(),
                dateTime.getMonthOfYear() - 1,
                dateTime.getDayOfMonth());

        if (null != maxTime) {
            datePicker.getDatePicker().setMaxDate(maxTime.getMillis());
        }

        if (null != maxTime) {
            datePicker.getDatePicker().setMaxDate(maxTime.getMillis());
        }

        dialog = datePicker;
        datePicker.setTitle(context.getString(R.string.select_a_date));
        datePicker.show();

        updateDatePickerUI(datePicker, context);
    }

    /**
     * Display a date picker dialog.
     *
     * @param context  context.
     * @param initial  initial date.
     * @param listener pick callback listener.
     */
    public static void showDateSelection(Context context,
                                         DateTime initial,
                                         DateTime maxTime,
                                         DateTime minTime,
                                         final DateTimePickListener listener) {

        final DatePickerDialog.OnDateSetListener setListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                DateTime selected = DateTime.now()
                        .withYear(year)
                        .withMonthOfYear(month + 1)
                        .withDayOfMonth(day)
                        .withMillisOfDay(0);
                listener.onDateTimeSelected(selected);
            }
        };

        DateTime dateTime = initial == null ? DateTime.now() : initial;

        DatePickerDialog datePicker = new DatePickerDialog(
                context,
                R.style.DialogTheme,
                setListener,
                dateTime.getYear(),
                dateTime.getMonthOfYear() - 1,
                dateTime.getDayOfMonth());

        if (null != maxTime) {
            datePicker.getDatePicker().setMaxDate(maxTime.getMillis());
        }
        if (null != minTime) {
            datePicker.getDatePicker().setMinDate(minTime.getMillis());
        }


        dialog = datePicker;
        datePicker.setTitle(context.getString(R.string.select_a_date));
        datePicker.show();

        updateDatePickerUI(datePicker, context);
    }

    public static void updateDatePickerUI(DatePickerDialog datePicker, Context context){

        if(DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.LOLLIPOP){

            if(DeviceUtil.getAndroidVersion() == Build.VERSION_CODES.M || DeviceUtil.getAndroidVersion() == Build.VERSION_CODES.N || DeviceUtil.getAndroidVersion() == Build.VERSION_CODES.N_MR1){

                if(datePicker.getWindow() != null){
                    if(datePicker.getWindow().getDecorView() != null){
                        if(datePicker.getWindow().getDecorView() != null && datePicker.getWindow().getDecorView() instanceof FrameLayout){
                            if(((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0) != null && ((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0) instanceof LinearLayout){
                                if(((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1) != null && ((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1) instanceof FrameLayout){
                                    if(((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0) != null && ((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0) instanceof LinearLayout){
                                        if(((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2) != null && ((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2) instanceof FrameLayout){
                                            if(((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0) != null && ((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0) instanceof FrameLayout){
                                                if(((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0) != null && ((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0) instanceof DatePicker){
                                                    if(((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null && ((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0) instanceof LinearLayout){
                                                        if(((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null && ((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) instanceof LinearLayout){
                                                            if(((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null && ((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) instanceof LinearLayout){

                                                                AppCompatTextView textview = ((AppCompatTextView)((LinearLayout)((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0));
                                                                AppCompatTextView textview1 = ((AppCompatTextView)((LinearLayout)((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1));
                                                                ((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(0).setVisibility(View.VISIBLE);


                                                                if(textview != null){

                                                                    ((LinearLayout)textview.getParent()).setPadding(0, 0, 0, 0);
                                                                    ((LinearLayout.LayoutParams)textview.getLayoutParams()).setMargins(0, 0, 0, 0);
                                                                    ((LinearLayout.LayoutParams)textview.getLayoutParams()).width = LinearLayout.LayoutParams.MATCH_PARENT;
                                                                    textview.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                                                                    textview.setPadding(15, 15, 15, 15);
                                                                    textview.setGravity(Gravity.CENTER);
                                                                    textview.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_bg));
                                                                }

                                                                if(textview1 != null){
                                                                    ((LinearLayout)textview1.getParent()).setPadding(0, 0, 0, 0);
                                                                    ((LinearLayout)textview1.getParent()).setGravity(Gravity.CENTER);
                                                                    textview1.setPadding(15, 10, 15, 10);
                                                                    ((LinearLayout.LayoutParams)textview1.getLayoutParams()).setMargins(0,0,0,0);
                                                                    ((LinearLayout.LayoutParams)textview1.getLayoutParams()).width = LinearLayout.LayoutParams.MATCH_PARENT;
                                                                    textview1.setGravity(Gravity.CENTER);
                                                                }

                                                            } else if(((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null){

                                                                AppCompatTextView textview = null;
                                                                AppCompatTextView textview1 = null;
                                                                if(((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null){
                                                                    textview =  ((AppCompatTextView)((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0));
                                                                }

                                                                if(((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1) != null){
                                                                    textview1 =  ((AppCompatTextView)((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1));
                                                                }

                                                                ((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(0).setVisibility(View.VISIBLE);

                                                                if(textview != null){
                                                                    ((LinearLayout)textview.getParent()).setPadding(0, 0, 0, 0);
                                                                    ((LinearLayout.LayoutParams)textview.getLayoutParams()).setMargins(0, 0, 0, 0);
                                                                    textview.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                                                                    //textview.setTextSize(20.0f);
                                                                    textview.setPadding(15, 15, 15, 15);
                                                                    textview.setGravity(Gravity.CENTER);
                                                                    textview.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_bg));
                                                                }

                                                                if(textview1 != null){
                                                                    ((LinearLayout)textview1.getParent()).setPadding(0, 0, 0, 0);
                                                                    ((LinearLayout)textview1.getParent()).setGravity(Gravity.CENTER);
                                                                    textview1.setPadding(15, 10, 15, 10);
                                                                    ((LinearLayout.LayoutParams)textview1.getLayoutParams()).setMargins(0, 0, 0, 0);
                                                                    ((LinearLayout.LayoutParams)textview1.getLayoutParams()).width = LinearLayout.LayoutParams.MATCH_PARENT;
                                                                    textview1.setGravity(Gravity.CENTER);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{

                if(datePicker.getWindow() != null){
                    if(datePicker.getWindow().getDecorView() != null){
                        if(datePicker.getWindow().getDecorView() instanceof FrameLayout){
                            if(((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0) != null && ((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0) instanceof LinearLayout){
                                if(((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1) != null && ((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1) instanceof FrameLayout){
                                    if(((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0) != null && ((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0) instanceof LinearLayout){
                                        if(((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2) != null && ((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2) instanceof FrameLayout){
                                            if(((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0) != null && ((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0) instanceof FrameLayout){
                                                if(((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0) != null && ((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0) instanceof DatePicker){
                                                    if(((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null && ((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0) instanceof LinearLayout){
                                                        if(((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) != null && ((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0) instanceof LinearLayout){
                                                            if(((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1) != null && ((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1) instanceof LinearLayout){

                                                                (((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(0)).setVisibility(View.VISIBLE);
                                                                if(((LinearLayout)((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1) != null){
                                                                    AppCompatTextView textview = ((AppCompatTextView)((LinearLayout)((LinearLayout)((LinearLayout)((DatePicker)((FrameLayout)((FrameLayout)((LinearLayout)((FrameLayout)((LinearLayout)((FrameLayout)datePicker.getWindow().getDecorView()).getChildAt(0)).getChildAt(1)).getChildAt(0)).getChildAt(2)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(1)).getChildAt(1));
                                                                    ((LinearLayout)textview.getParent()).setPadding(0, 0, 0, 0);
                                                                    textview.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_bg));
                                                                    ((LinearLayout.LayoutParams)textview.getLayoutParams()).setMargins(0, 0, 0, 0);
                                                                    ((LinearLayout.LayoutParams)textview.getLayoutParams()).height = LinearLayout.LayoutParams.MATCH_PARENT;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Display a time picker dialog.
     *
     * @param context  context.
     * @param initial  initial date time.
     * @param listener pick callback listener.
     */
    public static void showTimeSelection(Context context, DateTime initial, final DateTimePickListener listener) {

        final TimePickerDialog.OnTimeSetListener setListener = new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker datePicker, int hour, int minute) {
                DateTime selected = DateTime.now()
                        .withHourOfDay(hour)
                        .withMinuteOfHour(minute)
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);
                listener.onDateTimeSelected(selected);
            }
        };

        DateTime dateTime = initial == null ? DateTime.now() : initial;

        TimePickerDialog timePicker = new TimePickerDialog(
                context,
                R.style.DialogTheme,
                setListener,
                dateTime.getHourOfDay(),
                dateTime.getMinuteOfHour(),
                false);

        timePicker.show();

    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateWithFormat(String date, String timeZone, String convertFormat) {
        if (date == null) {
            return null;
        }
        try {
            SimpleDateFormat birthTimeServer = new SimpleDateFormat(SERVER_FORMAT);
            birthTimeServer.setTimeZone(TimeZone.getDefault());

            java.util.Date birthDate = birthTimeServer.parse(date);

            SimpleDateFormat birthFormat = new SimpleDateFormat(convertFormat);

            String parsedDate = birthFormat.format(birthDate);
            parsedDate = parsedDate.replace("am", "AM");
            return parsedDate.replace("pm", "PM");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static long getDateTimestamp(String date, String timeZone) {
        if (date == null || timeZone == null) {
            return 0;
        }
        try {
            SimpleDateFormat birthTimeServer = new SimpleDateFormat(SERVER_FORMAT);
            birthTimeServer.setTimeZone(TimeZone.getTimeZone(timeZone));
            java.util.Date parsedDate = birthTimeServer.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getDateOnly(String date) {
        if (date == null) {
            return 0;
        }
        try {
            SimpleDateFormat birthTimeServer = new SimpleDateFormat(SERVER_FORMAT);
            java.util.Date birthDate = birthTimeServer.parse(date);

            DateTime convertedDate = new DateTime(birthDate.getTime());
            DateTime finalTIme = DateTime.now(DateTimeZone.forID("Europe/Rome"))
                    .withYear(convertedDate.getYear())
                    .withMonthOfYear(convertedDate.getMonthOfYear())
                    .withDayOfMonth(convertedDate.getDayOfMonth())
                    .withMillisOfDay(0);

            return finalTIme.getMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFromTimestamp(long date, String format) {
        if (format == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new java.util.Date(date));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDateInFormat(String format) {
        if (format == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new java.util.Date(Calendar.getInstance().getTimeInMillis()));
    }

    /**
     * Interface to listen to date pick events.
     */
    public interface DateTimePickListener {
        /**
         * Called when date time is selected.
         *
         * @param selected new date time.
         */
        void onDateTimeSelected(DateTime selected);
    }

}
