package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 14-03-2018.
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.WishListBean;

public class MySavedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WishListBean> list;
    private Context mContext;
    private OnWishListSelectedListener onWishListSelectedListener;

    public MySavedListAdapter(Context mContext, ArrayList<WishListBean> list) {
        this.mContext = mContext;
        this.list = list;
        if(this.list == null || this.list.size() == 0){
            WishListBean wishListBean = new WishListBean();
            wishListBean.setRegionId(OnlineMartApplication.mLocalStore.getSelectedRegionId());
            wishListBean.setRegionName(OnlineMartApplication.mLocalStore.getSelectedRegionName());
            this.list.add(wishListBean);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_savelist_row, parent, false);
        return new WishListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof WishListHolder) {

            WishListHolder wishListHolder = (WishListHolder) holder;
            WishListBean wishListBean = list.get(position);

            if (wishListBean.getId() == null) {
                wishListHolder.imgViewMenu.setVisibility(View.INVISIBLE);
                wishListHolder.btnAddToCart.setVisibility(View.GONE);
                wishListHolder.txtRegionName.setText(wishListBean.getRegionName());
            } else {
                wishListHolder.imgViewMenu.setVisibility(View.VISIBLE);
                wishListHolder.btnAddToCart.setVisibility(View.VISIBLE);
                wishListHolder.txtRegionName.setText(wishListBean.getRegionName());
                wishListHolder.txtWishListName.setText(wishListBean.getName());
                wishListHolder.txtWeeklyList.setText(wishListBean.getDescription());
                if(Integer.parseInt(wishListBean.getNoOfItems()) > 1){
                    wishListHolder.txtProductCount.setText(wishListBean.getNoOfItems() + AppConstants.SPACE + mContext.getString(R.string.products));
                }else{
                    wishListHolder.txtProductCount.setText(wishListBean.getNoOfItems() + " " + mContext.getString(R.string.product));
                }

                wishListHolder.view_disable.setVisibility(wishListBean.isEnable() ? View.GONE : View.VISIBLE);
                wishListHolder.view_disable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtil.showErrorDialog(mContext, mContext.getString(R.string.error_wishlist_region));
                    }
                });

                wishListHolder.btnAddToCart.setVisibility(Integer.parseInt(wishListBean.getNoOfItems()) > 0 ? View.VISIBLE : View.INVISIBLE);

                for (int i = 0; i < list.get(position).getImages().length; i++) {
                    String imageUrl = list.get(position).getImages()[i];
                    if (i == 0) {
                        if (!ValidationUtil.isNullOrBlank(imageUrl)) {
                            ImageLoader.getInstance().displayImage(imageUrl, wishListHolder.imageView1, OnlineMartApplication.intitOptions3());
                        }
                    } else if (i == 1) {
                        if (!ValidationUtil.isNullOrBlank(imageUrl)) {
                            ImageLoader.getInstance().displayImage(imageUrl, wishListHolder.imageView2, OnlineMartApplication.intitOptions3());
                        }
                    } else if (i == 2) {
                        if (!ValidationUtil.isNullOrBlank(imageUrl)) {
                            ImageLoader.getInstance().displayImage(imageUrl, wishListHolder.imageView3, OnlineMartApplication.intitOptions3());
                        }
                    } else if (i == 3) {
                        if (!ValidationUtil.isNullOrBlank(imageUrl)) {
                            ImageLoader.getInstance().displayImage(imageUrl, wishListHolder.imageView4, OnlineMartApplication.intitOptions3());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private class WishListHolder extends RecyclerView.ViewHolder {

        private ImageView imageView1;
        private ImageView imageView3;
        private ImageView imageView2;
        private ImageView imageView4;
        private TextView txtWishListName;
        private ImageView imgViewMenu;
        private TextView txtWeeklyList;
        private RippleButton btnAddToCart;
        private TextView txtProductCount;
        private TextView txtRegionName;
        private View view_disable;


        WishListHolder(View v) {
            super(v);
            imageView1 = v.findViewById(R.id.imageView1);
            imageView3 = v.findViewById(R.id.imageView3);
            imageView2 = v.findViewById(R.id.imageView2);
            imageView4 = v.findViewById(R.id.imageView4);
            txtWishListName = v.findViewById(R.id.txtWishListName);
            imgViewMenu = v.findViewById(R.id.imgViewMenu);
            txtWeeklyList = v.findViewById(R.id.txtWeeklyList);
            btnAddToCart = v.findViewById(R.id.btnAddToCart);
            txtProductCount = v.findViewById(R.id.txtProductCount);
            txtRegionName = v.findViewById(R.id.txtWishLisRegionName);
            view_disable = v.findViewById(R.id.view_disable);

            imgViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onWishListSelectedListener != null) {
                        onWishListSelectedListener.onMenuItemClicked(getAdapterPosition(), imgViewMenu, list.get(getAdapterPosition()).getId());
                    }
                }
            });

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnAddToCart.isEnabled()) {
                        btnAddToCart.setEnabled(false);
                        if (onWishListSelectedListener != null) {
                            onWishListSelectedListener.onAddWishListToCart(getAdapterPosition(), btnAddToCart);
                        }
                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onWishListSelectedListener != null) {
                        onWishListSelectedListener.onWishListSelected(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void updateWishList(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void setOnWishListSelectedListener(OnWishListSelectedListener onWishListSelectedListener) {
        this.onWishListSelectedListener = onWishListSelectedListener;
    }

    public interface OnWishListSelectedListener {
        void onWishListSelected(int position);
        void onAddWishListToCart(int position, View btn);
        void onMenuItemClicked(int position, View anchor, String list_id);
    }
}
