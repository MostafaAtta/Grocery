package thegroceryshop.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.SpinnerItemAdapterNormal;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.SelectItemUtil;
import thegroceryshop.com.modal.Brand;
import thegroceryshop.com.modal.Category;
import thegroceryshop.com.modal.Item;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.products.ProductListGridAdapter;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.application.OnlineMartApplication.mLocalStore;

/**
 * Created by rohitg on 4/12/2017.
 */

public class ProductListActivity extends BaseActivity {

    private FrameLayout lyt_parent;
    private View lyt_root;
    private Spinner spinner_view_by;
    private Spinner spinner_sort_by;
    private RecyclerView recyl_products;
    private LoaderLayout lyt_loader_products;
    private LinearLayout lyt_add_to_cart;
    private TextView txt_item_add_or_remove;
    private TextView txt_cart_capecity;
    private LinearLayout lyt_progress;
    private ProgressBar progress_cart;
    private TextView txt_eligible;
    private ProductListGridAdapter productListGridAdapter;
    private Handler cart_progress_handler;
    private Runnable cart_progress_runnable;
    public RelativeLayout lyt_filter;
    public TextView txt_filter_display;
    public ImageView img_filter;
    private PopupWindow popupWindow;
    private boolean isLoadMore;
    private TextView text_see_all;
    private TextView text_sort_by;
    private TextView text_filter_by;
    private FrameLayout lyt_loader;

    private int PAGE_INDEX = 1;
    private boolean isProductsLoaded;
    public static final String CATEGORY_ID = "category_id";
    public static final String PROMOTION_ID = "promotion_id";
    public static final String IS_LOAD_CATEGORY = "is_load_category";
    public static final String SHOULD_MENU_VISIBLE = "should_menu_visible";
    public static final String PROMOTION_NAME = "promotion_name";
    public static final String SEARCH_KEYWORD = "search_keyword";
    public static final String CATEGORY_NAME = "category_name";
    private String category_id;
    private String promotion_id;
    private boolean shouldCategoriesLoad;
    private boolean isProductLoading;
    private String category_name;
    private String promotion_name;
    private Context mContext;
    private ApiInterface apiService;
    private Call<ResponseBody> call;
    private String search_keyword;

    private ArrayList<Item> list_view_by, list_sort_by;
    ArrayList<Product> list_products = new ArrayList<>();
    private ArrayList<Category> list_categories = new ArrayList<>();
    private ArrayList<String> list_selected_brands = new ArrayList<>();
    private ArrayList<String> list_selected_prices = new ArrayList<>();
    private ArrayList<String> list_selected_origins = new ArrayList<>();
    private ArrayList<String> list_selected_lifestyles = new ArrayList<>();
    public ArrayList<Brand> list_brands;
    public ArrayList<Brand> list_prices = new ArrayList<>();
    public ArrayList<Brand> list_origins;
    public ArrayList<Brand> list_lifestyle;
    private boolean isPromotions = false;
    private MenuItem mBarCodeMenu, mSerachMenu;
    private boolean isFilterLoaded;
    private boolean shouldMenuVisisble;
    private GridLayoutManager gridLayoutManager;
    private HashMap<String, Call<ResponseBody>> map_loading_objects = new HashMap<>();
    private ArrayList<String> list_alerts = new ArrayList<String>();
    private LinearLayout lyt_sub1, lyt_sub2, lyt_sub3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            category_id = getIntent().getStringExtra(CATEGORY_ID);
            shouldCategoriesLoad = getIntent().getBooleanExtra(IS_LOAD_CATEGORY, false);
            category_name = getIntent().getStringExtra(CATEGORY_NAME);
            promotion_id = getIntent().getStringExtra(PROMOTION_ID);
            promotion_name = getIntent().getStringExtra(PROMOTION_NAME);
            search_keyword = getIntent().getStringExtra(SEARCH_KEYWORD);
            shouldMenuVisisble = getIntent().getBooleanExtra(SHOULD_MENU_VISIBLE, false);

            isPromotions = !(promotion_id == null && promotion_name == null);

