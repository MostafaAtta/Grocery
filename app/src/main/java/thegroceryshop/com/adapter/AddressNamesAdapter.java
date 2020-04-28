package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 4/6/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Item;

public class AddressNamesAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> list;
    private boolean isAddStringOnTop = false;
    private String hintText;

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public AddressNamesAdapter(Context context, ArrayList<Item> list) {
        this.list = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = mLayoutInflater.inflate(R.layout.country_spinner_dropdown_view, parent, false);

        TextView text_label = row.findViewById(R.id.textViewCountryName);
        text_label.setText(list.get(position).getItemLabel());
        return row;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = mLayoutInflater.inflate(R.layout.simple_spinner_item, parent, false);

        TextView text_label = row.findViewById(android.R.id.text1);
        text_label.setTextColor(Color.WHITE);
        row.setBackgroundColor(Color.parseColor("#00000000"));
        if (ValidationUtil.isNullOrBlank(hintText)) {
            text_label.setText(list.get(position).getItemLabel());
        } else {
            if (position == 0) {
                text_label.setText(list.get(position).getItemLabel());
            } else {
                text_label.setText(list.get(position).getItemLabel());
            }
        }
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
}
