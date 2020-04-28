package thegroceryshop.com.checkoutProcess.checkoutfragment;

/*
 * Created by umeshk on 4/12/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.activity.CountriesListActivity;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.country_list.CountriesBaseFragment;
import thegroceryshop.com.country_list.model.CountryPhoneCode;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.KeyboardVisibilityListener;
import thegroceryshop.com.custom.ValidationUtil;

public class JustForThisOrderFragment extends Fragment implements View.OnClickListener {

    private int count = 0;
    private AppCompatTextView txtViewCountryCode;
    private AppCompatEditText txtViewNumber;
    private AppCompatEditText editTextSplIns;
    private RelativeLayout addNewAddress;
    private TextView textViewCheckOut;
    private ImageView imageViewDeliveryCheck;
    private FrameLayout deliveryTimingLayout;
    private TextView textViewJustForThisOrder;
    private int mCountryCode = 0;
    private CountryPhoneCode selected_country;
    private CheckOutProcessActivity checkOutProcessActivity;

    public static JustForThisOrderFragment newInstance(CheckOutProcessActivity checkOutProcessActivity) {
        JustForThisOrderFragment justForThisOrderFragment = new JustForThisOrderFragment();
        justForThisOrderFragment.checkOutProcessActivity = checkOutProcessActivity;
        return justForThisOrderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OnlineMartApplication.endCheckoutSessionIfCartEmpty(checkOutProcessActivity, false);

        View view = inflater.inflate(R.layout.layout_just_for_this_order, container, false);
        checkOutProcessActivity = (CheckOutProcessActivity) getActivity();
        initView(view);
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        checkOutProcessActivity = (CheckOutProcessActivity) getActivity();

        if (txtViewNumber.getText().toString().length() == 0) {
            txtViewNumber.setText(OnlineMartApplication.mLocalStore.getUserPhone());
        }

        if (txtViewCountryCode.getText().toString().length() == 0) {
            checkOutProcessActivity.getOrder().setCountryCode(OnlineMartApplication.mLocalStore.getUserCountryCode());
            txtViewCountryCode.setText("+" + OnlineMartApplication.mLocalStore.getUserCountryCode());
        }

        AppUtil.setKeyboardVisibilityListener(getActivity(), new KeyboardVisibilityListener() {
            @Override
            public void onKeyboardVisibilityChanged(boolean keyboardVisible) {
                if (keyboardVisible) {
                    deliveryTimingLayout.setVisibility(View.GONE);
                } else {
                    deliveryTimingLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //new AsyncPhoneInitTask(checkOutProcessActivity).execute();

    }

    private void initView(View view) {
        txtViewCountryCode = view.findViewById(R.id.txtViewCountryCode);
        txtViewNumber = view.findViewById(R.id.txtViewNumber);
        editTextSplIns = view.findViewById(R.id.editTextSplIns);
        addNewAddress = view.findViewById(R.id.addNewAddress);
        textViewCheckOut = view.findViewById(R.id.textViewCheckOut);
        imageViewDeliveryCheck = view.findViewById(R.id.imageViewDeliveryCheck);
        deliveryTimingLayout = view.findViewById(R.id.deliveryTimingLayout);
        textViewJustForThisOrder = view.findViewById(R.id.textViewJustForThisOrder);
        setOnClickListener();
        new AsyncPhoneInitTask(checkOutProcessActivity).execute();

        editTextSplIns.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.editTextSplIns) {
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

        if (checkOutProcessActivity.getOrder().isDoorStepAvailable()) {

            if(checkOutProcessActivity.getOrder().isDoorStep()){
                //textViewCheckOut.setEnabled(true);
                //imageViewDeliveryCheck.setEnabled(true);
                imageViewDeliveryCheck.setTag(true);
                imageViewDeliveryCheck.setImageResource(R.drawable.icon_check_large);
                imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
            }else{
                //textViewCheckOut.setEnabled(true);
                //imageViewDeliveryCheck.setEnabled(true);
                imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
                imageViewDeliveryCheck.setTag(false);
            }

        } else {
            //textViewCheckOut.setEnabled(false);
            //imageViewDeliveryCheck.setEnabled(false);
            imageViewDeliveryCheck.setTag(false);
            imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
            //imageViewDeliveryCheck.setImageResource(R.color.white_50);
        }

        if(!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getInstructions())){
            editTextSplIns.setText(checkOutProcessActivity.getOrder().getInstructions());
        }

        imageViewDeliveryCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageViewDeliveryCheck.isEnabled()) {

                    if (checkOutProcessActivity.getOrder().isDoorStepAvailable()) {
                        if ((Boolean) imageViewDeliveryCheck.getTag()) {
                            imageViewDeliveryCheck.setTag(false);
                            imageViewDeliveryCheck.setImageResource(0);
                            imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
                        } else {
                            imageViewDeliveryCheck.setTag(true);
                            imageViewDeliveryCheck.setImageResource(R.drawable.icon_check_large);
                            imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
                            //imageViewDeliveryCheck.setImageResource(R.color.transparent_color);
                            //imageViewDeliveryCheck.setBackgroundResource(R.mipmap.check);
                        }
                    }else{
                        Toast.makeText(checkOutProcessActivity, getString(R.string.error_door_step), Toast.LENGTH_LONG).show();
                    }

                    /*if ((Boolean) imageViewDeliveryCheck.getTag()) {
                        imageViewDeliveryCheck.setTag(false);
                        imageViewDeliveryCheck.setImageResource(0);
                        imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
                    } else {
                        imageViewDeliveryCheck.setTag(true);
                        imageViewDeliveryCheck.setImageResource(R.drawable.icon_check_large);
                        imageViewDeliveryCheck.setBackgroundResource(R.drawable.dark_red_layout_no_border_bg);
                        //imageViewDeliveryCheck.setImageResource(R.color.transparent_color);
                        //imageViewDeliveryCheck.setBackgroundResource(R.mipmap.check);
                    }*/
                }
            }
        });
    }

    private void setOnClickListener() {
        deliveryTimingLayout.setOnClickListener(this);
        txtViewCountryCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deliveryTimingLayout:
                if (!ValidationUtil.isNullOrBlank(txtViewNumber.getText().toString())) {
                    if (AppUtil.isValidPhone(checkOutProcessActivity, txtViewNumber.getText().toString())) {

                        if (ValidationUtil.isNullOrBlank(editTextSplIns.getText().toString())) {
                            checkOutProcessActivity.getOrder().setInstructions(AppConstants.BLANK_STRING);
                        } else {
                            checkOutProcessActivity.getOrder().setInstructions(editTextSplIns.getText().toString());
                        }

                        checkOutProcessActivity.getOrder().setDoorStep((Boolean) imageViewDeliveryCheck.getTag());
                        checkOutProcessActivity.getOrder().setCountryCode(mCountryCode + AppConstants.BLANK_STRING);
                        checkOutProcessActivity.getOrder().setContactNo(txtViewNumber.getText().toString() + AppConstants.BLANK_STRING);

                        checkOutProcessActivity.viewpager.setCurrentItem(2);
                    } else {
                        AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.mobile_no_error), getString(R.string.ok), false);
                    }
                } else {
                    AppUtil.displaySingleActionAlert(checkOutProcessActivity, AppConstants.BLANK_STRING, getString(R.string.blank_contact_no), getString(R.string.ok), false);
                }
                break;

            case R.id.txtViewCountryCode:
                Intent intentCountry = new Intent(checkOutProcessActivity, CountriesListActivity.class);
                startActivityForResult(intentCountry, AppConstants.REQUEST_PICK_COUNTRY_SELF);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.REQUEST_PICK_COUNTRY_SELF) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getParcelableExtra(CountriesBaseFragment.result_country) != null) {
                    selected_country = data.getParcelableExtra(CountriesBaseFragment.result_country);
                    if (selected_country != null) {
                        txtViewCountryCode.setText(selected_country.getCountryCodeStr());
                        //Log.e("country code", " " + selected_country.getCountryCode());
                        mCountryCode = selected_country.getCountryCode();
                    }
                }
            }
        }
    }

    // load  the countries list from loacal to set the device's default information
    private class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<CountryPhoneCode>> {

        private Context mContext;
        private ArrayList<CountryPhoneCode> mCountry = new ArrayList<>();
        private SparseArray<ArrayList<CountryPhoneCode>> mCountriesMap = new SparseArray<ArrayList<CountryPhoneCode>>();

        AsyncPhoneInitTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<CountryPhoneCode> doInBackground(Void... params) {

            /**
             *  Load RequestData From Text File
             * */
            ArrayList<CountryPhoneCode> data = new ArrayList<>(233);
            mCountry = new ArrayList<>(233);
            BufferedReader reader = null;
            try {
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));
                }else{
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries_arabic.dat"), "UTF-8"));
                }

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    CountryPhoneCode c = new CountryPhoneCode(mContext, line, i);
                    data.add(c);
                    ArrayList<CountryPhoneCode> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<CountryPhoneCode>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<CountryPhoneCode> data) {
            mCountry = data;

            if(!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getCountryCode())){
                txtViewCountryCode.setText("+" + checkOutProcessActivity.getOrder().getCountryCode());
            }

            for (int i = 0; i < mCountry.size(); i++) {
                if(!ValidationUtil.isNullOrBlank(checkOutProcessActivity.getOrder().getCountryCode())){
                    if(checkOutProcessActivity.getOrder().getCountryCode().equalsIgnoreCase(mCountry.get(i).getCountryCode() + "")){
                        selected_country = mCountry.get(i);
                        txtViewCountryCode.setText(selected_country.getCountryCodeStr());
                        mCountryCode = selected_country.getCountryCode();
                        break;
                    }
                }else{
                    if ("eg".equalsIgnoreCase(mCountry.get(i).getCountryISO().toUpperCase())) {
                        selected_country = mCountry.get(i);
                        txtViewCountryCode.setText(selected_country.getCountryCodeStr());
                        mCountryCode = selected_country.getCountryCode();
                        break;
                    }
                }
            }

            /*if (selected_country.getCountryCode()==1)
            {
                //txtViewCountryCode.setText("+20");
                //mCountryCode = 20;
            }

            if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserCountryCode())){
                txtViewCountryCode.setText("+" + OnlineMartApplication.mLocalStore.getUserCountryCode());
                mCountryCode = Integer.parseInt(OnlineMartApplication.mLocalStore.getUserCountryCode());
            }else{
                txtViewCountryCode.setText("+20");
                mCountryCode = 20;
            }*/

        }
    }
}
