package thegroceryshop.com.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.adapter.ImageViewPagerAdapter;
import thegroceryshop.com.custom.circleIndicator.CirclePageIndicator;
import thegroceryshop.com.utils.LocaleHelper;

/**
 * Created by jaikishang on 3/28/2017.
 */

public class FullImage_View extends AppCompatActivity {
    private ViewPager pagerIntroduction;
    private CirclePageIndicator circlepageIndicaor;
    private Button skip_btn;
    private LinearLayout cancel_action_layout;
    private ArrayList<String> imageList ;
    private FrameLayout main_layout;
    private CirclePageIndicator pager_indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_tour_activity);

        if (getIntent() !=null) {
            if (getIntent().getStringArrayListExtra("image") !=null)
            {
                imageList = new ArrayList<>();
                imageList = getIntent().getStringArrayListExtra("image");
            }
        }

        initView();
    }

    private void initView()
    {
        main_layout = findViewById(R.id.main_layout);
        main_layout.setBackgroundColor(getResources().getColor(R.color.text_color));

        pagerIntroduction = findViewById(R.id.pager_introduction);
        circlepageIndicaor = findViewById(R.id.circlepageIndicaor);
        circlepageIndicaor.setFillColor(R.color.colorAccent);
        circlepageIndicaor.setStrokeColor(R.color.colorAccent);
        skip_btn = findViewById(R.id.skip_btn);
        skip_btn.setVisibility(View.GONE);

        cancel_action_layout = findViewById(R.id.cancel_action_layout);
        cancel_action_layout.setVisibility(View.VISIBLE);

        if(imageList != null){
            pagerIntroduction.setAdapter(new ImageViewPagerAdapter(this, imageList));
            circlepageIndicaor.setViewPager(pagerIntroduction);
        }

        cancel_action_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
