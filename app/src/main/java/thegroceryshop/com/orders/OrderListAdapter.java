package thegroceryshop.com.orders;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.modal.OrderData;


/**
 * Created by JUNED on 6/10/2016.
 */
public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OrderData> list_orders;
    private Context mContext;
    private OnOrderClickListener onOrderClickListener;

    public OrderListAdapter(Context mContext, ArrayList<OrderData> list_orders) {
        this.mContext = mContext;
        this.list_orders = list_orders;
    }


    private static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_order_id, txt_order_delivery_date, txt_order_delivery_time, txt_no_of_items, txt_date_of_placing_order;
        private TextView txt_order_type, txt_order_amount, txt_payment_method, txt_order_status, txt_region_name;
        private RippleButton btn_track_order;
        private LinearLayout lyt_root;

        OrderViewHolder(View v) {
            super(v);
            lyt_root = v.findViewById(R.id.order_item_lyt_root);
            btn_track_order = v.findViewById(R.id.order_item_btn_track_order);
            txt_order_id = v.findViewById(R.id.order_item_txt_order_id);
            txt_order_delivery_date = v.findViewById(R.id.order_item_txt_order_delivery_date);
            txt_order_delivery_time = v.findViewById(R.id.order_item_txt_order_delivery_time);
            txt_no_of_items = v.findViewById(R.id.order_item_txt_no_of_items);
            txt_date_of_placing_order = v.findViewById(R.id.order_item_txt_date_of_placing_order);
            txt_order_type = v.findViewById(R.id.order_item_txt_order_type);
            txt_order_amount = v.findViewById(R.id.order_item_txt_order_amount);
            txt_payment_method = v.findViewById(R.id.order_item_txt_payment_method);
            txt_order_status = v.findViewById(R.id.order_item_txt_order_status);
            txt_region_name = v.findViewById(R.id.order_item_txt_region_name);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        OrderData orderData = list_orders.get(position);
        if (holder instanceof OrderViewHolder) {

            OrderViewHolder orderViewHolder = (OrderViewHolder)holder;

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                orderViewHolder.txt_order_id.setText("#" + orderData.getOrderId());
            }else{
                orderViewHolder.txt_order_id.setText(orderData.getOrderId() + "#");
            }

            orderViewHolder.txt_order_delivery_date.setText(orderData.getDeliveryDate());
            orderViewHolder.txt_order_delivery_time.setText(orderData.getDeliveryTimeInString());
            orderViewHolder.txt_no_of_items.setText(orderData.getNoOfItems());
            orderViewHolder.txt_date_of_placing_order.setText(orderData.getOrderPlaceDate());
            orderViewHolder.txt_order_type.setText(orderData.getOrderType());
            orderViewHolder.txt_order_amount.setText(orderData.getOrderAmount());
            orderViewHolder.txt_payment_method.setText(orderData.getPaymentType());
            orderViewHolder.txt_order_status.setText(orderData.getOrderStatus());
            orderViewHolder.txt_region_name.setText(orderData.getRegionName());

            if(orderData != null && orderData.getOrderStatus() != null && orderData.getOrderStatus().equalsIgnoreCase(mContext.getString(R.string.out_for_delivery))){
                orderViewHolder.btn_track_order.setVisibility(View.VISIBLE);
            }else{
                orderViewHolder.btn_track_order.setVisibility(View.GONE);
                //orderViewHolder.btn_track_order.setVisibility(View.VISIBLE);
            }

            orderViewHolder.lyt_root.setTag(position);
            orderViewHolder.btn_track_order.setTag(position);

            orderViewHolder.lyt_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onOrderClickListener != null){
                        onOrderClickListener.onOrderClick((int)v.getTag());
                    }
                }
            });

            orderViewHolder.btn_track_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onOrderClickListener != null){
                        onOrderClickListener.onTrackOrder((int)v.getTag());
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list_orders.size();
    }


    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    public interface OnOrderClickListener {
        void onOrderClick(int position);
        void onTrackOrder(int position);
    }
}
