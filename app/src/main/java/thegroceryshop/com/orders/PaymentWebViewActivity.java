package thegroceryshop.com.orders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.ApiClient;

public class PaymentWebViewActivity extends AppCompatActivity {

    public static final String KEY_URL = "URL";
    public static final String KEY_CALLBACK_URL = "callback_url";

    private Toolbar toolbar;
    private TextView txt_title;
    private WebView webView;
    private String url;
    private boolean isResultSet;
    private AppDialogDoubleAction appDialogDoubleAction;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_webview);
        setResult(Activity.RESULT_CANCELED);

        webView = findViewById(R.id.webView);

        if(getIntent() != null){

            Bundle bundle = getIntent().getExtras();
            url = bundle.getString(KEY_URL);
            //String title = bundle.getString(KEY_TITLE);

            toolbar = findViewById(R.id.toolbar);
            txt_title = findViewById(R.id.txt_title);
            setSupportActionBar(toolbar);

            if(getSupportActionBar()!= null){
                getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    toolbar.setNavigationIcon(R.mipmap.top_back);
                } else {
                    toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
                }
                //toolbar.setNavigationIcon(null);
                txt_title.setText(getString(R.string.payment).toUpperCase());
            }
        }

        Toast.makeText(PaymentWebViewActivity.this, getString(R.string.pls_wait_while_we_are_initiating_payment), Toast.LENGTH_LONG).show();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getLoadWithOverviewMode();
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebViewClientResult());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if(!ValidationUtil.isNullOrBlank(url) && url.startsWith(ApiClient.getCallBackURL())){

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY_CALLBACK_URL, url);
                    setResult(Activity.RESULT_OK, resultIntent);
                    isResultSet = true;

                    finish();

                }else{
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(!isResultSet){
                    //AppUtil.hideProgress();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //AppUtil.hideProgress();
                /*Intent dataIntent = new Intent();
                dataIntent.putExtra("type", "fail");
                setResult(Activity.RESULT_CANCELED, dataIntent);
                finish();*/
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

                //AppUtil.hideProgress();

                if(appDialogDoubleAction != null && appDialogDoubleAction.isShowing()){
                    appDialogDoubleAction.dismiss();
                }

                String url = view.getUrl();

                if (!url.startsWith(ApiClient.getCallBackURL())) {
                    appDialogDoubleAction = new AppDialogDoubleAction(PaymentWebViewActivity.this, getString(R.string.app_name), getString(R.string.webview_error), getString(R.string.no), getString(R.string.yes));
                    appDialogDoubleAction.show();
                    appDialogDoubleAction.setCanceledOnTouchOutside(false);
                    appDialogDoubleAction.setCancelable(false);
                    appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                        @Override
                        public void onLeftActionClick(View view) {
                            appDialogDoubleAction.dismiss();
                            Intent dataIntent = new Intent();
                            dataIntent.putExtra("type", "cancel");
                            setResult(Activity.RESULT_CANCELED, dataIntent);
                            finish();
                        }

                        @Override
                        public void onRightActionClick(View view) {
                            appDialogDoubleAction.dismiss();
                            //AppUtil.showProgress(PaymentWebViewActivity.this);
                            handler.proceed();
                        }
                    });
                }
            }
        });
    }

    class WebViewClientResult extends WebChromeClient
    {
        private int       webViewPreviousState;
        private final int PAGE_STARTED    = 0x1;
        private final int PAGE_REDIRECTED = 0x2;


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            // TODO Auto-generated method stub
            Log.i("my log","Alert box popped");
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).
                    setTitle(null).
                    setMessage(message).
                    setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .create();
            dialog.show();
            return true;
        }
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
                    /*if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }*/
                    finish();
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




