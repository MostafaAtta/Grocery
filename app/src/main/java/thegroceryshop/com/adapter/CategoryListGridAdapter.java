package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Category;

/**
 * Created by mohitd on 16-Mar-17.
 */

public class CategoryListGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Category> list_category;
    private OnCategoryClickListener onCategoryClickListener;

    public CategoryListGridAdapter(Context mContext, ArrayList<Category> list_category) {
        this.list_category = list_category;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_category_listing_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoryViewHolder) {
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            Category category = list_category.get(position);

            if (!ValidationUtil.isNullOrBlank(category.getLabel())) {
                categoryViewHolder.txt_label.setText(category.getLabel());
            }

            if (!ValidationUtil.isNullOrBlank(category.getImage())) {
                ImageLoader.getInstance().displayImage(category.getImage(), categoryViewHolder.img_category, OnlineMartApplication.intitOptions());
            } else {
                ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, categoryViewHolder.img_category, OnlineMartApplication.intitOptions());
            }

            categoryViewHolder.lyt_category.setTag(position);
            categoryViewHolder.lyt_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onCategoryClickListener != null){
                        onCategoryClickListener.onCaegoryClick((int)v.getTag());
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

        public CategoryViewHolder(View view) {
            super(view);

            txt_label = view.findViewById(R.id.cat_item_txt_label);
            img_category = view.findViewById(R.id.cat_item_img_category);
            lyt_category = view.findViewById(R.id.cat_item_lyt_cat);
        }
    }

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener){
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public interface OnCategoryClickListener{
        void onCaegoryClick(int position);
    }
}
