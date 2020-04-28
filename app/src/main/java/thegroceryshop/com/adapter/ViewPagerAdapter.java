package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;


public class ViewPagerAdapter extends PagerAdapter {
 
    private Context mContext;
    private int[] mResources;
    private String[] description_txt;
    private String[] title_txt_1;
    public ViewPagerAdapter(Context mContext, int[] mResources, String[] description_txt,String[] title_txt) {
        this.mContext = mContext;
        this.mResources = mResources;
        this.description_txt = description_txt;
        this.title_txt_1 = title_txt;
    }
 
    @Override
    public int getCount() {
        return mResources.length;
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView;

        itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
        ImageView imageView = itemView.findViewById(R.id.img_pager_item);
        TextView descptn_txt = itemView.findViewById(R.id.descptn_txt);
        TextView title_txt = itemView.findViewById(R.id.title_txt);
        container.addView(itemView);

        imageView.setImageResource(mResources[position]);
        descptn_txt.setText(description_txt[position]);
        title_txt.setText(title_txt_1[position]);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            //itemView.setRotation(0);
        }else{
            //itemView.setRotation(180);
        }

        return itemView;
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((RelativeLayout)object);
    }
}