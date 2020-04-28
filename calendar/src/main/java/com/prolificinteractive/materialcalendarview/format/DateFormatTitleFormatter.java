package com.prolificinteractive.materialcalendarview.format;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalenderLocalStore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Format using a {@linkplain java.text.DateFormat} instance.
 */
public class DateFormatTitleFormatter implements TitleFormatter {

    private final DateFormat dateFormat;

    /**
     * Format using "LLLL yyyy" for formatting
     */
    public DateFormatTitleFormatter() {
        if(CalenderLocalStore.getObj().getAppLangId().equalsIgnoreCase("1")){
            this.dateFormat = new SimpleDateFormat(
                    "LLLL yyyy", new Locale("en")
            );
        }else{
            this.dateFormat = new SimpleDateFormat(
                    "LLLL yyyy", new Locale("ar")
            );
        }
    }

    /**
     * Format using a specified {@linkplain DateFormat}
     *
     * @param format the format to use
     */
    public DateFormatTitleFormatter(DateFormat format) {
        this.dateFormat = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence format(CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
