package thegroceryshop.com.slideMenu;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.sildemenu_model.NestedCategoryItem;

/**
 * Created by JUNED on 6/10/2016.
 */
public class HomeCategoryViewAdapter extends RecyclerView.Adapter<HomeCategoryViewAdapter.ViewHolder> {

    private AppCompatActivity appCompatActivity;
    private List<NestedCategoryItem> home_nestedCategory;
    private OnClickListener onClickListener;
    private NumberFormat numberFormat;

    public HomeCategoryViewAdapter(AppCompatActivity appCompatActivity, List<NestedCategoryItem> home_nestedCategory) {
        this.appCompatActivity = appCompatActivity;
        this.home_nestedCategory = home_nestedCategory;
        numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView product_name_txt, total_prodct_count, textViewSeeAll;
        public ImageView categoryImageview;
        private LinearLayout lyt_root;
        private LoaderLayout loader_img;

        public ViewHolder(View v) {

            super(v);

            product_name_txt = v.findViewById(R.id.product_name_txt);
            total_prodct_count = v.findViewById(R.id.total_prodct_count);
            textViewSeeAll = v.findViewById(R.id.textViewSeeAll);
            categoryImageview = v.findViewById(R.id.categoryImageview);
            lyt_root = v.findViewById(R.id.lyt_root);
            loader_img = v.findViewById(R.id.loader_img);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(appCompatActivity).inflate(R.layout.layout_home_product_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        NestedCategoryItem nestedCategoryItem = home_nestedCategory.get(position);
        holder.product_name_txt.setText(nestedCategoryItem.getName());

        int count =  Integer.parseInt(nestedCategoryItem.getProduct_count());

        if(count > 1){
            holder.total_prodct_count.setText(numberFormat.format(count) + AppConstants.SPACE + appCompatActivity.getString(R.string.products));
        }else{
            holder.total_prodct_count.setText(numberFormat.format(count) + AppConstants.SPACE + appCompatActivity.getString(R.string.product));
        }

        final Drawable alternate_image = ContextCompat.getDrawable(appCompatActivity, R.mipmap.place_holder);
        ImageLoader.getInstance().displayImage(nestedCategoryItem.getImage(), holder.categoryImageview, OnlineMartApplication.intitOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.loader_img.showProgress();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.loader_img.showContent();
                holder.categoryImageview.setImageDrawable(alternate_image);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.loader_img.showContent();
                holder.categoryImageview.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.loader_img.showContent();
                holder.categoryImageview.setImageDrawable(alternate_image);
            }
        });

        holder.lyt_root.setTag(position);
        holder.lyt_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null){
                    onClickListener.onClick((int)v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return home_nestedCategory.size();
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onClick(int position);
    }
}
