package thegroceryshop.com.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Region;

/**
 * Created by JUNE on 6/10/2016.
 */
public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

    private ArrayList<Region> list_regions;
    private Context context;
    private OnRegionChangedListener onRegionChangedListener;

    public RegionAdapter(Context context, ArrayList<Region> list_regions) {
        this.context = context;
        this.list_regions = list_regions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        RadioButton radio_region;
        LinearLayout lyt_root;
        Typeface typeface;

        public ViewHolder(View view) {
            super(view);
            radio_region    = view.findViewById(R.id.radio_region);
            lyt_root = view.findViewById(R.id.root);
            typeface = radio_region.getTypeface();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.lyt_region_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Region region = list_regions.get(position);
        holder.lyt_root.setTag(position);

        if(!ValidationUtil.isNullOrBlank(region.getRegionName())){
            holder.radio_region.setText(region.getRegionName());
            holder.radio_region.setChecked(region.isSelected());
        }else{
            holder.radio_region.setText(AppConstants.BLANK_STRING);
            holder.radio_region.setChecked(false);
        }

        if(region.isSelected()){
            holder.radio_region.setTypeface(holder.typeface, Typeface.BOLD);
        }else{
            holder.radio_region.setTypeface(holder.typeface, Typeface.NORMAL);
        }

        holder.lyt_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onRegionChangedListener != null){
                    int position = (int)view.getTag();
                    if(!list_regions.get(position).getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId())){
                        onRegionChangedListener.onRegionChanged(list_regions.get(position));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_regions.size();
    }

    public void setOnRegionChangedListener(OnRegionChangedListener onRegionChangedListener){
        this.onRegionChangedListener = onRegionChangedListener;
    }

    public interface OnRegionChangedListener{
        void onRegionChanged(Region selected_region);
    }
}
