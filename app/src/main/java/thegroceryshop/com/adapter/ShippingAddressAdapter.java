package thegroceryshop.com.adapter;

/*
 * Created by rohitg on 4/7/2017.
 */

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.ShippingAddress;

public class ShippingAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ShippingAddress> listAddress;
    private Context mContext;
    private OnAdressClickListener onAdressClickListener;

    public ShippingAddressAdapter(Context mContext, List<ShippingAddress> listAddress) {
        this.mContext = mContext;
        this.listAddress = listAddress;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_shipping_address, parent, false);
        return new ShippingAddressHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ShippingAddressHolder) {
            ShippingAddressHolder shippingAddressHolder = (ShippingAddressHolder) holder;

            ShippingAddress shippingAddress = listAddress.get(position);

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
//            if (!ValidationUtil.isNullOrBlank()) {
//                shippingAddressHolder.txt_name.setText(name);
//            }

            String address_line1 = AppConstants.BLANK_STRING;
            if (!ValidationUtil.isNullOrBlank(shippingAddress.getAddress_name())) {
                address_line1 = shippingAddress.getAddress_name();
                shippingAddressHolder.txt_name.setText(shippingAddress.getAddress_name() + "(" + shippingAddress.getRegionName() +")");
            }

            if (!ValidationUtil.isNullOrBlank("")) {
                shippingAddressHolder.txt_address.setVisibility(View.VISIBLE);
                shippingAddressHolder.txt_address.setText(address_line1);
            } else {
                shippingAddressHolder.txt_address.setVisibility(View.GONE);
            }

            if (!ValidationUtil.isNullOrBlank(shippingAddress.getBuilding_name())) {
                shippingAddressHolder.address_item_txt_building.setText(mContext.getResources().getString(R.string.building_name_num)+ " " +shippingAddress.getBuilding_name());
                shippingAddressHolder.address_item_txt_building.setVisibility(View.VISIBLE);
            }else {
                shippingAddressHolder.address_item_txt_building.setVisibility(View.GONE);
            }

            if (!ValidationUtil.isNullOrBlank(shippingAddress.getStreetName())) {
                shippingAddressHolder.txt_street.setVisibility(View.VISIBLE);
                shippingAddressHolder.txt_street.setText(mContext.getResources().getString(R.string.street)+ " : " + shippingAddress.getStreetName());
            } else {
                shippingAddressHolder.txt_street.setVisibility(View.GONE);
            }

            String address_line2 = AppConstants.BLANK_STRING;

            if (!ValidationUtil.isNullOrBlank(shippingAddress.getFloor_number())) {
                address_line2 = mContext.getString(R.string.floor) + AppConstants.SPACE + "# " + shippingAddress.getFloor_number();
            }
            if (!ValidationUtil.isNullOrBlank(address_line2)) {
                shippingAddressHolder.txt_floor.setVisibility(View.VISIBLE);
                shippingAddressHolder.txt_floor.setText(address_line2);
            } else {
                shippingAddressHolder.txt_floor.setVisibility(View.GONE);
            }

            String address_line3 = AppConstants.BLANK_STRING;
            if (!ValidationUtil.isNullOrBlank(shippingAddress.getUnit_number())) {
                address_line3 = mContext.getString(R.string.unit) + AppConstants.SPACE + "# " + shippingAddress.getUnit_number();
            }
            if (!ValidationUtil.isNullOrBlank(address_line3)) {
                shippingAddressHolder.txt_flat.setVisibility(View.VISIBLE);
                shippingAddressHolder.txt_flat.setText(address_line3);
            } else {
                shippingAddressHolder.txt_flat.setVisibility(View.GONE);
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
                shippingAddressHolder.txt_area.setVisibility(View.VISIBLE);
                shippingAddressHolder.txt_area.setText(address_line4);
            } else {
                shippingAddressHolder.txt_area.setVisibility(View.GONE);
            }

            if (shippingAddress.isSelected()) {
                shippingAddressHolder.img_check.setVisibility(View.VISIBLE);
            } else {
                shippingAddressHolder.img_check.setVisibility(View.GONE);
            }

            shippingAddressHolder.view_disable.setVisibility(shippingAddress.isEnable() ? View.GONE :View.VISIBLE);
            shippingAddressHolder.view_disable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtil.showErrorDialog(mContext, mContext.getString(R.string.error_address_region));
                }
            });

            shippingAddressHolder.lyt_root.setTag(position);
            shippingAddressHolder.lyt_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdressClickListener != null) {
                        onAdressClickListener.onAddressClick((int) v.getTag());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listAddress.size();
    }

    private static class ShippingAddressHolder extends RecyclerView.ViewHolder {

        private CardView lyt_root;
        private ImageView img_check;
        private TextView txt_name;
        private TextView txt_address;
        private TextView txt_street;
        private TextView txt_flat;
        private TextView txt_floor;
        private TextView txt_area;
        private TextView address_item_txt_building;
        private View view_disable;

        ShippingAddressHolder(View v) {
            super(v);

            lyt_root = v.findViewById(R.id.address_item_lyt_root);
            img_check = v.findViewById(R.id.address_item_img_checke);
            txt_name = v.findViewById(R.id.address_item_txt_name);
            txt_address = v.findViewById(R.id.address_item_txt_address);
            txt_flat = v.findViewById(R.id.address_item_txt_flat);
            txt_floor = v.findViewById(R.id.address_item_txt_floor);
            txt_area = v.findViewById(R.id.address_item_txt_area);
            txt_street = v.findViewById(R.id.address_item_txt_street);
            address_item_txt_building = v.findViewById(R.id.address_item_txt_building);
            view_disable = v.findViewById(R.id.address_item_view_disable);
        }
    }

    public void setOnAdressClickListener(OnAdressClickListener onAdressClickListener) {
        this.onAdressClickListener = onAdressClickListener;
    }

    public interface OnAdressClickListener {
        void onAddressClick(int positon);
    }
}
