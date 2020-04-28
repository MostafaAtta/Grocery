package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.StrikeTextView;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Product;

/**
 * Created by mohitd on 16-Mar-17.
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PRODUCT = 1;
    private Context mContext;
    private ArrayList<Product> list_products;
    private OnProductClickLIstener onProductClickLIstener;
    int width, height;
    private LinearLayout.LayoutParams parms;

    public ProductListAdapter(Context mContext, final ArrayList<Product> list_products, int itemWidth) {
        this.mContext = mContext;
        this.list_products = list_products;
        parms = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_PRODUCT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_listing_item1, parent, false);
            return new ProductViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_hearder_item, parent, false);
            return new LoaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductViewHolder) {

            final ProductViewHolder productViewHolder = (ProductViewHolder) holder;

            productViewHolder.lyt_root.setLayoutParams(parms);
            Product product = list_products.get(position);

            if (!ValidationUtil.isNullOrBlank(product.getLabel())) {
                productViewHolder.txt_label.setText(product.getLabel());
            } else {
                productViewHolder.txt_label.setText(AppConstants.BLANK_STRING);
            }

            if (!ValidationUtil.isNullOrBlank(product.getBrandName())) {
                productViewHolder.txt_brand_name.setText(product.getBrandName());
            } else {
                productViewHolder.txt_brand_name.setText(AppConstants.BLANK_STRING);
            }

            if (!ValidationUtil.isNullOrBlank(product.getQuantity())) {
                productViewHolder.txt_quantity.setText(product.getQuantity());
            } else {
                productViewHolder.txt_quantity.setText(AppConstants.BLANK_STRING);
            }

            if (product.isOffer()) {
                productViewHolder.txt_actual_price.setText(product.getActualPrice()/* + AppConstants.SPACE + product.getCurrency()*/);
                productViewHolder.txt_actual_price.setVisibility(View.VISIBLE);

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    productViewHolder.txt_final_price.setText(product.getCurrency()+ AppConstants.SPACE +product.getOfferedPrice());
                }else{
                    productViewHolder.txt_final_price.setText(product.getOfferedPrice() + AppConstants.SPACE + product.getCurrency());
                }

                String final_text = productViewHolder.txt_final_price.getText().toString();
                Spannable wordtoSpan = new SpannableString(final_text);
                wordtoSpan.setSpan(
                        new AbsoluteSizeSpan(23),
                        final_text.indexOf(product.getCurrency()),
                        (final_text.indexOf(product.getCurrency()) + product.getCurrency().length()),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                productViewHolder.txt_final_price.setText(wordtoSpan);

            } else {
                productViewHolder.txt_actual_price.setVisibility(View.GONE);

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    productViewHolder.txt_final_price.setText(product.getCurrency()+ AppConstants.SPACE + product.getActualPrice());
                }else{
                    productViewHolder.txt_final_price.setText(product.getActualPrice() + AppConstants.SPACE + product.getCurrency());
                }
            }

            productViewHolder.img_added_to_list.setImageDrawable(
                    product.isAddedToWishList() ? ContextCompat.getDrawable(mContext, R.drawable.icon_heart_filled) : ContextCompat.getDrawable(mContext, R.drawable.icon_heart)
            );

            /*if (product.isOffer()) {
                productViewHolder.txt_final_price.setVisibility(View.VISIBLE);
                productViewHolder.txt_final_price.setText(product.getOfferedPrice() + AppConstants.SPACE + product.getCurrency());
            } else {
                productViewHolder.txt_final_price.setVisibility(View.GONE);
            }

            if (!ValidationUtil.isNullOrBlank(product.getActualPrice())) {
                productViewHolder.txt_actual_price.setText(product.getActualPrice() + AppConstants.SPACE + product.getCurrency());
            } else {
                productViewHolder.txt_actual_price.setText(AppConstants.BLANK_STRING);
            }*/

            if (!ValidationUtil.isNullOrBlank(product.getOfferString()) && product.isOffer()) {
                productViewHolder.txt_discount.setVisibility(View.VISIBLE);
                productViewHolder.txt_discount.setText(product.getOfferString());
            } else {
                productViewHolder.txt_discount.setVisibility(View.INVISIBLE);
            }

            if (product.isShippingThirdParty()) {
                productViewHolder.txt_fast_shipping.setVisibility(View.VISIBLE);
                productViewHolder.txt_fast_shipping.setText(String.format(mContext.getString(R.string.shipping_in_n_hours), product.getShippingHours()));
            } else {
                productViewHolder.txt_fast_shipping.setVisibility(View.GONE);
            }

            if (product.isSoldOut()) {
                productViewHolder.txt_sold_out.setVisibility(View.VISIBLE);
                productViewHolder.txt_fast_shipping.setVisibility(View.GONE);
                productViewHolder.txt_add_to_cart.setText(mContext.getString(R.string.notify_me).toUpperCase());
            } else {
                productViewHolder.txt_sold_out.setVisibility(View.GONE);
                productViewHolder.txt_fast_shipping.setVisibility(product.isShippingThirdParty() ? View.VISIBLE : View.GONE);
                productViewHolder.txt_add_to_cart.setText(mContext.getString(R.string.add_to_cart_caps).toUpperCase());
            }

            if (product.isAddedToCart()) {
                productViewHolder.lyt_quantity.setVisibility(View.VISIBLE);
                productViewHolder.txt_add_to_cart.setVisibility(View.GONE);

                /*if (product.getSelectedQuantity() == 0) {
                    product.setSelectedQuantity(1);
                }*/
                productViewHolder.txt_order_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);

                if(OnlineMartApplication.getCartList().size() == 0){
                    productViewHolder.lyt_root.setSelected(false);
                    product.setAddedToCart(false);
                }else{
                    productViewHolder.lyt_root.setSelected(true);
                }

            } else {
                productViewHolder.lyt_quantity.setVisibility(View.GONE);
                productViewHolder.txt_add_to_cart.setVisibility(View.VISIBLE);
                productViewHolder.lyt_root.setSelected(false);
                productViewHolder.txt_order_quantity.setText(AppConstants.BLANK_STRING);
            }

            if (!ValidationUtil.isNullOrBlank(product.getImage())) {
                ImageLoader.getInstance().displayImage(product.getImage(), productViewHolder.img_product, OnlineMartApplication.intitOptions());
            } else {
                ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, productViewHolder.img_product, OnlineMartApplication.intitOptions());
            }

            productViewHolder.lyt_product.setTag(position);
            productViewHolder.txt_add_to_cart.setTag(position);
            productViewHolder.txt_plus.setTag(position);
            productViewHolder.txt_minus.setTag(position);

            if (width == 0 && height == 0) {
                productViewHolder.lyt_root.post(new Runnable() {
                    @Override
                    public void run() {
                        height = productViewHolder.lyt_root.getHeight();
                        width = productViewHolder.lyt_root.getWidth();
                    }
                });
            } else {
            }

            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

            if(cartItem != null){
                product.setAddedToCart(true);
                product.setSelectedQuantity(cartItem.getSelectedQuantity());
                productViewHolder.txt_add_to_cart.setVisibility(View.GONE);
                productViewHolder.lyt_quantity.setVisibility(View.VISIBLE);
                productViewHolder.txt_order_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
            }else{
                product.setAddedToCart(false);
                product.setSelectedQuantity(0);
                productViewHolder.txt_add_to_cart.setVisibility(View.VISIBLE);
                productViewHolder.lyt_quantity.setVisibility(View.GONE);
            }

            productViewHolder.lyt_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickLIstener != null) {
                        onProductClickLIstener.onProductClick((int) v.getTag());
                    }
                }
            });

            productViewHolder.txt_add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickLIstener != null) {

                        if(productViewHolder.txt_add_to_cart.getText().toString().equalsIgnoreCase(mContext.getString(R.string.notify_me))){
                            onProductClickLIstener.onNotifyMe((int) v.getTag());
                        }else{
                            onProductClickLIstener.onAddToCart((int) v.getTag());
                        }
                    }
                }
            });

            productViewHolder.txt_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickLIstener != null) {
                        onProductClickLIstener.onIncreaseQuantity((int) v.getTag());
                    }
                }
            });

            productViewHolder.txt_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickLIstener != null) {
                        onProductClickLIstener.onDecreaseQuantity((int) v.getTag());
                    }
                }
            });

            if(product.isAddedToCart()){
                productViewHolder.lyt_root.setSelected(true);
            }else{
                productViewHolder.lyt_root.setSelected(false);
            }

        }
    }

    @Override
    public int getItemCount() {
        return list_products.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_sold_out, txt_fast_shipping, txt_label, txt_brand_name, txt_quantity, txt_final_price, txt_add_to_cart, txt_plus, txt_minus, txt_order_quantity, txt_discount;

        public StrikeTextView txt_actual_price;
        public ImageView img_product;
        private FrameLayout lyt_product;
        private RelativeLayout lyt_quantity;
        private LinearLayout lyt_root;
        private ImageView img_added_to_list;

        public ProductViewHolder(View view) {
            super(view);

            txt_sold_out = view.findViewById(R.id.product_item_txt_sold_out);
            txt_fast_shipping = view.findViewById(R.id.product_item_txt_fast_shipping);
            txt_label = view.findViewById(R.id.product_item_txt_label);
            txt_brand_name = view.findViewById(R.id.product_item_txt_brand_name);
            txt_quantity = view.findViewById(R.id.product_item_txt_quantity);
            txt_final_price = view.findViewById(R.id.product_item_txt_final_price);
            txt_actual_price = view.findViewById(R.id.product_item_txt_actual_price);
            txt_add_to_cart = view.findViewById(R.id.product_item_txt_add_to_cart);
            txt_plus = view.findViewById(R.id.product_item_txt_plus_quantity);
            txt_minus = view.findViewById(R.id.product_item_txt_minus_quantity);
            txt_order_quantity = view.findViewById(R.id.product_item_txt_order_quantity);
            txt_discount = view.findViewById(R.id.product_item_txt_discount);
            img_product = view.findViewById(R.id.product_item_img_product);
            lyt_product = view.findViewById(R.id.product_item_lyt_product);
            lyt_quantity = view.findViewById(R.id.product_item_lyt_quantity);
            lyt_root = view.findViewById(R.id.lyt_root);
            img_added_to_list = view.findViewById(R.id.product_list_img_added_to_list);

            //txt_actual_price.setPaintFlags(txt_actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public class LoaderViewHolder extends RecyclerView.ViewHolder {

        LoaderLayout loader;

        public LoaderViewHolder(View view) {
            super(view);

            //loader = (LoaderLayout) view.findViewById(R.id.loader);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_PRODUCT;
    }

    public void setOnProductClickLIstener(OnProductClickLIstener onProductClickLIstener) {
        this.onProductClickLIstener = onProductClickLIstener;
    }

    public interface OnProductClickLIstener {

        void onProductClick(int position);

        void onAddToCart(int position);

        void onIncreaseQuantity(int position);

        void onDecreaseQuantity(int position);

        void onNotifyMe(int tag);
    }
}
