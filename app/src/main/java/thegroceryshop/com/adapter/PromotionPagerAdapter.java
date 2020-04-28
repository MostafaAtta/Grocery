package thegroceryshop.com.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AspectRatioImageView;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.sildemenu_model.PromotionCategoryItem;

public class PromotionPagerAdapter extends PagerAdapter {

    private Context mContext;
   private  ArrayList<PromotionCategoryItem> mResources;
    private OnPromotionClickListener onPromotionClickListener;

    public PromotionPagerAdapter(Context mContext, ArrayList<PromotionCategoryItem> mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }
 
    @Override
    public int getCount() {
        return mResources.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_pager_promotion, container, false);
 
        final AspectRatioImageView imageView = itemView.findViewById(R.id.img_pager_item);
        final LoaderLayout loader_img = itemView.findViewById(R.id.loader_image);
        if(!ValidationUtil.isNullOrBlank(mResources.get(position))) {
            final Drawable alternate_image = ContextCompat.getDrawable(mContext, R.mipmap.place_holder);
            ImageLoader.getInstance().displayImage(mResources.get(position).getImage(), imageView, OnlineMartApplication.intitOptions2(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    loader_img.showProgress();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    loader_img.showContent();
                    imageView.setImageDrawable(alternate_image);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    loader_img.showContent();
                    imageView.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    loader_img.showContent();
                    imageView.setImageDrawable(alternate_image);
                }
            });
        }else{
            ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, imageView, OnlineMartApplication.intitOptions2());
        }

        imageView.setTag(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPromotionClickListener != null){
                    onPromotionClickListener.onPromotionClick((Integer) v.getTag());
                }
            }
        });
        container.addView(itemView);
 
        return itemView;
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void setOnPromotionClicklIstener(OnPromotionClickListener onPromotionClickListener){
        this.onPromotionClickListener = onPromotionClickListener;
    }

    public interface OnPromotionClickListener{
        void onPromotionClick(int position);
    }
}