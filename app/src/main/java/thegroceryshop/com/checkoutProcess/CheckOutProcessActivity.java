package thegroceryshop.com.checkoutProcess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.checkoutfragment.CardListFragment;
import thegroceryshop.com.checkoutProcess.checkoutfragment.DeliveryDateAndTimeFragment;
import thegroceryshop.com.checkoutProcess.checkoutfragment.JustForThisOrderFragment;
import thegroceryshop.com.checkoutProcess.checkoutfragment.OrdersSummaryFragment;
import thegroceryshop.com.checkoutProcess.checkoutfragment.SelectAddressFragment;
import thegroceryshop.com.checkoutProcess.checkoutfragment.TimeSlotService;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.modal.Order;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.utils.CustomViewPager;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.utils.SwipeDirection;

/*
 * Created by rohitg on 3/16/2017.
 */

public class CheckOutProcessActivity extends AppCompatActivity {

    public static final String KEY_DELIVERY_FEE = "key_delivery_fee";
    public static final String KEY_TOTAL_ITEMS = "key_total_items";
    public static final String KEY_TOTAL_AMOUNT = "key_total_amount";
    public static final String KEY_SUBTOTAL = "key_subtotal";
    public static final String KEY_IS_DOOR_STEP_DELIVERY_AVAILABLE = "key_is_door_step_delivery_available";
    public static final String KEY_TOTAL_SHIPPING_HOURS = "key_total_shipping_hours";
    public static final String KEY_IS_SHIPPING_TO_THIRD_PARTY = "key_is_shipping_to_third_party";
    public static final String KEY_TAX_CHARGES = "key_tax_charges";
    public static final String KEY_TOTAL_SAVINGS = "key_total_savings";
    public static final String KEY_CREDITS_CAN_USE = "key_credits_can_use";

    public static final int REQUEST_DELIVERY_DATE_AND_TIME = 121;
    public static final int REQUEST_PAYMENT = 155;
    public static final int REQUEST_REVIEW_CART = 112;
    private FrameLayout selectAddressFrame;
    private TextView selectAddress;
    private FrameLayout gotoOrderFrame;
    private TextView gotoOrder;
    private FrameLayout deliveryTimeFrame;
    private TextView deliveryTime;
    private FrameLayout orderReviewFrame;
    private TextView orderReview;
    private TextView orderCheckOutDone;
    private FrameLayout orderCheckOutDoneFrame;
    public CustomViewPager viewpager;
    ViewPagerAdapter adapter;
    private Order order;
    private ImageView img_back;

