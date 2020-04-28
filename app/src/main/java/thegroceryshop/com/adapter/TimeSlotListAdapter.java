package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.modal.TimeSlot;

/**
 * Created by mohitd on 16-Mar-17.
 */

public class TimeSlotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<TimeSlot> list_slots;
    private OnTimeSlotClickLIstener onTimeSlotClickLIstener;
    DateTimeFormatter timeFormatter1;
    DateTimeFormatter timeFormatter2;

    public TimeSlotListAdapter(Context mContext, final ArrayList<TimeSlot> list_slots) {
        this.mContext = mContext;
        this.list_slots = list_slots;

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            //timeFormatter1 = (DateTimeFormat.forPattern("h")).withLocale(new Locale("en"));
            timeFormatter1 = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("en"));

            //timeFormatter2 = (DateTimeFormat.forPattern("h\na")).withLocale(new Locale("en"));
            timeFormatter2 = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("en"));
        }else{
            //timeFormatter1 = (DateTimeFormat.forPattern("h")).withLocale(new Locale("ar"));
            timeFormatter1 = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("ar"));

           // timeFormatter2 = (DateTimeFormat.forPattern("h\na")).withLocale(new Locale("ar"));
            timeFormatter2 = (DateTimeFormat.forPattern("HH:mm")).withLocale(new Locale("ar"));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_slot_item, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TimeSlotViewHolder) {

            TimeSlotViewHolder timeSlotViewHolder = (TimeSlotViewHolder) holder;
            TimeSlot timeSlot = list_slots.get(position);

            DateTime startTime = timeSlot.getStartTime();
            DateTime endTime = timeSlot.getEndTime();

            timeSlotViewHolder.txt_time.setSelected(timeSlot.isSlotSelected());
            timeSlotViewHolder.txt_time.setEnabled(timeSlot.isSlotAvailable());
            timeSlotViewHolder.txt_time.setTag(position);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")){
                    //timeSlotViewHolder.txt_time.setText(String.format(mContext.getString(R.string.time_slot_format1), timeFormatter1.print(startTime), timeFormatter2.print(endTime).replace("ص", mContext.getString(R.string.am)).replace("م", mContext.getString(R.string.pm))));
                    timeSlotViewHolder.txt_time.setText(String.format(mContext.getString(R.string.time_slot_format1), timeFormatter1.print(startTime), timeFormatter2.print(endTime).replace("ص", mContext.getString(R.string.am)).replace("م", mContext.getString(R.string.pm))));
                }else{
                    timeSlotViewHolder.txt_time.setText(String.format(mContext.getString(R.string.time_slot_format1), timeFormatter1.print(startTime), timeFormatter2.print(endTime)));
                }
            }else{
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")){
                    timeSlotViewHolder.txt_time.setText(String.format(mContext.getString(R.string.time_slot_format1), timeFormatter1.print(startTime), timeFormatter2.print(endTime).replace("ص", mContext.getString(R.string.am)).replace("م", mContext.getString(R.string.pm))));
                }else{
                    timeSlotViewHolder.txt_time.setText(String.format(mContext.getString(R.string.time_slot_format1), timeFormatter1.print(startTime), timeFormatter2.print(endTime)));
                }
            }

            timeSlotViewHolder.txt_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onTimeSlotClickLIstener != null){
                        onTimeSlotClickLIstener.onTimeSotClick((int)v.getTag());
                    }
                }
            });



        }
    }

    @Override
    public int getItemCount() {
        return list_slots.size();
    }

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_time;
        public TimeSlotViewHolder(View view) {
            super(view);
            txt_time = view.findViewById(R.id.slot_item_time);
        }
    }

    public void setOnTimeSlotClickLIstener(OnTimeSlotClickLIstener onTimeSlotClickLIstener) {
        this.onTimeSlotClickLIstener = onTimeSlotClickLIstener;
    }

    public interface OnTimeSlotClickLIstener {
        void onTimeSotClick(int position);
    }
}
