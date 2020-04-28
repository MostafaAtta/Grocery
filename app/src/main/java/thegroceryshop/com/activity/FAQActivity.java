package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.ApiClient;

import static thegroceryshop.com.R.id.faq_lyt_ordering;

/*
 * Created by umeshk on 23-Mar-17.
 */

public class FAQActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout lyt_ordering;
    private RelativeLayout lyt_reg_and_personal;
    private RelativeLayout lyt_shipment_and_delivery;
    private RelativeLayout lyt_payment;
    private RelativeLayout lyt_return_or_exchange;
    private RelativeLayout lyt_promotion;
    private RelativeLayout lyt_online_ordering;
    private RelativeLayout lyt_compments;

    private Toolbar toolbar;
    private TextView txt_title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_faq);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }

            txt_title.setText(getResources().getString(R.string.faqs));
        }

        initView();
    }

    private void initView() {
        lyt_ordering = findViewById(R.id.faq_lyt_ordering);
        lyt_reg_and_personal = findViewById(R.id.faq_lyt_registration);
        lyt_shipment_and_delivery = findViewById(R.id.faq_lyt_shipment_and_delivery);
        lyt_payment = findViewById(R.id.faq_lyt_payment);
        lyt_return_or_exchange = findViewById(R.id.faq_lyt_return_or_exchange);
        lyt_promotion = findViewById(R.id.faq_lyt_promotions);
        lyt_online_ordering = findViewById(R.id.faq_lyt_online_ordering);
        lyt_compments = findViewById(R.id.faq_lyt_comments);

        lyt_ordering.setOnClickListener(this);
        lyt_reg_and_personal.setOnClickListener(this);
        lyt_shipment_and_delivery.setOnClickListener(this);
        lyt_payment.setOnClickListener(this);
        lyt_return_or_exchange.setOnClickListener(this);
        lyt_promotion.setOnClickListener(this);
        lyt_online_ordering.setOnClickListener(this);
        lyt_compments.setOnClickListener(this);

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
    public void onClick(View v) {

        switch (v.getId()) {

            case faq_lyt_ordering:
                callWebView(getResources().getString(R.string.faqs), "order");
                break;

            case R.id.faq_lyt_registration:
                callWebView(getResources().getString(R.string.faqs), "registration_and_personal_info");
                break;

            case R.id.faq_lyt_shipment_and_delivery:
                callWebView(getResources().getString(R.string.faqs), "shipment_delvery");
                break;

            case R.id.faq_lyt_payment:
                callWebView(getResources().getString(R.string.faqs), "payment");
                break;

            case R.id.faq_lyt_return_or_exchange:
                callWebView(getResources().getString(R.string.faqs), "return_exchange_policy");
                break;

            case R.id.faq_lyt_promotions:
                callWebView(getResources().getString(R.string.faqs), "promotions");
                break;

            case R.id.faq_lyt_online_ordering:
                callWebView(getResources().getString(R.string.faqs), "online_ordering");
                break;

            case R.id.faq_lyt_comments:
                callWebView(getResources().getString(R.string.faqs), "comments_complains");
                break;

        }
    }

    private void callWebView(String title, String keyword) {
        Intent intentAbout = new Intent(this, CommonWebViewActivity.class);
        intentAbout.putExtra(CommonWebViewActivity.KEY_URL, ApiClient.getPageURL() + "slug=" + keyword + "&lang_id=" + OnlineMartApplication.mLocalStore.getAppLangId() + "");
        intentAbout.putExtra(CommonWebViewActivity.KEY_TITLE, title);
        startActivity(intentAbout);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
