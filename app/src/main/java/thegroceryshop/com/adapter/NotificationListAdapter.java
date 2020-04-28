package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 4/7/2017.
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.NotificationItem;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NotificationItem> list;
    private Context mContext;
    private OnNotificationRemoveListener onNotificationRemoveListener;

    public NotificationListAdapter(Context mContext, ArrayList<NotificationItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_notification_item, parent, false);
        return new NotificationItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NotificationItemHolder) {
            NotificationItemHolder notificationItemHolder = (NotificationItemHolder) holder;

            NotificationItem notificationItem = list.get(position);

            if(!ValidationUtil.isNullOrBlank(notificationItem.getDatetime())){
                notificationItemHolder.txt_datetime.setText(notificationItem.getDatetime());
            }else{
                notificationItemHolder.txt_datetime.setText(AppConstants.BLANK_STRING);
            }

            if(!ValidationUtil.isNullOrBlank(notificationItem.getMessage())){
                notificationItemHolder.txt_message.setText(notificationItem.getMessage());
            }else{
                notificationItemHolder.txt_message.setText(AppConstants.BLANK_STRING);
            }

            notificationItemHolder.img_close.setTag(position);
            notificationItemHolder.img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNotificationRemoveListener != null) {
                        onNotificationRemoveListener.onNotificationRemove((int) v.getTag());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class NotificationItemHolder extends RecyclerView.ViewHolder {

        private ImageView img_close;
        private TextView txt_datetime;
        private TextView txt_message;

        NotificationItemHolder(View v) {
            super(v);

            img_close = v.findViewById(R.id.notification_item_img_remove);
            txt_datetime = v.findViewById(R.id.notification_item_txt_datetime);
            txt_message = v.findViewById(R.id.notification_item_txt_message);
        }
    }

    public void setOnNotificationRemoveListener(OnNotificationRemoveListener onNotificationRemoveListener) {
        this.onNotificationRemoveListener = onNotificationRemoveListener;
    }

    public interface OnNotificationRemoveListener {
        void onNotificationRemove(int positon);
    }
}
