package thegroceryshop.com.adapter;

import android.content.Context;
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
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Product;

/*
 * Created by mohitd on 16-Mar-17.
 */

public class OrderProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Product> list_products;

    public OrderProductListAdapter(Context mContext, final ArrayList<Product> list_products) {
        this.mContext = mContext;
        this.list_products = list_products;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_order_product_listing_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductViewHolder) {

            final ProductViewHolder productViewHolder = (ProductViewHolder) holder;

            Product product = list_products.get(position);

            if (!ValidationUtil.isNullOrBlank(product.getLabel())) {
                productViewHolder.txt_product_name.setText(product.getLabel());
            } else {
                productViewHolder.txt_product_name.setText(mContext.getString(R.string.na));
            }

            if (!ValidationUtil.isNullOrBlank(product.getBrandName())) {
                productViewHolder.txt_brand_name.setText(product.getBrandName());
            } else {
                productViewHolder.txt_brand_name.setText(mContext.getString(R.string.na));
            }

            if (!ValidationUtil.isNullOrBlank(product.getQuantity())) {
                productViewHolder.txt_product_volume.setText(product.getQuantity());
            } else {
                productViewHolder.txt_product_volume.setText(mContext.getString(R.string.na));
            }

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                if (!ValidationUtil.isNullOrBlank(product.getActualPrice())) {
                    if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                        productViewHolder.txt_product_price.setText(mContext.getString(R.string.egp) + AppConstants.SPACE + product.getActualPrice());
                    }else{
                        productViewHolder.txt_product_price.setText(product.getActualPrice() + AppConstants.SPACE + mContext.getString(R.string.egp));
                    }
                } else {
                    productViewHolder.txt_product_price.setText(mContext.getString(R.string.na));
                }
            } else {
                if (!ValidationUtil.isNullOrBlank(product.getActualPrice())) {
                    if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                        productViewHolder.txt_product_price.setText(mContext.getString(R.string.egp) + AppConstants.SPACE + product.getActualPrice());
                    }else{
                        productViewHolder.txt_product_price.setText(product.getActualPrice() + AppConstants.SPACE + mContext.getString(R.string.egp));
                    }

                } else {
                    productViewHolder.txt_product_price.setText(mContext.getString(R.string.na));
                }
            }

            if (!ValidationUtil.isNullOrBlank(product.getActualPrice())) {
                productViewHolder.txt_product_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
            } else {
                productViewHolder.txt_product_quantity.setText(mContext.getString(R.string.na));
            }

            if (!ValidationUtil.isNullOrBlank(product.getImage())) {
                ImageLoader.getInstance().displayImage(product.getImage(), productViewHolder.img_product, OnlineMartApplication.intitOptions());
            } else {
                ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, productViewHolder.img_product, OnlineMartApplication.intitOptions());
            }
        }
    }

    @Override
    public int getItemCount() {
        return list_products.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_product_name, txt_brand_name, txt_product_volume, txt_product_price, txt_product_quantity;
        public ImageView img_product;

        public ProductViewHolder(View view) {
            super(view);
            txt_product_name = view.findViewById(R.id.order_product_item_txt_product_name);
            txt_brand_name = view.findViewById(R.id.order_product_item_txt_brand_name);
            txt_product_volume = view.findViewById(R.id.order_product_item_txt_product_volume);
            txt_product_price = view.findViewById(R.id.order_product_item_txt_price);
            txt_product_quantity = view.findViewById(R.id.order_product_item_txt_quantity);
            img_product = view.findViewById(R.id.order_product_item_img_product);

        }
    }


}
