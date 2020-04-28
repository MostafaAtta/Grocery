package thegroceryshop.com.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.utils.LocaleHelper;

public class CommonWebViewActivity extends AppCompatActivity {

    public static final String KEY_URL = "URL";
    public static final String KEY_TITLE = "TITLE";

    private Toolbar toolbar;
    private TextView txt_title;
    private WebView webView;
    String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_webview);

        webView = findViewById(R.id.webView);

        if(getIntent() != null){

            Bundle bundle = getIntent().getExtras();
            url = bundle.getString(KEY_URL);
            String title = bundle.getString(KEY_TITLE);

            toolbar = findViewById(R.id.toolbar);
            txt_title = findViewById(R.id.txt_title);
            setSupportActionBar(toolbar);

            if(getSupportActionBar()!= null){
                getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    toolbar.setNavigationIcon(R.mipmap.top_back);
                }else{
                    toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
                }

                txt_title.setText(title.toUpperCase());
            }
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getLoadWithOverviewMode();
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                AppUtil.showProgress(CommonWebViewActivity.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                AppUtil.hideProgress();
            }
        });
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}




