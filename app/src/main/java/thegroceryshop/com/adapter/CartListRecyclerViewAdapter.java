package thegroceryshop.com.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.CartItem;

/**
 * Created by JUNE on 6/10/2016.
 */
public class CartListRecyclerViewAdapter extends RecyclerView.Adapter<CartListRecyclerViewAdapter.ViewHolder> {

    View view1;
    ViewHolder viewHolder1;
    AppCompatActivity appCompatActivity;
    private ArrayList<CartItem> list_cart;
    private OnQuantityChangedListener onQuantityChangedListener;
    private OnAddToListListener onAddToListListener;

    public CartListRecyclerViewAdapter(AppCompatActivity appCompatActivity, ArrayList<CartItem> list_cart) {
        this.list_cart = list_cart;
        this.appCompatActivity = appCompatActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView textViewProductName, textViewBrandName, textViewProductQuantity, textViewProductPrice, textViewProductItems, txt_price, txt_saved, txt_tax_charges;
        public ImageView imageViewProduct, imageViewMinus, imageViewPlus;
        private LoaderLayout loader_images;
        private ImageView img_add_to_list;

        public ViewHolder(View v) {

            super(v);

            textViewProductName    = v.findViewById(R.id.textViewProductName);
            textViewBrandName    = v.findViewById(R.id.textViewBrandName);
            textViewProductQuantity    = v.findViewById(R.id.textViewProductQuantity);
            textViewProductPrice    = v.findViewById(R.id.textViewProductPrice);
            textViewProductItems    = v.findViewById(R.id.textViewProductItems);
            imageViewProduct = v. findViewById(R.id.imageViewProduct);
            imageViewMinus = v. findViewById(R.id.imageViewMinus);
            imageViewPlus = v. findViewById(R.id.imageViewPlus);
            loader_images = v.findViewById(R.id.loader_image);
            img_add_to_list = v.findViewById(R.id.img_add_to_list);

            txt_price = v.findViewById(R.id.textViewProductPrice);
            txt_saved = v.findViewById(R.id.textViewProductSaved);
            txt_tax_charges = v.findViewById(R.id.textViewProductTaxCharges);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(appCompatActivity).inflate(R.layout.layout_cart_list_item, parent, false);
        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        CartItem cartItem = list_cart.get(position);

        if(!ValidationUtil.isNullOrBlank(cartItem.getLabel())){
            holder.textViewProductName.setText(cartItem.getLabel());
        }

        holder.img_add_to_list.setImageDrawable(
                (cartItem.isAddedToWishList() ? ContextCompat.getDrawable(appCompatActivity, R.drawable.icon_heart_filled): ContextCompat.getDrawable(appCompatActivity, R.drawable.icon_heart))
        );

        if (!ValidationUtil.isNullOrBlank(cartItem.getImage())) {
            if(holder.imageViewProduct.getTag() != null && ((String)(holder.imageViewProduct.getTag())).equalsIgnoreCase(cartItem.getImage())){
                holder.imageViewPlus.setTag(cartItem.getImage());
            }else{
                final Drawable alternate_image = ContextCompat.getDrawable(appCompatActivity, R.mipmap.place_holder);
                ImageLoader.getInstance().displayImage(cartItem.getImage(), holder.imageViewProduct, OnlineMartApplication.intitOptions1(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.loader_images.showProgress();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.loader_images.showContent();
                        holder.imageViewProduct.setImageDrawable(alternate_image);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.loader_images.showContent();
                        holder.imageViewProduct.setImageBitmap(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        holder.loader_images.showContent();
                        holder.imageViewProduct.setImageDrawable(alternate_image);
                    }
                });
            }
        } else {
            ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, holder.imageViewProduct, OnlineMartApplication.intitOptions1());
        }

        if(!ValidationUtil.isNullOrBlank(cartItem.getQuantity())){
            holder.textViewProductQuantity.setText(cartItem.getQuantity());
        }

        if(!ValidationUtil.isNullOrBlank(cartItem.getBrandName())){
            holder.textViewBrandName.setText(cartItem.getBrandName());
        }

        holder.textViewProductItems.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);

        holder.imageViewPlus.setTag(position);
        holder.imageViewMinus.setTag(position);
        holder.imageViewProduct.setTag(cartItem.getImage());

        holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onQuantityChangedListener != null) {
                    onQuantityChangedListener.onQuantityChanged(true, (int)v.getTag(), holder);
                    if(Integer.parseInt(holder.textViewProductItems.getText().toString()) < list_cart.get(position).getMaxQuantity()){
                        holder.textViewProductItems.setText(""+(Integer.parseInt(holder.textViewProductItems.getText().toString())+1));
                    }
                }
            }
        });

        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onQuantityChangedListener != null){
                    if (Integer.parseInt(holder.textViewProductItems.getText().toString()) !=0) {
                        holder.textViewProductItems.setText(""+(Integer.parseInt(holder.textViewProductItems.getText().toString())-1));
                    }else {
                        holder.textViewProductItems.setText("0");
                    }
                    onQuantityChangedListener.onQuantityChanged(false, (int)v.getTag(), holder);
                }
            }
        });

        holder.img_add_to_list.setTag(position);
        holder.img_add_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onAddToListListener != null){
                    onAddToListListener.onAddToWishList((int)view.getTag());
                }
            }
        });

        float final_price = 0.0f;
        if(!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())){
            final_price = Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity();
        }

        float final_tax_charges = cartItem.getTaxCharges() * cartItem.getSelectedQuantity();

        if (ValidationUtil.isNullOrBlank(final_tax_charges))
        {
            final_tax_charges = 0.00f;
        }

        float final_saved = 0.00f;
        if(cartItem.isOffer()){
            final_saved = Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity();
        }

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            holder.txt_price.setText(cartItem.getCurrency() + AppConstants.SPACE + AppUtil.mSetRoundUpPrice(""+final_price));
            holder.txt_saved.setText(cartItem.getCurrency() + AppConstants.SPACE+ AppUtil.mSetRoundUpPrice(""+final_saved));
        }else{
            holder.txt_price.setText(AppUtil.mSetRoundUpPrice(""+final_price) + AppConstants.SPACE + cartItem.getCurrency());
            holder.txt_saved.setText(AppUtil.mSetRoundUpPrice(""+final_saved) + AppConstants.SPACE + cartItem.getCurrency());
        }

    }

    public void updateUI(final ViewHolder holder, final int position) {

        CartItem cartItem = list_cart.get(position);

        if(!ValidationUtil.isNullOrBlank(cartItem.getLabel())){
            holder.textViewProductName.setText(cartItem.getLabel());
        }

        if(!ValidationUtil.isNullOrBlank(cartItem.getQuantity())){
            holder.textViewProductQuantity.setText(cartItem.getQuantity());
        }

        if(!ValidationUtil.isNullOrBlank(cartItem.getBrandName())){
            holder.textViewBrandName.setText(cartItem.getBrandName());
        }

        holder.textViewProductItems.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
        holder.img_add_to_list.setImageDrawable(
                cartItem.isAddedToWishList() ?
                        ContextCompat.getDrawable(appCompatActivity, R.drawable.icon_heart_filled) :
                        ContextCompat.getDrawable(appCompatActivity, R.drawable.icon_heart));

        holder.imageViewPlus.setTag(position);
        holder.imageViewMinus.setTag(position);
        holder.imageViewProduct.setTag(cartItem.getImage());

        holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onQuantityChangedListener != null) {
                    onQuantityChangedListener.onQuantityChanged(true, (int)v.getTag(), holder);
                    if(Integer.parseInt(holder.textViewProductItems.getText().toString()) < list_cart.get(position).getMaxQuantity()){
                        holder.textViewProductItems.setText(""+(Integer.parseInt(holder.textViewProductItems.getText().toString())+1));
                    }
                }
            }
        });

        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onQuantityChangedListener != null){
                    if (Integer.parseInt(holder.textViewProductItems.getText().toString()) !=0) {
                        holder.textViewProductItems.setText(""+(Integer.parseInt(holder.textViewProductItems.getText().toString())-1));
                    }else {
                        holder.textViewProductItems.setText("0");
                    }
                    onQuantityChangedListener.onQuantityChanged(false, (int)v.getTag(), holder);
                }
            }
        });

        float final_price = 0.0f;
        if(!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())){
            final_price = Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity();
        }

        float final_tax_charges = cartItem.getTaxCharges() * cartItem.getSelectedQuantity();

        if (ValidationUtil.isNullOrBlank(final_tax_charges))
        {
            final_tax_charges = 0.00f;
        }

        float final_saved = 0.00f;
        if(cartItem.isOffer()){
            final_saved = Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity();
        }

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            holder.txt_price.setText(cartItem.getCurrency() + AppConstants.SPACE + AppUtil.mSetRoundUpPrice(""+final_price));
            holder.txt_saved.setText(cartItem.getCurrency() + AppConstants.SPACE + AppUtil.mSetRoundUpPrice(""+final_saved));
        }else{
            holder.txt_price.setText(AppUtil.mSetRoundUpPrice(""+final_price) + AppConstants.SPACE + cartItem.getCurrency());
            holder.txt_saved.setText(AppUtil.mSetRoundUpPrice(""+final_saved) + AppConstants.SPACE + cartItem.getCurrency());
        }
    }

    @Override
    public int getItemCount() {

        return list_cart.size();
    }

    public void setOnQuantityChangedListener(OnQuantityChangedListener onQuantityChangedListener){
        this.onQuantityChangedListener = onQuantityChangedListener;
    }

    public void setOnAddToListListener(OnAddToListListener onAddToListListener){
        this.onAddToListListener = onAddToListListener;
    }

    public interface OnQuantityChangedListener{
        void onQuantityChanged(boolean isIncreased, int position, ViewHolder holder);
    }

    public interface OnAddToListListener{
        void onAddToWishList(int position);
        void onAddedToWishList(int position);
    }
}
