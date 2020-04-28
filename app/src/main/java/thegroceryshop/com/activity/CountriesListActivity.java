package thegroceryshop.com.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.country_list.VerifyPhoneFragment;
import thegroceryshop.com.utils.LocaleHelper;


/**
 * Created by rohitg on 12/12/2016.
 */

public class CountriesListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_countries_list);

        fragmentManager = getSupportFragmentManager();
        toolbar = findViewById(R.id.countries_lyt_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new VerifyPhoneFragment())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.top_back);
            }else{
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.top_back_arabic);
            }
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


}
