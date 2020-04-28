package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.fragments.Manually_Address_Fragment;
import thegroceryshop.com.fragments.OverAMapFragment;
import thegroceryshop.com.modal.Item;
import thegroceryshop.com.modal.ShippingAddress;
import thegroceryshop.com.utils.LocaleHelper;

public class AddNewAddressActivity extends AppCompatActivity  {


    private Button addLayout;
    public static ArrayList<Item> areaList = new ArrayList<>();
    public double currentLatitude;
    public double currentLongitude;
    private ShippingAddress shippingAddress;
    private Toolbar toolbar;
    private TextView txt_title;
    private ViewPager mViewPager;
    public String navigateTo;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_new_address);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        //txt_title.setText(getResources().getString(R.string.add_new_address).toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            toolbar.setNavigationIcon(R.mipmap.top_back);
        }else{
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        mViewPager = findViewById(R.id.viewpager);
        addLayout = findViewById(R.id.footer);

        if (getIntent() != null) {
            shippingAddress = getIntent().getParcelableExtra("shipping_address");
            navigateTo = getIntent().getStringExtra("input_from");
            if (shippingAddress != null) {
                txt_title.setText(getResources().getString(R.string.update_address).toUpperCase());
                addLayout.setText(getString(R.string.update));
            } else {
                txt_title.setText(getResources().getString(R.string.add_new_address).toUpperCase());
                addLayout.setText(getString(R.string.add1));
            }
        }

        /*
         * Tab layout setUp with view pager
         */
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        if (shippingAddress != null) {
            tabLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
        }

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }else{
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);
        }

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(ContextCompat.getColor(AddNewAddressActivity.this, R.color.tab_layout_divider));
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(2);
        linearLayout.setDividerDrawable(drawable);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(AddNewAddressActivity.this, AddNewAddressActivity.this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putParcelable("address", shippingAddress);

        Manually_Address_Fragment manually_address_fragment = new Manually_Address_Fragment();
        manually_address_fragment.setArguments(bundle);

        OverAMapFragment overAMapFragment = new OverAMapFragment();
        overAMapFragment.setArguments(bundle);

        if (shippingAddress == null) {
            adapter.addFrag(overAMapFragment, getResources().getString(R.string.over_a_map_caps));
            adapter.addFrag(manually_address_fragment, getResources().getString(R.string.manually_caps));
        } else {

            if (!ValidationUtil.isNullOrBlank(navigateTo)) {
                if (navigateTo.equalsIgnoreCase("map")) {
                    adapter.addFrag(overAMapFragment, getResources().getString(R.string.over_a_map_caps));
                } else {
                    adapter.addFrag(manually_address_fragment, getResources().getString(R.string.manually_caps));
                }
            } else {
                adapter.addFrag(manually_address_fragment, getResources().getString(R.string.manually_caps));
            }
        }

        viewPager.setAdapter(adapter);

        if (!ValidationUtil.isNullOrBlank(navigateTo)) {
            if (navigateTo.equalsIgnoreCase("map")) {
                viewPager.setCurrentItem(1);
            }
        }

        addLayout = findViewById(R.id.footer);
        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = adapter.mFragmentList.get(viewPager.getCurrentItem());
                if (fragment != null && fragment instanceof Manually_Address_Fragment) {
                    Manually_Address_Fragment manually_address_fragment = (Manually_Address_Fragment) fragment;
                    manually_address_fragment.addUserAddress();
                } else {
                    OverAMapFragment overAMapFragment = (OverAMapFragment) fragment;
                    overAMapFragment.addUserAddress();
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AppUtil.hideSoftKeyboard(AddNewAddressActivity.this, AddNewAddressActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }




    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

    public void setAddEnable(boolean isEnable) {

        if (isEnable) {
            addLayout.setVisibility(View.VISIBLE);
        } else {
            if(shippingAddress != null){
                addLayout.setVisibility(View.GONE);
            }else{
                if (mViewPager.getCurrentItem() == 1) {
                    addLayout.setVisibility(View.VISIBLE);
                } else {
                    addLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void setResult() {
        Intent intent = new Intent();
        intent.putExtra("isAddressAdded", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