            if(!ValidationUtil.isNullOrBlank(search_keyword)){
                isPromotions = true;
            }
        }

        mContext = this;
        apiService = ApiClient.createService(ApiInterface.class, mContext);

        lyt_parent = findViewById(R.id.base_child);
        lyt_root = getLayoutInflater().inflate(R.layout.activity_product_listing, null);

        lyt_parent.addView(lyt_root);
        initView(lyt_root);

    }

    private void initView(View view) {

        apiService = ApiClient.createService(ApiInterface.class, mContext);

        spinner_view_by = view.findViewById(R.id.spinnerSee);
        spinner_sort_by = view.findViewById(R.id.spinnerSortBy);

        lyt_sub1 = (LinearLayout) view.findViewById(R.id.lyt_sub1);
        lyt_sub2 = (LinearLayout) view.findViewById(R.id.lyt_sub2);
        lyt_sub3 = (LinearLayout) view.findViewById(R.id.lyt_sub3);

        lyt_sub1.post(new Runnable() {
            @Override
            public void run() {
                spinner_view_by.setDropDownWidth(lyt_sub1.getWidth());
                spinner_view_by.setDropDownVerticalOffset(spinner_view_by.getHeight());

            }
        });

        lyt_sub2.post(new Runnable() {
            @Override
            public void run() {
                spinner_sort_by.setDropDownWidth(lyt_sub2.getWidth());
                spinner_sort_by.setDropDownVerticalOffset(spinner_sort_by.getHeight());
            }
        });

        recyl_products = view.findViewById(R.id.gridViewProductListing);

        text_see_all = view.findViewById(R.id.textViewSeeAll);
        text_sort_by = view.findViewById(R.id.textViewSortByPopularity);
        text_filter_by = view.findViewById(R.id.textViewFilterBy);

        lyt_loader_products = view.findViewById(R.id.list_product_loader_products);
        lyt_loader_products.setStatuText(getString(R.string.no_product_found));
        lyt_add_to_cart = view.findViewById(R.id.list_product_lyt_add_cart);
        txt_item_add_or_remove = view.findViewById(R.id.list_product_txt_cart_add_or_remove);
        txt_cart_capecity = view.findViewById(R.id.list_product_txt_cart_capecity);
        lyt_progress = view.findViewById(R.id.list_product_lyt_progress);
        progress_cart = view.findViewById(R.id.list_product_progress_cart);
        txt_eligible = view.findViewById(R.id.list_product_txt_eligible);
        lyt_loader = view.findViewById(R.id.listing_lyt_loader);

        lyt_add_to_cart.setVisibility(View.GONE);
        recyl_products.getItemAnimator().setChangeDuration(0);

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.cart_progress);
        progress_cart.setProgressDrawable(drawable);
        progress_cart.setMax((int) mLocalStore.getFreeShippingAmount());
        txt_eligible.setVisibility(View.GONE);

        lyt_filter = view.findViewById(R.id.product_list_lyt_filter);
        txt_filter_display = view.findViewById(R.id.product_list_txt_filter);
        img_filter = view.findViewById(R.id.product_list_img_filter);

        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.filter))) {
                    resetFilter();
                } else {
                    initiatePopupWindow();
                }
            }
        });

        setFilterData();

        lyt_loader_products.showProgress();
        loadProducts();
        //loadFilters();

        list_view_by = new ArrayList<>();

        recyl_products.setHasFixedSize(true);
        recyl_products.setFocusable(false);
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        recyl_products.setLayoutManager(gridLayoutManager);

        if (shouldCategoriesLoad) {
            list_products.add(null);
            loadCategories();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? 3 : 1;
                }
            });

            productListGridAdapter = new ProductListGridAdapter(mContext, list_products, recyl_products);
            productListGridAdapter.setHasStableIds(true);
            productListGridAdapter.setCategoryList(list_categories);
            recyl_products.setAdapter(productListGridAdapter);
        }

        cart_progress_runnable = new Runnable() {
            @Override
            public void run() {
                lyt_add_to_cart.setVisibility(View.GONE);
            }
        };
    }

    private void initiatePopupWindow() {

        if (isFilterLoaded) {
            try {

                // We need to get the instance of the LayoutInflater
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.popup_filter, (ViewGroup) lyt_root.findViewById(R.id.lyt_pop_up_root));
                popupWindow = new PopupWindow(layout, lyt_sub3.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(lyt_sub3);

                FrameLayout lyt_brand = layout.findViewById(R.id.lyt_brand);
                FrameLayout lyt_price = layout.findViewById(R.id.lyt_price);
                FrameLayout lyt_origin = layout.findViewById(R.id.lyt_origin);
                RelativeLayout lyt_lifestyle = layout.findViewById(R.id.lyt_lifestyle);

                final ImageView img_barnd_close = layout.findViewById(R.id.filter_brand_img_close);
                final ImageView img_price_close = layout.findViewById(R.id.filter_price_img_close);
                final ImageView img_origin_close = layout.findViewById(R.id.filter_origin_img_close);
                final ImageView img_lifestyle_close = layout.findViewById(R.id.filter_lifestyle_img_close);

                if(list_brands == null || list_brands.size() == 0){
                    lyt_brand.setVisibility(View.GONE);
                }else{
                    lyt_brand.setVisibility(View.VISIBLE);
                }

                if(list_origins == null || list_origins.size() == 0){
                    lyt_origin.setVisibility(View.GONE);
                }else{
                    lyt_origin.setVisibility(View.VISIBLE);
                }

                if(list_lifestyle == null || list_lifestyle.size() == 0){
                    lyt_lifestyle.setVisibility(View.GONE);
                }else{
                    lyt_lifestyle.setVisibility(View.VISIBLE);
                }

                img_barnd_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        PAGE_INDEX = 1;
                        list_selected_brands.clear();
                        setFilterOptions();
                        if(list_products != null){
                            list_products.clear();
                            if(shouldCategoriesLoad){
                                list_products.add(null);
                            }
                            if(productListGridAdapter != null){
                                productListGridAdapter.notifyDataSetChanged();
                            }
                        }
                        lyt_loader_products.showProgress();
                        loadProducts();
                    }

                });

                img_price_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        PAGE_INDEX = 1;
                        list_selected_prices.clear();
                        setFilterOptions();
                        if(list_products != null){
                            list_products.clear();
                            if(shouldCategoriesLoad){
                                list_products.add(null);
                            }
                            if(productListGridAdapter != null){
                                productListGridAdapter.notifyDataSetChanged();
                            }
                        }
                        lyt_loader_products.showProgress();
                        loadProducts();
                    }
                });

                img_origin_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        PAGE_INDEX = 1;
                        list_selected_origins.clear();
                        setFilterOptions();
                        if(list_products != null){
                            list_products.clear();
                            if(shouldCategoriesLoad){
                                list_products.add(null);
                            }
                            if(productListGridAdapter != null){
                                productListGridAdapter.notifyDataSetChanged();
                            }
                        }
                        lyt_loader_products.showProgress();
                        loadProducts();
                    }
                });

                img_lifestyle_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        PAGE_INDEX = 1;
                        list_selected_lifestyles.clear();
                        setFilterOptions();
                        if(list_products != null){
                            list_products.clear();
                            if(shouldCategoriesLoad){
                                list_products.add(null);
                            }
                            if(productListGridAdapter != null){
                                productListGridAdapter.notifyDataSetChanged();
                            }
                        }
                        lyt_loader_products.showProgress();
                        loadProducts();
                    }
                });

                if (list_selected_brands != null && list_selected_brands.size() > 0) {
                    img_barnd_close.setVisibility(View.VISIBLE);
                } else {
                    img_barnd_close.setVisibility(View.GONE);
                }

                if (list_selected_prices != null && list_selected_prices.size() > 0) {
                    img_price_close.setVisibility(View.VISIBLE);
                } else {
                    img_price_close.setVisibility(View.GONE);
                }

                if (list_selected_origins != null && list_selected_origins.size() > 0) {
                    img_origin_close.setVisibility(View.VISIBLE);
                } else {
                    img_origin_close.setVisibility(View.GONE);
                }

                if (list_selected_lifestyles != null && list_selected_lifestyles.size() > 0) {
                    img_lifestyle_close.setVisibility(View.VISIBLE);
                } else {
                    img_lifestyle_close.setVisibility(View.GONE);
                }

                lyt_brand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }

                        if(list_brands != null && list_brands.size() > 0){
                            SelectItemUtil.showMultiSelectDialog(mContext, getString(R.string.select_brand), list_brands, new SelectItemUtil.ObjectSelectedListener() {
                                @Override
                                public void onObjectSelected(String key, Object object, String diaplayValue) {

                                    list_selected_brands.clear();
                                    for (int i = 0; i < list_brands.size(); i++) {
                                        if (list_brands.get(i).isSelected()) {
                                            list_selected_brands.add(list_brands.get(i).getKey());
                                        }
                                    }

                                    if (list_selected_brands.size() > 0) {
                                        img_filter.setImageResource(R.drawable.img_clase_white);
                                        if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.filter))
                                                || txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.brand))) {
                                            txt_filter_display.setText(getString(R.string.brand));
                                        } else {
                                            txt_filter_display.setText(getString(R.string.multiple));
                                        }
                                    }else{
                                        if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.brand))) {
                                            txt_filter_display.setText(getString(R.string.filter));
                                            img_filter.setImageResource(R.drawable.img_dropdown_white);
                                        }else if(txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.multiple))){

                                            int visible_count = 0; String selected = AppConstants.BLANK_STRING;
                                            if(img_lifestyle_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.lifestyle_benefits);
                                            }
                                            if(img_price_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.pricee);
                                            }
                                            if(img_origin_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.origin);
                                            }

                                            if(visible_count > 1){
                                                txt_filter_display.setText(getString(R.string.multiple));
                                            }else{
                                                txt_filter_display.setText(selected);
                                            }
                                        }
                                    }

                                    list_products.clear();
                                    if(shouldCategoriesLoad){
                                        list_products.add(null);
                                    }
                                    PAGE_INDEX = 1;
                                    if(list_products != null){
                                        list_products.clear();
                                        if(shouldCategoriesLoad){
                                            list_products.add(null);
                                        }
                                        if(productListGridAdapter != null){
                                            productListGridAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    lyt_loader_products.showProgress();
                                    loadProducts();
                                }
                            });
                        }else{
                            AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, getString(R.string.no_brand_list), getString(R.string.ok));
                        }
                    }
                });

                lyt_price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }

                        SelectItemUtil.showSelectDialog(mContext, getString(R.string.select_price_range), list_prices, new SelectItemUtil.ObjectSelectedListener() {
                            @Override
                            public void onObjectSelected(String key, Object object, String diaplayValue) {

                                list_selected_prices.clear();
                                for (int i = 0; i < list_prices.size(); i++) {
                                    if (list_prices.get(i).isSelected()) {
                                        list_selected_prices.add(list_prices.get(i).getKey());
                                    }
                                }

                                if (list_selected_prices.size() > 0) {
                                    img_filter.setImageResource(R.drawable.img_clase_white);
                                    if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.filter))
                                            || txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.pricee))) {
                                        txt_filter_display.setText(getString(R.string.pricee));
                                    } else {
                                        txt_filter_display.setText(getString(R.string.multiple));
                                    }
                                } else {
                                    if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.pricee))) {
                                        txt_filter_display.setText(getString(R.string.filter));
                                        img_filter.setImageResource(R.drawable.img_dropdown_white);
                                    }else if(txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.multiple))){

                                        int visible_count = 0; String selected = AppConstants.BLANK_STRING;
                                        if(img_lifestyle_close.getVisibility() == View.VISIBLE){
                                            visible_count = visible_count + 1;
                                            selected = getString(R.string.lifestyle_benefits);
                                        }
                                        if(img_barnd_close.getVisibility() == View.VISIBLE){
                                            visible_count = visible_count + 1;
                                            selected = getString(R.string.brand);
                                        }
                                        if(img_origin_close.getVisibility() == View.VISIBLE){
                                            visible_count = visible_count + 1;
                                            selected = getString(R.string.origin);
                                        }

                                        if(visible_count > 1){
                                            txt_filter_display.setText(getString(R.string.multiple));
                                        }else{
                                            txt_filter_display.setText(selected);
                                        }
                                    }
                                }

                                list_products.clear();
                                if(shouldCategoriesLoad){
                                    list_products.add(null);
                                }
                                PAGE_INDEX = 1;
                                if(list_products != null){
                                    list_products.clear();
                                    if(shouldCategoriesLoad){
                                        list_products.add(null);
                                    }
                                    if(productListGridAdapter != null){
                                        productListGridAdapter.notifyDataSetChanged();
                                    }
                                }
                                lyt_loader_products.showProgress();
                                loadProducts();
                            }
                        });
                    }
                });

                lyt_origin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }

                        if(list_origins != null && list_origins.size() > 0){
                            SelectItemUtil.showMultiSelectDialog(mContext, getString(R.string.select_origin), list_origins, new SelectItemUtil.ObjectSelectedListener() {
                                @Override
                                public void onObjectSelected(String key, Object object, String diaplayValue) {

                                    list_selected_origins.clear();
                                    for (int i = 0; i < list_origins.size(); i++) {
                                        if (list_origins.get(i).isSelected()) {
                                            list_selected_origins.add(list_origins.get(i).getKey());
                                        }
                                    }

                                    if (list_selected_origins.size() > 0) {
                                        img_filter.setImageResource(R.drawable.img_clase_white);
                                        if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.filter))
                                                || txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.origin))) {
                                            txt_filter_display.setText(getString(R.string.origin));
                                        } else {
                                            txt_filter_display.setText(getString(R.string.multiple));
                                        }
                                    }else{
                                        if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.origin))) {
                                            txt_filter_display.setText(getString(R.string.filter));
                                            img_filter.setImageResource(R.drawable.img_dropdown_white);
                                        }else if(txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.multiple))){

                                            int visible_count = 0; String selected = AppConstants.BLANK_STRING;
                                            if(img_lifestyle_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.lifestyle_benefits);
                                            }
                                            if(img_barnd_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.brand);
                                            }
                                            if(img_price_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.pricee);
                                            }

                                            if(visible_count > 1){
                                                txt_filter_display.setText(getString(R.string.multiple));
                                            }else{
                                                txt_filter_display.setText(selected);
                                            }
                                        }
                                    }

                                    list_products.clear();
                                    if(shouldCategoriesLoad){
                                        list_products.add(null);
                                    }
                                    PAGE_INDEX = 1;
                                    if(list_products != null){
                                        list_products.clear();
                                        if(shouldCategoriesLoad){
                                            list_products.add(null);
                                        }
                                        if(productListGridAdapter != null){
                                            productListGridAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    lyt_loader_products.showProgress();
                                    loadProducts();
                                }
                            });
                        }else{
                            AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, getString(R.string.no_origin_list), getString(R.string.ok));
                        }
                    }
                });

                lyt_lifestyle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }

                        if(list_lifestyle != null && list_lifestyle.size() > 0){
                            SelectItemUtil.showMultiSelectDialog(mContext, getString(R.string.select_lifestyle_benefits), list_lifestyle, new SelectItemUtil.ObjectSelectedListener() {
                                @Override
                                public void onObjectSelected(String key, Object object, String diaplayValue) {

                                    list_selected_lifestyles.clear();
                                    for (int i = 0; i < list_lifestyle.size(); i++) {
                                        if (list_lifestyle.get(i).isSelected()) {
                                            list_selected_lifestyles.add(list_lifestyle.get(i).getKey());
                                        }
                                    }

                                    if (list_selected_lifestyles.size() > 0) {
                                        img_filter.setImageResource(R.drawable.img_clase_white);
                                        if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.filter))
                                                || txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.lifestyle_benefits))) {
                                            txt_filter_display.setText(getString(R.string.lifestyle_benefits));
                                        } else {
                                            txt_filter_display.setText(getString(R.string.multiple));
                                        }
                                    }else{
                                        if (txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.lifestyle_benefits))) {
                                            txt_filter_display.setText(getString(R.string.filter));
                                            img_filter.setImageResource(R.drawable.img_dropdown_white);
                                        }else if(txt_filter_display.getText().toString().equalsIgnoreCase(getString(R.string.multiple))){

                                            int visible_count = 0; String selected = AppConstants.BLANK_STRING;
                                            if(img_price_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.pricee);
                                            }
                                            if(img_barnd_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.brand);
                                            }
                                            if(img_origin_close.getVisibility() == View.VISIBLE){
                                                visible_count = visible_count + 1;
                                                selected = getString(R.string.origin);
                                            }

                                            if(visible_count > 1){
                                                txt_filter_display.setText(getString(R.string.multiple));
                                            }else{
                                                txt_filter_display.setText(selected);
                                            }
                                        }
                                    }

                                    list_products.clear();
                                    if(shouldCategoriesLoad){
                                        list_products.add(null);
                                    }
                                    PAGE_INDEX = 1;
                                    if(list_products != null){
                                        list_products.clear();
                                        if(shouldCategoriesLoad){
                                            list_products.add(null);
                                        }
                                        if(productListGridAdapter != null){
                                            productListGridAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    lyt_loader_products.showProgress();
                                    loadProducts();
                                }
                            });
                        }else{
                            AppUtil.displaySingleActionAlert(mContext, AppConstants.BLANK_STRING, getString(R.string.no_tag_list), getString(R.string.ok));
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setFilterData() {

        list_view_by = new ArrayList<>();
        list_view_by.add(new Item("0", getString(R.string.all), false));
        list_view_by.add(new Item("1", getString(R.string.new_string), false));
        list_view_by.add(new Item("2", getString(R.string.on_sale), false));
        list_view_by.add(new Item("3", getString(R.string.in_stock), false));
        list_view_by.add(new Item("4", getString(R.string.bulk_orders), false));

        final SpinnerItemAdapterNormal view_by_adapter = new SpinnerItemAdapterNormal(mContext, list_view_by);
        spinner_view_by.setAdapter(view_by_adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner_view_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        list_products.clear();
                        if(shouldCategoriesLoad){
                            list_products.add(null);
                        }
                        PAGE_INDEX = 1;
                        if(list_products != null){
                            list_products.clear();
                            if(shouldCategoriesLoad){
                                list_products.add(null);
                            }
                            if(productListGridAdapter != null){
                                productListGridAdapter.notifyDataSetChanged();
                            }
                        }
                        lyt_loader_products.showProgress();
                        loadProducts();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }, 500);
        //{"Popularity", "Savings", "Recently Added", "Price - Low to High", "Price - High to Low", "Brand A-Z", "Brand Z-A"}

        list_sort_by = new ArrayList<>();
        list_sort_by.add(new Item("popularity", getString(R.string.popularity), false));
        list_sort_by.add(new Item("saving", getString(R.string.savings), false));
        list_sort_by.add(new Item("new", getString(R.string.recently_added), false));
        list_sort_by.add(new Item("pricel", getString(R.string.price_low_to_high), false));
        list_sort_by.add(new Item("priceh", getString(R.string.price_high_to_low), false));
        list_sort_by.add(new Item("branda", getString(R.string.brand_a_to_z), false));
        list_sort_by.add(new Item("brandz", getString(R.string.brand_z_to_a), false));
        spinner_sort_by.setAdapter(new SpinnerItemAdapterNormal(mContext, list_sort_by));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner_sort_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        list_products.clear();

                        Item i = list_sort_by.get(position);

                        if(shouldCategoriesLoad){
                            list_products.add(null);
                        }
                        PAGE_INDEX = 1;
                        if(list_products != null){
                            list_products.clear();
                            if(shouldCategoriesLoad){
                                list_products.add(null);
                            }
                            if(productListGridAdapter != null){
                                productListGridAdapter.notifyDataSetChanged();
                            }
                        }
                        lyt_loader_products.showProgress();
                        loadProducts();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }, 500);

        lyt_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });

        if(list_prices != null){
            list_prices.clear();
        }else{
            list_prices = new ArrayList<>();
        }
    }

    public void setFilterOptions() {
        if (list_selected_brands.size() == 0
                && list_selected_prices.size() == 0
                && list_selected_origins.size() == 0
                && list_selected_lifestyles.size() == 0) {

            txt_filter_display.setText(getString(R.string.filter));
            img_filter.setImageResource(R.drawable.img_dropdown_white);
        } else {
            int checkMultiple = 0;
            if (list_selected_brands.size() > 0) {
                checkMultiple++;
                txt_filter_display.setText(getString(R.string.brand));
            }
            if (list_selected_prices.size() > 0) {
                checkMultiple++;
                txt_filter_display.setText(getString(R.string.pricee));
            }
            if (list_selected_origins.size() > 0) {
                checkMultiple++;
                txt_filter_display.setText(getString(R.string.origin));
            }
            if (list_selected_lifestyles.size() > 0) {
                checkMultiple++;
                txt_filter_display.setText(getString(R.string.lifestyle_benefits));
            }

            img_filter.setImageResource(R.drawable.img_clase_white);
            if (checkMultiple > 1) {
                txt_filter_display.setText(getString(R.string.multiple));
            }
        }
    }

    private void resetFilter() {
        list_selected_brands.clear();
        list_selected_prices.clear();
        list_selected_origins.clear();
        list_selected_lifestyles.clear();
        txt_filter_display.setText(getString(R.string.filter));
        img_filter.setImageResource(R.drawable.img_dropdown_white);
        list_products.clear();
        if(shouldCategoriesLoad){
            list_products.add(null);
        }
        PAGE_INDEX = 1;
        if(list_products != null){
            list_products.clear();
            if(shouldCategoriesLoad){
                list_products.add(null);
            }
            if(productListGridAdapter != null){
                productListGridAdapter.notifyDataSetChanged();
            }
        }
        lyt_loader_products.showProgress();
        loadProducts();
    }

    public JSONObject getFilterObj() {

        JSONObject filterObj = new JSONObject();

        try {

            if (list_selected_brands.size() > 0) {
                String selected_brands = AppConstants.BLANK_STRING;
                for (int i = 0; i < list_selected_brands.size(); i++) {
                    if (i == list_selected_brands.size() - 1) {
                        selected_brands = selected_brands + list_selected_brands.get(i);
                    } else {
                        selected_brands = selected_brands + list_selected_brands.get(i) + ",";
                    }
                }
                filterObj.put("brands", selected_brands);

            } else {
                filterObj.put("brands", AppConstants.BLANK_STRING);
            }

            if (list_selected_prices.size() > 0) {

                String selected_prices = AppConstants.BLANK_STRING;
                for (int i = 0; i < list_selected_prices.size(); i++) {
                    if (i == list_selected_prices.size() - 1) {
                        selected_prices = selected_prices + list_selected_prices.get(i);
                    } else {
                        selected_prices = selected_prices + list_selected_prices.get(i) + ",";
                    }
                }
                filterObj.put("price", selected_prices);

            } else {
                filterObj.put("price", AppConstants.BLANK_STRING);
            }

            if (list_selected_origins.size() > 0) {

                String selected_origin = AppConstants.BLANK_STRING;
                for (int i = 0; i < list_selected_origins.size(); i++) {
                    if (i == list_selected_origins.size() - 1) {
                        selected_origin = selected_origin + list_selected_origins.get(i);
                    } else {
                        selected_origin = selected_origin + list_selected_origins.get(i) + ",";
                    }
                }
                filterObj.put("origin", selected_origin);

            } else {
                filterObj.put("origin", AppConstants.BLANK_STRING);
            }

            if (list_selected_lifestyles.size() > 0) {

                String selected_lifestyles = AppConstants.BLANK_STRING;
                for (int i = 0; i < list_selected_lifestyles.size(); i++) {
                    if (i == list_selected_lifestyles.size() - 1) {
                        selected_lifestyles = selected_lifestyles + list_selected_lifestyles.get(i);
                    } else {
                        selected_lifestyles = selected_lifestyles + list_selected_lifestyles.get(i) + ",";
                    }
                }
                filterObj.put("selected_lifestyles", selected_lifestyles);

            } else {
                filterObj.put("selected_lifestyles", AppConstants.BLANK_STRING);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return filterObj;

    }

    private void loadCategories() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());
            request_data.put("category_id", category_id);

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    //AppUtil.showProgress(mContext);
                    Call<ResponseBody> call = apiService.loadcategories((new ConvertJsonToMap().jsonToMap(request_data)));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //AppUtil.hideProgress();

                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONArray categoriesArray = resObj.optJSONArray("data");
                                            if (categoriesArray != null && categoriesArray.length() > 0) {

                                                list_categories.clear();
                                                for (int i = 0; i < categoriesArray.length(); i++) {

                                                    JSONObject categoryObj = categoriesArray.getJSONObject(i);
                                                    if (categoryObj != null) {
                                                        Category category = new Category();

                                                        if (!ValidationUtil.isNullOrBlank(categoryObj.optInt("level"))) {
                                                            category.setCategoryLevel(categoryObj.optInt("level"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(categoryObj.optInt("admin_category_level"))) {
                                                            category.setAdminCategoryLevel(categoryObj.optInt("admin_category_level"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(categoryObj.optString("category_id"))) {
                                                            category.setId(categoryObj.optString("category_id"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(categoryObj.optString("name"))) {
                                                            category.setLabel(categoryObj.optString("name"));
                                                        }

                                                        if (!ValidationUtil.isNullOrBlank(categoryObj.optString("image"))) {
                                                            category.setImage(categoryObj.optString("image"));
                                                        }

                                                        list_categories.add(category);
                                                    }
                                                }

                                                if (productListGridAdapter == null) {
                                                    productListGridAdapter = new ProductListGridAdapter(mContext, list_products, recyl_products);
                                                    productListGridAdapter.setCategoryList(list_categories);
                                                    recyl_products.setAdapter(productListGridAdapter);
                                                }else{
                                                    productListGridAdapter.setCategoryList(list_categories);
                                                    productListGridAdapter.notifyItemChanged(0);
                                                }


                                            } else {
                                                AppUtil.showErrorDialog(mContext, errorMsg);
                                            }

                                        } else {
                                            if (productListGridAdapter == null) {
                                                productListGridAdapter = new ProductListGridAdapter(mContext, list_products, recyl_products);
                                                productListGridAdapter.setCategoryList(null);
                                                recyl_products.setAdapter(productListGridAdapter);
                                            }else{
                                                productListGridAdapter.setCategoryList(null);
                                                productListGridAdapter.notifyItemChanged(0);
                                            }
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-628)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-629)");
                                }
                            } else {
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-630)");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCartProgress(final String priceString, final int quantity) {

        if(quantity != 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                    float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                    String updatedTotal = null;

                    txt_item_add_or_remove.setText(getString(R.string.product_removed_from_cart));
                    updatedTotal = " "+ getString(R.string.egp) +" " + Math.round((current_total - price) * quantity);

                    OnlineMartApplication.mLocalStore.saveCartTotal(Float.parseFloat(updatedTotal.trim().split(" ")[1]));

                    if (OnlineMartApplication.mLocalStore.getFreeShippingAmount() > Float.parseFloat(updatedTotal.trim().split(" ")[1])) {
                        lyt_progress.setVisibility(View.VISIBLE);
                        txt_eligible.setVisibility(View.GONE);
                        progress_cart.setProgress((int) Float.parseFloat(updatedTotal.trim().split(" ")[1]));
                    } else {
                        lyt_progress.setVisibility(View.GONE);
                        txt_eligible.setVisibility(View.VISIBLE);
                    }

                    if (cart_progress_handler == null) {
                        cart_progress_handler = new Handler();
                    }

                    lyt_add_to_cart.setVisibility(View.VISIBLE);
                    int remaingEGP = Math.round(OnlineMartApplication.mLocalStore.getFreeShippingAmount() - Float.parseFloat(updatedTotal.trim().split(" ")[1]));

                    if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                        txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), getString(R.string.egp) + AppConstants.SPACE + remaingEGP));
                    }else{
                        txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), remaingEGP + AppConstants.SPACE + getString(R.string.egp)));
                    }

                    //txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), remaingEGP + AppConstants.BLANK_STRING).toUpperCase());
                    cart_progress_handler.postDelayed(cart_progress_runnable, 4000);

                }
            });
        }

    }

    private void loadProductInfo(final String id, final int position, final String quantityMode, final ProductListGridAdapter.ProductViewHolder productViewHolder) {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", id);
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    if(list_products != null && list_products.size()>0){
                        list_products.get(position).setLoadingInfo(true);

                    }
                    if(map_loading_objects.size() > 0){

                        for(Map.Entry<String, Call<ResponseBody>> entry: map_loading_objects.entrySet()) {

                            if(entry.getKey().equalsIgnoreCase(id)){
                                Call<ResponseBody> call = entry.getValue();
                                call.cancel();
                                break;
                            }
                        }
                    }

                    Call<ResponseBody> call = apiService.updateCartData((new ConvertJsonToMap().jsonToMap(request_data)));
                    map_loading_objects.put(id, call);
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //AppUtil.hideProgress();

                            map_loading_objects.remove(id);
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject object = resObj.optJSONObject("data");
                                            if (object != null) {

                                                JSONArray products_arr = object.optJSONArray("Product");
                                                if (products_arr != null && products_arr.length() > 0) {

                                                    for (int i = 0; i < products_arr.length(); i++) {

                                                        JSONObject jsonObject = products_arr.getJSONObject(i);
                                                        if (jsonObject != null) {
                                                            Product product = new Product();

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_id"))) {
                                                                product.setId(jsonObject.optString("product_id"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("english_product_name"))) {
                                                                product.setEnglishName(jsonObject.optString("english_product_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("arabic_product_name"))) {
                                                                product.setArabicName(jsonObject.optString("arabic_product_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("english_product_size"))) {
                                                                product.setEnglishQuantity(jsonObject.optString("english_product_size"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("arabic_product_size"))) {
                                                                product.setArabicQuantity(jsonObject.optString("arabic_product_size"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("english_brand_name"))) {
                                                                product.setEnglishBrandName(jsonObject.optString("english_brand_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("arabic_brand_name"))) {
                                                                product.setArabicBrandName(jsonObject.optString("arabic_brand_name"));
                                                            }

                                                            product.setOffer(jsonObject.optBoolean("offered_price_status", false));
                                                            product.setAddedToWishList(jsonObject.optBoolean("in_wishlist", false));

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_acutal_price"))) {
                                                                product.setActualPrice(jsonObject.optString("product_acutal_price"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optDouble("tax"))) {
                                                                product.setTaxCharges((float) jsonObject.optDouble("tax", 0.00f));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("door_step"))) {
                                                                if (jsonObject.optString("door_step").equalsIgnoreCase("1")) {
                                                                    product.setDoorStepDelivery(true);
                                                                } else {
                                                                    product.setDoorStepDelivery(false);
                                                                }
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_offer_price"))) {
                                                                product.setOfferedPrice(jsonObject.optString("product_offer_price"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("mark_sold_qty"))) {
                                                                product.setMarkSoldQuantity(jsonObject.optInt("mark_sold_qty"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("qty"))) {
                                                                if(jsonObject.optInt("qty") < 0){
                                                                    product.setMaxQuantity(0);
                                                                }else{
                                                                    product.setMaxQuantity(jsonObject.optInt("qty"));
                                                                }
                                                            }else{
                                                                product.setMaxQuantity(0);
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("currency"))) {
                                                                product.setCurrency(jsonObject.optString("currency"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optDouble("offer_percent", 0.0f))) {
                                                                //product.setOfferString(jsonObject.optString("offer_percent") + "% OFF");
                                                                product.setOfferString(Math.round(jsonObject.optDouble("offer_percent", 0.0f)) + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                            }

                                                            product.setShippingThirdParty(jsonObject.optBoolean("shipping_third_party", false));

                                                            if (product.isShippingThirdParty()) {
                                                                if (!ValidationUtil.isNullOrBlank(jsonObject.optString("shipping_hours"))) {
                                                                    product.setShippingHours(jsonObject.optString("shipping_hours"));
                                                                }
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_description"))) {

                                                                String htmlDescription = String.format("<div style='font-family: 'Monstserrat'; src: url('fonts/Monstserrat-Regular.otf');'>" + jsonObject.optString("product_description") + "</div>");
                                                                product.setProducDescription(htmlDescription);
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("currency"))) {
                                                                product.setCurrency(jsonObject.optString("currency"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("image"))) {
                                                                product.setImage(jsonObject.optString("image"));
                                                            }

                                                            if (product.getMaxQuantity() <= 0) {
                                                                product.setSoldOut(true);
                                                            } else {
                                                                product.setSoldOut(false);
                                                            }

                                                            if(productListGridAdapter != null && list_products.size()>0){

                                                                if(quantityMode.equalsIgnoreCase("addToCart")){

                                                                    list_products.set(position, product);
                                                                    product = list_products.get(position);

                                                                    if(product.getMaxQuantity() >= 1){

                                                                    } else {

                                                                        list_products.get(position).setSoldOut(true);
                                                                        list_products.get(position).setSelectedQuantity(0);

                                                                        updateCartProgress(
                                                                                list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                                list_products.get(position).getSelectedQuantity() - list_products.get(position).getMaxQuantity());
                                                                        list_products.get(position).setSelectedQuantity(list_products.get(position).getMaxQuantity());
                                                                        OnlineMartApplication.removeProductFromCart(product.getId());
                                                                        //Toast.makeText(mContext, String.format(getString(R.string.notify_out_of_stock), list_products.get(position).getLabel()), Toast.LENGTH_SHORT).show();

                                                                        String tag = listUpdateCart.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()});
                                                                        if(!list_alerts.contains(tag)){
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()}), getString(R.string.ok));
                                                                            appDialogSingleAction.show();
                                                                            list_alerts.add(tag);
                                                                            appDialogSingleAction.setTag(tag);
                                                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                                @Override
                                                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                    appDialogSingleAction.dismiss();
                                                                                    list_alerts.remove(appDialogSingleAction.getTag());
                                                                                }
                                                                            });
                                                                        }


                                                                    }

                                                                    if(productListGridAdapter != null){
                                                                        productListGridAdapter.updateUI(productViewHolder, position);
                                                                    }
                                                                    invalidateOptionsMenu();


                                                                } else if(quantityMode.equalsIgnoreCase("add")){

                                                                    product.setSelectedQuantity(list_products.get(position).getSelectedQuantity());
                                                                    product.setQuantityUpdated(list_products.get(position).isQuantityUpdated());
                                                                    list_products.set(position, product);
                                                                    product = list_products.get(position);

                                                                    if (product.getSelectedQuantity() < product.getMaxQuantity()) {

                                                                    } else {

                                                                        if(!product.isQuantityUpdated()){

                                                                            updateCartProgress(
                                                                                    list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                                    list_products.get(position).getSelectedQuantity() - list_products.get(position).getMaxQuantity());
                                                                            list_products.get(position).setSelectedQuantity(list_products.get(position).getMaxQuantity());
                                                                            OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());
                                                                            //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()}), Toast.LENGTH_SHORT).show();

                                                                            String tag = list_products.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()});
                                                                            if(!list_alerts.contains(tag)){
                                                                                AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()}), getString(R.string.ok));
                                                                                appDialogSingleAction.show();
                                                                                list_alerts.add(tag);
                                                                                appDialogSingleAction.setTag(tag);
                                                                                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                                    @Override
                                                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                        appDialogSingleAction.dismiss();
                                                                                        list_alerts.remove(appDialogSingleAction.getTag());
                                                                                    }
                                                                                });
                                                                            }

                                                                        }
                                                                    }

                                                                    if(productListGridAdapter != null){
                                                                        productListGridAdapter.updateUI(productViewHolder, position);
                                                                    }

                                                                } else if(quantityMode.equalsIgnoreCase("remove")){

                                                                    product.setSelectedQuantity(list_products.get(position).getSelectedQuantity());
                                                                    list_products.set(position, product);
                                                                    product = list_products.get(position);

                                                                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {

                                                                    } else {

                                                                        updateCartProgress(
                                                                                list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                                list_products.get(position).getSelectedQuantity() - list_products.get(position).getMaxQuantity());
                                                                        list_products.get(position).setSelectedQuantity(list_products.get(position).getMaxQuantity());
                                                                        OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());
                                                                        //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()}), Toast.LENGTH_SHORT).show();

                                                                        String tag = list_products.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()});
                                                                        if(!list_alerts.contains(tag)){
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_products.get(position).getMaxQuantity()+ AppConstants.BLANK_STRING, list_products.get(position).getLabel()}), getString(R.string.ok));
                                                                            appDialogSingleAction.show();
                                                                            list_alerts.add(tag);
                                                                            appDialogSingleAction.setTag(tag);
                                                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                                @Override
                                                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                    appDialogSingleAction.dismiss();
                                                                                    list_alerts.remove(appDialogSingleAction.getTag());
                                                                                }
                                                                            });
                                                                        }

                                                                    }

                                                                    if(productListGridAdapter != null){
                                                                        productListGridAdapter.updateUI(productViewHolder, position);
                                                                    }

                                                                    invalidateOptionsMenu();
                                                                }
                                                            }
                                                        }
                                                    }

                                                }

                                            } else {

                                            }

                                        } else if(statusCode == 305){
                                            OnlineMartApplication.openRegionPicker(ProductListActivity.this, errorMsg);

                                        } else {

                                        }

                                    } else {

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            map_loading_objects.remove(id);

                            if(!call.isCanceled()){
                                AppUtil.hideProgress();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {

        if (isProductLoading && call != null) {
            call.cancel();
        }

        try {
            isProductLoading = true;
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

            if (!ValidationUtil.isNullOrBlank(search_keyword)) {
                request_data.put("promotion_category_id", AppConstants.BLANK_STRING);
                request_data.put("keyword", search_keyword);
            } else {
                request_data.put("keyword", AppConstants.BLANK_STRING);
                if (isPromotions) {
                    request_data.put("promotion_category_id", promotion_id);
                } else {
                    request_data.put("category_id", category_id);
                }
            }
            request_data.put("index", PAGE_INDEX);
            request_data.put("sort_by", ((Item) spinner_sort_by.getSelectedItem()).getItemId());
            request_data.put("view_by", ((Item) spinner_view_by.getSelectedItem()).getItemId());
            request_data.put("filter", getFilterObj());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            loadFilters(request_data);

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    isProductsLoaded = false;
                    if (isPromotions) {
                        call = apiService.loadPromotionProducts((new ConvertJsonToMap().jsonToMap(request_data)));
                    } else {
                        call = apiService.loadProducts((new ConvertJsonToMap().jsonToMap(request_data)));
                    }

                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    /*call.enqueue(new Callback<ResponseBody>() {*/
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            isProductLoading = false;
                            isLoadMore = false;
                            isProductsLoaded = true;
                            lyt_loader_products.showContent();
                            lyt_loader.setVisibility(View.GONE);

                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if (dataObj != null && dataObj.length() > 0) {

                                                JSONArray array_products = dataObj.optJSONArray("Product");
                                                if (array_products != null) {

                                                    for (int i = 0; i < array_products.length(); i++) {

                                                        Product product = new Product();
                                                        JSONObject object = (JSONObject) array_products.get(i);

                                                        if (object != null) {

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("product_id"))) {
                                                                product.setId(object.optString("product_id"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("english_product_name"))) {
                                                                product.setEnglishName(object.optString("english_product_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("arabic_product_name"))) {
                                                                product.setArabicName(object.optString("arabic_product_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("english_product_size"))) {
                                                                product.setEnglishQuantity(object.optString("english_product_size"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("arabic_product_size"))) {
                                                                product.setArabicQuantity(object.optString("arabic_product_size"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("english_brand_name"))) {
                                                                product.setEnglishBrandName(object.optString("english_brand_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("arabic_brand_name"))) {
                                                                product.setArabicBrandName(object.optString("arabic_brand_name"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("image"))) {
                                                                product.setImage(object.optString("image"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("currency"))) {
                                                                product.setCurrency(object.optString("currency"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("product_acutal_price"))) {
                                                                product.setActualPrice(object.optString("product_acutal_price"));
                                                            }else{
                                                                product.setActualPrice("0.0");
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("product_offer_price"))) {
                                                                product.setOfferedPrice(object.optString("product_offer_price"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("brand_id"))) {
                                                                product.setBrandId(object.optString("brand_id"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optDouble("tax"))) {
                                                                product.setTaxCharges((float) object.optDouble("tax", 0.00f));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("door_step"))) {
                                                                if (object.optString("door_step").equalsIgnoreCase("1")) {
                                                                    product.setDoorStepDelivery(true);
                                                                } else {
                                                                    product.setDoorStepDelivery(false);
                                                                }
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optBoolean("offered_price_status", false))) {
                                                                product.setOffer(object.optBoolean("offered_price_status", false));
                                                            }

                                                            product.setAddedToWishList(object.optBoolean("in_wishlist", false));

                                                            if (!ValidationUtil.isNullOrBlank(object.optDouble("product_offer_percent", 0.0f))) {
                                                                //product.setOfferString(object.optString("product_offer_percent") + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                                product.setOfferString(Math.round(object.optDouble("product_offer_percent", 0.0f)) + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optBoolean("shipping_third_party", false))) {
                                                                product.setShippingThirdParty(object.optBoolean("shipping_third_party", false));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("shipping_hours"))) {
                                                                product.setShippingHours(object.optString("shipping_hours"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("qty"))) {
                                                                if(object.optInt("qty") < 0){
                                                                    product.setMaxQuantity(0);
                                                                }else{
                                                                    product.setMaxQuantity(object.optInt("qty"));
                                                                }
                                                            }else{
                                                                product.setMaxQuantity(0);
                                                            }

                                                            if (product.getMaxQuantity() <= 0) {
                                                                product.setSoldOut(true);
                                                            } else {
                                                                product.setSoldOut(false);
                                                            }

                                                            list_products.add(product);
                                                        }
                                                    }

                                                    if (productListGridAdapter == null) {
                                                        productListGridAdapter = new ProductListGridAdapter(mContext, list_products, recyl_products);
                                                        productListGridAdapter.setHasStableIds(true);
                                                        recyl_products.setAdapter(productListGridAdapter);
                                                    } else {
                                                        productListGridAdapter.notifyDataSetChanged();
                                                    }

                                                    if (array_products.length() < 30 || array_products.length() == 0) {
                                                        productListGridAdapter.setLoadMore(false);
                                                    } else {
                                                        productListGridAdapter.setLoadMore(true);
                                                    }

                                                    if(list_products.size() == 0){
                                                        lyt_loader_products.showStatusText();
                                                    }

                                                    productListGridAdapter.setLoaded();
                                                    productListGridAdapter.setOnLoadMoreListener(new ProductListGridAdapter.OnLoadMoreListener() {
                                                        @Override
                                                        public void onLoadMore() {

                                                            lyt_loader.setVisibility(View.VISIBLE);
                                                            isLoadMore = true;
                                                            PAGE_INDEX = PAGE_INDEX + 1;
                                                            loadProducts();
                                                        }
                                                    });

                                                    productListGridAdapter.setOnProductClickLIstener(new ProductListGridAdapter.OnProductClickLIstener() {

                                                        @Override
                                                        public void onProductClick(final int position) {

                                                            Intent productDetailsIntent = new Intent(mContext, ProductMoreDetailsActivity.class);
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_ID, list_products.get(position).getId());
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_NAME, list_products.get(position).getLabel());
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_BRAND_ID, list_products.get(position).getBrandId());

                                                            startActivity(productDetailsIntent);

                                                        }

                                                        @Override
                                                        public void onAddToCart(int position, View v, ProductListGridAdapter.ProductViewHolder productViewHolder) {
                                                            if (list_products != null && list_products.size() > position) {

                                                                 if (!list_products.get(position).isSoldOut()) {
                                                                    list_products.get(position).setAddedToCart(true);
                                                                    list_products.get(position).setLoadingInfo(true);
                                                                    OnlineMartApplication.addToCart(AppUtil.getCartObject(list_products.get(position)));

                                                                     Product product = list_products.get(position);

                                                                     if(product.getMaxQuantity() >= 1){
                                                                         loadProductInfo(list_products.get(position).getId(), position, "addToCart", productViewHolder);
                                                                         list_products.get(position).setAddedToCart(true);
                                                                         list_products.get(position).setSelectedQuantity(1);

                                                                         if (!list_products.get(position).isSoldOut()) {
                                                                             productListGridAdapter.updateUI(productViewHolder, position);

                                                                             OnlineMartApplication.addToCart(AppUtil.getCartObject(list_products.get(position)));
                                                                             updateCartProgress(
                                                                                     list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                                     true);
                                                                         }
                                                                     } else {}

                                                                     if(productListGridAdapter != null && list_products.size() > position){
                                                                         productListGridAdapter.updateUI(productViewHolder, position);
                                                                     }
                                                                     invalidateOptionsMenu();
                                                                 }
                                                            }
                                                        }

                                                        @Override
                                                        public void onIncreaseQuantity(int position, ProductListGridAdapter.ProductViewHolder productViewHolder) {
                                                            if (list_products != null && list_products.size() > position) {
                                                                loadProductInfo(list_products.get(position).getId(), position, "add", productViewHolder);

                                                                Product product = list_products.get(position);

                                                                product.setSelectedQuantity(list_products.get(position).getSelectedQuantity());
                                                                list_products.set(position, product);
                                                                product = list_products.get(position);

                                                                if (product.getSelectedQuantity() < product.getMaxQuantity()) {
                                                                    product.setQuantityUpdated(true);
                                                                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);

                                                                    if (cart_progress_handler != null) {
                                                                        lyt_add_to_cart.setVisibility(View.GONE);
                                                                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                    }
                                                                    OnlineMartApplication.increaseORDecreaseQtyOnCart(true, list_products.get(position).getId());
                                                                    updateCartProgress(
                                                                            list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                            true);
                                                                } else {
                                                                    product.setQuantityUpdated(false);
                                                                }
                                                                list_products.set(position, product);
                                                                if(productListGridAdapter != null){
                                                                    productListGridAdapter.updateUI(productViewHolder, position);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onDecreaseQuantity(int position, ProductListGridAdapter.ProductViewHolder productViewHolder) {
                                                            if (list_products != null && list_products.size() > position) {
                                                                loadProductInfo(list_products.get(position).getId(), position, "remove", productViewHolder);

                                                                Product product = list_products.get(position);

                                                                product.setSelectedQuantity(list_products.get(position).getSelectedQuantity());
                                                                list_products.set(position, product);
                                                                product = list_products.get(position);

                                                                if (product.getSelectedQuantity() <= product.getMaxQuantity()) {

                                                                    if (product.getSelectedQuantity() <= 1) {
                                                                        product.setAddedToCart(false);
                                                                        product.setSelectedQuantity(0);
                                                                        OnlineMartApplication.removeProductFromCart(product.getId());
                                                                        lyt_add_to_cart.setVisibility(View.GONE);

                                                                        if (cart_progress_handler != null) {
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                            cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                        }

                                                                        //OnlineMartApplication.increaseORDecreaseQtyOnCart(false, list_products.get(position).getId());
                                                                        updateCartProgress(
                                                                                list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                                false);
                                                                    }

                                                                    if (product.getSelectedQuantity() != 0) {
                                                                        product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                                                                        if(product.getSelectedQuantity() < product.getMaxQuantity()){
                                                                            if (cart_progress_handler != null) {
                                                                                lyt_add_to_cart.setVisibility(View.GONE);
                                                                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                            }

                                                                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, list_products.get(position).getId());
                                                                            updateCartProgress(
                                                                                    list_products.get(position).isOffer() ? list_products.get(position).getOfferedPrice() : list_products.get(position).getActualPrice(),
                                                                                    false);
                                                                        }else{
                                                                            product.setSelectedQuantity(product.getMaxQuantity());
                                                                            OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());

                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity()+ AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
                                                                            appDialogSingleAction.show();
                                                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                                @Override
                                                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                    appDialogSingleAction.dismiss();
                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                } else {


                                                                }

                                                                if(productListGridAdapter != null){
                                                                      productListGridAdapter.updateUI(productViewHolder, position);
                                                                }

                                                                invalidateOptionsMenu();

                                                            }
                                                        }

                                                        @Override
                                                        public void onNotifyMe(int position) {
                                                            if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())){
                                                                notifyme(list_products.get(position).getId());
                                                            }else{
                                                                AppUtil.requestLogin(mContext);
                                                            }
                                                        }

                                                    });
                                                } else {
                                                    if (productListGridAdapter != null) {
                                                        productListGridAdapter.notifyDataSetChanged();
                                                    }
                                                    lyt_loader_products.showStatusText();
                                                }

                                            } else {
                                                lyt_loader_products.showStatusText();
                                            }

                                        } else if(statusCode == 305){

                                            OnlineMartApplication.openRegionPicker(ProductListActivity.this, errorMsg);

                                        } else {
                                            AppUtil.showErrorDialog(mContext, errorMsg);
                                            lyt_loader_products.showStatusText();
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-631)");
                                        lyt_loader_products.showStatusText();
                                    }

                                } catch (Exception e) {

                                    lyt_loader_products.showStatusText();
                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-632)");
                                }
                            } else {
                                lyt_loader.setVisibility(View.GONE);
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-633)");

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            isLoadMore = false;
                            isProductsLoaded = true;
                            isProductLoading = false;
                            lyt_loader_products.showContent();
                            lyt_loader.setVisibility(View.GONE);

                            //AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), t.getMessage(), getString(R.string.ok));
                        }

                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    lyt_loader_products.showContent();
                }

            } else {
                isProductLoading = false;
                lyt_loader_products.showContent();
                lyt_loader.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            lyt_loader_products.showContent();
            lyt_loader.setVisibility(View.GONE);
        }
    }

    private void loadFilters(JSONObject request_data) {

        try {
            if (NetworkUtil.networkStatus(mContext)) {
                try {

                    Call<ResponseBody> call;

                    if(isPromotions){
                        call = apiService.getPromotionFilter((new ConvertJsonToMap().jsonToMap(request_data)));
                    }else{
                        call = apiService.getCategoryFilter((new ConvertJsonToMap().jsonToMap(request_data)));
                    }

                    isFilterLoaded = false;
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {
                                            
                                            isFilterLoaded = true;
                                            JSONObject dataObj = resObj.optJSONObject("data");
                                            if(dataObj != null){
                                                JSONArray array_brands = dataObj.optJSONArray("Brand");
                                                if (array_brands != null) {

                                                    list_brands = new ArrayList<>();
                                                    for (int i = 0; i < array_brands.length(); i++) {

                                                        Brand brand = null;
                                                        JSONObject object = (JSONObject) array_brands.get(i);

                                                        if (object != null) {

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("id"))
                                                                    && !ValidationUtil.isNullOrBlank(object.optString("name"))) {

                                                                brand = new Brand(object.optString("id"), object.optString("name"), false, object.optString("count"));
                                                                list_brands.add(brand);
                                                            }
                                                        }
                                                    }

                                                    if(list_brands.size() > 0){
                                                        if(list_selected_brands != null && list_selected_brands.size() > 0){
                                                            for(int i=0; i<list_selected_brands.size(); i++){

                                                                inner : for(int j=0; j<list_brands.size(); j++){

                                                                    if(list_brands.get(j).getBrandId().equalsIgnoreCase(list_selected_brands.get(i))){
                                                                        list_brands.get(j).setSelected(true);
                                                                        break inner;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                JSONArray array_price_ranges = dataObj.optJSONArray("PriceRange");
                                                if (array_price_ranges != null) {

                                                    list_prices = new ArrayList<>();
                                                    for (int i = 0; i < array_price_ranges.length(); i++) {

                                                        Brand brand = null;
                                                        JSONObject object = (JSONObject) array_price_ranges.get(i);

                                                        if (object != null) {

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("id"))
                                                                    && !ValidationUtil.isNullOrBlank(object.optString("name"))) {

                                                                brand = new Brand(object.optString("id"), object.optString("name"), false, object.optString("count"));
                                                                list_prices.add(brand);
                                                            }
                                                        }
                                                    }

                                                    if(list_prices.size() > 0){
                                                        if(list_selected_prices != null && list_selected_prices.size() > 0){
                                                            for(int i=0; i<list_selected_prices.size(); i++){

                                                                inner : for(int j=0; j<list_prices.size(); j++){

                                                                    if(list_prices.get(j).getBrandId().equalsIgnoreCase(list_selected_prices.get(i))){
                                                                        list_prices.get(j).setSelected(true);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                JSONArray array_lifestyles = dataObj.optJSONArray("LifeStyle");
                                                if (array_lifestyles != null) {

                                                    list_lifestyle = new ArrayList<>();
                                                    for (int i = 0; i < array_lifestyles.length(); i++) {

                                                        Brand brand = null;
                                                        JSONObject object = (JSONObject) array_lifestyles.get(i);

                                                        if (object != null) {

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("id"))
                                                                    && !ValidationUtil.isNullOrBlank(object.optString("name"))) {

                                                                brand = new Brand(object.optString("id"), object.optString("name"), false, object.optString("count"));
                                                            }
                                                            list_lifestyle.add(brand);
                                                        }
                                                    }

                                                    if(list_lifestyle.size() > 0){
                                                        if(list_selected_lifestyles != null && list_selected_lifestyles.size() > 0){
                                                            for(int i=0; i<list_selected_lifestyles.size(); i++){

                                                                inner : for(int j=0; j<list_lifestyle.size(); j++){

                                                                    if(list_lifestyle.get(j).getBrandId().equalsIgnoreCase(list_selected_lifestyles.get(i))){
                                                                        list_lifestyle.get(j).setSelected(true);
                                                                        break inner;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                JSONArray array_origins = dataObj.optJSONArray("OriginCountry");
                                                if (array_origins != null) {

                                                    list_origins = new ArrayList<>();
                                                    for (int i = 0; i < array_origins.length(); i++) {

                                                        Brand brand = null;
                                                        JSONObject object = (JSONObject) array_origins.get(i);

                                                        if (object != null) {

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("id"))
                                                                    && !ValidationUtil.isNullOrBlank(object.optString("name"))) {

                                                                brand = new Brand(object.optString("id"), object.optString("name"), false, object.optString("count"));
                                                            }
                                                            list_origins.add(brand);
                                                        }
                                                    }

                                                    if(list_origins.size() > 0){
                                                        if(list_selected_origins != null && list_selected_origins.size() > 0){
                                                            for(int i=0; i<list_selected_origins.size(); i++){

                                                                inner : for(int j=0; j<list_origins.size(); j++){

                                                                    if(list_origins.get(j).getBrandId().equalsIgnoreCase(list_selected_origins.get(i))){
                                                                        list_origins.get(j).setSelected(true);
                                                                        break inner;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                            }

                                        } else {
                                        }

                                    } else {
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), "\n" + t.getMessage(), getString(R.string.ok));
                            AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(ERR-635)", getString(R.string.ok));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtil.hideProgress();
        }

    }

    private void notifyme(String product_id) {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", product_id);
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    Call<ResponseBody> call = apiService.notifyMe((new ConvertJsonToMap().jsonToMap(request_data)));
                    AppUtil.showProgress(mContext);
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            AppUtil.hideProgress();
                            if (response.code() == 200) {
                                try {
                                    JSONObject obj = new JSONObject(response.body().string());
                                    JSONObject resObj = obj.optJSONObject("response");
                                    if (resObj.length() > 0) {

                                        int statusCode = resObj.optInt("status_code", 0);
                                        String errorMsg = resObj.optString("error_message");
                                        if (statusCode == 200) {

                                            String responseMessage = resObj.optString("success_message");
                                            AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), responseMessage, getString(R.string.ok));

                                        } else {
                                            AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), errorMsg, getString(R.string.ok));
                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(Err-683)", getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(Err-684)", getString(R.string.ok));
                                }
                            } else {
                                AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(Err-685)", getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(Err-686)", getString(R.string.ok));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppUtil.hideProgress();
                }

            } else {
                AppUtil.hideProgress();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppUtil.hideProgress();
        }
    }


    private void updateCartProgress(final String priceString, final boolean isAddToCart) {

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                float current_total = mLocalStore.getCartTotal();
                float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                String updatedTotal = null;
                if (isAddToCart) {
                    txt_item_add_or_remove.setText(String.format(getString(R.string.product_added_to_cart), "1" + AppConstants.BLANK_STRING).toUpperCase());
                    updatedTotal = " "+ getString(R.string.egp) +" " + String.format(Locale.ENGLISH,"%d",Math.round(current_total + price));
                } else {
                    txt_item_add_or_remove.setText(getString(R.string.product_removed_from_cart));
                    //txt_item_add_or_remove.setText(String.format(getString(R.string.product_removed_from_cart), "1" + AppConstants.BLANK_STRING).toUpperCase());
                    updatedTotal = " "+ getString(R.string.egp) +" " + String.format(Locale.ENGLISH,"%d",Math.round(current_total - price));
                }

                mLocalStore.saveCartTotal(Float.parseFloat(updatedTotal.trim().split(" ")[1]));

                if (mLocalStore.getFreeShippingAmount() > Float.parseFloat(updatedTotal.trim().split(" ")[1])) {
                    lyt_progress.setVisibility(View.VISIBLE);
                    txt_eligible.setVisibility(View.GONE);
                    progress_cart.setProgress((int) Float.parseFloat(updatedTotal.trim().split(" ")[1]));
                } else {
                    lyt_progress.setVisibility(View.GONE);
                    txt_eligible.setVisibility(View.VISIBLE);
                }

                if (cart_progress_handler == null) {
                    lyt_add_to_cart.setVisibility(View.GONE);
                    cart_progress_handler = new Handler();
                }

                lyt_add_to_cart.setVisibility(View.VISIBLE);
                int remaingEGP = Math.round(OnlineMartApplication.mLocalStore.getFreeShippingAmount() - Float.parseFloat(updatedTotal.trim().split(" ")[1]));

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), getString(R.string.egp) + AppConstants.SPACE + remaingEGP));
                }else{
                    txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), remaingEGP + AppConstants.SPACE + getString(R.string.egp)));
                }

                cart_progress_handler.postDelayed(cart_progress_runnable, 4000);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        super.headerShouldBeInLeft(true);
        if (!ValidationUtil.isNullOrBlank(search_keyword)) {
            super.setPageTitle(search_keyword);
        } else {
            if (isPromotions) {
                super.setPageTitle(promotion_name);
            } else {
                super.setPageTitle(category_name);
            }
        }

        super.setSearchBarVisiblity(false);
        if(isPromotions){
            super.setToolbarTag(getString(R.string.back));
        }else{
            if(shouldMenuVisisble){
                super.setToolbarTag(getString(R.string.menu));
            }else{
                super.setToolbarTag(getString(R.string.back));
            }
        }

        super.setCurrentActivity(this);

        if (productListGridAdapter != null) {

            if (OnlineMartApplication.getCartList().size() == 0) {
                if (list_products != null && list_products.size() > 0) {
                    for (int i = 0; i < list_products.size(); i++) {
                        if(list_products.get(i) != null){
                            list_products.get(i).setSelectedQuantity(0);
                            list_products.get(i).setAddedToCart(false);
                        }
                    }
                }
            }

            productListGridAdapter.notifyDataSetChanged();
        }
    }

    public void notifyData() {
        if (productListGridAdapter != null) {
            productListGridAdapter.notifyDataSetChanged();
        }
    }

    public void notifyData(final String product_id) {
        if (productListGridAdapter != null) {
            if(list_products != null){
                for(int i=1; i<list_products.size(); i++){
                    if(list_products.get(i) != null
                            && list_products.get(i).getId() != null
                            && list_products.get(i).getId().equalsIgnoreCase(product_id)){
                        list_products.get(i).setAddedToWishList(true);
                        break;
                    }
                }
            }
            productListGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cart_progress_handler != null) {
            lyt_add_to_cart.setVisibility(View.GONE);
            cart_progress_handler.removeCallbacks(cart_progress_runnable);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mBarCodeMenu =  menu.findItem(R.id.bar_code_menu);
        mSerachMenu  =  menu.findItem(R.id.serach_menu);
        mBarCodeMenu.setVisible(true);
        mSerachMenu.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.bar_code_menu:
                //startActivity(new Intent(ProductListActivity.this,BarCodeScan_Activity.class));
                startActivity(new Intent(ProductListActivity.this,BarcodeCaptureActivity.class));
                break;
            case R.id.serach_menu:
                Intent searchIntent = new Intent(ProductListActivity.this, SearchingActivity.class);
                startActivity(searchIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }

        super.onBackPressed();
    }

    class HtmlLoader extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //String s = getHtml("https://uat.thegroceryshop.com/FinePrint/FingerPrint.html");
                getWebsite();


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private void getWebsite() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder builder = new StringBuilder();

                    try {
                        Document doc = Jsoup.connect("https://uat.thegroceryshop.com/FinePrint/FingerPrint.html").get();
                        String title = doc.title();
                        Elements links = doc.select("a[href]");

                        builder.append(title).append("\n");

                        for (Element link : links) {
                            builder.append("\n").append("Link : ").append(link.attr("href"))
                                    .append("\n").append("Text : ").append(link.text());
                        }
                    } catch (IOException e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String ssss = (builder.toString());
                        }
                    });
                }
            }).start();
        }

        public String getHtml(String url) throws Exception, IOException {
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
    }
}
