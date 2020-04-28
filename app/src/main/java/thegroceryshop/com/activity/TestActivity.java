package thegroceryshop.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import thegroceryshop.com.R;

public class TestActivity extends Activity {

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_webview);
        final WebView webview = (WebView) findViewById(R.id.webView);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().getLoadWithOverviewMode();
        webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        webview.setWebChromeClient(new WebViewClientResult());

        /*webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String s = ("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                System.out.println(s);
            }
        });*/

        webview.loadUrl("https://uat.thegroceryshop.com/FinePrint/FingerPrint.html");
    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        public void showHTML(String html) {
            new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
                    .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
        }

    }

    public static String getHtml(String url) throws Exception, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet, localContext);
        String result = "";

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        response.getEntity().getContent()
                )
        );

        String line = null;
        while ((line = reader.readLine()) != null){
            result += line + "\n";
        }
        return result;
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
            Toast.makeText(TestActivity.this, message, Toast.LENGTH_LONG).show();
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(view.getContext()).
                    setTitle("Fingerprint").
                    setMessage(message).
                    setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                            finish();
                        }
                    })
                    .create();
            dialog.show();
            return true;
        }
    }
}