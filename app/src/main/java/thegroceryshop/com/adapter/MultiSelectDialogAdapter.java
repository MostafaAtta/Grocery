package thegroceryshop.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.modal.Brand;

public class MultiSelectDialogAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Brand> list;
    private OnItemSelectedListener onItemSelectedListener;
    
    public MultiSelectDialogAdapter(Context context, List<Brand> list) {
        this.list = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = mLayoutInflater.inflate(R.layout.layout_option_chooser, parent, false);

        TextView text_label = row.findViewById(R.id.item_label);
        text_label.setText(list.get(position).getString() + " (" + list.get(position).getProductCount() + ")");

        CheckBox checkBox = row.findViewById(R.id.item_check_right);
        checkBox.setChecked(list.get(position).isSelected());
        checkBox.setClickable(false);

        row.setTag(position);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemSelectedListener != null){
                    onItemSelectedListener.onItemSelected((int)v.getTag());
                }
            }
        });
        return row;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener{
        void onItemSelected(int position);
    }
}