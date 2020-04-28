package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
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
import thegroceryshop.com.modal.MySavingBean;

/*
 * Created by umeshk on 16-03-2018.
 */

public class MySavingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MySavingBean> list;
    private Context mContext;

    public MySavingAdapter(Context mContext, ArrayList<MySavingBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_saved_kilos_item, parent, false);
        return new MySavingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MySavingHolder) {
            MySavingHolder mySavingHolder = (MySavingHolder) holder;
            MySavingBean mySavingBean = list.get(position);
            mySavingHolder.textViewDate.setText(mySavingBean.getDate());
            mySavingHolder.textViewTime.setText((getTimeFormat(mySavingBean.getStartTime()) + " - " + getTimeFormat(mySavingBean.getEndTime())));
            mySavingHolder.textViewOrder.setText(mContext.getString(R.string.order_id_colon) + " #" + mySavingBean.getOrderId());
            mySavingHolder.textViewSavedPrice.setText(mySavingBean.getSaving());
        }
    }

    private String getTimeFormat(String time) {
        DateTimeFormatter serverDateTimeFormat;
        DateTimeFormatter desiredDateTimeFormat;

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            serverDateTimeFormat = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("en"));
            desiredDateTimeFormat = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("en"));
        } else {
            serverDateTimeFormat = (DateTimeFormat.forPattern("HH:mm:ss")).withLocale(new Locale("ar"));
            desiredDateTimeFormat = (DateTimeFormat.forPattern("h a")).withLocale(new Locale("ar"));
        }
        DateTime dateTime = serverDateTimeFormat.parseDateTime(time);
        time = desiredDateTimeFormat.print(dateTime);
        if(time.contains("ص")){
            time = time.replace("ص", mContext.getString(R.string.am));
        }else{
            time = time.replace("م", mContext.getString(R.string.pm));
        }
        return time.toUpperCase();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class MySavingHolder extends RecyclerView.ViewHolder {

        private TextView textViewDate;
        private TextView textViewTime;
        private TextView textViewOrder;
        private TextView textViewSavedPrice;

        MySavingHolder(View v) {
            super(v);
            textViewDate = v.findViewById(R.id.textViewDate);
            textViewTime = v.findViewById(R.id.textViewTime);
            textViewOrder = v.findViewById(R.id.textViewOrder);
            textViewSavedPrice = v.findViewById(R.id.textViewSavedPrice);
        }
    }
}
