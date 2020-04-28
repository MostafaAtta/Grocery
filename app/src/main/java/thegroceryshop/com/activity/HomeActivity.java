package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.PromotionPagerAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.circleIndicator.CirclePageIndicator;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.modal.sildemenu_model.NestedCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.PromotionCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.SequenceNumberSorter;
import thegroceryshop.com.service.LoadInitialData;
import thegroceryshop.com.slideMenu.HomeCategoryViewAdapter;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/12/2017.
 */

public class HomeActivity extends BaseActivity {

    private FrameLayout lyt_parent;
    private LinearLayout lyt_root;
    private LoaderLayout lyt_loader;
    private ImageView img_welcome;
    private SliderLayout lyt_slider;
    private RecyclerView recyl_categories;
    private FrameLayout lyt_promotion_slider;
    private RippleButton btn_getStarted;
    private ViewPager pager_promotion;
    private ArrayList<PromotionCategoryItem> list_promotions = new ArrayList<>();
    private CirclePageIndicator promotion_indicator;
    private Runnable pager_runnable;
    private Handler pager_handler;
    private LoaderLayout loader_welcome_img;

    private ApiInterface apiService;
    private Context mContext;
    private HashMap<String, String> url_maps = new HashMap<String, String>();
    private String mWelcomImage = AppConstants.BLANK_STRING;
    private boolean isUserActive;
    private NestedCategoryItem mainCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lyt_parent = findViewById(R.id.base_child);
        lyt_root = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_home, null);

        mContext = this;
        lyt_parent.addView(lyt_root);

        if (getIntent() != null) {
            boolean isFromInternal = getIntent().getBooleanExtra("isFromInternal", false);

            if (getIntent().getBooleanExtra("isFromNotification", false)) {
                startActivity(new Intent(mContext, NotificationListActivity.class));
                isUserActive = true;
                isFromInternal = true;
                loadSideMenuData(true, false);
            } else {
                isUserActive = OnlineMartApplication.mLocalStore.isUserActive();
            }

            if (!isUserActive) {
                if (!isFromInternal) {
                    if (!OnlineMartApplication.mLocalStore.getUserId().equalsIgnoreCase(AppConstants.BLANK_STRING)) {
                        logoutUser();
                    }
                }
            }
        }

        lyt_loader = lyt_root.findViewById(R.id.home_loader);

        img_welcome = lyt_root.findViewById(R.id.home_img_welcome);
        lyt_slider = lyt_root.findViewById(R.id.home_lyt_slider);
        pager_promotion = lyt_root.findViewById(R.id.home_pager_promotion);
        recyl_categories = lyt_root.findViewById(R.id.home_recyl_category);
        lyt_promotion_slider = lyt_root.findViewById(R.id.home_lyt_promotions);
        btn_getStarted = lyt_root.findViewById(R.id.home_btn_get_started);
        promotion_indicator = lyt_root.findViewById(R.id.home_lyt_promotion_indicator);
        btn_getStarted.setText(btn_getStarted.getText().toString().toUpperCase());
        recyl_categories.setNestedScrollingEnabled(false);
        loader_welcome_img = findViewById(R.id.loader_welcome_img);

        apiService = ApiClient.createService(ApiInterface.class, mContext);
        recyl_categories.setHasFixedSize(true);
        recyl_categories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(OnlineMartApplication.mLocalStore.getSelectedRegionId() != null && OnlineMartApplication.mLocalStore.getSelectedRegionId().length()>0){
            loadCategoriesForHome();
        }
    }

    private void logoutUser() {

        try {

            JSONObject request = new JSONObject();
            request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request.put("device_type", AppConstants.DEVICE_TYPE);
            request.put("device_token", (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) ? "0000" : FirebaseInstanceId.getInstance().getToken());

            ApiInterface apiService = ApiClient.createService(ApiInterface.class, this);
            Call<ResponseBody> call = apiService.logout((new ConvertJsonToMap().jsonToMap(request)));
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
                                String status = resObj.optString("status");
                                String errorMsg = resObj.optString("error_message");
                                if (statusCode == 200 && status != null && status.equalsIgnoreCase("OK")) {

                                    OnlineMartApplication.mLocalStore.clearOnLogout();
                                    OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                                    OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                                    OnlineMartApplication.getCartList().clear();
                                    updateUserUI();

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppUtil.hideProgress();
                }
            });

        } catch (JSONException e) {
            AppUtil.hideProgress();
        }

    }

    public void loadCategoriesForHome() {

        if(recyl_categories.getAdapter() != null){
            recyl_categories.getAdapter().notifyDataSetChanged();
        }

        if(pager_promotion.getAdapter() != null){
            if(list_promotions != null){list_promotions.clear();}
            if(url_maps != null){url_maps.clear();}
            mainCategory = null;
            pager_promotion.getAdapter().notifyDataSetChanged();
        }

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            requestData.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            requestData.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                lyt_loader.showProgress();

                Call<ResponseBody> call = apiService.mGetHomeCatDetails(new ConvertJsonToMap().jsonToMap(requestData));
                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        lyt_loader.showContent();
                        try {
                            if (response.body() != null) {

                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getJSONObject("response").getInt("status_code") == 200) {

                                    OnlineMartApplication.home_promotionCategory.clear();
                                    url_maps.clear();

                                    JSONObject dataJsonObject = jsonObject.getJSONObject("response").getJSONObject("data");

                                    if (dataJsonObject.get("PromotionCategory") instanceof JSONArray) {
                                        JSONArray promotionJsonArray = dataJsonObject.getJSONArray("PromotionCategory");

                                        for (int promoIndex = 0; promoIndex < promotionJsonArray.length(); promoIndex++) {
                                            JSONObject promoData = promotionJsonArray.getJSONObject(promoIndex);

                                            PromotionCategoryItem promotionCategoryItem = new PromotionCategoryItem();
                                            promotionCategoryItem.setId(promoData.optString("id"));
                                            promotionCategoryItem.setName(promoData.optString("name"));
                                            promotionCategoryItem.setImage(promoData.optString("image"));
                                            OnlineMartApplication.home_promotionCategory.add(promotionCategoryItem);
                                            url_maps.put("" + promoData, promoData.optString("image"));
                                        }
                                    }

                                    OnlineMartApplication.home_nestedCategory.clear();

                                    if (dataJsonObject.get("NestedCategory") instanceof JSONArray) {

                                        JSONArray nestedCatJsonArray = dataJsonObject.getJSONArray("NestedCategory");

                                        for (int nestedIndex = 0; nestedIndex < nestedCatJsonArray.length(); nestedIndex++) {
                                            JSONObject nestedData = nestedCatJsonArray.getJSONObject(nestedIndex);
                                            NestedCategoryItem nestedCategoryItem = new NestedCategoryItem();
                                            nestedCategoryItem.setCategoryId(nestedData.optString("category_id"));
                                            nestedCategoryItem.setImage(nestedData.optString("image"));
                                            nestedCategoryItem.setName(nestedData.optString("name"));
                                            nestedCategoryItem.setLevel(nestedData.optString("level"));
                                            nestedCategoryItem.setSequenceNumber(nestedData.optString("sequence_number"));
                                            nestedCategoryItem.setAdminCategoryLevel(nestedData.optString("admin_category_level"));
                                            nestedCategoryItem.setParentId(nestedData.optString("parent_id"));
                                            nestedCategoryItem.setProductCount(nestedData.optString("product_count"));

                                            if (!nestedCategoryItem.getProduct_count().equalsIgnoreCase("0")) {
                                                OnlineMartApplication.home_nestedCategory.add(nestedCategoryItem);
                                            }

                                            if (nestedCategoryItem.getName() != null
                                                    &&
                                                    (nestedCategoryItem.getName().trim().equalsIgnoreCase("Grocery")
                                                            || nestedCategoryItem.getName().trim().equalsIgnoreCase("البقالة"))) {

                                                mainCategory = nestedCategoryItem;
                                            }
                                        }
                                    }

                                    Collections.sort(OnlineMartApplication.home_nestedCategory, new SequenceNumberSorter());

                                    JSONObject welComeJsonData = dataJsonObject.getJSONObject("WelcomeBanner");
                                    final Drawable alternate_image = ContextCompat.getDrawable(mContext, R.mipmap.place_holder);
                                    mWelcomImage = welComeJsonData.optString("welcome_banner_url");
                                    ImageLoader.getInstance().displayImage(mWelcomImage, img_welcome, OnlineMartApplication.intitOptions(), new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String imageUri, View view) {
                                            loader_welcome_img.showProgress();
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                            loader_welcome_img.showContent();
                                            img_welcome.setImageDrawable(alternate_image);
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            loader_welcome_img.showContent();
                                            img_welcome.setImageBitmap(loadedImage);
                                        }

                                        @Override
                                        public void onLoadingCancelled(String imageUri, View view) {
                                            loader_welcome_img.showContent();
                                            img_welcome.setImageDrawable(alternate_image);
                                        }
                                    });

                                    setPromotionSlider();
                                    HomeCategoryViewAdapter homeCategoryViewAdapter = new HomeCategoryViewAdapter(HomeActivity.this, OnlineMartApplication.home_nestedCategory);
                                    recyl_categories.setAdapter(homeCategoryViewAdapter);

                                    homeCategoryViewAdapter.setOnClickListener(new HomeCategoryViewAdapter.OnClickListener() {
                                        @Override
                                        public void onClick(int position) {

                                            NestedCategoryItem categoryItem = OnlineMartApplication.home_nestedCategory.get(position);
                                            Intent productListintent = new Intent(mContext, ProductListActivity.class);

                                            productListintent.putExtra(ProductListActivity.CATEGORY_ID, categoryItem.getCategoryId());
                                            productListintent.putExtra(ProductListActivity.CATEGORY_NAME, categoryItem.getName());

                                            if (!categoryItem.getLevel().equalsIgnoreCase("1")) {
                                                productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, true);
                                            } else {
                                                productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);
                                            }

                                            if (categoryItem.getAdminCategoryLevel().equalsIgnoreCase("3")) {
                                                productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, false);
                                            } else {
                                                productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, true);
                                            }

                                            startActivity(productListintent);
                                        }
                                    });

                                    btn_getStarted.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent productListintent = new Intent(mContext, ProductListActivity.class);

                                            if (mainCategory != null) {


                                                productListintent.putExtra(ProductListActivity.CATEGORY_ID, mainCategory.getCategoryId());
                                                productListintent.putExtra(ProductListActivity.CATEGORY_NAME, mainCategory.getName());

                                                if (!mainCategory.getLevel().equalsIgnoreCase("1")) {
                                                    productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, true);
                                                } else {
                                                    productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);
                                                }

                                                if (mainCategory.getAdminCategoryLevel().equalsIgnoreCase("3")) {
                                                    productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, false);
                                                } else {
                                                    productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, true);
                                                }

                                                startActivity(productListintent);


                                            } else {

                                                if (OnlineMartApplication.home_nestedCategory != null && OnlineMartApplication.home_nestedCategory.size() > 0) {

                                                    NestedCategoryItem categoryItem = OnlineMartApplication.home_nestedCategory.get(0);

                                                    productListintent.putExtra(ProductListActivity.CATEGORY_ID, categoryItem.getCategoryId());
                                                    productListintent.putExtra(ProductListActivity.CATEGORY_NAME, categoryItem.getName());

                                                    if (!categoryItem.getLevel().equalsIgnoreCase("1")) {
                                                        productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, true);
                                                    } else {
                                                        productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);
                                                    }

                                                    if (categoryItem.getAdminCategoryLevel().equalsIgnoreCase("3")) {
                                                        productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, false);
                                                    } else {
                                                        productListintent.putExtra(ProductListActivity.SHOULD_MENU_VISIBLE, true);
                                                    }

                                                    startActivity(productListintent);
                                                }
                                            }

                                        }
                                    });
                                }else{
                                    setPromotionSlider();
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            lyt_loader.showContent();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        lyt_loader.showContent();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            lyt_loader.showContent();
        }

    }

    private void setPromotionSlider() {

        if (url_maps.size() == 0) {
            lyt_promotion_slider.setVisibility(View.GONE);
        } else {
            lyt_promotion_slider.setVisibility(View.VISIBLE);

            list_promotions.clear();
            for (String name : url_maps.keySet()) {

                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.image(url_maps.get(name)).setScaleType(BaseSliderView.ScaleType.Fit);

                try {
                    JSONObject promotionObj = new JSONObject(name);

                    PromotionCategoryItem promotionCategoryItem = new PromotionCategoryItem();
                    promotionCategoryItem.setId(promotionObj.optString("id"));
                    promotionCategoryItem.setName(promotionObj.optString("name"));
                    promotionCategoryItem.setImage(promotionObj.optString("image"));

                    list_promotions.add(promotionCategoryItem);

                    Bundle bundle = new Bundle();
                    bundle.putString("promotion_id", promotionObj.optString("id"));
                    bundle.putString("promotion_name", promotionObj.optString("name"));
                    textSliderView.bundle(bundle);

                    textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            if (!ValidationUtil.isNullOrBlank(slider.getBundle())) {

                                Intent productListintent = new Intent(mContext, ProductListActivity.class);
                                productListintent.putExtra(ProductListActivity.PROMOTION_ID, slider.getBundle().getString("promotion_id"));
                                productListintent.putExtra(ProductListActivity.PROMOTION_NAME, slider.getBundle().getString("promotion_name"));
                                productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);

                                startActivity(productListintent);
                            }
                        }
                    });

                    lyt_slider.addSlider(textSliderView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            lyt_slider.setPresetTransformer(SliderLayout.Transformer.Default);
            lyt_slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            lyt_slider.setCustomAnimation(new DescriptionAnimation());
            lyt_slider.setDuration(6000);

            PromotionPagerAdapter promotionPagerAdapter = new PromotionPagerAdapter(this, list_promotions);
            pager_promotion.setAdapter(promotionPagerAdapter);
            promotion_indicator.setViewPager(pager_promotion);
            promotionPagerAdapter.setOnPromotionClicklIstener(new PromotionPagerAdapter.OnPromotionClickListener() {
                @Override
                public void onPromotionClick(int position) {

                    Intent productListintent = new Intent(mContext, ProductListActivity.class);
                    productListintent.putExtra(ProductListActivity.PROMOTION_ID, list_promotions.get(position).getId());
                    productListintent.putExtra(ProductListActivity.PROMOTION_NAME, list_promotions.get(position).getName());
                    productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);

                    startActivity(productListintent);
                }
            });

            pager_handler.removeCallbacks(pager_runnable);
            pager_handler.postDelayed(pager_runnable, 6000);
        }
    }

    @Override
    protected void onResume() {
        isUserActive = OnlineMartApplication.mLocalStore.isUserActive();
        super.onResume();
        super.setPageTitle(getString(R.string.title_activity_home_drawer), true);
        super.setSearchBarVisiblity(true);
        super.setToolbarTag(getString(R.string.menu));
        super.resetSelection();
        super.setHomeSelected();

        LoadInitialData.loadInitialData(this);

        if (pager_handler == null) {
            pager_handler = new Handler();
        }

        if (list_promotions != null && list_promotions.size() > 0) {
            pager_handler.removeCallbacks(pager_runnable);
            pager_handler.postDelayed(pager_runnable, 6000);
        }

        pager_runnable = new Runnable() {
            @Override
            public void run() {
                if (pager_promotion.getCurrentItem() == list_promotions.size() - 1) {
                    pager_promotion.setCurrentItem(0);
                } else {
                    pager_promotion.setCurrentItem(pager_promotion.getCurrentItem() + 1);
                }
                pager_handler.removeCallbacks(pager_runnable);
                pager_handler.postDelayed(pager_runnable, 6000);
            }
        };

        if(OnlineMartApplication.mLocalStore.getSelectedRegionId().equalsIgnoreCase(AppConstants.BLANK_STRING)){
            invalidateOptionsMenu();
            openRegionPicker(false, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pager_handler != null) {
            pager_handler.removeCallbacks(pager_runnable);
        }
    }
}
