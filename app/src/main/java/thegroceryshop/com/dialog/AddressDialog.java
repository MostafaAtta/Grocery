package thegroceryshop.com.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.adapter.AddressNamesAdapter;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.Item;
import thegroceryshop.com.modal.ShippingAddress;

import static thegroceryshop.com.R.id.editTextSplIns;


/**
 * Created by rohitg on 12/8/2016.
 */

public class AddressDialog extends Dialog implements View.OnClickListener {

    private final Context context;
    private final View mView;

    // View components
    public AppCompatSpinner spinnerAddressName;
    public AppCompatEditText edtTextFloor;
    public AppCompatEditText edtTextUnit;
    public AppCompatEditText edtTextBuildingName;
    public AppCompatEditText edtTextAddress;
    public AppCompatEditText edtTextFirstName;
    public AppCompatEditText edtTextLastName;
    public AppCompatEditText edtTextStreet;
    public TextView txtViewAddressSpecific;
    public TextView txtViewBuildingName;
    public Button btn_submit, btn_cancel;
    private OnAddAddressClickLisener onAddAddressClickLisener;

    private String addressName;
    private String floor;
    private String unit;
    private String firstName;
    private String lastName;
    private ShippingAddress shippingAddress;
    private String buildingName;
    private String address;
    private String street;

    private ArrayList<Item> addressNamesList = new ArrayList<>();
    private AddressNamesAdapter addressNamesAdapter;

    public AddressDialog(final Context context, int themeResId, ShippingAddress shippingAddress) {
        super(context, themeResId);
        this.context = context;
        this.shippingAddress = shippingAddress;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window mWindow = getWindow();
        setContentView(R.layout.lyt_dialog_address);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        mView = mWindow.getDecorView();
        mView.setBackgroundResource(android.R.color.transparent);

        spinnerAddressName = findViewById(R.id.spinnerAddressName);
        edtTextFloor = findViewById(R.id.edtTextFloor);
        edtTextUnit = findViewById(R.id.edtTextUnit);
        edtTextBuildingName = findViewById(R.id.edtTextBuildingName);
        edtTextAddress = findViewById(R.id.edtTextAddress);
        edtTextFirstName = findViewById(R.id.edtTextFirstName);
        edtTextLastName = findViewById(R.id.edtTextLastName);
        txtViewAddressSpecific = findViewById(R.id.txtViewAddressSpecific);
        txtViewBuildingName = findViewById(R.id.txtViewBuildingName);
        edtTextStreet = findViewById(R.id.edtTextStreet);
        btn_submit = findViewById(R.id.btn_submit);
        btn_cancel= findViewById(R.id.btn_cancel);

        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        addressNamesList.add(new Item("0", context.getString(R.string.select_address_name), false));
        addressNamesList.add(new Item("1", context.getString(R.string.home), false));
        addressNamesList.add(new Item("2", context.getString(R.string.work), false));
        addressNamesList.add(new Item("3", context.getString(R.string.other), false));

        addressNamesAdapter = new AddressNamesAdapter(context, addressNamesList);
        spinnerAddressName.setAdapter(addressNamesAdapter);

        SpannableString ss1 = new SpannableString(txtViewAddressSpecific.getText().toString());
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            ss1.setSpan(new RelativeSizeSpan(.8f), 21, 31, 0); // set size
        }else{
            ss1.setSpan(new RelativeSizeSpan(.8f), 25, 34, 0);
        }
        txtViewAddressSpecific.setText(ss1);

