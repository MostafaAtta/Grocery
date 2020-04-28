package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import thegroceryshop.com.BuildConfig;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.utils.LocaleHelper;

/*
 * Created by umeshk on 24-Feb-17.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout termsLayout;
    private RelativeLayout privacyPolicyLayout;
    private RelativeLayout licencesLayout;

    private Toolbar toolbar;
    private TextView txt_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.mipmap.top_back);

        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                toolbar.setNavigationIcon(R.mipmap.top_back);
            }else{
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            txt_title.setText(getResources().getString(R.string.about_us).toUpperCase());
        }
        initView();
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

    private void initView() {

        TextView txtViewVersion = findViewById(R.id.txtViewVersion);
        termsLayout = findViewById(R.id.termsLayout);
        privacyPolicyLayout = findViewById(R.id.privacyPolicyLayout);
        licencesLayout = findViewById(R.id.licencesLayout);

        txtViewVersion.setText(BuildConfig.VERSION_NAME);
        termsLayout.setOnClickListener(this);
        privacyPolicyLayout.setOnClickListener(this);
        licencesLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intentAbout = new Intent(this, CommonWebViewActivity.class);
        if (v == termsLayout) {
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_TERMS_OF_USE + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.terms_of_use));
        } else if (v == privacyPolicyLayout) {
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_PRIVACY_POLICY + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.privacy_policy));
        } else if (v == licencesLayout) {
            intentAbout.putExtra(CommonWebViewActivity.KEY_URL, AppConstants.URL_LICENSE + OnlineMartApplication.mLocalStore.getAppLangId());
            intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, getResources().getString(R.string.licences));
        }
        startActivity(intentAbout);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
