package thegroceryshop.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Item;

public class SpinnerItemAdapterNormal extends BaseAdapter {

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

    public SpinnerItemAdapterNormal(Context context, ArrayList<Item> list) {
        this.list = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public SpinnerItemAdapterNormal(Context context, ArrayList<Item> list, String whatToAddOnListTop) {
        this.list = list;
        if (whatToAddOnListTop != null && !whatToAddOnListTop.equalsIgnoreCase("")) {

            if (list.size() == 0 || !this.list.get(0).getItemId().equalsIgnoreCase(AppConstants.BLANK_STRING)) {
                this.list.add(0, new Item(AppConstants.BLANK_STRING, whatToAddOnListTop, false));
                isAddStringOnTop = true;
            } else if (this.list.get(0).getItemLabel().equalsIgnoreCase(whatToAddOnListTop)) {
                isAddStringOnTop = true;
            }
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    public SpinnerItemAdapterNormal(Context context, ArrayList<Item> list, String whatToAddOnListTop, Spinner spinner) {
        this.list = list;
        if (whatToAddOnListTop != null && !whatToAddOnListTop.equalsIgnoreCase("")) {

            if (list.size() == 0 || !this.list.get(0).getItemId().equalsIgnoreCase(AppConstants.BLANK_STRING)) {
                this.list.add(0, new Item(AppConstants.BLANK_STRING, whatToAddOnListTop, false));
                isAddStringOnTop = true;
            } else if (this.list.get(0).getItemLabel().equalsIgnoreCase(whatToAddOnListTop)) {
                isAddStringOnTop = true;
            }
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = mLayoutInflater.inflate(R.layout.layout_spinner_dropdown_view, parent, false);

        TextView text_label = row.findViewById(R.id.item_label);
        text_label.setText(list.get(position).getItemLabel());

        CheckBox checkBox = row.findViewById(R.id.item_check_right);
        checkBox.setVisibility(View.GONE);

        return row;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = mLayoutInflater.inflate(R.layout.layout_spinner_item_view1, parent, false);

        TextView text_label = row.findViewById(R.id.item_label);

        if (ValidationUtil.isNullOrBlank(hintText)) {
            text_label.setText(list.get(position).getItemLabel());
        } else {
            if (position == 0) {
                text_label.setText(list.get(position).getItemLabel());
            } else {
                text_label.setText(hintText + " : " + list.get(position).getItemLabel());
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