    private SelectAddressFragment selectAddressFragment;
    private DeliveryDateAndTimeFragment deliveryDateAndTimeFragment;

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    //private boolean isKeybordShowing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_process);
        order = new Order();
        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            //actionBar.setHomeAsUpIndicator(R.mipmap.top_back);
        }

        selectAddressFrame = findViewById(R.id.select_address_frame);
        selectAddress = findViewById(R.id.select_address);
        gotoOrderFrame = findViewById(R.id.goto_order_frame);
        gotoOrder = findViewById(R.id.goto_order);
        deliveryTimeFrame = findViewById(R.id.delivery_time_frame);
        deliveryTime = findViewById(R.id.delivery_time);
        orderReviewFrame = findViewById(R.id.order_review_frame);
        orderReview = findViewById(R.id.order_review);
        orderCheckOutDone = findViewById(R.id.order_chekout_done);
        orderCheckOutDoneFrame = findViewById(R.id.order_chekout_done_frame);
        viewpager = findViewById(R.id.viewpager);
        img_back = findViewById(R.id.checkout_img_back);

        setupViewPager(viewpager);
        viewpager.setOffscreenPageLimit(0);

        selectAddressFrame.setVisibility(View.VISIBLE);
        selectAddress.setVisibility(View.GONE);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent() != null) {
            if (getIntent() != null && getIntent().hasExtra(KEY_DELIVERY_FEE)) {
                getOrder().setDeliveryCharges((float) getIntent().getDoubleExtra(KEY_DELIVERY_FEE, 0.0f));
            }

            if (getIntent().hasExtra(KEY_TOTAL_AMOUNT)) {
                getOrder().setTotalAmountToPay(getIntent().getFloatExtra(KEY_TOTAL_AMOUNT, 0.0f));
            }

            if (getIntent().hasExtra(KEY_SUBTOTAL)) {
                getOrder().setSubTotal(getIntent().getFloatExtra(KEY_SUBTOTAL, 0.0f));
            }

            if (getIntent().hasExtra(KEY_TAX_CHARGES)) {
                getOrder().setTax(getIntent().getFloatExtra(KEY_TAX_CHARGES, 0.0f));
            }

            if (getIntent().hasExtra(KEY_TOTAL_ITEMS)) {
                getOrder().setNoOfItems(getIntent().getIntExtra(KEY_TOTAL_ITEMS, 0));
            }

            if (getIntent().hasExtra(KEY_TOTAL_SAVINGS)) {
                getOrder().setTotalSavings(getIntent().getFloatExtra(KEY_TOTAL_SAVINGS, 0.0f));
            }

            getOrder().setList_cart(OnlineMartApplication.getCartList());
        }

        getOrder().setUserId(OnlineMartApplication.mLocalStore.getUserId());

        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(0);

                if (getOrder() != null && getOrder().getTimeSlot() != null) {
                    try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", getOrder().getTimeSlot().getId());
                        request_data.put("date", getOrder().getDeliveruDate().split(" ")[0]);
                        request_data.put("is_solt_book", "");

                        TimeSlotService.reserveOrReleaseTimeSlot(CheckOutProcessActivity.this, request_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                getOrder().setDate(null);
                getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
                getOrder().setTimeSlot(null);
                getOrder().setDeliveyType(AppConstants.BLANK_STRING);
                getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                getOrder().setCardId(AppConstants.BLANK_STRING);
                getOrder().setSelectedCard(null);

                if (deliveryDateAndTimeFragment != null) {
                    deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
                    deliveryDateAndTimeFragment.selectedTimeSlot = null;
                }

            }
        });

        gotoOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewpager.getCurrentItem() > 1) {
                    viewpager.setCurrentItem(1);

                    if (getOrder() != null && getOrder().getTimeSlot() != null) {
                        try {
                            JSONObject request_data = new JSONObject();
                            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                            request_data.put("time_slot_id", getOrder().getTimeSlot().getId());
                            request_data.put("date", getOrder().getDeliveruDate().split(" ")[0]);
                            request_data.put("is_solt_book", "");

                            TimeSlotService.reserveOrReleaseTimeSlot(CheckOutProcessActivity.this, request_data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    getOrder().setDate(null);
                    getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
                    getOrder().setTimeSlot(null);
                    getOrder().setDeliveyType(AppConstants.BLANK_STRING);
                    getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                    getOrder().setCardId(AppConstants.BLANK_STRING);
                    getOrder().setSelectedCard(null);

                    if (deliveryDateAndTimeFragment != null) {
                        deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
                        deliveryDateAndTimeFragment.selectedTimeSlot = null;
                    }
                }
            }
        });

        deliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewpager.getCurrentItem() > 2) {
                    viewpager.setCurrentItem(2);

                    if (getOrder() != null && getOrder().getTimeSlot() != null) {
                        getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                        getOrder().setCardId(AppConstants.BLANK_STRING);
                        getOrder().setSelectedCard(null);
                    }
                }
            }
        });

        orderReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewpager.getCurrentItem() > 3) {
                    viewpager.setCurrentItem(3);

                    getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                    getOrder().setCardId(AppConstants.BLANK_STRING);
                    getOrder().setSelectedCard(null);
                }


            }
        });

        int shippingHours = 0;
        boolean isShippingThirdParty = false;
        if (getOrder() != null && getOrder().getList_cart() != null && getOrder().getList_cart().size() > 0) {

            boolean isDoorStepFinal = false;
            for (int i = 0; i < getOrder().getList_cart().size(); i++) {

                if(getOrder().getList_cart().get(i).isShippingThirdParty()){
                    isShippingThirdParty = true;
                }

                if (getOrder().getList_cart().get(i).getShippingHours() > shippingHours) {
                    shippingHours = getOrder().getList_cart().get(i).getShippingHours();
                }

                if (!getOrder().getList_cart().get(i).isDoorStepDelivery()) {
                    getOrder().setDoorStepAvailable(false);
                    isDoorStepFinal = true;
                }else{
                    if(!isDoorStepFinal){
                        getOrder().setDoorStepAvailable(true);
                    }
                }
            }

            getOrder().setShippinghirdParty(isShippingThirdParty);
        }else{
            getOrder().setShippinghirdParty(false);
        }

        getOrder().setOrderShippingHours(shippingHours);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("onPageScrolled", " " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.v("onPageSelected", " " + position);
                AppUtil.hideSoftKeyboard(CheckOutProcessActivity.this, CheckOutProcessActivity.this);
                switch (position) {
                    case 0:

                        selectAddressFrame.setVisibility(View.VISIBLE);
                        selectAddress.setVisibility(View.GONE);

                        gotoOrderFrame.setVisibility(View.GONE);
                        gotoOrder.setVisibility(View.VISIBLE);

                        deliveryTimeFrame.setVisibility(View.GONE);
                        deliveryTime.setVisibility(View.VISIBLE);

                        orderReviewFrame.setVisibility(View.GONE);
                        orderReview.setVisibility(View.VISIBLE);

                        orderCheckOutDoneFrame.setVisibility(View.GONE);
                        orderCheckOutDone.setVisibility(View.VISIBLE);

                        if (deliveryDateAndTimeFragment != null) {
                            deliveryDateAndTimeFragment.updateUI();
                        }
                        break;

                    case 1:

                        selectAddressFrame.setVisibility(View.GONE);
                        selectAddress.setVisibility(View.VISIBLE);

                        gotoOrderFrame.setVisibility(View.VISIBLE);
                        gotoOrder.setVisibility(View.GONE);

                        deliveryTimeFrame.setVisibility(View.GONE);
                        deliveryTime.setVisibility(View.VISIBLE);

                        orderReviewFrame.setVisibility(View.GONE);
                        orderReview.setVisibility(View.VISIBLE);

                        orderCheckOutDoneFrame.setVisibility(View.GONE);
                        orderCheckOutDone.setVisibility(View.VISIBLE);

                        if (deliveryDateAndTimeFragment != null) {
                            deliveryDateAndTimeFragment.updateUI();
                        }
                        break;

                    case 2:

                        selectAddressFrame.setVisibility(View.GONE);
                        selectAddress.setVisibility(View.VISIBLE);

                        gotoOrderFrame.setVisibility(View.GONE);
                        gotoOrder.setVisibility(View.VISIBLE);

                        deliveryTimeFrame.setVisibility(View.VISIBLE);
                        deliveryTime.setVisibility(View.GONE);

                        orderReviewFrame.setVisibility(View.GONE);
                        orderReview.setVisibility(View.VISIBLE);

                        orderCheckOutDoneFrame.setVisibility(View.GONE);
                        orderCheckOutDone.setVisibility(View.VISIBLE);

                        if (deliveryDateAndTimeFragment != null) {
                            deliveryDateAndTimeFragment.updateUI();
                        }
                        break;

                    case 3:
                        selectAddressFrame.setVisibility(View.GONE);
                        selectAddress.setVisibility(View.VISIBLE);

                        gotoOrderFrame.setVisibility(View.GONE);
                        gotoOrder.setVisibility(View.VISIBLE);

                        deliveryTimeFrame.setVisibility(View.GONE);
                        deliveryTime.setVisibility(View.VISIBLE);

                        orderReviewFrame.setVisibility(View.VISIBLE);
                        orderReview.setVisibility(View.GONE);

                        orderCheckOutDoneFrame.setVisibility(View.GONE);
                        orderCheckOutDone.setVisibility(View.VISIBLE);

                        break;

                    case 4:

                        selectAddressFrame.setVisibility(View.GONE);
                        selectAddress.setVisibility(View.VISIBLE);

                        gotoOrderFrame.setVisibility(View.GONE);
                        gotoOrder.setVisibility(View.VISIBLE);

                        deliveryTimeFrame.setVisibility(View.GONE);
                        deliveryTime.setVisibility(View.VISIBLE);

                        orderReviewFrame.setVisibility(View.GONE);
                        orderReview.setVisibility(View.VISIBLE);

                        orderCheckOutDoneFrame.setVisibility(View.VISIBLE);
                        orderCheckOutDone.setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Adding fragments to ViewPager
     *
     * @param viewPager
     */
    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        selectAddressFragment = SelectAddressFragment.newInstance(CheckOutProcessActivity.this);
        adapter.addFrag(selectAddressFragment, "");

        adapter.addFrag(JustForThisOrderFragment.newInstance(CheckOutProcessActivity.this), "");

        deliveryDateAndTimeFragment = DeliveryDateAndTimeFragment.newInstance(CheckOutProcessActivity.this);
        adapter.addFrag(deliveryDateAndTimeFragment, "");
        adapter.addFrag(CardListFragment.newInstance(CheckOutProcessActivity.this), "");
        adapter.addFrag(new OrdersSummaryFragment(CheckOutProcessActivity.this), "");
        viewPager.setAdapter(adapter);
        viewPager.setAllowedSwipeDirection(SwipeDirection.none);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DELIVERY_DATE_AND_TIME) {
            mFragmentList.get(2).onActivityResult(requestCode, resultCode, data);
        }

        if(requestCode == REQUEST_REVIEW_CART){
            mFragmentList.get(4).onActivityResult(requestCode, resultCode, data);
        }

        if(requestCode == REQUEST_PAYMENT){
            mFragmentList.get(4).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onPause(){
        super.onPause();
        AppUtil.hideProgress();
    }


    public void getShippingCharges() {
        LoadUsualData.loadShippingCharges(this);
    }


    @Override
    public void onBackPressed() {
        if(viewpager.getCurrentItem() == 0){
            super.onBackPressed();
        }else{

            if(viewpager.getCurrentItem() == 1){

                if(getOrder() != null && getOrder().getTimeSlot() != null){
                    try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", getOrder().getTimeSlot().getId());
                        request_data.put("date", getOrder().getDeliveruDate().split(" ")[0]);
                        request_data.put("is_solt_book", "");

                        TimeSlotService.reserveOrReleaseTimeSlot(CheckOutProcessActivity.this, request_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                getOrder().setDate(null);
                getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
                getOrder().setTimeSlot(null);
                getOrder().setDeliveyType(AppConstants.BLANK_STRING);
                getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                getOrder().setCardId(AppConstants.BLANK_STRING);
                getOrder().setSelectedCard(null);

                if (deliveryDateAndTimeFragment != null) {
                    deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
                    deliveryDateAndTimeFragment.selectedTimeSlot = null;
                }

            } else if (viewpager.getCurrentItem() == 2) {

                if (getOrder() != null && getOrder().getTimeSlot() != null) {
                    try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", getOrder().getTimeSlot().getId());
                        request_data.put("date", getOrder().getDeliveruDate().split(" ")[0]);
                        request_data.put("is_solt_book", "");

                        TimeSlotService.reserveOrReleaseTimeSlot(CheckOutProcessActivity.this, request_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                getOrder().setDate(null);
                getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
                getOrder().setTimeSlot(null);
                getOrder().setDeliveyType(AppConstants.BLANK_STRING);
                getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                getOrder().setCardId(AppConstants.BLANK_STRING);
                getOrder().setSelectedCard(null);

                if(deliveryDateAndTimeFragment != null){
                    deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
                    deliveryDateAndTimeFragment.selectedTimeSlot = null;
                }

            }else if(viewpager.getCurrentItem() == 3){

                if(getOrder() != null && getOrder().getTimeSlot() != null){
                    /*try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", "1");
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", getOrder().getTimeSlot().getId());
                        request_data.put("date", getOrder().getDeliveruDate().split(" ")[0]);
                        request_data.put("is_solt_book", "");

                        TimeSlotService.reserveOrReleaseTimeSlot(CheckOutProcessActivity.this, request_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }

                //getOrder().setDate(null);
                //getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
                //getOrder().setTimeSlot(null);
                //getOrder().setDeliveyType(AppConstants.BLANK_STRING);
                getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                getOrder().setCardId(AppConstants.BLANK_STRING);
                getOrder().setSelectedCard(null);

                /*if(deliveryDateAndTimeFragment != null){
                    deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
                    deliveryDateAndTimeFragment.selectedTimeSlot = null;
                }*/

            }else if(viewpager.getCurrentItem() == 4){

                getOrder().setPaymentMode(AppConstants.BLANK_STRING);
                getOrder().setCardId(AppConstants.BLANK_STRING);
                getOrder().setSelectedCard(null);

            }

            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void navigateTODeliveryType(){

        //getOrder().setDate(null);
        //getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
        //getOrder().setTimeSlot(null);
        //getOrder().setDeliveyType(AppConstants.BLANK_STRING);
        getOrder().setPaymentMode(AppConstants.BLANK_STRING);
        getOrder().setCardId(AppConstants.BLANK_STRING);
        getOrder().setSelectedCard(null);
        viewpager.setCurrentItem(2);

        /*if(deliveryDateAndTimeFragment != null){
            deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
            deliveryDateAndTimeFragment.selectedTimeSlot = null;
        }*/
    }

    public void navigateTODeliveryType1(){

        getOrder().setDate(null);
        getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
        getOrder().setTimeSlot(null);
        getOrder().setDeliveyType(AppConstants.BLANK_STRING);
        getOrder().setPaymentMode(AppConstants.BLANK_STRING);
        getOrder().setCardId(AppConstants.BLANK_STRING);
        getOrder().setSelectedCard(null);
        viewpager.setCurrentItem(2);

        if(deliveryDateAndTimeFragment != null){
            deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
            deliveryDateAndTimeFragment.selectedTimeSlot = null;
        }
    }

    public void navigateTOPayment() {

        getOrder().setPaymentMode(AppConstants.BLANK_STRING);
        getOrder().setCardId(AppConstants.BLANK_STRING);
        getOrder().setSelectedCard(null);
        viewpager.setCurrentItem(3);
    }


    public void navigateTOAddress() {

        getOrder().setDate(null);
        getOrder().setDeliveruDate(AppConstants.BLANK_STRING);
        getOrder().setTimeSlot(null);
        getOrder().setDeliveyType(AppConstants.BLANK_STRING);
        getOrder().setPaymentMode(AppConstants.BLANK_STRING);
        getOrder().setCardId(AppConstants.BLANK_STRING);
        getOrder().setSelectedCard(null);
        viewpager.setCurrentItem(0);

        if (deliveryDateAndTimeFragment != null) {
            deliveryDateAndTimeFragment.date = AppConstants.BLANK_STRING;
            deliveryDateAndTimeFragment.selectedTimeSlot = null;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
