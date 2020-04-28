package thegroceryshop.com.country_list;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.country_list.model.CountryPhoneCode;

/**
 * Created by rohitg on 12/12/2016.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private List<CountryPhoneCode> items;
    private int itemLayout;
    private Context context;
    private OnItemSelectedListener onItemSelectedListener;

    public CountryAdapter(Context context, List<CountryPhoneCode> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CountryPhoneCode item = items.get(position);

        holder.country_image.setImageResource(item.getResId());
        holder.country_name.setText(item.getName());
        holder.country_code.setText(item.getCountryCodeStr());
        holder.lyt_country.setTag(position);
        holder.lyt_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemSelectedListener != null){
                    onItemSelectedListener.onItemSelected((int)view.getTag());
                }
            }
        });

        holder.itemView.setTag(item);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView country_image;
        public TextView country_name;
        public TextView country_code;
        LinearLayout lyt_country;

        public ViewHolder(View itemView) {
            super(itemView);
            country_image = itemView.findViewById(R.id.lyt_country_image);
            country_name = itemView.findViewById(R.id.lyt_country_name);
            country_code = itemView.findViewById(R.id.lyt_country_code);
            lyt_country = itemView.findViewById(R.id.lyt_country);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
    }

    interface OnItemSelectedListener{
        void onItemSelected(int position);
    }
}
