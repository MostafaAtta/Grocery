package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import androidx.core.content.res.ResourcesCompat;
import android.view.Gravity;

import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.util.Calendar;

import me.grantland.widget.AutofitTextView;

/**
 * Display a day of the week
 */
@Experimental
@SuppressLint("ViewConstructor")
class WeekDayView extends AutofitTextView {

    private WeekDayFormatter formatter = WeekDayFormatter.DEFAULT;
    private int dayOfWeek;
    private Typeface typeface = null;

    public WeekDayView(Context context, int dayOfWeek) {
        super(context);

        setGravity(Gravity.CENTER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }

        setMinTextSize(8);
        setMaxLines(1);
        setMinLines(1);
        setMaxTextSize(12);

        setDayOfWeek(dayOfWeek);
        if(CalenderLocalStore.getObj().getAppLangId().equalsIgnoreCase("1")){
            typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular);
        }else{
            typeface = ResourcesCompat.getFont(context, R.font.ge_ss_two_medium);
        }
        setTypeface(typeface);
    }

    public void setWeekDayFormatter(WeekDayFormatter formatter) {
        this.formatter = formatter == null ? WeekDayFormatter.DEFAULT : formatter;
        setDayOfWeek(dayOfWeek);
        setTypeface(typeface);
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        setText(formatter.format(dayOfWeek));
        setTypeface(typeface);
    }

    public void setDayOfWeek(Calendar calendar) {
        setDayOfWeek(CalendarUtils.getDayOfWeek(calendar));
    }
}
