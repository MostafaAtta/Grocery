package thegroceryshop.com.products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.activity.ProductListActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.CustomGridLayoutManager;
import thegroceryshop.com.custom.StrikeTextView;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Category;
import thegroceryshop.com.modal.Product;

/**
 * Created by mohitd on 16-Mar-17.
 */

public class ProductListGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PRODUCT = 1;
    private Context mContext;
    private ArrayList<Product> list_products;
    private ArrayList<Category> list_categories;
    private OnProductClickLIstener onProductClickLIstener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoadMore;
    private boolean isLoading;

    public ProductListGridAdapter(Context mContext, ArrayList<Product> list_products, final RecyclerView recyl) {
        this.mContext = mContext;
        this.list_products = list_products;

        if(recyl != null){
            recyl.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if(!(dx == 0 && dy == 0)){
                        if (isLoadMore) {
                            GridLayoutManager gridLayoutManager= (GridLayoutManager)recyclerView.getLayoutManager();
                            if(!isLoading && (gridLayoutManager != null && gridLayoutManager.findLastVisibleItemPosition() >= getItemCount()- 6)){
                                if (mOnLoadMoreListener != null) {
                                    mOnLoadMoreListener.onLoadMore();
                                }
                                isLoading = true;
                            }
                        }
                    }
                }
            });
        }
    }

    public void setCategoryList(ArrayList<Category> list_categories){
        this.list_categories = list_categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_PRODUCT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_listing_item, parent, false);
            return new ProductViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_hearder_item, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductViewHolder) {

            final ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            final Product product = list_products.get(position);

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
                    productViewHolder.txt_offer_price.setText(product.getCurrency()+ AppConstants.SPACE + product.getOfferedPrice());
                }else{
                    productViewHolder.txt_offer_price.setText(product.getOfferedPrice() + AppConstants.SPACE + product.getCurrency());
                }

                String final_text = productViewHolder.txt_offer_price.getText().toString();
                Spannable wordtoSpan = new SpannableString(final_text);
                wordtoSpan.setSpan(
                        new AbsoluteSizeSpan(18),
                        final_text.indexOf(product.getCurrency()),
                        (final_text.indexOf(product.getCurrency()) + product.getCurrency().length()),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                productViewHolder.txt_offer_price.setText(wordtoSpan);

            } else {
                productViewHolder.txt_actual_price.setVisibility(View.GONE);
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    productViewHolder.txt_offer_price.setText(product.getCurrency() + AppConstants.SPACE + product.getActualPrice());
                }else{
                    productViewHolder.txt_offer_price.setText(product.getActualPrice() + AppConstants.SPACE + product.getCurrency());
                }
            }

            productViewHolder.img_added_to_list.setImageDrawable(
                    product.isAddedToWishList() ? ContextCompat.getDrawable(mContext, R.drawable.icon_heart_filled) : ContextCompat.getDrawable(mContext, R.drawable.icon_heart)
            );

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

            if (!ValidationUtil.isNullOrBlank(product.getImage())) {
                final Drawable alternate_image = ContextCompat.getDrawable(mContext, R.mipmap.place_holder);
                ImageLoader.getInstance().displayImage(product.getImage(), productViewHolder.img_product, OnlineMartApplication.intitOptions1(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        productViewHolder.loader_img.showProgress();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        productViewHolder.img_product.setImageDrawable(alternate_image);
                        productViewHolder.loader_img.showContent();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        productViewHolder.img_product.setImageBitmap(loadedImage);
                        productViewHolder.loader_img.showContent();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        //productViewHolder.img_product.setImageDrawable(alternate_image);
                        //productViewHolder.loader_img.showContent();
                    }
                });
            } else {
                ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, productViewHolder.img_product, OnlineMartApplication.intitOptions1());
            }

            productViewHolder.txt_add_to_cart.setTag(position);
            productViewHolder.lyt_product.setTag(position);
            productViewHolder.txt_plus.setTag(position);
            productViewHolder.txt_minus.setTag(position);

            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

            if(cartItem != null){
                product.setAddedToCart(true);
                product.setSelectedQuantity(cartItem.getSelectedQuantity());
                productViewHolder.txt_add_to_cart.setVisibility(View.GONE);
                productViewHolder.loader_quantity.setVisibility(View.VISIBLE);
                productViewHolder.lyt_quantity.setVisibility(View.VISIBLE);
                productViewHolder.txt_order_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
            }else{
                product.setAddedToCart(false);
                product.setSelectedQuantity(0);
                productViewHolder.txt_add_to_cart.setVisibility(View.VISIBLE);
                productViewHolder.lyt_quantity.setVisibility(View.GONE);
            }

            if (product.isAddedToCart()) {
                productViewHolder.loader_quantity.setVisibility(View.VISIBLE);
                productViewHolder.lyt_quantity.setVisibility(View.VISIBLE);
                productViewHolder.txt_add_to_cart.setVisibility(View.GONE);
                productViewHolder.txt_order_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                productViewHolder.lyt_root.setSelected(true);
            } else {
                productViewHolder.loader_quantity.setVisibility(View.GONE);
                productViewHolder.lyt_quantity.setVisibility(View.GONE);
                productViewHolder.txt_add_to_cart.setVisibility(View.VISIBLE);
                productViewHolder.lyt_root.setSelected(false);
                productViewHolder.txt_order_quantity.setText(AppConstants.BLANK_STRING);
            }

            if(product.isLoadingInfo()){
                productViewHolder.loader_quantity.showContent();
            }else{
                productViewHolder.loader_quantity.showContent();
            }

            productViewHolder.lyt_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickLIstener != null)
                    {
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
                            onProductClickLIstener.onAddToCart((int) v.getTag(), v, productViewHolder);
                        }

                    }
                }
            });

            productViewHolder.txt_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (onProductClickLIstener != null) {
                        onProductClickLIstener.onIncreaseQuantity(position, productViewHolder);
                    }

                }
            });

            productViewHolder.txt_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickLIstener != null) {
                        onProductClickLIstener.onDecreaseQuantity((int) v.getTag(), productViewHolder);
                    }
                }
            });

        } else {
            HeaderViewHolder headerHolder = (HeaderViewHolder)holder;
            if(list_categories != null){
                headerHolder.loader.setVisibility(View.VISIBLE);
                headerHolder.recyl_categories.setHasFixedSize(true);
                headerHolder.recyl_categories.setLayoutManager(new CustomGridLayoutManager(mContext, 4));

                if(list_categories == null || list_categories.size() == 0){
                    headerHolder.loader.showContent();
                }else{
                    headerHolder.loader.showContent();
                    CategoryListGridAdapter categoryListGridAdapter = new CategoryListGridAdapter(mContext, list_categories);
                    headerHolder.recyl_categories.setNestedScrollingEnabled(false);
                    headerHolder.recyl_categories.setAdapter(categoryListGridAdapter);

                    categoryListGridAdapter.setOnCategoryClickListener(new CategoryListGridAdapter.OnCategoryClickListener() {
                        @Override
                        public void onCategoryClick(int position) {

                            Category categoryItem = list_categories.get(position);
                            Intent productListintent = new Intent(mContext, ProductListActivity.class);

                            productListintent.putExtra(ProductListActivity.CATEGORY_ID, categoryItem.getId());
                            productListintent.putExtra(ProductListActivity.CATEGORY_NAME, categoryItem.getLabel());

                            if (!(categoryItem.getCategoryLevel() + AppConstants.BLANK_STRING).equalsIgnoreCase("1")) {
                                productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, true);
                            } else {
                                productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);
                            }

                            if (categoryItem.getAdminCategoryLevel() == 3) {
                                productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, false);
                            } else {
                                productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, true);
                            }

                            mContext.startActivity(productListintent);
                        }
                    });
                }
            }else{
                headerHolder.loader.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list_products.size();
    }

    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public void updateUI(final ProductViewHolder productViewHolder, int position) {

        final Product product = list_products.get(position);

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

        productViewHolder.img_added_to_list.setImageDrawable(
                product.isAddedToWishList() ? ContextCompat.getDrawable(mContext, R.drawable.icon_heart_filled) : ContextCompat.getDrawable(mContext, R.drawable.icon_heart)
        );

        if (product.isOffer()) {
            productViewHolder.txt_actual_price.setText(product.getActualPrice()/* + AppConstants.SPACE + product.getCurrency()*/);
            productViewHolder.txt_actual_price.setVisibility(View.VISIBLE);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                productViewHolder.txt_offer_price.setText( product.getCurrency()+ AppConstants.SPACE +product.getOfferedPrice());
            }else{
                productViewHolder.txt_offer_price.setText(product.getOfferedPrice() + AppConstants.SPACE + product.getCurrency());
            }

        } else {
            productViewHolder.txt_actual_price.setVisibility(View.GONE);


            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                productViewHolder.txt_offer_price.setText( product.getCurrency()+ AppConstants.SPACE +product.getActualPrice());
            }else{
                productViewHolder.txt_offer_price.setText(product.getActualPrice() + AppConstants.SPACE + product.getCurrency());
            }
        }

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

        productViewHolder.txt_add_to_cart.setTag(position);
        productViewHolder.lyt_product.setTag(position);
        productViewHolder.txt_plus.setTag(position);
        productViewHolder.txt_minus.setTag(position);

        CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

        if(cartItem != null){
            product.setAddedToCart(true);
            product.setSelectedQuantity(cartItem.getSelectedQuantity());
            productViewHolder.txt_add_to_cart.setVisibility(View.GONE);
            productViewHolder.loader_quantity.setVisibility(View.VISIBLE);
            productViewHolder.lyt_quantity.setVisibility(View.VISIBLE);
            productViewHolder.txt_order_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
        }else{
            product.setAddedToCart(false);
            product.setSelectedQuantity(0);
            productViewHolder.txt_add_to_cart.setVisibility(View.VISIBLE);
            productViewHolder.lyt_quantity.setVisibility(View.GONE);
        }

        if (product.isAddedToCart()) {
            productViewHolder.loader_quantity.setVisibility(View.VISIBLE);
            productViewHolder.lyt_quantity.setVisibility(View.VISIBLE);
            productViewHolder.txt_add_to_cart.setVisibility(View.GONE);

                /*if (product.getSelectedQuantity() == 0) {
                    product.setSelectedQuantity(1);
                }*/
            productViewHolder.txt_order_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
            productViewHolder.lyt_root.setSelected(true);
        } else {
            productViewHolder.loader_quantity.setVisibility(View.GONE);
            productViewHolder.lyt_quantity.setVisibility(View.GONE);
            productViewHolder.txt_add_to_cart.setVisibility(View.VISIBLE);
            productViewHolder.lyt_root.setSelected(false);
            productViewHolder.txt_order_quantity.setText(AppConstants.BLANK_STRING);
        }

        if(product.isLoadingInfo()){
            productViewHolder.loader_quantity.showProgress();
        }else{
            productViewHolder.loader_quantity.showContent();
        }

        productViewHolder.lyt_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onProductClickLIstener != null)
                {
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
                        onProductClickLIstener.onAddToCart((int) v.getTag(), v, productViewHolder);
                    }

                }
            }
        });

        productViewHolder.txt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (onProductClickLIstener != null) {
                    onProductClickLIstener.onIncreaseQuantity(position, productViewHolder);
                        /*if(Integer.parseInt(productViewHolder.txt_order_quantity.getText().toString()) < list_products.get(position).getMaxQuantity()){
                            productViewHolder.txt_order_quantity.setText(
                                    AppConstants.BLANK_STRING
                                            +(Integer.parseInt(productViewHolder.txt_order_quantity.getText().toString())
                                            +1));
                        }*/
                }

            }
        });

        productViewHolder.txt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onProductClickLIstener != null) {
                    onProductClickLIstener.onDecreaseQuantity((int) v.getTag(), productViewHolder);

                        /*if (Integer.parseInt(productViewHolder.txt_order_quantity.getText().toString()) !=0) {
                            productViewHolder.txt_order_quantity.setText(
                                    AppConstants.BLANK_STRING
                                            +(Integer.parseInt(productViewHolder.txt_order_quantity.getText().toString())
                                            -1));
                        }else{
                            productViewHolder.txt_order_quantity.setText("0");
                        }*/
                }
            }
        });

    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_sold_out, txt_fast_shipping, txt_label, txt_brand_name, txt_quantity, txt_offer_price, txt_add_to_cart, txt_plus, txt_minus, txt_order_quantity, txt_discount;

        public StrikeTextView txt_actual_price;
        public ImageView img_product;
        public FrameLayout lyt_product;
        public RelativeLayout lyt_quantity;
        public LinearLayout lyt_root;
        private LoaderLayout loader_quantity;
        private LoaderLayout loader_img;
        private ImageView img_added_to_list;

        public ProductViewHolder(View view) {
            super(view);

            txt_sold_out = view.findViewById(R.id.product_item_txt_sold_out);
            txt_fast_shipping = view.findViewById(R.id.product_item_txt_fast_shipping);
            txt_label = view.findViewById(R.id.product_item_txt_label);
            txt_brand_name = view.findViewById(R.id.product_item_txt_brand_name);
            txt_quantity = view.findViewById(R.id.product_item_txt_quantity);
            txt_offer_price = view.findViewById(R.id.product_item_txt_final_price);
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
            loader_quantity = view.findViewById(R.id.product_item_lyt_loader_quantity);
            loader_img = view.findViewById(R.id.product_item_lyt_productImg);
            img_added_to_list = view.findViewById(R.id.product_list_img_added_to_list);

            //txt_offer_price.setPaintFlags(txt_offer_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        LoaderLayout loader;
        RecyclerView recyl_categories;

        public HeaderViewHolder(View view) {
            super(view);

            loader = view.findViewById(R.id.list_product_loader_categories);
            recyl_categories = view.findViewById(R.id.gridViewProductCategories);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list_products.get(position) == null) {
            return TYPE_HEADER;
        } else {
            return TYPE_PRODUCT;
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnProductClickLIstener(OnProductClickLIstener onProductClickLIstener) {
        this.onProductClickLIstener = onProductClickLIstener;
    }

    public interface OnProductClickLIstener {

        void onProductClick(int position);

        void onAddToCart(int position, View v, ProductViewHolder productViewHolder);

        void onIncreaseQuantity(int position, ProductViewHolder productViewHolder);

        void onDecreaseQuantity(int position, ProductViewHolder productViewHolder);

        void onNotifyMe(int tag);
    }

    public void setLoaded() {
        isLoading = false;
    }
}
