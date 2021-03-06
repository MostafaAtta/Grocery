package thegroceryshop.com.activity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.fragments.Received_Fragment;
import thegroceryshop.com.fragments.Redeemed_Fragment;
import thegroceryshop.com.fragments.UnRedeemed_Fragment;
import thegroceryshop.com.utils.LocaleHelper;

public class MyCouponsActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private Toolbar toolbar;
    private TextView txt_title;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_coupons);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            toolbar.setNavigationIcon(R.mipmap.top_back);
        }else{
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        mViewPager = findViewById(R.id.viewpager);

        /**
        * Tab layout setUp with view pager
        */
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);


        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }else{
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);
        }

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(ContextCompat.getColor(MyCouponsActivity.this, R.color.tab_layout_divider));
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(2);
        linearLayout.setDividerDrawable(drawable);

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Received_Fragment(MyCouponsActivity.this), getResources().getString(R.string.received));
        adapter.addFrag(new Redeemed_Fragment(MyCouponsActivity.this), getResources().getString(R.string.redeemed));
        adapter.addFrag(new UnRedeemed_Fragment(MyCouponsActivity.this), getResources().getString(R.string.unredeemed));
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