        SpannableString ss2 = new SpannableString(txtViewBuildingName.getText().toString());
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            ss2.setSpan(new RelativeSizeSpan(.8f), 21, 32, 0); // set size
        }else{
            ss2.setSpan(new RelativeSizeSpan(.8f), 24, ss2.length()-1, 0); // set size
        }
        txtViewBuildingName.setText(ss2);

        if (shippingAddress != null) {
            edtTextFirstName.setText(shippingAddress.getFirst_name());
            edtTextLastName.setText(shippingAddress.getLast_name());
            edtTextFloor.setText(shippingAddress.getFloor_number());
            edtTextUnit.setText(shippingAddress.getUnit_number());
            edtTextBuildingName.setText(shippingAddress.getBuilding_name());
            edtTextAddress.setText(shippingAddress.getAddress_instruction());

            if(ValidationUtil.isNullOrBlank(shippingAddress.getStreetName())){
                edtTextStreet.setText(AppConstants.BLANK_STRING);
            }else{
                edtTextStreet.setText(shippingAddress.getStreetName());
            }

            for(int i=0; i<addressNamesList.size(); i++){
                if(addressNamesList.get(i).getItemLabel().equalsIgnoreCase(shippingAddress.getAddress_name())){
                    if(addressNamesAdapter != null){
                        spinnerAddressName.setSelection(i);
                        break;
                    }
                }
            }

            edtTextFloor.setText(shippingAddress.getFloor_number());

        } else {
            edtTextFirstName.setText(ApiLocalStore.getInstance(context).getFirstName());
            edtTextLastName.setText(ApiLocalStore.getInstance(context).getLastName());
        }

        edtTextAddress.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == editTextSplIns) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AppUtil.hideSoftKeyboard((Activity)context, context);
            }
        });
        if (view == btn_submit) {
            if(onAddAddressClickLisener != null){

                if(spinnerAddressName != null && spinnerAddressName.getSelectedItem() != null && spinnerAddressName.getSelectedItemPosition() != 0 && ((Item)spinnerAddressName.getSelectedItem()).getItemLabel() != null){
                    addressName = ((Item)spinnerAddressName.getSelectedItem()).getItemLabel();
                }else{
                    addressName = AppConstants.BLANK_STRING;
                }

                if(edtTextFloor != null && edtTextFloor.getText() != null && edtTextFloor.getText().toString() != null){
                    floor = edtTextFloor.getText().toString();
                }else{
                    floor = AppConstants.BLANK_STRING;
                }

                if(edtTextUnit != null && edtTextUnit.getText() != null && edtTextUnit.getText().toString() != null){
                    unit = edtTextUnit.getText().toString();
                }else{
                    unit = AppConstants.BLANK_STRING;
                }

                if(edtTextBuildingName != null && edtTextBuildingName.getText() != null && edtTextBuildingName.getText().toString() != null){
                    buildingName = edtTextBuildingName.getText().toString();
                }else{
                    buildingName = AppConstants.BLANK_STRING;
                }

                if(edtTextAddress != null && edtTextAddress.getText() != null && edtTextAddress.getText().toString() != null){
                    address = edtTextAddress.getText().toString();
                }else{
                    address = AppConstants.BLANK_STRING;
                }

                if(edtTextStreet != null && edtTextStreet.getText() != null && edtTextStreet.getText().toString() != null){
                    street = edtTextStreet.getText().toString();
                }else{
                    street = AppConstants.BLANK_STRING;
                }

                if(edtTextFirstName != null && edtTextFirstName.getText() != null && edtTextFirstName.getText().toString() != null){
                    firstName = edtTextFirstName.getText().toString();
                }else{
                    firstName = AppConstants.BLANK_STRING;
                }

                if(edtTextLastName != null && edtTextLastName.getText() != null && edtTextLastName.getText().toString() != null){
                    lastName = edtTextLastName.getText().toString();
                }else{
                    lastName = AppConstants.BLANK_STRING;
                }

                if (addressName.isEmpty()) {
                    AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.pls_enter_address_name), context.getString(R.string.ok), false);
                } else if (floor.isEmpty()) {
                    AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.pls_enter_floor_number), context.getString(R.string.ok), false);
                } else if (unit.isEmpty()) {
                    AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.pls_enter_apt_number), context.getString(R.string.ok), false);
                } else if (street.isEmpty()) {
                    AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.pls_enter_street_name), context.getString(R.string.ok), false);
                } else if (firstName.isEmpty()) {
                    AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_first_name_of_user), context.getString(R.string.ok), false);
                } else if (lastName.isEmpty()) {
                    AppUtil.displaySingleActionAlert(context, AppConstants.ERROR_TAG, context.getResources().getString(R.string.error_blank_last_name_of_user), context.getString(R.string.ok), false);
                } else {
                    onAddAddressClickLisener.onAddAddressSubmit(firstName, lastName, floor, unit, addressName, buildingName, address, street);
                }
                
            }
        }else if(view == btn_cancel){
            dismiss();
        }
    }

    public void setOnAddAddressClickLisener(OnAddAddressClickLisener onAddAddressClickLisener) {
        this.onAddAddressClickLisener = onAddAddressClickLisener;
    }

    public interface OnAddAddressClickLisener{
        void onAddAddressSubmit(String firstname, String lastName, String floor, String unit, String address_name, String building_name, String address_instrution, String street);
    }

}
