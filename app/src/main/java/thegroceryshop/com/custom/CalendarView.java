package thegroceryshop.com.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import thegroceryshop.com.R;


public class CalendarView extends LinearLayout {
    // for logging
    //private static final String LOG_TAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    // seasons' rainbow
    int[] rainbow = new int[]{
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[]{2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        } finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = findViewById(R.id.calendar_header);
        btnPrev = findViewById(R.id.calendar_prev_button);
        btnNext = findViewById(R.id.calendar_next_button);
        txtDate = findViewById(R.id.calendar_date_display);
        grid = findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers() {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (eventHandler != null)

                    if(parent.getChildAt(position).isEnabled()){
                        for(int i=0; i<parent.getChildCount(); i++){
                            if(i == position){
                                parent.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle));
                            }else{
                                parent.getChildAt(i).setBackground(null);
                            }
                        }

                        eventHandler.onDayLongPress((Date) parent.getItemAtPosition(position));
                    }
            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events) {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));
        setDynamicHeight(grid);

        // update title
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));

        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        int season = monthSeason[month];
        int color = rainbow[season];

        header.setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    private void setDynamicHeight(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            return;
        }

        int totalHeight;
        int items = gridViewAdapter.getCount();
        int rows;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x;
        if (items > 7) {
            x = items / 7;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }


    private class CalendarAdapter extends ArrayAdapter<Date> {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;
        private Context context;

        CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays) {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            // day in question
            Date date = getItem(position);
            int day = new DateTime(date).getDayOfMonth();//date.getDate();
            int month = new DateTime(date).getMonthOfYear();//date.getMonth();
            int year = new DateTime(date).getYear();//getYear();

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            // if this day has an event, specify event image
            view.setMinimumHeight(view.getWidth());
            view.setBackgroundResource(0);
            /*final View v = view;
            view.post(new Runnable() {
                @Override
                public void run() {
                    v.setMinimumHeight(v.getWidth());
                }
            });*/
            if (eventDays != null) {
                for (Date eventDate : eventDays) {
                    if (new DateTime(eventDate).getDayOfMonth() == day &&
                            new DateTime(eventDate).getMonthOfYear() == month &&
                            new DateTime(eventDate).getYear() == year) {
                        // mark this day for event
                        //view.setBackgroundResource(R.drawable.reminder);
                        break;
                    }
                }
            }

            // clear styling
            ((TextView) view).setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            ((TextView) view).setTextColor(Color.BLACK);

            if (month != new DateTime(today).getMonthOfYear() || year != new DateTime(today).getYear()) {
                // if this day is outside current month, grey it out
                ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.greyed_out));
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
            } else if (day == new DateTime(today).getDayOfMonth()) {
                // if it is today, set it to blue/bold
                ((TextView) view).setTypeface(null, Typeface.BOLD);
                ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.today));
                view.setEnabled(true);
            } else if(month == new DateTime(today).getMonthOfYear() && year == new DateTime(today).getYear()){
                if(day >= new DateTime(today).getDayOfMonth()){
                    ((TextView) view).setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    ((TextView) view).setTextColor(Color.BLACK);
                    view.setEnabled(true);
                }else{

                    if(month > new DateTime(today).getMonthOfYear() && year >= new DateTime(today).getYear()) {
                        if ((month + 2) >= new DateTime(today).getMonthOfYear()) {
                            ((TextView) view).setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                            ((TextView) view).setTextColor(Color.BLACK);
                            view.setEnabled(true);
                        } else {
                            ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.greyed_out));
                            view.setEnabled(false);
                        }
                    }else{
                        ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.greyed_out));
                        view.setEnabled(false);
                    }

                    //((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.greyed_out));
                }
            } else if(month > new DateTime(today).getMonthOfYear() && year >= new DateTime(today).getYear()){
                if((month+2) >= new DateTime(today).getMonthOfYear()){
                    ((TextView) view).setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    ((TextView) view).setTextColor(Color.BLACK);
                    view.setEnabled(true);
                }else{
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.greyed_out));
                    view.setEnabled(false);
                }
            }

            // set text
            ((TextView) view).setText(String.valueOf(new DateTime(date).getDayOfMonth()));

            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {
        void onDayLongPress(Date date);
    }
}