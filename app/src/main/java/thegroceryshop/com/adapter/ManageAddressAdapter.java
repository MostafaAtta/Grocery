package thegroceryshop.com.adapter;

/*
 * Created by umeshk on 4/13/2017.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.ShippingAddress;

public class ManageAddressAdapter extends RecyclerView.Adapter<ManageAddressAdapter.MyViewHolder> {

    private List<ShippingAddress> addressList;
    private Context mContext;
    public OnAddressSelectListener onAddressSelectListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_street, txtViewEdit, txtViewDelete, txt_address_line_1, txt_address_line_2, txt_address_line_3, txt_address_line_4,manage_address_txt_building_name;
        private View view_disable;

        MyViewHolder(View v) {
            super(v);
            txtViewEdit = v.findViewById(R.id.txtViewEdit);
            txtViewDelete = v.findViewById(R.id.txtViewDelete);
            txt_name = v.findViewById(R.id.manage_address_txt_name);
            txt_address_line_1 = v.findViewById(R.id.manage_address_txt_address_line1);
            txt_address_line_2 = v.findViewById(R.id.manage_address_txt_address_line2);
            txt_address_line_3 = v.findViewById(R.id.manage_address_txt_address_line3);
            txt_address_line_4 = v.findViewById(R.id.manage_address_txt_address_line4);
            txt_street = v.findViewById(R.id.manage_address_txt_street_name);
            manage_address_txt_building_name = v.findViewById(R.id.manage_address_txt_building_name);
            view_disable = v.findViewById(R.id.manage_address_view_disable);
        }
    }

    public ManageAddressAdapter(Context mContext, List<ShippingAddress> addressList) {
        this.mContext = mContext;
        this.addressList = addressList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_manage_shipping_address, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ShippingAddress shippingAddress = addressList.get(position);

        String name = AppConstants.BLANK_STRING;
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getFirst_name())) {
            name = shippingAddress.getFirst_name();
        }

        if (!ValidationUtil.isNullOrBlank(shippingAddress.getLast_name())) {
            if (name.length() == 0) {
                name = shippingAddress.getLast_name();
            } else {
                name = name + AppConstants.SPACE + shippingAddress.getLast_name();
            }
        }

        if (!ValidationUtil.isNullOrBlank(name)) {
            holder.txt_name.setText(name);
        } else {
            holder.txt_name.setText(mContext.getString(R.string.na));
        }

        String address_line1 = AppConstants.BLANK_STRING;
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getAddress_name())) {
            address_line1 = shippingAddress.getAddress_name() + "(" + shippingAddress.getRegionName() + ")";
        }

        if (!ValidationUtil.isNullOrBlank(address_line1)) {
            holder.txt_address_line_1.setVisibility(View.VISIBLE);
            holder.txt_address_line_1.setText(address_line1.toUpperCase());
        } else {
            holder.txt_address_line_1.setVisibility(View.GONE);
        }

        if (!ValidationUtil.isNullOrBlank(shippingAddress.getStreetName())) {
            holder.txt_street.setVisibility(View.VISIBLE);
            holder.txt_street.setText(mContext.getResources().getString(R.string.street)+ " : " + shippingAddress.getStreetName());
        } else {
            holder.txt_street.setVisibility(View.GONE);
        }

        if (!ValidationUtil.isNullOrBlank(shippingAddress.getBuilding_name())) {
            holder.manage_address_txt_building_name.setText(mContext.getResources().getString(R.string.building_name_num)+ " " + shippingAddress.getBuilding_name());
            holder.manage_address_txt_building_name.setVisibility(View.VISIBLE);
        }else
        {
            holder.manage_address_txt_building_name.setVisibility(View.GONE);
        }

        String address_line2 = AppConstants.BLANK_STRING;
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getFloor_number())) {
            address_line2 = mContext.getString(R.string.floor) + AppConstants.SPACE + "# " + shippingAddress.getFloor_number();
        }
        if (!ValidationUtil.isNullOrBlank(address_line2)) {
            holder.txt_address_line_2.setVisibility(View.VISIBLE);
            holder.txt_address_line_2.setText(address_line2);
        } else {
            holder.txt_address_line_2.setVisibility(View.GONE);
        }

        String address_line3 = AppConstants.BLANK_STRING;
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getUnit_number())) {
            address_line3 = mContext.getString(R.string.unit) + AppConstants.SPACE + "# " + shippingAddress.getUnit_number();
        }
        if (!ValidationUtil.isNullOrBlank(address_line3)) {
            holder.txt_address_line_3.setVisibility(View.VISIBLE);
            holder.txt_address_line_3.setText(address_line3);
        } else {
            holder.txt_address_line_3.setVisibility(View.GONE);
        }

        String address_line4 = AppConstants.BLANK_STRING;
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getArea())) {
            address_line4 = shippingAddress.getArea();
        }
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getCity())) {
            if (address_line4.length() == 0) {
                address_line4 = shippingAddress.getCity();
            } else {
                address_line4 = address_line4 + ", " + shippingAddress.getCity();
            }
        }
        if (!ValidationUtil.isNullOrBlank(shippingAddress.getCountry())) {
            if (address_line4.length() == 0) {
                address_line4 = shippingAddress.getCountry();
            } else {
                address_line4 = address_line4 + ", " + shippingAddress.getCountry();
            }
        }

        if (!ValidationUtil.isNullOrBlank(address_line1)) {
            holder.txt_address_line_4.setVisibility(View.VISIBLE);
            holder.txt_address_line_4.setText(address_line4);
        } else {
            holder.txt_address_line_4.setVisibility(View.GONE);
        }

        holder.view_disable.setVisibility(shippingAddress.isEnable() ? View.GONE : View.VISIBLE);
        holder.view_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.showErrorDialog(mContext, mContext.getString(R.string.error_address_region));
            }
        });

        holder.txtViewEdit.setTag(position);
        holder.txtViewDelete.setTag(position);
        holder.txtViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAddressSelectListener != null)
                {
                    onAddressSelectListener.onAddressSelectListener((int)v.getTag(), "edit");
                }
            }
        });

        holder.txtViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAddressSelectListener != null){
                    onAddressSelectListener.onAddressSelectListener((int)v.getTag(), "delete");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void setOnAddressSelectListener(OnAddressSelectListener onAddressSelectListener) {
        this.onAddressSelectListener = onAddressSelectListener;
    }

    public interface OnAddressSelectListener {
        void onAddressSelectListener(int position, String type);
    }
}
