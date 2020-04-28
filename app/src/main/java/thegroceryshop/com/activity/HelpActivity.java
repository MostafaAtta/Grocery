package thegroceryshop.com.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.utils.LocaleHelper;

/*
 * Created by umeshk on 24-Feb-17.
 */

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Toolbar toolbar;
    private TextView txt_title;

    private TextView textViewCall;

    private static final int PERMISSION_REQUEST_CODE = 11;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);

        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        setSupportActionBar(toolbar);
        txt_title.setText(getResources().getString(R.string.help_caps).toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            toolbar.setNavigationIcon(R.mipmap.top_back);
        } else {
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewCall:
                invokeCall();
                break;

            case R.id.textViewEmail:
                invokeEmail();
                break;
        }
    }

    private void initView() {
        textViewCall = findViewById(R.id.textViewCall);
        TextView textViewEmail = findViewById(R.id.textViewEmail);

        textViewCall.setOnClickListener(this);
        textViewEmail.setOnClickListener(this);
    }

    protected void invokeCall() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                makeCall();
            }
        } else {
            makeCall();
        }
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + textViewCall.getText().toString()));
        startActivity(callIntent);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((HelpActivity)mContext, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else {
                    Toast.makeText(mContext, "Permission Denied, You cannot make call.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected void invokeEmail() {
        if (NetworkUtil.networkStatus(mContext)) {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"help@thegroceryshop.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "The Grocery App Help.");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            final PackageManager pm = mContext.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.name.toLowerCase().contains("mail"))
                    best = info;
            }
            if (best != null) {
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                startActivity(emailIntent);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
