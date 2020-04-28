package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import thegroceryshop.com.R;
import thegroceryshop.com.adapter.ViewPagerAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.custom.circleIndicator.CirclePageIndicator;
import thegroceryshop.com.utils.LocaleHelper;

/**
 * Created by jaikishang on 3/28/2017.
 */

public class AppIntro_Activity extends AppCompatActivity {
    private ViewPager pagerIntroduction;
    private CirclePageIndicator circlepageIndicaor;
    private Button skip_btn;
    private int[] mImageResources = {
            R.drawable.new_intro_1,
            R.drawable.new_intro_2,
            R.drawable.new_intro_3,
            R.drawable.new_intro_4,
            R.drawable.new_intro_5
    };

    private int[] mArabicImageResources = {
            R.drawable.new_intro_ar_5,
            R.drawable.new_intro_ar_4,
            R.drawable.new_intro_ar_3,
            R.drawable.new_intro_ar_2,
            R.drawable.new_intro_ar_1
    };

    private String[] description_txt;
    private String[] title_txt;

    private String[] ar_description_txt;
    private String[] ar_title_txt;

    private String mfrom = "";
    private boolean isFromNotification;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_tour_activity);

        description_txt = new String[]{
                getResources().getString(R.string.explore_wide_range_of_categories),
                getResources().getString(R.string.you_copuld_search_for_any),
                getResources().getString(R.string.select_you_address_on_the_map),
                getResources().getString(R.string.we_will_notify_you_when),
                getResources().getString(R.string.we_will_help_you_free)
        };

        title_txt = new String[]{
                getString(R.string.with_easy_to_use_menus),
                getString(R.string.with_barcode_scanning),
                getString(R.string.for_easier_registration),
                getString(R.string.whenever_a_product_is_out_of_stock),
                getString(R.string.with_your_tgs_account)
        };

        ar_description_txt = new String[]{
                getResources().getString(R.string.we_will_help_you_free),
                getResources().getString(R.string.we_will_notify_you_when),
                getResources().getString(R.string.select_you_address_on_the_map),
                getResources().getString(R.string.you_copuld_search_for_any),
                getResources().getString(R.string.explore_wide_range_of_categories)
        };

        ar_title_txt = new String[]{
                getString(R.string.with_your_tgs_account),
                getString(R.string.whenever_a_product_is_out_of_stock),
                getString(R.string.for_easier_registration),
                getString(R.string.with_barcode_scanning),
                getString(R.string.with_easy_to_use_menus)
        };

        if (getIntent() != null) {
            if (getIntent().getStringExtra("from") != null) {
                if (getIntent().getStringExtra("from").equalsIgnoreCase("slide")) {
                    mfrom = getIntent().getStringExtra("from");
                    isFromNotification = (getIntent().getBooleanExtra("isFromNotification", false));
                }
            }
        }

        initView();
    }

    private void initView() {
        pagerIntroduction = findViewById(R.id.pager_introduction);
        circlepageIndicaor = findViewById(R.id.circlepageIndicaor);
        skip_btn = findViewById(R.id.skip_btn);

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            pagerAdapter = new ViewPagerAdapter(this, mImageResources, description_txt, title_txt);
            pagerIntroduction.setAdapter(pagerAdapter);
            circlepageIndicaor.setViewPager(pagerIntroduction);
        } else {
            pagerAdapter = new ViewPagerAdapter(this, mArabicImageResources, ar_description_txt, ar_title_txt);
            pagerIntroduction.setAdapter(pagerAdapter);
            pagerIntroduction.setCurrentItem(mArabicImageResources.length-1);
            circlepageIndicaor.setViewPager(pagerIntroduction);
            circlepageIndicaor.setCurrentItem(mArabicImageResources.length-1);
        }

        skip_btn.setVisibility(View.VISIBLE);
        pagerIntroduction.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    if (position == mImageResources.length - 1) {
                        skip_btn.setText(getString(R.string.got_it_start_shopping));
                    } else {
                        skip_btn.setText(getString(R.string.skip));
                    }
                }else{
                    if (position == 0) {
                        skip_btn.setText(getString(R.string.got_it_start_shopping));
                    } else {
                        skip_btn.setText(getString(R.string.skip));
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mfrom.isEmpty()) {
                    OnlineMartApplication.getSharedPreferences().edit().putBoolean("appIntro", true).apply();

                    Intent intent;
                    intent = new Intent(AppIntro_Activity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("isFromNotification", isFromNotification);
                    startActivity(intent);
                    finish();
                } else {

                    finish();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
