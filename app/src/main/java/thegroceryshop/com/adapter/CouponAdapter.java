package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 16-03-2018.
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
import thegroceryshop.com.modal.WishListBean;

public class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WishListBean> list;
    private Context mContext;
    private OnWishListSelectedListener onWishListSelectedListener;


    public CouponAdapter(Context mContext, ArrayList<WishListBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_my_coupons_item, parent, false);
        return new CouponHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CouponHolder) {
            CouponHolder couponHolder = (CouponHolder) holder;
            couponHolder.couponText.setText("COUPON CODE\nDDF7897456566");
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    private static class CouponHolder extends RecyclerView.ViewHolder {

        private TextView couponText;
        private TextView txtViewDiscount;
        private ImageView imgViewCoupon;

        CouponHolder(View v) {
            super(v);
            couponText = v.findViewById(R.id.coupon_text);
            txtViewDiscount = v.findViewById(R.id.txtViewDiscount);
            imgViewCoupon = v.findViewById(R.id.imgViewCoupon);
        }
    }

    public void setOnWishListSelectedListener(OnWishListSelectedListener onWishListSelectedListener) {
        this.onWishListSelectedListener = onWishListSelectedListener;
    }

    public interface OnWishListSelectedListener {
        void onWishListSelected(int position);
    }
}
