package thegroceryshop.com.products;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.Category;

/**
 * Created by mohitd on 16-Mar-17.
 */

public class CategoryListGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Category> list_category;
    private OnCategoryClickListener onCategoryClickListener;
    private Typeface typeface;

    public CategoryListGridAdapter(Context mContext, ArrayList<Category> list_category) {
        this.list_category = list_category;
        this.mContext = mContext;

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            typeface = ResourcesCompat.getFont(mContext, R.font.montserrat_regular);
        }else{
            typeface = ResourcesCompat.getFont(mContext, R.font.ge_ss_two_medium);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_category_listing_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoryViewHolder) {
            final CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            Category category = list_category.get(position);

            if (!ValidationUtil.isNullOrBlank(category.getLabel())) {
                if(typeface != null){
                    categoryViewHolder.txt_label.setTypeface(typeface);
                }
                categoryViewHolder.txt_label.setText(category.getLabel());
            }

            if (!ValidationUtil.isNullOrBlank(category.getImage())) {
                final Drawable alternate_image = ContextCompat.getDrawable(mContext, R.mipmap.place_holder);
                ImageLoader.getInstance().displayImage(category.getImage(), categoryViewHolder.img_category, OnlineMartApplication.intitOptions(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        categoryViewHolder.loader_img.showProgress();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        categoryViewHolder.loader_img.showContent();
                        categoryViewHolder.img_category.setImageDrawable(alternate_image);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        categoryViewHolder.loader_img.showContent();
                        categoryViewHolder.img_category.setImageBitmap(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        categoryViewHolder.loader_img.showContent();
                        categoryViewHolder.img_category.setImageDrawable(alternate_image);
                    }
                });
            } else {
                ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, categoryViewHolder.img_category, OnlineMartApplication.intitOptions());
            }

            categoryViewHolder.lyt_category.setTag(position);
            categoryViewHolder.lyt_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onCategoryClickListener != null){
                        onCategoryClickListener.onCategoryClick((int)v.getTag());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list_category.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_label;
        public ImageView img_category;
        private LinearLayout lyt_category;
        private LoaderLayout loader_img;

        public CategoryViewHolder(View view) {
            super(view);

            txt_label = view.findViewById(R.id.cat_item_txt_label);
            img_category = view.findViewById(R.id.cat_item_img_category);
            lyt_category = view.findViewById(R.id.cat_item_lyt_cat);
            loader_img = view.findViewById(R.id.cat_item_lyt_loader_img);
        }
    }

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public interface OnCategoryClickListener{

        void onCategoryClick(int position);

    }
}
