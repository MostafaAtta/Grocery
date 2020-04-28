package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 14-03-2018.
 */

import android.content.Context;
import android.content.Intent;
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
import thegroceryshop.com.activity.ProductMoreDetailsActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Product;

public class WishListDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Product> list;
    private Context mContext;
    private OnWishListProductListener onWishListProductListener;


    public WishListDetailAdapter(Context mContext, ArrayList<Product> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wishlist_detail_row, parent, false);
        return new WishListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof WishListHolder) {
            final WishListHolder wishListHolder = (WishListHolder) holder;

            Product wishListDetailBean = list.get(position);

            wishListHolder.textViewProductName.setText(wishListDetailBean.getLabel());

            String imageUrl = wishListDetailBean.getImage();
            if (!ValidationUtil.isNullOrBlank(imageUrl)) {
                ImageLoader.getInstance().displayImage(imageUrl, wishListHolder.imageViewProduct, OnlineMartApplication.intitOptions3());
            }

            wishListHolder.textViewBrandName.setText(wishListDetailBean.getQuantity());

            if (!wishListDetailBean.isOffer()) {
                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    wishListHolder.textViewProductQuantity.setText(wishListDetailBean.getCurrency() + AppConstants.SPACE + wishListDetailBean.getActualPrice());
                } else {
                    wishListHolder.textViewProductQuantity.setText(wishListDetailBean.getActualPrice() + AppConstants.SPACE + wishListDetailBean.getCurrency());
                }
            } else {
                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    wishListHolder.textViewProductQuantity.setText(wishListDetailBean.getCurrency() + AppConstants.SPACE + wishListDetailBean.getOfferedPrice());
                } else {
                    wishListHolder.textViewProductQuantity.setText(wishListDetailBean.getOfferedPrice() + AppConstants.SPACE + wishListDetailBean.getCurrency());
                }
            }

            wishListHolder.textViewProductItems.setText(wishListDetailBean.getWishListQty() + AppConstants.BLANK_STRING);

            wishListHolder.txt_sold_out.setVisibility(wishListDetailBean.getMaxQuantity() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private class WishListHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewProduct;
        private TextView textViewProductName;
        private TextView textViewBrandName;
        private TextView textViewProductQuantity;
        private ImageView imageViewMinus;
        private TextView textViewProductItems;
        private ImageView imageViewPlus;
        private ImageView imageViewDelete;
        private TextView txt_add_to_cart;
        private TextView txt_sold_out;

        WishListHolder(View v) {
            super(v);
            imageViewProduct = v.findViewById(R.id.imageViewProduct);
            textViewProductName = v.findViewById(R.id.textViewProductName);
            textViewBrandName = v.findViewById(R.id.textViewBrandName);
            textViewProductQuantity = v.findViewById(R.id.textViewProductQuantity);
            imageViewMinus = v.findViewById(R.id.imageViewMinus);
            textViewProductItems = v.findViewById(R.id.textViewProductItems);
            imageViewPlus = v.findViewById(R.id.imageViewPlus);
            imageViewDelete = v.findViewById(R.id.imgViewDelete);
            txt_add_to_cart = v.findViewById(R.id.txt_add_to_cart);
            txt_sold_out = v.findViewById(R.id.txt_sold_out);

            imageViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = list.get(getAdapterPosition()).getWishListQty();
                    if (quantity > 1) {
                        quantity = quantity - 1;
                        textViewProductItems.setText("" + quantity);
                        list.get(getAdapterPosition()).setWishlistQty(quantity);
                    }else{
                        if (onWishListProductListener != null) {
                            onWishListProductListener.onRemoveFromWishList(getAdapterPosition(), list.get(getAdapterPosition()));
                        }
                    }
                }
            });

            imageViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = list.get(getAdapterPosition()).getWishListQty();
                    quantity = quantity + 1;
                    textViewProductItems.setText("" + quantity);
                    list.get(getAdapterPosition()).setWishlistQty(quantity);
                }
            });

            txt_add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onWishListProductListener != null) {
                        onWishListProductListener.onAddToCart(getAdapterPosition());
                    }
                }
            });

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onWishListProductListener != null) {
                        onWishListProductListener.onRemoveFromWishList(getAdapterPosition(), list.get(getAdapterPosition()));
                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productDetailsIntent = new Intent(mContext, ProductMoreDetailsActivity.class);
                    productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_ID, list.get(getAdapterPosition()).getId());
                    productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_NAME, list.get(getAdapterPosition()).getLabel());
                    productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_BRAND_ID, list.get(getAdapterPosition()).getBrandId());
                    mContext.startActivity(productDetailsIntent);
                }
            });
        }
    }

    public void updateWishList(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void setOnWishListProductListener(OnWishListProductListener onWishListProductListener) {
        this.onWishListProductListener = onWishListProductListener;
    }

    public interface OnWishListProductListener {
        void onAddToCart(int position);
        void onRemoveFromWishList(int position, Product product);
    }
}

