package thegroceryshop.com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.ProductListAdapter1;
import thegroceryshop.com.adapter.ProductRateReviewAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.AspectRatioImageView;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.circleIndicator.CirclePageIndicator;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.WishListDialog;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.modal.ProductRateReviewBean;
import thegroceryshop.com.modal.WishListBean;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.application.OnlineMartApplication.mLocalStore;

/**
 * Created by mohitd on 01-Mar-17.
 */

public class ProductMoreDetailsActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private FrameLayout lyt_parent;
    private View lyt_root;

    private ViewPager pager_images;
    private CirclePageIndicator pager_indicator;
    private TextView txt_product_name;
    private TextView txt_brand_name;
    private TextView product_txt_quantity;
    private TextView txt_product_price;
    private TextView txt_product_final_price;
    private TextView txt_prodcut_description;
    private TextView txt_add_to_cart;
    private TextView txt_plus;
    private TextView txt_minus;
    private TextView txt_selected_quantity;
    private RelativeLayout lyt_quantity;
    private RecyclerView recyl_brand_products;
    private RecyclerView recyl_similer_products;
    private LinearLayout lyt_same_brands, lyt_similar_products;
    private LoaderLayout loader_quantity;

    private ProductListAdapter1 adapter_same_brand, adapter_similar_products;
    private RecyclerView.LayoutManager lyt_manager_brands, lyt_manager_similer_products;
    private Context mContext;

    public static String KEY_PRODUCT_ID = "key_product_id";
    public static String KEY_BRAND_ID = "key_brand_id";
    public static String KEY_PRODUCT_NAME = "key_product_name";

    private String product_id, brand_id, product_name;
    private ApiInterface apiService;
    private LoaderLayout loader_root, loader_same_brands, loader_similar_producs;
    Product product;
    ArrayList<Product> list_same_brands_products = new ArrayList<>();
    ArrayList<Product> list_similer_products = new ArrayList<>();
    private TextView txt_cart_capecity;
    private LinearLayout lyt_progress;
    private ProgressBar progress_cart;
    private TextView txt_eligible;
    private Handler cart_progress_handler;
    private Runnable cart_progress_runnable;
    private LinearLayout lyt_add_to_cart;
    private TextView txt_add_or_remove;
    private TextView txt_offer, txt_item_remaining, txt_sold_out, txt_shipped_in;
    private ImageView img_save_to_list;
    private int suggested_width = 0;
    private MenuItem mBarCodeMenu, mSerachMenu;
    private HashMap<String, Call<ResponseBody>> map_loading_objects = new HashMap<>();
    private ArrayList<String> list_alerts = new ArrayList<>();
    private LoaderLayout commentLytLoader;
    private RecyclerView commentRecycler;
    private SimpleRatingBar productRating;
    private LinearLayout commentLayout;
    private TextView productTextReviewCount;

    private SimpleRatingBar finalRating;
    //private AppCompatEditText editTextComment;
    private RippleButton mRippleButtonSubmit;

    private int rating;
    private String comment;
    private WishListDialog.OnAddToWishListLister onAddToWishListLister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.layout_product_more_details);

        lyt_parent = findViewById(R.id.base_child);
        lyt_root = getLayoutInflater().inflate(R.layout.layout_product_more_details, null);

        lyt_parent.addView(lyt_root);

        apiService = ApiClient.createService(ApiInterface.class, mContext);
        mContext = ProductMoreDetailsActivity.this;
        initView();

        getSupportActionBar().setTitle(getResources().getString(R.string.product_details));
    }

    private void initView() {

        pager_images = lyt_root.findViewById(R.id.product_pager_images);
        pager_indicator = lyt_root.findViewById(R.id.product_pager_indicator);
        txt_product_name = lyt_root.findViewById(R.id.product_txt_name);
        txt_brand_name = lyt_root.findViewById(R.id.product_txt_brand_name);
        product_txt_quantity = lyt_root.findViewById(R.id.product_txt_quantity);
        txt_product_price = lyt_root.findViewById(R.id.product_txt_actual_price);
        txt_product_final_price = lyt_root.findViewById(R.id.product_txt_final_price);
        txt_prodcut_description = lyt_root.findViewById(R.id.product_txt_description);
        txt_add_to_cart = lyt_root.findViewById(R.id.product_txt_add_to_cart);
        txt_plus = lyt_root.findViewById(R.id.product_txt_plus_quantity);
        txt_minus = lyt_root.findViewById(R.id.product_txt_minus_quantity);
        txt_selected_quantity = lyt_root.findViewById(R.id.product_txt_order_quantity);
        lyt_quantity = lyt_root.findViewById(R.id.product_lyt_quantity);
        lyt_add_to_cart = lyt_root.findViewById(R.id.product_lyt_add_cart);
        txt_add_or_remove = lyt_root.findViewById(R.id.product_txt_cart_add_or_remove);
        lyt_same_brands = lyt_root.findViewById(R.id.product_lyt_same_brands);
        lyt_similar_products = lyt_root.findViewById(R.id.product_lyt_similar_products);
        loader_quantity = lyt_root.findViewById(R.id.product_lyt_loader_quantity);
        img_save_to_list = lyt_root.findViewById(R.id.product_img_save_to_list);

        txt_cart_capecity = lyt_root.findViewById(R.id.product_txt_cart_capecity);
        lyt_progress = lyt_root.findViewById(R.id.product_lyt_progress);
        progress_cart = lyt_root.findViewById(R.id.product_progress_cart);
        txt_eligible = lyt_root.findViewById(R.id.product_txt_eligible);

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.cart_progress);
        progress_cart.setProgressDrawable(drawable);
        progress_cart.setMax((int) mLocalStore.getFreeShippingAmount());
        txt_eligible.setVisibility(View.GONE);

        txt_offer = lyt_root.findViewById(R.id.product_txt_offer);
        txt_item_remaining = lyt_root.findViewById(R.id.product_txt_remaining);
        txt_sold_out = lyt_root.findViewById(R.id.product_txt_sold_out);
        txt_shipped_in = lyt_root.findViewById(R.id.product_txt_shipped_in);

        txt_offer.setVisibility(View.GONE);
        txt_item_remaining.setVisibility(View.GONE);
        txt_sold_out.setVisibility(View.GONE);
        txt_shipped_in.setVisibility(View.GONE);

        loader_root = lyt_root.findViewById(R.id.product_lyt_loader_root);
        loader_same_brands = lyt_root.findViewById(R.id.product_lyt_loader_brand);
        loader_similar_producs = lyt_root.findViewById(R.id.product_lyt_loader_similar_products);

        recyl_brand_products = lyt_root.findViewById(R.id.product_recyl_brand_products);
        recyl_similer_products = lyt_root.findViewById(R.id.product_recyl_similer_products);

        if (getIntent() != null) {
            if (!ValidationUtil.isNullOrBlank(getIntent().hasExtra(KEY_PRODUCT_ID))) {
                product_id = getIntent().getStringExtra(KEY_PRODUCT_ID);
                brand_id = getIntent().getStringExtra(KEY_BRAND_ID);
                product_name = getIntent().getStringExtra(KEY_PRODUCT_NAME);
            }
        }

        onAddToWishListLister = new WishListDialog.OnAddToWishListLister() {
            @Override
            public void onAddedToWishListListener(int position, String product_id) {
                if(product != null){
                    img_save_to_list.setImageDrawable(ContextCompat.getDrawable(ProductMoreDetailsActivity.this, R.drawable.icon_heart_filled));
                    product.setAddedToWishList(true);
                    OnlineMartApplication.updateWishlistFlagOnCart(product_id, true);

                    Intent intent = new Intent("WISHLIST_UPDATED");
                    intent.putExtra("product_id", product_id);
                    intent.putExtra("isAddedToWishList", true);
                    getLocalBroadCastManager().sendBroadcast(intent);
                }
            }
        };

        lyt_manager_brands = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        lyt_manager_similer_products = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyl_brand_products.setLayoutManager(lyt_manager_brands);
        recyl_similer_products.setLayoutManager(lyt_manager_similer_products);


        commentLytLoader = lyt_root.findViewById(R.id.comment_lyt_loader);
        commentRecycler = lyt_root.findViewById(R.id.comment_recycler);

        commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        commentRecycler.setHasFixedSize(true);

        finalRating = findViewById(R.id.rating);
        //editTextComment = findViewById(R.id.editTextComment);
        mRippleButtonSubmit = findViewById(R.id.mRippleButtonSubmit);

        /*editTextComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editTextComment) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });*/

        getProductRateReview();

        loader_root.showProgress();
        pager_images.addOnPageChangeListener(this);


        mRippleButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = (int) finalRating.getRating();
                //comment = editTextComment.getText().toString();

                if (rating <= 0) {
                    AppUtil.showErrorDialog(ProductMoreDetailsActivity.this, getString(R.string.please_enter_rating));
                } else {
                    submitRateAndReview();
                }
            }
        });

        pager_indicator = lyt_root.findViewById(R.id.product_pager_indicator);
        //  mCirclePageIndicator.setRadius(15);
        pager_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager_indicator.setCurrentItem(pager_images.getCurrentItem());
            }
        });

        txt_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product != null) {

                    if (txt_add_to_cart.getText().toString().equalsIgnoreCase(mContext.getString(R.string.notify_me))) {
                        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                            notifyme(product.getId());
                        } else {
                            AppUtil.requestLogin(mContext);
                        }
                    } else {
                        if (product.getSelectedQuantity() == 0) {

                            if (!product.isSoldOut()) {

                                txt_add_to_cart.setVisibility(View.GONE);
                                product.setAddedToCart(true);
                                loader_quantity.setVisibility(View.VISIBLE);
                                //loader_quantity.showProgress();
                                OnlineMartApplication.addToCart(AppUtil.getCartObject(product));
                                lyt_quantity.setVisibility(View.VISIBLE);
                                txt_selected_quantity.setVisibility(View.VISIBLE);
                                txt_selected_quantity.setText(product.getSelectedQuantity() + "");

                                txt_add_to_cart.setVisibility(View.GONE);
                                loader_quantity.setVisibility(View.VISIBLE);
                                //loader_quantity.showProgress();

                                loadProductInfo(product.getId(), -1, "addToCart", "3", null);

                                loader_quantity.showContent();
                                if (product.getMaxQuantity() >= 1) {
                                    OnlineMartApplication.addToCart(AppUtil.getCartObject(product));
                                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);
                                    updateCartProgress(
                                            product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                            true);
                                    updateData();
                                } else {

                                }
                            }
                        }
                        //invalidateOptionsMenu();
                    }

                }
            }
        });

        img_save_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                    String myListsNamesJSON = OnlineMartApplication.mLocalStore.getMyWishListNames();
                    ArrayList<WishListBean> listNames = new ArrayList<>();
                    if (!ValidationUtil.isNullOrBlank(myListsNamesJSON)) {
                        try {
                            JSONArray jsonArray = new JSONArray(myListsNamesJSON);
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    WishListBean wishListBean = new WishListBean();
                                    JSONObject dataObject = new JSONObject(jsonArray.getString(i));
                                    wishListBean.setId(dataObject.getString("id"));
                                    wishListBean.setName(dataObject.getString("name"));
                                    wishListBean.setDescription(dataObject.getString("description"));
                                    wishListBean.setNoOfItems(dataObject.getString("no_of_items"));
                                    wishListBean.setSelected(false);
                                    wishListBean.setRegionId(dataObject.optString("warehouse_id"));
                                    wishListBean.setRegionName(dataObject.optString("warehouse_name"));
                                    wishListBean.setEnable(wishListBean.getRegionId().equalsIgnoreCase(OnlineMartApplication.mLocalStore.getSelectedRegionId()));

                                    JSONArray imagesArr = dataObject.getJSONArray("images");
                                    String images[] = new String[imagesArr.length()];
                                    for (int j = 0; j < images.length; j++) {
                                        images[j] = imagesArr.getString(j);
                                    }
                                    wishListBean.setImages(images);
                                    listNames.add(wishListBean);
                                }
                                final WishListDialog wishListDialog = new WishListDialog(ProductMoreDetailsActivity.this, R.style.AddressDialog, listNames, product.getId());
                                wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                wishListDialog.show();
                            }else{
                                final WishListDialog wishListDialog = new WishListDialog(ProductMoreDetailsActivity.this, R.style.AddressDialog, listNames, product_id);
                                wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                wishListDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        final WishListDialog wishListDialog = new WishListDialog(ProductMoreDetailsActivity.this, R.style.AddressDialog, listNames, product_id);
                        wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                        wishListDialog.show();
                    }
                } else {
                    AppUtil.requestLogin(ProductMoreDetailsActivity.this);
                }

            }
        });

        txt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product != null && product.getSelectedQuantity() < product.getMaxQuantity()) {

                    txt_add_to_cart.setVisibility(View.GONE);
                    product.setAddedToCart(true);
                    loader_quantity.setVisibility(View.VISIBLE);

                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);
                    lyt_quantity.setVisibility(View.VISIBLE);
                    txt_selected_quantity.setVisibility(View.VISIBLE);
                    txt_selected_quantity.setText(product.getSelectedQuantity() + "");

                    txt_add_to_cart.setVisibility(View.GONE);
                    product.setQuantityUpdated(true);
                    loader_quantity.setVisibility(View.VISIBLE);

                    loadProductInfo(product.getId(), -1, "add", "3", null);

                    loader_quantity.showContent();
                    if (cart_progress_handler != null) {
                        lyt_add_to_cart.setVisibility(View.GONE);
                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                    }

                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {
                        OnlineMartApplication.increaseORDecreaseQtyOnCart(true, product.getId());
                        updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), true);
                    } else {

                    }

                    txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);

                } else {

                    if (product != null) {
                        product.setQuantityUpdated(false);
                    }
                    AppUtil.showErrorDialog(ProductMoreDetailsActivity.this, String.format(getString(R.string.max_quantity_reached_msg), product.getMaxQuantity() + AppConstants.BLANK_STRING));
                }
            }
        });

        txt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product != null) {

                    txt_add_to_cart.setVisibility(View.GONE);
                    loader_quantity.setVisibility(View.VISIBLE);
                    loadProductInfo(product.getId(), -1, "remove", "3", null);

                    loader_quantity.showContent();
                    if (cart_progress_handler != null) {
                        lyt_add_to_cart.setVisibility(View.GONE);
                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                    }

                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {

                        if (product.getSelectedQuantity() <= 1) {
                            product.setAddedToCart(false);
                            lyt_add_to_cart.setVisibility(View.GONE);
                        }

                        if (product.getSelectedQuantity() != 0) {
                            product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                            if (cart_progress_handler != null) {
                                lyt_add_to_cart.setVisibility(View.GONE);
                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                            }

                            if (product.getSelectedQuantity() == 0) {
                                txt_add_to_cart.setVisibility(View.VISIBLE);
                                loader_quantity.setVisibility(View.GONE);
                                lyt_quantity.setVisibility(View.GONE);
                                product.setAddedToCart(false);
                                product.setSelectedQuantity(0);
                            }

                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, product.getId());
                            updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), false);
                            txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                        }
                    } else {

                    }

                    txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                }
            }
        });

        cart_progress_runnable = new Runnable() {
            @Override
            public void run() {
                lyt_add_to_cart.setVisibility(View.GONE);
            }
        };

        lyt_same_brands.post(new Runnable() {
            @Override
            public void run() {
                suggested_width = (lyt_same_brands.getWidth() / 3) - 20;
            }
        });
        productRating = findViewById(R.id.product_rating);
        commentLayout = findViewById(R.id.commentLayout);
        productTextReviewCount = findViewById(R.id.product_text_review_count);
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
                                        AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(Err-676)", getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(mContext, getString(R.string.title), getString(R.string.error_msg) + "(Err-687)", getString(R.string.ok));
                                }
                            } else {
                                AppUtil.displaySingleActionAlert(mContext, getString(R.string.app_name), getString(R.string.error_msg) + "(Err-688)", getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(mContext, getString(R.string.app_name), getString(R.string.error_msg) + "(Err-689)", getString(R.string.ok));
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

    @Override
    protected void onPause() {
        super.onPause();
        if (cart_progress_handler != null) {
            lyt_add_to_cart.setVisibility(View.GONE);
            cart_progress_handler.removeCallbacks(cart_progress_runnable);
        }
    }

    private void loadSameBrandsProduct() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", product_id);
            request_data.put("brand_id", brand_id);
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    Call<ResponseBody> call = apiService.loadSameBrandProducts((new ConvertJsonToMap().jsonToMap(request_data)));
                    loader_same_brands.showProgress();
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loader_same_brands.showContent();
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
                                                if (array_products != null && array_products.length() > 0) {

                                                    list_same_brands_products.clear();
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

                                                            product.setAddedToWishList(object.optBoolean("in_wishlist", false));

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

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("offered_price"))) {
                                                                product.setOfferedPrice(object.optString("offered_price"));
                                                            }

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
                                                                if (object.optInt("qty") < 0) {
                                                                    product.setMaxQuantity(0);
                                                                } else {
                                                                    product.setMaxQuantity(object.optInt("qty"));
                                                                }
                                                            } else {
                                                                product.setMaxQuantity(0);
                                                            }

                                                            if (product.getMaxQuantity() <= 0) {
                                                                product.setSoldOut(true);
                                                            } else {
                                                                product.setSoldOut(false);
                                                            }

                                                            list_same_brands_products.add(product);
                                                        }
                                                    }

                                                    if (adapter_same_brand == null) {
                                                        adapter_same_brand = new ProductListAdapter1(mContext, list_same_brands_products, suggested_width);
                                                        recyl_brand_products.setAdapter(adapter_same_brand);
                                                    } else {
                                                        adapter_same_brand.notifyDataSetChanged();
                                                    }

                                                    adapter_same_brand.setOnProductClickLIstener(new ProductListAdapter1.OnProductClickLIstener() {
                                                        @Override
                                                        public void onProductClick(int position) {

                                                            Intent productDetailsIntent = new Intent(mContext, ProductMoreDetailsActivity.class);
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_ID, list_same_brands_products.get(position).getId());
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_NAME, list_same_brands_products.get(position).getLabel());
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_BRAND_ID, list_same_brands_products.get(position).getBrandId());
                                                            startActivity(productDetailsIntent);

                                                        }

                                                        @Override
                                                        public void onAddToCart(int position, ProductListAdapter1.ProductViewHolder productViewHolder) {

                                                            if (list_same_brands_products != null && list_same_brands_products.size() > position) {

                                                                if (!list_same_brands_products.get(position).isSoldOut()) {
                                                                    list_same_brands_products.get(position).setLoadingInfo(true);
                                                                }

                                                                loadProductInfo(list_same_brands_products.get(position).getId(), position, "addToCart", "1", productViewHolder);
                                                                Product product = list_same_brands_products.get(position);

                                                                if (product.getMaxQuantity() >= 1) {
                                                                    list_same_brands_products.get(position).setSelectedQuantity(1);
                                                                    list_same_brands_products.get(position).setAddedToCart(true);
                                                                    OnlineMartApplication.addToCart(AppUtil.getCartObject(list_same_brands_products.get(position)));

                                                                    if (!list_same_brands_products.get(position).isSoldOut()) {
                                                                        adapter_same_brand.updateUI(productViewHolder, position);

                                                                        updateCartProgress(
                                                                                list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                                true);
                                                                    }
                                                                } else {

                                                                }

                                                                if (adapter_similar_products != null) {
                                                                    adapter_similar_products.notifyDataSetChanged();
                                                                }
                                                            }

                                                            invalidateOptionsMenu();
                                                        }

                                                        @Override
                                                        public void onIncreaseQuantity(int position, ProductListAdapter1.ProductViewHolder productViewHolder) {
                                                            if (list_same_brands_products != null && list_same_brands_products.size() > position) {
                                                                loadProductInfo(list_same_brands_products.get(position).getId(), position, "add", "1", productViewHolder);

                                                                Product product = list_same_brands_products.get(position);
                                                                product.setSelectedQuantity(list_same_brands_products.get(position).getSelectedQuantity());
                                                                list_same_brands_products.set(position, product);

                                                                if (product.getSelectedQuantity() < product.getMaxQuantity()) {
                                                                    product.setQuantityUpdated(true);
                                                                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);

                                                                    if (cart_progress_handler != null) {
                                                                        lyt_add_to_cart.setVisibility(View.GONE);
                                                                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                    }
                                                                    OnlineMartApplication.increaseORDecreaseQtyOnCart(true, list_same_brands_products.get(position).getId());
                                                                    updateCartProgress(
                                                                            list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                            true);

                                                                } else {
                                                                    product.setQuantityUpdated(false);
                                                                }
                                                                list_same_brands_products.set(position, product);
                                                            }

                                                            if (adapter_same_brand != null) {
                                                                adapter_same_brand.updateUI(productViewHolder, position);
                                                            }

                                                            if (adapter_similar_products != null) {
                                                                adapter_similar_products.notifyDataSetChanged();
                                                            }

                                                            invalidateOptionsMenu();
                                                        }

                                                        @Override
                                                        public void onDecreaseQuantity(int position, ProductListAdapter1.ProductViewHolder productViewHolder) {
                                                            if (list_same_brands_products != null && list_same_brands_products.size() > position) {
                                                                loadProductInfo(list_same_brands_products.get(position).getId(), position, "remove", "1", productViewHolder);

                                                                Product product = list_same_brands_products.get(position);

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

                                                                        updateCartProgress(
                                                                                product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                                false);
                                                                    }

                                                                    if (product.getSelectedQuantity() != 0) {
                                                                        product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                                                                        if (cart_progress_handler != null) {
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                            cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                        }

                                                                        OnlineMartApplication.increaseORDecreaseQtyOnCart(false, list_same_brands_products.get(position).getId());
                                                                        updateCartProgress(
                                                                                list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                                false);
                                                                    }
                                                                } else {

                                                                }

                                                                if (adapter_same_brand != null) {
                                                                    adapter_same_brand.updateUI(productViewHolder, position);
                                                                }

                                                                if (adapter_similar_products != null) {
                                                                    adapter_similar_products.notifyDataSetChanged();
                                                                }
                                                            }

                                                            invalidateOptionsMenu();
                                                        }

                                                        @Override
                                                        public void onNotifyMe(int position) {
                                                            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                                                                notifyme(list_same_brands_products.get(position).getId());
                                                            } else {
                                                                AppUtil.requestLogin(mContext);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    lyt_same_brands.setVisibility(View.GONE);
                                                }

                                            } else {
                                                lyt_same_brands.setVisibility(View.GONE);
                                            }

                                        } else {
                                            lyt_same_brands.setVisibility(View.GONE);
                                        }

                                    } else {
                                        lyt_same_brands.setVisibility(View.GONE);
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                    loader_same_brands.setVisibility(View.GONE);
                                    lyt_same_brands.setVisibility(View.GONE);
                                }
                            } else {
                                loader_same_brands.setVisibility(View.GONE);
                                lyt_same_brands.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loader_same_brands.showContent();
                            lyt_same_brands.setVisibility(View.GONE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    lyt_same_brands.setVisibility(View.GONE);
                    loader_same_brands.showContent();
                }

            } else {
                lyt_same_brands.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            lyt_same_brands.setVisibility(View.GONE);
        }
    }

    private void loadSimilarProducts() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", product_id);
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    Call<ResponseBody> call = apiService.loadSsimilarProducts((new ConvertJsonToMap().jsonToMap(request_data)));
                    loader_similar_producs.showProgress();
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loader_similar_producs.showContent();
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
                                                if (array_products != null && array_products.length() > 0) {

                                                    list_similer_products.clear();
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

                                                            product.setAddedToWishList(object.optBoolean("in_wishlist", false));

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("image"))) {
                                                                product.setImage(object.optString("image"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("currency"))) {
                                                                product.setCurrency(object.optString("currency"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("product_acutal_price"))) {
                                                                product.setActualPrice(object.optString("product_acutal_price"));
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

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("offered_price"))) {
                                                                product.setOfferedPrice(object.optString("offered_price"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optDouble("product_offer_percent", 0.0f))) {
                                                                //product.setOfferString(object.optString("product_offer_percent") + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                                product.setOfferString(Math.round(object.optDouble("product_offer_percent", 0.0f)) + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optBoolean("shipping_third_party", false))) {
                                                                product.setShippingThirdParty(object.optBoolean("shipping_third_party", false));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("shipping_hours"))) {
                                                                product.setShippingHours(object.optString("shipping_hours"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(object.optString("qty"))) {
                                                                if (object.optInt("qty") < 0) {
                                                                    product.setMaxQuantity(0);
                                                                } else {
                                                                    product.setMaxQuantity(object.optInt("qty"));
                                                                }
                                                            } else {
                                                                product.setMaxQuantity(0);
                                                            }

                                                            if (product.getMaxQuantity() <= 0) {
                                                                product.setSoldOut(true);
                                                            } else {
                                                                product.setSoldOut(false);
                                                            }

                                                            list_similer_products.add(product);
                                                        }
                                                    }

                                                    if (adapter_similar_products == null) {
                                                        adapter_similar_products = new ProductListAdapter1(mContext, list_similer_products, suggested_width);
                                                        recyl_similer_products.setAdapter(adapter_similar_products);
                                                    } else {
                                                        adapter_similar_products.notifyDataSetChanged();
                                                    }

                                                    adapter_similar_products.setOnProductClickLIstener(new ProductListAdapter1.OnProductClickLIstener() {
                                                        @Override
                                                        public void onProductClick(int position) {

                                                            Intent productDetailsIntent = new Intent(mContext, ProductMoreDetailsActivity.class);
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_ID, list_similer_products.get(position).getId());
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_PRODUCT_NAME, list_similer_products.get(position).getLabel());
                                                            productDetailsIntent.putExtra(ProductMoreDetailsActivity.KEY_BRAND_ID, list_similer_products.get(position).getBrandId());
                                                            startActivity(productDetailsIntent);

                                                        }

                                                        @Override
                                                        public void onAddToCart(int position, ProductListAdapter1.ProductViewHolder productViewHolder) {

                                                            if (list_similer_products != null && list_similer_products.size() > position) {

                                                                if (!list_similer_products.get(position).isSoldOut()) {
                                                                    list_similer_products.get(position).setLoadingInfo(true);
                                                                }

                                                                loadProductInfo(list_similer_products.get(position).getId(), position, "addToCart", "2", productViewHolder);
                                                                Product product = list_similer_products.get(position);

                                                                if (product.getMaxQuantity() >= 1) {
                                                                    list_similer_products.get(position).setAddedToCart(true);
                                                                    list_similer_products.get(position).setSelectedQuantity(1);
                                                                    OnlineMartApplication.addToCart(AppUtil.getCartObject(list_similer_products.get(position)));

                                                                    if (!list_similer_products.get(position).isSoldOut()) {
                                                                        adapter_similar_products.updateUI(productViewHolder, position);

                                                                        updateCartProgress(
                                                                                list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                true);
                                                                    }
                                                                } else {

                                                                }

                                                                if (adapter_same_brand != null) {
                                                                    adapter_same_brand.notifyDataSetChanged();
                                                                }
                                                            }

                                                            invalidateOptionsMenu();
                                                        }

                                                        @Override
                                                        public void onIncreaseQuantity(int position, ProductListAdapter1.ProductViewHolder productViewHolder) {
                                                            if (list_similer_products != null && list_similer_products.size() > position) {
                                                                loadProductInfo(list_similer_products.get(position).getId(), position, "add", "2", productViewHolder);

                                                                Product product = list_similer_products.get(position);
                                                                list_similer_products.set(position, product);
                                                                product.setSelectedQuantity(list_similer_products.get(position).getSelectedQuantity());

                                                                if (product.getSelectedQuantity() < product.getMaxQuantity()) {
                                                                    product.setQuantityUpdated(true);
                                                                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);

                                                                    if (cart_progress_handler != null) {
                                                                        lyt_add_to_cart.setVisibility(View.GONE);
                                                                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                    }
                                                                    OnlineMartApplication.increaseORDecreaseQtyOnCart(true, list_similer_products.get(position).getId());
                                                                    updateCartProgress(
                                                                            list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                            true);

                                                                } else {

                                                                    product.setQuantityUpdated(false);

                                                                }
                                                                list_similer_products.set(position, product);
                                                            }
                                                            if (adapter_similar_products != null) {
                                                                adapter_similar_products.updateUI(productViewHolder, position);
                                                            }
                                                            if (adapter_same_brand != null) {
                                                                adapter_same_brand.notifyDataSetChanged();
                                                            }
                                                            invalidateOptionsMenu();
                                                        }

                                                        @Override
                                                        public void onDecreaseQuantity(int position, ProductListAdapter1.ProductViewHolder productViewHolder) {
                                                            if (list_similer_products != null && list_similer_products.size() > position) {
                                                                loadProductInfo(list_similer_products.get(position).getId(), position, "remove", "2", productViewHolder);

                                                                Product product = list_similer_products.get(position);
                                                                list_similer_products.set(position, product);
                                                                product.setSelectedQuantity(list_similer_products.get(position).getSelectedQuantity());

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

                                                                        updateCartProgress(
                                                                                product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                                false);
                                                                    }

                                                                    if (product.getSelectedQuantity() != 0) {
                                                                        product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                                                                        if (cart_progress_handler != null) {
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                            cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                        }

                                                                        OnlineMartApplication.increaseORDecreaseQtyOnCart(false, list_similer_products.get(position).getId());
                                                                        updateCartProgress(
                                                                                list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                false);
                                                                    }
                                                                } else {

                                                                }

                                                                if (adapter_similar_products != null) {
                                                                    adapter_similar_products.updateUI(productViewHolder, position);
                                                                }

                                                                if (adapter_same_brand != null) {
                                                                    adapter_same_brand.notifyDataSetChanged();
                                                                }
                                                            }

                                                            invalidateOptionsMenu();
                                                        }

                                                        @Override
                                                        public void onNotifyMe(int position) {
                                                            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                                                                notifyme(list_similer_products.get(position).getId());
                                                            } else {
                                                                AppUtil.requestLogin(mContext);
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                lyt_similar_products.setVisibility(View.GONE);
                                            }

                                        } else {
                                            lyt_similar_products.setVisibility(View.GONE);
                                        }

                                    } else {
                                        lyt_similar_products.setVisibility(View.GONE);
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                    lyt_similar_products.setVisibility(View.GONE);
                                }
                            } else {
                                lyt_similar_products.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            lyt_similar_products.setVisibility(View.GONE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    loader_same_brands.showContent();
                    lyt_similar_products.setVisibility(View.GONE);
                }

            } else {
                lyt_similar_products.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            lyt_similar_products.setVisibility(View.GONE);
        }
    }

    private void loadProductInfo(final String id, final int position, final String quantityMode, final String source, final ProductListAdapter1.ProductViewHolder productViewHolder) {

        /* Sources
        1   -   Same Brand List
        2   -   Similar Prodcuts List
        3   -   Product Detail
         */

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", id);
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {

                    if (map_loading_objects.size() > 0) {

                        for (Map.Entry<String, Call<ResponseBody>> entry : map_loading_objects.entrySet()) {

                            if (entry.getKey().equalsIgnoreCase(id)) {
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

                                                            Product product = null;
                                                            if (source.equalsIgnoreCase("3")) {
                                                                product = ProductMoreDetailsActivity.this.product;
                                                            } else {
                                                                product = new Product();
                                                            }

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

                                                            int maxQty = 0;
                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("qty"))) {
                                                                if (jsonObject.optInt("qty") < 0) {
                                                                    maxQty = 0;
                                                                    product.setMaxQuantity(0);
                                                                } else {
                                                                    maxQty = jsonObject.optInt("qty");
                                                                    product.setMaxQuantity(jsonObject.optInt("qty"));
                                                                }
                                                            } else {
                                                                maxQty = 0;
                                                                product.setMaxQuantity(0);
                                                            }

                                                            if (source.equalsIgnoreCase("3")) {
                                                                if (maxQty == product.getMaxQuantity()) {
                                                                    product.setQuantityUpdated(product.isQuantityUpdated());
                                                                } else
                                                                    product.setQuantityUpdated(false);
                                                            }

                                                            product.setMaxQuantity(maxQty);

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


                                                            if (quantityMode.equalsIgnoreCase("addToCart")) {

                                                                if (source.equalsIgnoreCase("1")) {
                                                                    list_same_brands_products.set(position, product);
                                                                    product = list_same_brands_products.get(position);

                                                                    if (product.getMaxQuantity() >= 1) {
                                                                        list_same_brands_products.get(position).setAddedToCart(true);
                                                                        list_same_brands_products.get(position).setSelectedQuantity(1);

                                                                        if (!list_same_brands_products.get(position).isSoldOut()) {

                                                                        }
                                                                    } else {

                                                                        list_same_brands_products.get(position).setSoldOut(true);
                                                                        list_same_brands_products.get(position).setSelectedQuantity(0);

                                                                        updateCartProgress(
                                                                                list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                                list_same_brands_products.get(position).getSelectedQuantity() - list_same_brands_products.get(position).getMaxQuantity());
                                                                        list_similer_products.get(position).setSelectedQuantity(product.getMaxQuantity());
                                                                        OnlineMartApplication.removeProductFromCart(list_similer_products.get(position).getId());
                                                                        //Toast.makeText(mContext, String.format(getString(R.string.notify_out_of_stock), product.getLabel()), Toast.LENGTH_SHORT).show();

                                                                        String tag = list_same_brands_products.get(position).getId() + "_" + String.format(getString(R.string.notify_out_of_stock), list_same_brands_products.get(position).getLabel());
                                                                        if (!list_alerts.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.notify_out_of_stock), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
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

                                                                        if (adapter_same_brand != null) {
                                                                            adapter_same_brand.updateUI(productViewHolder, position);
                                                                        }

                                                                    }
                                                                } else if (source.equalsIgnoreCase("2")) {
                                                                    list_similer_products.set(position, product);
                                                                    product = list_similer_products.get(position);

                                                                    if (product.getMaxQuantity() >= 1) {
                                                                        list_similer_products.get(position).setAddedToCart(true);
                                                                        list_similer_products.get(position).setSelectedQuantity(1);

                                                                        if (!list_similer_products.get(position).isSoldOut()) {

                                                                        }
                                                                    } else {

                                                                        list_similer_products.get(position).setSoldOut(true);
                                                                        list_similer_products.get(position).setSelectedQuantity(0);

                                                                        updateCartProgress(
                                                                                list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                list_similer_products.get(position).getSelectedQuantity() - list_similer_products.get(position).getMaxQuantity());
                                                                        list_similer_products.get(position).setSelectedQuantity(product.getMaxQuantity());
                                                                        OnlineMartApplication.removeProductFromCart(list_similer_products.get(position).getId());

                                                                        String tag = list_similer_products.get(position).getId() + "_" + String.format(getString(R.string.notify_out_of_stock), list_similer_products.get(position).getLabel());
                                                                        if (!list_alerts.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.notify_out_of_stock), list_similer_products.get(position).getLabel()), getString(R.string.ok));
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

                                                                        if (adapter_similar_products != null) {
                                                                            adapter_similar_products.updateUI(productViewHolder, position);
                                                                        }

                                                                    }
                                                                } else if (source.equalsIgnoreCase("3")) {

                                                                    loader_quantity.showContent();
                                                                    if (product.getMaxQuantity() >= 1) {

                                                                    } else {

                                                                        product.setSoldOut(true);
                                                                        product.setSelectedQuantity(0);

                                                                        updateCartProgress(
                                                                                product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                                product.getSelectedQuantity() - product.getMaxQuantity());
                                                                        product.setSelectedQuantity(product.getMaxQuantity());
                                                                        OnlineMartApplication.removeProductFromCart(product.getId());

                                                                        String tag = product.getId() + "_" + String.format(getString(R.string.notify_out_of_stock), product.getLabel());
                                                                        if (!list_alerts.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.notify_out_of_stock), product.getLabel()), getString(R.string.ok));
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

                                                                        updateData();
                                                                    }
                                                                }

                                                                invalidateOptionsMenu();


                                                            } else if (quantityMode.equalsIgnoreCase("add")) {

                                                                if (source.equalsIgnoreCase("1")) {

                                                                    if (product.getMaxQuantity() == list_same_brands_products.get(position).getMaxQuantity()) {
                                                                        product.setQuantityUpdated(list_same_brands_products.get(position).isQuantityUpdated());
                                                                    } else {
                                                                        product.setQuantityUpdated(false);
                                                                    }

                                                                    product.setSelectedQuantity(list_same_brands_products.get(position).getSelectedQuantity());
                                                                    list_same_brands_products.set(position, product);
                                                                    product = list_same_brands_products.get(position);

                                                                    if (product.getSelectedQuantity() < product.getMaxQuantity()) {

                                                                    } else {

                                                                        if (!list_same_brands_products.get(position).isQuantityUpdated()) {
                                                                            updateCartProgress(
                                                                                    list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                                    list_same_brands_products.get(position).getSelectedQuantity() - list_same_brands_products.get(position).getMaxQuantity());
                                                                            product.setSelectedQuantity(list_same_brands_products.get(position).getMaxQuantity());
                                                                            OnlineMartApplication.updateQuantityOnCart(list_same_brands_products.get(position).getId(), list_same_brands_products.get(position).getSelectedQuantity());

                                                                            String tag = list_same_brands_products.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, list_same_brands_products.get(position).getLabel()});
                                                                            if (!list_alerts.contains(tag)) {
                                                                                AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, list_same_brands_products.get(position).getLabel()}), getString(R.string.ok));
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

                                                                            if (adapter_same_brand != null) {
                                                                                adapter_same_brand.updateUI(productViewHolder, position);
                                                                            }
                                                                        }
                                                                        //AppUtil.showErrorDialog(ProductListActivity.this, String.format(getString(R.string.max_quantity_reached_msg1), product.getMaxQuantity(), product.getLabel()));
                                                                    }

                                                                } else if (source.equalsIgnoreCase("2")) {

                                                                    if (product.getMaxQuantity() == list_similer_products.get(position).getMaxQuantity()) {
                                                                        product.setQuantityUpdated(list_similer_products.get(position).isQuantityUpdated());
                                                                    } else {
                                                                        product.setQuantityUpdated(false);
                                                                    }

                                                                    product.setSelectedQuantity(list_similer_products.get(position).getSelectedQuantity());
                                                                    list_similer_products.set(position, product);
                                                                    product = list_similer_products.get(position);

                                                                    if (product.getSelectedQuantity() < product.getMaxQuantity()) {
                                                                        /*product.setSelectedQuantity(product.getSelectedQuantity() + 1);

                                                                        if (cart_progress_handler != null) {
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                            cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                        }
                                                                        OnlineMartApplication.increaseORDecreaseQtyOnCart(true, list_similer_products.get(position).getId());
                                                                        updateCartProgress(
                                                                                list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                true);

                                                                        if(adapter_similar_products != null){
                                                                            adapter_similar_products.notifyItemChanged(position);
                                                                        }*/

                                                                    } else {

                                                                        if (!list_same_brands_products.get(position).isQuantityUpdated()) {
                                                                            updateCartProgress(
                                                                                    list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                    list_similer_products.get(position).getSelectedQuantity() - list_similer_products.get(position).getMaxQuantity());
                                                                            product.setSelectedQuantity(list_similer_products.get(position).getMaxQuantity());
                                                                            OnlineMartApplication.updateQuantityOnCart(list_similer_products.get(position).getId(), list_similer_products.get(position).getSelectedQuantity());
                                                                            //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity()+ AppConstants.BLANK_STRING, product.getLabel()}), Toast.LENGTH_SHORT).show();

                                                                            String tag = list_similer_products.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()});
                                                                            if (!list_alerts.contains(tag)) {
                                                                                AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
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
                                                                                //AppUtil.showErrorDialog(ProductListActivity.this, String.format(getString(R.string.max_quantity_reached_msg1), product.getMaxQuantity(), product.getLabel()));
                                                                            }

                                                                            if (adapter_similar_products != null) {
                                                                                adapter_similar_products.updateUI(productViewHolder, position);
                                                                            }
                                                                        }
                                                                    }

                                                                } else if (source.equalsIgnoreCase("3")) {

                                                                    loader_quantity.showContent();
                                                                    if (cart_progress_handler != null) {
                                                                        lyt_add_to_cart.setVisibility(View.GONE);
                                                                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                    }

                                                                    if (product.getSelectedQuantity() < product.getMaxQuantity()) {
                                                                        /*OnlineMartApplication.increaseORDecreaseQtyOnCart(true, product.getId());
                                                                        updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), true);*/
                                                                    } else {

                                                                        if (!product.isQuantityUpdated()) {
                                                                            updateCartProgress(
                                                                                    product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                                    product.getSelectedQuantity() - product.getMaxQuantity());
                                                                            product.setSelectedQuantity(product.getMaxQuantity());
                                                                            OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());
                                                                            //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity()+ AppConstants.BLANK_STRING, product.getLabel()}), Toast.LENGTH_SHORT).show();

                                                                            String tag = product.getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()});
                                                                            if (!list_alerts.contains(tag)) {
                                                                                AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
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

                                                                    updateData();

                                                                    txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);

                                                                }

                                                            } else if (quantityMode.equalsIgnoreCase("remove")) {

                                                                if (source.equalsIgnoreCase("1")) {

                                                                    product.setSelectedQuantity(list_same_brands_products.get(position).getSelectedQuantity());
                                                                    list_same_brands_products.set(position, product);
                                                                    product = list_same_brands_products.get(position);

                                                                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {
                                                                        /*if (product.getSelectedQuantity() <= 1) {
                                                                            product.setAddedToCart(false);
                                                                            product.setSelectedQuantity(0);
                                                                            OnlineMartApplication.removeProductFromCart(product.getId());
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                        }

                                                                        if (product.getSelectedQuantity() != 0) {
                                                                            product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                                                                            if (cart_progress_handler != null) {
                                                                                lyt_add_to_cart.setVisibility(View.GONE);
                                                                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                            }

                                                                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, list_same_brands_products.get(position).getId());
                                                                            updateCartProgress(
                                                                                    list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                                    false);
                                                                        }*/
                                                                    } else {
                                                                        updateCartProgress(
                                                                                list_same_brands_products.get(position).isOffer() ? list_same_brands_products.get(position).getOfferedPrice() : list_same_brands_products.get(position).getActualPrice(),
                                                                                list_same_brands_products.get(position).getSelectedQuantity() - list_same_brands_products.get(position).getMaxQuantity());
                                                                        product.setSelectedQuantity(list_same_brands_products.get(position).getMaxQuantity());
                                                                        OnlineMartApplication.updateQuantityOnCart(list_same_brands_products.get(position).getId(), list_same_brands_products.get(position).getSelectedQuantity());
                                                                        //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity()+ AppConstants.BLANK_STRING, product.getLabel()}), Toast.LENGTH_SHORT).show();

                                                                        String tag = list_same_brands_products.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_same_brands_products.get(position).getMaxQuantity() + AppConstants.BLANK_STRING, list_same_brands_products.get(position).getLabel()});
                                                                        if (!list_alerts.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_same_brands_products.get(position).getMaxQuantity() + AppConstants.BLANK_STRING, list_same_brands_products.get(position).getLabel()}), getString(R.string.ok));
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

                                                                    if (adapter_same_brand != null) {
                                                                        adapter_same_brand.updateUI(productViewHolder, position);
                                                                    }

                                                                } else if (source.equalsIgnoreCase("2")) {

                                                                    product.setSelectedQuantity(list_similer_products.get(position).getSelectedQuantity());
                                                                    list_similer_products.set(position, product);
                                                                    product = list_similer_products.get(position);

                                                                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {
                                                                        /*if (product.getSelectedQuantity() <= 1) {
                                                                            product.setAddedToCart(false);
                                                                            product.setSelectedQuantity(0);
                                                                            OnlineMartApplication.removeProductFromCart(product.getId());
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                        }

                                                                        if (product.getSelectedQuantity() != 0) {
                                                                            product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                                                                            if (cart_progress_handler != null) {
                                                                                lyt_add_to_cart.setVisibility(View.GONE);
                                                                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                            }

                                                                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, list_similer_products.get(position).getId());
                                                                            updateCartProgress(
                                                                                    list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                    false);
                                                                        }*/
                                                                    } else {
                                                                        updateCartProgress(
                                                                                list_similer_products.get(position).isOffer() ? list_similer_products.get(position).getOfferedPrice() : list_similer_products.get(position).getActualPrice(),
                                                                                list_similer_products.get(position).getSelectedQuantity() - list_similer_products.get(position).getMaxQuantity());
                                                                        product.setSelectedQuantity(list_similer_products.get(position).getMaxQuantity());
                                                                        OnlineMartApplication.updateQuantityOnCart(list_similer_products.get(position).getId(), list_similer_products.get(position).getSelectedQuantity());
                                                                        //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity()+ AppConstants.BLANK_STRING, product.getLabel()}), Toast.LENGTH_SHORT).show();

                                                                        String tag = list_similer_products.get(position).getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_similer_products.get(position).getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()});
                                                                        if (!list_alerts.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{list_similer_products.get(position).getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
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

                                                                        if (adapter_similar_products != null) {
                                                                            adapter_similar_products.updateUI(productViewHolder, position);
                                                                        }
                                                                    }

                                                                    if (adapter_similar_products != null) {
                                                                        adapter_similar_products.updateUI(productViewHolder, position);
                                                                    }

                                                                } else if (source.equalsIgnoreCase("3")) {

                                                                    loader_quantity.showContent();
                                                                    if (cart_progress_handler != null) {
                                                                        lyt_add_to_cart.setVisibility(View.GONE);
                                                                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                    }

                                                                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {

                                                                        /*if (product.getSelectedQuantity() <= 1) {
                                                                            product.setAddedToCart(false);
                                                                            //product.setSelectedQuantity(0);
                                                                            lyt_add_to_cart.setVisibility(View.GONE);
                                                                        }

                                                                        if (product.getSelectedQuantity() != 0) {
                                                                            product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                                                                            if (cart_progress_handler != null) {
                                                                                lyt_add_to_cart.setVisibility(View.GONE);
                                                                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                            }

                                                                            if (product.getSelectedQuantity() == 0) {
                                                                                txt_add_to_cart.setVisibility(View.VISIBLE);
                                                                                loader_quantity.setVisibility(View.GONE);
                                                                                lyt_quantity.setVisibility(View.GONE);
                                                                                product.setAddedToCart(false);
                                                                                product.setSelectedQuantity(0);
                                                                            }

                                                                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, product.getId());
                                                                            updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), false);
                                                                            txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                                                                        }*/
                                                                    } else {
                                                                        updateCartProgress(
                                                                                product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                                product.getSelectedQuantity() - product.getMaxQuantity());
                                                                        product.setSelectedQuantity(product.getMaxQuantity());
                                                                        OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());
                                                                        //Toast.makeText(mContext, String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity()+ AppConstants.BLANK_STRING, product.getLabel()}), Toast.LENGTH_SHORT).show();

                                                                        String tag = product.getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()});
                                                                        if (!list_alerts.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
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

                                                                        updateData();
                                                                    }

                                                                    txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);

                                                                }

                                                                invalidateOptionsMenu();
                                                            }

                                                        }
                                                    }

                                                }

                                            } else {
                                                //AppUtil.showErrorDialog(mContext, errorMsg);

                                                if (source.equalsIgnoreCase("1")) {

                                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                                                    appDialogSingleAction.show();
                                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                        @Override
                                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                            appDialogSingleAction.dismiss();
                                                        }
                                                    });*/
                                                } else if (source.equalsIgnoreCase("2")) {

                                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                                                    appDialogSingleAction.show();
                                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                        @Override
                                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                            appDialogSingleAction.dismiss();
                                                        }
                                                    });*/

                                                } else if (source.equalsIgnoreCase("3")) {

                                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                                                    appDialogSingleAction.show();
                                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                        @Override
                                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                            appDialogSingleAction.dismiss();
                                                        }
                                                    });*/

                                                }
                                            }

                                        } else if(statusCode == 305){
                                            OnlineMartApplication.openRegionPicker(ProductMoreDetailsActivity.this, errorMsg);

                                        } else {
                                            //AppUtil.showErrorDialog(mContext, errorMsg);

                                            if (source.equalsIgnoreCase("1")) {

                                                /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                                                appDialogSingleAction.show();
                                                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                    @Override
                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                        appDialogSingleAction.dismiss();
                                                    }
                                                });*/
                                            } else if (source.equalsIgnoreCase("2")) {

                                                /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                                                appDialogSingleAction.show();
                                                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                    @Override
                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                        appDialogSingleAction.dismiss();
                                                    }
                                                });*/

                                            } else if (source.equalsIgnoreCase("3")) {

                                                /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                                                appDialogSingleAction.show();
                                                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                    @Override
                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                        appDialogSingleAction.dismiss();
                                                    }
                                                });*/

                                            }
                                        }

                                    } else {
                                        //AppUtil.showErrorDialog(mContext, getString(R.string.some_error_occoured));

                                        if (source.equalsIgnoreCase("1")) {

                                            /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                                            appDialogSingleAction.show();
                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                }
                                            });*/
                                        } else if (source.equalsIgnoreCase("2")) {

                                            /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                                            appDialogSingleAction.show();
                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                }
                                            });*/

                                        } else if (source.equalsIgnoreCase("3")) {

                                            /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                                            appDialogSingleAction.show();
                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                }
                                            });*/

                                        }
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    //AppUtil.showErrorDialog(mContext, response.message());

                                    if (source.equalsIgnoreCase("1")) {

                                        /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                                        appDialogSingleAction.show();
                                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();
                                            }
                                        });*/
                                    } else if (source.equalsIgnoreCase("2")) {

                                        /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                                        appDialogSingleAction.show();
                                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();
                                            }
                                        });*/

                                    } else if (source.equalsIgnoreCase("3")) {

                                        /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                                        appDialogSingleAction.show();
                                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();
                                            }
                                        });*/

                                    }
                                }
                            } else {
                                //AppUtil.showErrorDialog(mContext, response.message());

                                if (source.equalsIgnoreCase("1")) {

                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                        }
                                    });*/
                                } else if (source.equalsIgnoreCase("2")) {

                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                        }
                                    });*/

                                } else if (source.equalsIgnoreCase("3")) {

                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                        }
                                    });*/

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();

                            if (!call.isCanceled()) {
                                if (source.equalsIgnoreCase("1")) {

                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                        }
                                    });*/
                                } else if (source.equalsIgnoreCase("2")) {

                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                        }
                                    });*/

                                } else if (source.equalsIgnoreCase("3")) {

                                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                                    appDialogSingleAction.show();
                                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                        }
                                    });*/

                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();

                    if (source.equalsIgnoreCase("1")) {

                        /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                        appDialogSingleAction.show();
                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                            @Override
                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                appDialogSingleAction.dismiss();
                            }
                        });*/
                    } else if (source.equalsIgnoreCase("2")) {

                        /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                        appDialogSingleAction.show();
                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                            @Override
                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                appDialogSingleAction.dismiss();
                            }
                        });*/

                    } else if (source.equalsIgnoreCase("3")) {

                        /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                        appDialogSingleAction.show();
                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                            @Override
                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                appDialogSingleAction.dismiss();
                            }
                        });*/

                    }
                }

            } else {

                if (source.equalsIgnoreCase("1")) {

                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                    appDialogSingleAction.show();
                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                        @Override
                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                            appDialogSingleAction.dismiss();
                        }
                    });*/
                } else if (source.equalsIgnoreCase("2")) {

                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                    appDialogSingleAction.show();
                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                        @Override
                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                            appDialogSingleAction.dismiss();
                        }
                    });*/

                } else if (source.equalsIgnoreCase("3")) {

                    /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                    appDialogSingleAction.show();
                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                        @Override
                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                            appDialogSingleAction.dismiss();
                        }
                    });*/

                }

                //Snackbar.make(term_condtn_checkBox, getResources().getString(R.string.error_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.try_again), networkCallBack1).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();

            if (source.equalsIgnoreCase("1")) {

                /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_same_brands_products.get(position).getLabel()), getString(R.string.ok));
                appDialogSingleAction.show();
                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        appDialogSingleAction.dismiss();
                    }
                });*/
            } else if (source.equalsIgnoreCase("2")) {

                /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), list_similer_products.get(position).getLabel()), getString(R.string.ok));
                appDialogSingleAction.show();
                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        appDialogSingleAction.dismiss();
                    }
                });*/

            } else if (source.equalsIgnoreCase("3")) {

                /*AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.error_update_quantity), product.getLabel()), getString(R.string.ok));
                appDialogSingleAction.show();
                appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                    @Override
                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                        appDialogSingleAction.dismiss();
                    }
                });*/

            }

        }
    }

    private void loadProduct() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", product_id);
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {
                    Call<ResponseBody> call = apiService.loadProduct(new ConvertJsonToMap().jsonToMap(request_data));
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loader_root.showContent();

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

                                                JSONObject jsonObject = object.optJSONObject("Product");
                                                if (jsonObject != null) {
                                                    product = new Product();

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_id"))) {
                                                        product.setId(jsonObject.optString("product_id"));
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("english_product_name"))) {
                                                        product.setEnglishName(jsonObject.optString("english_product_name"));
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("arabic_product_name"))) {
                                                        product.setArabicName(jsonObject.optString("arabic_product_name"));
                                                    }

                                                    if(txt_title.getText().toString().equalsIgnoreCase(AppConstants.BLANK_STRING)){
                                                        setPageTitle(product.getLabel());
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

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optInt("product_average_rating"))) {
                                                        product.setAvgRating(jsonObject.optInt("product_average_rating", 0));
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optInt("product_rating_count"))) {
                                                        product.setRatingCount(jsonObject.optInt("product_rating_count", 0));
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
                                                        if (jsonObject.optInt("qty") < 0) {
                                                            product.setMaxQuantity(0);
                                                        } else {
                                                            product.setMaxQuantity(jsonObject.optInt("qty"));
                                                        }
                                                    } else {
                                                        product.setMaxQuantity(0);
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("currency"))) {
                                                        product.setCurrency(jsonObject.optString("currency"));
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optDouble("product_offer_percent", 0.0f))) {
                                                        product.setOfferString(Math.round(jsonObject.optDouble("product_offer_percent", 0.0f)) + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                    }

                                                    product.setShippingThirdParty(jsonObject.optBoolean("shipping_third_party", false));

                                                    if (product.isShippingThirdParty()) {
                                                        if (!ValidationUtil.isNullOrBlank(jsonObject.optString("shipping_hours"))) {
                                                            product.setShippingHours(jsonObject.optString("shipping_hours"));
                                                        }
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_description"))) {

                                                        String noHTMLString = jsonObject.optString("product_description").replaceAll("\\<.*?>", "");
                                                        product.setProducDescription(noHTMLString);
                                                        product.setProducDescription(jsonObject.optString("product_description"));
                                                    }

                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("currency"))) {
                                                        product.setCurrency(jsonObject.optString("currency"));
                                                    }

                                                    JSONArray array_images = jsonObject.optJSONArray("File");
                                                    if (array_images != null && array_images.length() > 0) {
                                                        ArrayList<String> list_images = new ArrayList<>();
                                                        for (int i = 0; i < array_images.length(); i++) {
                                                            JSONObject imageObj = array_images.getJSONObject(i);
                                                            if (imageObj != null) {
                                                                if (!ValidationUtil.isNullOrBlank(imageObj.optString("image"))) {

                                                                    if (imageObj.optString("is_defalut") != null && imageObj.optString("is_defalut").equalsIgnoreCase("1")) {
                                                                        product.setImage(imageObj.optString("image"));
                                                                        list_images.add(0, imageObj.optString("image"));
                                                                    } else {
                                                                        list_images.add(imageObj.optString("image"));
                                                                    }

                                                                }
                                                            }
                                                        }

                                                        product.setListImages(list_images);
                                                    }

                                                    JSONArray array_orignal_images = jsonObject.optJSONArray("FileBig");
                                                    if (array_orignal_images != null && array_orignal_images.length() > 0) {
                                                        ArrayList<String> list_orignal_images = new ArrayList<>();
                                                        for (int i = 0; i < array_orignal_images.length(); i++) {
                                                            JSONObject imageObj = array_orignal_images.getJSONObject(i);
                                                            if (imageObj != null) {
                                                                if (!ValidationUtil.isNullOrBlank(imageObj.optString("image"))) {

                                                                    if (imageObj.optString("is_defalut") != null && imageObj.optString("is_defalut").equalsIgnoreCase("1")) {
                                                                        //product.setImage(imageObj.optString("image"));
                                                                        list_orignal_images.add(0, imageObj.optString("image"));
                                                                    } else {
                                                                        list_orignal_images.add(imageObj.optString("image"));
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        product.setListOrignalImages(list_orignal_images);
                                                    }

                                                    if (product.getMaxQuantity() <= 0) {
                                                        product.setSoldOut(true);
                                                    } else {
                                                        product.setSoldOut(false);
                                                    }

                                                    if (product.isSoldOut()) {
                                                        txt_add_to_cart.setText(getString(R.string.notify_me).toUpperCase());
                                                    }

                                                    setData();
                                                }
                                            } else {
                                                AppUtil.showErrorDialog(mContext, errorMsg);
                                            }

                                        } else if(statusCode == 305){

                                            OnlineMartApplication.openRegionPicker(ProductMoreDetailsActivity.this, errorMsg);

                                        } else {
                                            AppUtil.showErrorDialog(mContext, errorMsg);
                                        }

                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-636)");
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-637)");
                                }

                            } else {
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(ERR-638)");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loader_root.setStatuText(getString(R.string.no_product_found));
                            loader_root.showStatusText();
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

    private void setData() {

        if (product != null) {

            if (product.getAvgRating() <= 0 || product.getRatingCount() <= 0) {
                productRating.setVisibility(View.GONE);
                productTextReviewCount.setVisibility(View.GONE);
            } else {
                productRating.setVisibility(View.VISIBLE);
                productTextReviewCount.setVisibility(View.VISIBLE);
                productRating.setRating(product.getAvgRating());
                productTextReviewCount.setText("(" + product.getRatingCount() + ")");
            }

            img_save_to_list.setImageDrawable(
                    (product.isAddedToWishList()) ? ContextCompat.getDrawable(this, R.drawable.icon_heart_filled) : ContextCompat.getDrawable(this, R.drawable.icon_heart)
            );

            if (!ValidationUtil.isNullOrBlank(product.getLabel())) {
                txt_product_name.setText(product.getLabel());
            } else {
                txt_product_name.setText(getString(R.string.na));
            }

            if (!ValidationUtil.isNullOrBlank(product.getBrandName())) {
                txt_brand_name.setText(product.getBrandName());
            } else {
                txt_brand_name.setText(getString(R.string.na));
            }

            if (!ValidationUtil.isNullOrBlank(product.getQuantity())) {
                product_txt_quantity.setText(product.getQuantity());
            } else {
                product_txt_quantity.setText(getString(R.string.na));
            }

            if (product.isOffer()) {
                txt_product_price.setVisibility(View.VISIBLE);
                txt_product_price.setText(product.getActualPrice()/* + AppConstants.SPACE + product.getCurrency()*/);
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    txt_product_final_price.setText(getString(R.string.egp) + AppConstants.SPACE + product.getOfferedPrice());
                }else{
                    txt_product_final_price.setText(product.getOfferedPrice() + AppConstants.SPACE + getString(R.string.egp));
                }
            } else {
                txt_product_price.setVisibility(View.GONE);
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    txt_product_final_price.setText(getString(R.string.egp) + AppConstants.SPACE + product.getActualPrice());
                }else{
                    txt_product_final_price.setText(product.getActualPrice() + AppConstants.SPACE + getString(R.string.egp));
                }
            }

            if (!ValidationUtil.isNullOrBlank(product.getProducDescription())) {

                if (DeviceUtil.getAndroidVersion() >= Build.VERSION_CODES.N) {
                    txt_prodcut_description.setText(Html.fromHtml(product.getProducDescription(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    txt_prodcut_description.setText(Html.fromHtml(product.getProducDescription()));
                }

                //txt_prodcut_description.setText(product.getProducDescription());
            } else {
                txt_prodcut_description.setText(getString(R.string.na));
            }

            if (product.getListImages() != null && product.getListImages().size() > 0) {

                pager_images.setAdapter(new PagerAdapter() {

                    @Override
                    public int getCount() {
                        return product.getListImages().size();
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        View view = LayoutInflater.from(ProductMoreDetailsActivity.this).inflate(R.layout.layout_product_more_details_pager_view, null, false);
                        final AspectRatioImageView imageView = view.findViewById(R.id.imageViewProduct);
                        final LoaderLayout loader_img = view.findViewById(R.id.loader_img);

                        if (!ValidationUtil.isNullOrBlank(product.getListImages().get(position))) {
                            final Drawable alternate_image = ContextCompat.getDrawable(ProductMoreDetailsActivity.this, R.mipmap.place_holder);
                            ImageLoader.getInstance().displayImage(product.getListImages().get(position), imageView, OnlineMartApplication.intitOptions2(), new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    loader_img.showProgress();
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    loader_img.showContent();
                                    imageView.setImageDrawable(alternate_image);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    loader_img.showContent();
                                    imageView.setImageBitmap(loadedImage);
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    loader_img.showContent();
                                    imageView.setImageDrawable(alternate_image);
                                }
                            });
                        } else {
                            ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, imageView, OnlineMartApplication.intitOptions2());
                        }

                        container.addView(view);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (product.getListorignalImages() != null && product.getListorignalImages().size() > 0) {
                                    startActivity(new Intent(ProductMoreDetailsActivity.this, FullImage_View.class).putStringArrayListExtra("image", product.getListorignalImages()));
                                }
                            }
                        });
                        return view;
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object o) {
                        return view == o;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        container.removeView((LinearLayout) object);
                    }
                });

                pager_indicator.setViewPager(pager_images);
            }

            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

            if (cartItem != null) {
                product.setAddedToCart(true);
                product.setSelectedQuantity(cartItem.getSelectedQuantity());
                txt_add_to_cart.setVisibility(View.GONE);
                loader_quantity.setVisibility(View.VISIBLE);
                loader_quantity.showContent();
                lyt_quantity.setVisibility(View.VISIBLE);
                txt_selected_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
            } else {
                txt_add_to_cart.setVisibility(View.VISIBLE);
                lyt_quantity.setVisibility(View.GONE);
                loader_quantity.setVisibility(View.GONE);
            }

            if (product.isShippingThirdParty()) {
                txt_shipped_in.setVisibility(View.VISIBLE);
                txt_shipped_in.setText(String.format(mContext.getString(R.string.shipping_in_n_hours), product.getShippingHours()));
            } else {
                txt_shipped_in.setVisibility(View.GONE);
            }

            if (product.getMaxQuantity() <= product.getMarkSoldQuantity()) {
                txt_item_remaining.setVisibility(View.VISIBLE);
                txt_item_remaining.setText(String.format(getString(R.string.remaining).toUpperCase(),  product.getMaxQuantity() + AppConstants.BLANK_STRING));
            } else {
                txt_item_remaining.setVisibility(View.GONE);
            }

            if (product.isSoldOut()) {
                txt_sold_out.setVisibility(View.VISIBLE);
                txt_shipped_in.setVisibility(View.GONE);
                txt_add_to_cart.setText(mContext.getString(R.string.notify_me).toUpperCase());
                txt_item_remaining.setVisibility(View.GONE);
            } else {
                txt_sold_out.setVisibility(View.GONE);
            }

            if (product.isOffer() && !ValidationUtil.isNullOrBlank(product.getOfferString())) {
                txt_offer.setVisibility(View.VISIBLE);
                txt_offer.setText(product.getOfferString());
            } else {
                txt_offer.setVisibility(View.GONE);
            }
        }

    }

    private void updateData() {

        if (product != null) {

            if (!ValidationUtil.isNullOrBlank(product.getQuantity())) {
                product_txt_quantity.setText(product.getQuantity());
            } else {
                product_txt_quantity.setText(getString(R.string.na));
            }

            if (product.isOffer()) {
                txt_product_price.setVisibility(View.VISIBLE);
                txt_product_price.setText(product.getActualPrice()/* + AppConstants.SPACE + product.getCurrency()*/);
                txt_product_final_price.setText(getString(R.string.egp) + AppConstants.SPACE + product.getOfferedPrice());
            } else {
                txt_product_price.setVisibility(View.GONE);
                txt_product_final_price.setText(getString(R.string.egp) + AppConstants.SPACE + getString(R.string.egp));
            }

            img_save_to_list.setImageDrawable(
                    (product.isAddedToWishList()) ? ContextCompat.getDrawable(this, R.drawable.icon_heart_filled) : ContextCompat.getDrawable(this, R.drawable.icon_heart)
            );

            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

            if (cartItem != null) {
                product.setAddedToCart(true);
                product.setSelectedQuantity(cartItem.getSelectedQuantity());
                txt_add_to_cart.setVisibility(View.GONE);
                lyt_quantity.setVisibility(View.VISIBLE);
                loader_quantity.setVisibility(View.VISIBLE);

                if (cartItem.getSelectedQuantity() > product.getMaxQuantity()) {
                    product.setSelectedQuantity(product.getMaxQuantity());
                    OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());
                    //Toast.makeText(mContext, String.format(getString(R.string.notify_out_of_stock), product.getLabel()), Toast.LENGTH_SHORT).show();

                    AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.notify_out_of_stock), product.getLabel()), getString(R.string.ok));
                    appDialogSingleAction.show();
                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                        @Override
                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                            appDialogSingleAction.dismiss();
                        }
                    });
                }

                txt_selected_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);


            } else {
                txt_add_to_cart.setVisibility(View.VISIBLE);
                lyt_quantity.setVisibility(View.GONE);
                loader_quantity.setVisibility(View.GONE);
            }

            if (product.isShippingThirdParty()) {
                txt_shipped_in.setVisibility(View.VISIBLE);
                txt_shipped_in.setText(String.format(mContext.getString(R.string.shipping_in_n_hours), product.getShippingHours()));
            } else {
                txt_shipped_in.setVisibility(View.GONE);
            }

            if (product.getMaxQuantity() <= product.getMarkSoldQuantity()) {
                txt_item_remaining.setVisibility(View.VISIBLE);
                txt_item_remaining.setText(String.format(getString(R.string.remaining).toUpperCase(),  product.getMaxQuantity() + AppConstants.BLANK_STRING));
            } else {
                txt_item_remaining.setVisibility(View.GONE);
            }

            if (product.isSoldOut()) {
                txt_sold_out.setVisibility(View.VISIBLE);
                txt_shipped_in.setVisibility(View.GONE);
                txt_add_to_cart.setText(mContext.getString(R.string.notify_me).toUpperCase());
                txt_item_remaining.setVisibility(View.GONE);
            } else {
                txt_sold_out.setVisibility(View.GONE);
            }

            if (product.isOffer() && !ValidationUtil.isNullOrBlank(product.getOfferString())) {
                txt_offer.setVisibility(View.VISIBLE);
                txt_offer.setText(product.getOfferString());
            } else {
                txt_offer.setVisibility(View.GONE);
            }
        }
    }

    private void updateCartProgress(final String priceString, final boolean isAddToCart) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                String updatedTotal = null;
                if (isAddToCart) {
                    txt_add_or_remove.setText(String.format(getString(R.string.product_added_to_cart), "1" + AppConstants.BLANK_STRING).toUpperCase());
                    updatedTotal = " "+ getString(R.string.egp) +" " + String.format(Locale.ENGLISH, "%d", Math.round(current_total + price));
                } else {
                    txt_add_or_remove.setText(getString(R.string.product_removed_from_cart));
                    updatedTotal = " "+ getString(R.string.egp) +" " + String.format(Locale.ENGLISH, "%d", Math.round(current_total - price));
                }

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

    private void updateCartProgress(final String priceString, final int quantity) {

        if (quantity != 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                    float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                    String updatedTotal = null;

                    txt_add_or_remove.setText(getString(R.string.product_removed_from_cart));
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        super.headerShouldBeInLeft(true);
        super.setSearchBarVisiblity(false);
        super.setToolbarTag(getString(R.string.back));
        super.setCurrentActivity(this);
        super.setPageTitle(product_name);

        recyl_brand_products.setFocusable(false);
        recyl_similer_products.setFocusable(false);

        loadProduct();
        loadSameBrandsProduct();
        loadSimilarProducts();

        notifyData();
    }

    public void notifyData() {

        if (OnlineMartApplication.getCartList().size() == 0) {
            if (product != null) {
                product.setSelectedQuantity(0);
                product.setAddedToCart(false);

                img_save_to_list.setImageDrawable(
                       product.isAddedToWishList()
                                ? ContextCompat.getDrawable(ProductMoreDetailsActivity.this, R.drawable.icon_heart_filled)
                                : ContextCompat.getDrawable(ProductMoreDetailsActivity.this, R.drawable.icon_heart));
            }

            if (list_same_brands_products != null && list_same_brands_products.size() > 0) {
                for (int i = 0; i < list_same_brands_products.size(); i++) {
                    list_same_brands_products.get(i).setSelectedQuantity(0);
                    list_same_brands_products.get(i).setAddedToCart(false);
                }
            }

            if (list_similer_products != null && list_similer_products.size() > 0) {
                for (int i = 0; i < list_similer_products.size(); i++) {
                    list_similer_products.get(i).setSelectedQuantity(0);
                    list_similer_products.get(i).setAddedToCart(false);
                }
            }
        }

        if (adapter_same_brand != null) {
            adapter_same_brand.notifyDataSetChanged();
        }

        if (adapter_similar_products != null) {
            adapter_similar_products.notifyDataSetChanged();
        }

        if (product != null) {
            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

            if (cartItem != null && product != null) {
                product.setAddedToCart(true);
                product.setSelectedQuantity(cartItem.getSelectedQuantity());
                txt_add_to_cart.setVisibility(View.GONE);
                lyt_quantity.setVisibility(View.VISIBLE);
                loader_quantity.setVisibility(View.VISIBLE);
                txt_selected_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
            } else {
                txt_add_to_cart.setVisibility(View.VISIBLE);
                lyt_quantity.setVisibility(View.GONE);
                loader_quantity.setVisibility(View.GONE);
            }

            invalidateOptionsMenu();
        }
    }

    public void notifyData(String product_id) {

        if (product != null) {
            product.setAddedToWishList(true);
            OnlineMartApplication.updateWishlistFlagOnCart(product_id, true);

            img_save_to_list.setImageDrawable(
                    product.isAddedToWishList()
                            ? ContextCompat.getDrawable(ProductMoreDetailsActivity.this, R.drawable.icon_heart_filled)
                            : ContextCompat.getDrawable(ProductMoreDetailsActivity.this, R.drawable.icon_heart));

            Intent intent = new Intent("WISHLIST_UPDATED");
            intent.putExtra("product_id", product_id);
            intent.putExtra("isAddedToWishList", true);
            getLocalBroadCastManager().sendBroadcast(intent);
        }

        if (list_same_brands_products != null && list_same_brands_products.size() > 0) {
            for (int i = 0; i < list_same_brands_products.size(); i++) {
                if(list_same_brands_products.get(i).getId().equalsIgnoreCase(product_id)){
                    list_same_brands_products.get(i).setAddedToWishList(true);
                }
            }
        }

        if (list_similer_products != null && list_similer_products.size() > 0) {
            for (int i = 0; i < list_similer_products.size(); i++) {
                if(list_similer_products.get(i).getId().equalsIgnoreCase(product_id)){
                    list_similer_products.get(i).setAddedToWishList(true);
                }
            }
        }

        if (adapter_same_brand != null) {
            adapter_same_brand.notifyDataSetChanged();
        }

        if (adapter_similar_products != null) {
            adapter_similar_products.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mBarCodeMenu = menu.findItem(R.id.bar_code_menu);
        mSerachMenu = menu.findItem(R.id.serach_menu);
        mBarCodeMenu.setVisible(true);
        mSerachMenu.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serach_menu:
                Intent searchIntent = new Intent(ProductMoreDetailsActivity.this, SearchingActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.bar_code_menu:
                //startActivity(new Intent(this, BarCodeScan_Activity.class));
                startActivity(new Intent(this, BarcodeCaptureActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void getProductRateReview() {

        try {
            commentLytLoader.showProgress();
            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("product_id", product_id);

            Call<ResponseBody> call = apiService.getProductRateReview((new ConvertJsonToMap().jsonToMap(request)));
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
                                    ArrayList<ProductRateReviewBean> productRateReviewBeanList = new ArrayList<>();
                                    //String responseMessage = resObj.optString("success_message");
                                    if (resObj.optJSONArray("data") != null) {
                                        JSONArray listSavingArray = resObj.optJSONArray("data");

                                        for (int i = 0; i < listSavingArray.length(); i++) {
                                            JSONObject listSavingObject = listSavingArray.optJSONObject(i);
                                            ProductRateReviewBean productRateReviewBean = new ProductRateReviewBean();
                                            productRateReviewBean.setReviewId(listSavingObject.optString("review_id"));
                                            productRateReviewBean.setComment(listSavingObject.optString("comment"));
                                            productRateReviewBean.setRating(listSavingObject.optString("rate"));
                                            productRateReviewBean.setUserName(listSavingObject.optString("user_name"));
                                            productRateReviewBean.setDateTime(listSavingObject.optString("date_time"));
                                            productRateReviewBeanList.add(productRateReviewBean);
                                        }
                                        ProductRateReviewAdapter mySavedListAdapter = new ProductRateReviewAdapter(mContext, productRateReviewBeanList);
                                        commentRecycler.setAdapter(mySavedListAdapter);
                                        commentLytLoader.showContent();
                                        //commentLayout.setVisibility(View.VISIBLE);
                                        commentLayout.setVisibility(View.GONE);
                                    }
                                } else {
                                    commentLytLoader.showStatusText();
                                    commentLayout.setVisibility(View.GONE);
                                }

                            } else {
                                commentLytLoader.showStatusText();
                                commentLayout.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            commentLytLoader.showStatusText();
                            commentLayout.setVisibility(View.GONE);
                        }
                    } else {
                        commentLytLoader.showStatusText();
                        commentLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    commentLytLoader.showStatusText();
                    commentLayout.setVisibility(View.GONE);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog progressDialog;

    private void submitRateAndReview() {

        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("product_id", product_id);
                request.put("rate", "" + rating);
                request.put("comment", AppConstants.BLANK_STRING);

                progressDialog = new ProgressDialog(ProductMoreDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mContext);
                Call<ResponseBody> call = apiService.submitProductReviewRating((new ConvertJsonToMap().jsonToMap(request)));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200) {
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                JSONObject resObj = obj.optJSONObject("response");
                                if (resObj.length() > 0) {

                                    int statusCode = resObj.optInt("status_code", 0);
                                    String errorMsg = resObj.optString("error_message");
                                    if (statusCode == 200) {

                                        JSONObject dataObject = resObj.optJSONObject("data");

                                        if (dataObject != null) {

                                            if (product != null) {
                                                if (!ValidationUtil.isNullOrBlank(dataObject.optInt("product_average_rating"))) {
                                                    product.setAvgRating(dataObject.optInt("product_average_rating", 0));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(dataObject.optInt("product_rating_count"))) {
                                                    product.setRatingCount(dataObject.optInt("product_rating_count", 0));
                                                }

                                                if (product.getAvgRating() <= 0 || product.getRatingCount() <= 0) {
                                                    productRating.setVisibility(View.GONE);
                                                    productTextReviewCount.setVisibility(View.GONE);
                                                } else {
                                                    productRating.setVisibility(View.VISIBLE);
                                                    productTextReviewCount.setVisibility(View.VISIBLE);
                                                    productRating.setRating(product.getAvgRating());
                                                    productTextReviewCount.setText("(" + product.getRatingCount() + ")");
                                                }
                                            }
                                        }

                                        String responseMessage = resObj.optString("success_message");
                                        AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, AppConstants.BLANK_STRING, responseMessage, getString(R.string.ok));
                                        appDialogSingleAction.show();
                                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                getProductRateReview();
                                                finalRating.setRating(0);
                                                //editTextComment.setText("");
                                                appDialogSingleAction.dismiss();
                                                // finish();
                                            }
                                        });
                                    } else {
                                        //loaderLayout.showStatusText();
                                        AppUtil.showErrorDialog(mContext, errorMsg);
                                    }

                                } else {
                                    //loaderLayout.showStatusText();
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-727)");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-728)");
                                //loaderLayout.showStatusText();
                            }
                        } else {
                            //loaderLayout.showStatusText();
                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-729)");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-730)");
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AppUtil.requestLogin(ProductMoreDetailsActivity.this);
        }
    }
}

