package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 13-03-2018.
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.modal.WishListBean;

public class WishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WishListBean> list;
    private Context mContext;
    private OnWishListSelectedListener onWishListSelectedListener;

    public WishListAdapter(Context mContext, ArrayList<WishListBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wishlist_row, parent, false);
        return new WishListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof WishListHolder) {
            WishListHolder wishListHolder = (WishListHolder) holder;
            WishListBean wishListBean = list.get(position);

            wishListHolder.wishListName.setText(wishListBean.getName());

            wishListHolder.wishListName.setChecked(wishListBean.isSelected());

            wishListHolder.layoutWishList.setTag(position);

            //wishListHolder.layoutWishList.setEnabled(wishListBean.isEnable());
            wishListHolder.wishListName.setEnabled(wishListBean.isEnable());
            wishListHolder.layoutWishList.setBackground(wishListBean.isEnable() ? ContextCompat.getDrawable(mContext, R.drawable.wishlist_row) : ContextCompat.getDrawable(mContext, R.drawable.wishlist_row_disable));

            wishListHolder.layoutWishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    WishListBean wishListBean = list.get(pos);

                    if(wishListBean.isEnable()){
                        if(onWishListSelectedListener != null) {
                            onWishListSelectedListener.onWishListSelected(pos);
                        }
                    }else{
                        AppUtil.showErrorDialog(mContext, mContext.getString(R.string.error_wishlist_region));
                    }
                }
            });

            wishListHolder.wishListName.setTag(position);
            wishListHolder.wishListName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    if(onWishListSelectedListener != null) {
                        onWishListSelectedListener.onWishListSelected(pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class WishListHolder extends RecyclerView.ViewHolder {

        private RadioButton wishListName;
        private ConstraintLayout layoutWishList;

        WishListHolder(View v) {
            super(v);
            wishListName = v.findViewById(R.id.wishListName);
            layoutWishList = v.findViewById(R.id.layoutWishList);
        }
    }

    public void setOnWishListSelectedListener(OnWishListSelectedListener onWishListSelectedListener) {
        this.onWishListSelectedListener = onWishListSelectedListener;
    }

    public interface OnWishListSelectedListener {
        void onWishListSelected(int position);
    }
}
