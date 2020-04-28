package thegroceryshop.com.activity;

/*
 * Created by umeshk on 14-03-2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.WishListDetailAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.application.OnlineMartApplication.mLocalStore;

public class MyWishListDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTitle;
    private LoaderLayout loaderLayout;
    private RecyclerView recyclerWishListDetail;
    private Context context;
    private String list_id;
    private WishListDetailAdapter wishListDetailAdapter;
    private TextView txt_update;
    private ArrayList<Product> wishListDetailList;
    private ArrayList<Product> orignal_list = new ArrayList<>();
    private String list_name, list_description;
    private boolean isRemovedFlag = false;
    private boolean isWishListUpdated = false;

    private LinearLayout lyt_add_to_cart;
    private TextView txt_item_add_or_remove;
    private TextView txt_cart_capecity;
    private LinearLayout lyt_progress;
    private ProgressBar progress_cart;
    private TextView txt_eligible;
    private Handler cart_progress_handler;
    private Runnable cart_progress_runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_detail);
        context = this;

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById(R.id.txt_title);
        loaderLayout = findViewById(R.id.loaderLayout);
        recyclerWishListDetail = findViewById(R.id.recyclerWishListDetail);
        txt_update = findViewById(R.id.txt_update);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }
        }

        lyt_add_to_cart = findViewById(R.id.list_product_lyt_add_cart);
        txt_item_add_or_remove = findViewById(R.id.list_product_txt_cart_add_or_remove);
        txt_cart_capecity = findViewById(R.id.list_product_txt_cart_capecity);
        lyt_progress = findViewById(R.id.list_product_lyt_progress);
        progress_cart = findViewById(R.id.list_product_progress_cart);
        txt_eligible = findViewById(R.id.list_product_txt_eligible);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.cart_progress);
        progress_cart.setProgressDrawable(drawable);
        progress_cart.setMax((int) mLocalStore.getFreeShippingAmount());
        txt_eligible.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerWishListDetail.setLayoutManager(linearLayoutManager);
        recyclerWishListDetail.setHasFixedSize(true);

        if (getIntent() != null) {
            list_id = getIntent().getStringExtra("list_id");
            list_name = getIntent().getStringExtra("list_name");
            list_description = getIntent().getStringExtra("list_description");

            txtTitle.setText(getIntent().getStringExtra("list_name").toUpperCase());
        }

        txt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wishListDetailList != null && wishListDetailList.size() > 0) {
                    isRemovedFlag = false;
                    updateWishList(new Product(), 0);
                }
            }
        });

        cart_progress_runnable = new Runnable() {
            @Override
            public void run() {
                lyt_add_to_cart.setVisibility(View.GONE);
            }
        };

        loadWishListDetail();
    }

    private void updateCartProgress(final String priceString, final int quantity) {

        if(quantity != 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                    float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                    String updatedTotal = null;

                    txt_item_add_or_remove.setText(String.format(getString(R.string.product_added_to_cart), "1" + AppConstants.BLANK_STRING).toUpperCase());
                    updatedTotal = " "+ getString(R.string.egp) +" " + String.format(Locale.ENGLISH,"%d",Math.round(current_total + (price * quantity)));

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

                    //txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), getString(R.string.egp) + AppConstants.SPACE + remaingEGP).toUpperCase());
                    cart_progress_handler.postDelayed(cart_progress_runnable, 4000);

                }
            });
        }

    }

    private void updateWishList(Product product, final int position) {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {

                AppUtil.showProgress(MyWishListDetailActivity.this);

                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("list_id", list_id);
                request.put("list_name", list_name);
                request.put("list_description", list_description);

                JSONArray jsonArray = new JSONArray();
                if (isRemovedFlag) {
                    if (product != null) {
                        JSONObject obj = new JSONObject();
                        obj.put("product_id", product.getId());
                        obj.put("qty", product.getWishListQty());
                        obj.put("isRemoved", product.isRemovedFromWishList());
                        jsonArray.put(0, obj);
                    }
                } else {
                    for (int i = 0; i < wishListDetailList.size(); i++) {
                        product = wishListDetailList.get(i);
                        if (product != null) {
                            JSONObject obj = new JSONObject();
                            obj.put("product_id", product.getId());
                            obj.put("qty", product.getWishListQty());
                            obj.put("isRemoved", product.isRemovedFromWishList());
                            jsonArray.put(i, obj);
                        }
                    }
                }

                request.put("products", jsonArray);
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                Call<ResponseBody> call = apiService.wishListInfo((new ConvertJsonToMap().jsonToMap(request)));
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
                                    String successMessage = resObj.optString("success_message");
                                    if (statusCode == 200) {
                                        isWishListUpdated = true;
                                        orignal_list = cloneList(wishListDetailList);
                                        //Collections.copy(orignal_list, wishListDetailList);
                                        AppUtil.displaySingleActionAlert(MyWishListDetailActivity.this, getString(R.string.app_name), successMessage, getString(R.string.ok), new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                appDialogSingleAction.dismiss();
                                                if (isRemovedFlag) {
                                                    if (wishListDetailAdapter != null) {
                                                        orignal_list.remove(position);
                                                        wishListDetailAdapter.updateWishList(position);
                                                        if(wishListDetailList !=  null && wishListDetailList.size() == 0){
                                                            loaderLayout.showStatusText();
                                                        }
                                                    }
                                                } else {
                                                    Intent intent = new Intent();
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            }
                                        });

                                    } else {
                                        loaderLayout.showStatusText();
                                        AppUtil.displaySingleActionAlert(context, getString(R.string.title), errorMsg, getString(R.string.ok));
                                    }

                                } else {
                                    loaderLayout.showStatusText();
                                    AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-720)", getString(R.string.ok));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                loaderLayout.showStatusText();
                                AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-721)", getString(R.string.ok));
                            }
                        } else {
                            loaderLayout.showStatusText();
                            AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-722)", getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loaderLayout.showStatusText();
                        AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-723)", getString(R.string.ok));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AppUtil.requestLogin(context);
        }

    }

    public ArrayList<Product> cloneList(ArrayList<Product> list) {
        if(list != null && list.size() >0) {
            orignal_list = new ArrayList<>(list.size());
            for (Product product : list)
                try {
                    orignal_list.add((Product) product.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            return orignal_list;
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if(orignal_list != null && orignal_list.size()>0){
                    boolean isChanges = false;
                    for(int i=0; i<wishListDetailList.size(); i++){
                        if(wishListDetailList.get(i).getWishListQty() != orignal_list.get(i).getWishListQty()){
                            isChanges = true;
                            break;
                        }
                    }

                    if(isChanges){
                        AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(this, getString(R.string.app_name), getString(R.string.unsaved_changes), getString(R.string.cancel), getString(R.string.save));
                        appDialogDoubleAction.setCancelable(false);
                        appDialogDoubleAction.setCanceledOnTouchOutside(false);
                        appDialogDoubleAction.show();
                        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                            @Override
                            public void onLeftActionClick(View view) {
                                if (isWishListUpdated) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }

                            @Override
                            public void onRightActionClick(View view) {
                                isRemovedFlag = false;
                                updateWishList(new Product(), 0);

                            }
                        });

                    }else{
                        if (isWishListUpdated) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }
                }else{
                    if (isWishListUpdated) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }

        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onBackPressed() {

        if(orignal_list != null && orignal_list.size()>0){
            boolean isChanges = false;
            for(int i=0; i<wishListDetailList.size(); i++){
                if(wishListDetailList.get(i).getWishListQty() != orignal_list.get(i).getWishListQty()){
                    isChanges = true;
                    break;
                }
            }

            if(isChanges){
                AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(this, getString(R.string.app_name), getString(R.string.unsaved_changes), getString(R.string.cancel), getString(R.string.save));
                appDialogDoubleAction.setCancelable(false);
                appDialogDoubleAction.setCanceledOnTouchOutside(false);
                appDialogDoubleAction.show();
                appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                    @Override
                    public void onLeftActionClick(View view) {
                        if (isWishListUpdated) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }

                    @Override
                    public void onRightActionClick(View view) {
                        isRemovedFlag = false;
                        updateWishList(new Product(), 0);

                    }
                });

            }else{
                if (isWishListUpdated) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        }else{
            if (isWishListUpdated) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    private void loadWishListDetail() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                loaderLayout.showProgress();
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("list_id", list_id);
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                Call<ResponseBody> call = apiService.wishListInfo((new ConvertJsonToMap().jsonToMap(request)));
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

                                        loaderLayout.showContent();

                                        JSONObject dataObject = resObj.optJSONObject("data");
                                        JSONArray productsArray = dataObject.optJSONArray("products");
                                        wishListDetailList = new ArrayList<>();
                                        if (productsArray != null && productsArray.length() > 0) {

                                            for (int i = 0; i < productsArray.length(); i++) {

                                                Product product = new Product();
                                                JSONObject object = productsArray.getJSONObject(i);

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_id"))) {
                                                    product.setId(object.optString("product_id"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_name_english"))) {
                                                    product.setEnglishName(object.optString("product_name_english"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_name_arabic"))) {
                                                    product.setArabicName(object.optString("product_name_arabic"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_size_english"))) {
                                                    product.setEnglishQuantity(object.optString("product_size_english"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_size_arabic"))) {
                                                    product.setArabicQuantity(object.optString("product_size_arabic"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("brand_name_english"))) {
                                                    product.setEnglishBrandName(object.optString("brand_name_english"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("brand_name_arabic"))) {
                                                    product.setArabicBrandName(object.optString("brand_name_arabic"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optBoolean("offered_price_status", false))) {
                                                    product.setOffer(object.optBoolean("offered_price_status", false));
                                                }
                                                product.setAddedToWishList(true);

                                                if (!ValidationUtil.isNullOrBlank(object.optDouble("product_offer_percent", 0.0f))) {
                                                    product.setOfferString(Math.round(object.optDouble("product_offer_percent", 0.0f)) + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optBoolean("shipping_third_party", false))) {
                                                    product.setShippingThirdParty(object.optBoolean("shipping_third_party", false));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("shipping_hours"))) {
                                                    product.setShippingHours(object.optString("shipping_hours"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("image"))) {
                                                    product.setImage(object.optString("image"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("door_step"))) {
                                                    if (object.optString("door_step").equalsIgnoreCase("1")) {
                                                        product.setDoorStepDelivery(true);
                                                    } else {
                                                        product.setDoorStepDelivery(false);
                                                    }
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("currency"))) {
                                                    product.setCurrency(object.optString("currency"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_acutal_price"))) {
                                                    product.setActualPrice(object.optString("product_acutal_price"));
                                                } else {
                                                    product.setActualPrice("0.0");
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("product_offer_price"))) {
                                                    product.setOfferedPrice(object.optString("product_offer_price"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("brand_id"))) {
                                                    product.setBrandId(object.optString("brand_id"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optBoolean("shipping_third_party", false))) {
                                                    product.setShippingThirdParty(object.optBoolean("shipping_third_party", false));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("shipping_hours"))) {
                                                    product.setShippingHours(object.optString("shipping_hours"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(object.optString("wishlist_qty"))) {
                                                    if (object.optInt("wishlist_qty") < 0) {
                                                        product.setWishlistQty(0);
                                                    } else {
                                                        product.setWishlistQty(object.optInt("wishlist_qty"));
                                                    }
                                                } else {
                                                    product.setWishlistQty(0);
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

                                                wishListDetailList.add(product);
                                            }

                                            orignal_list = cloneList(wishListDetailList);
                                            //Collections.copy(orignal_list, wishListDetailList);

                                            if (wishListDetailList.size() > 0) {
                                                wishListDetailAdapter = new WishListDetailAdapter(context, wishListDetailList);
                                                recyclerWishListDetail.setAdapter(wishListDetailAdapter);
                                                wishListDetailAdapter.setOnWishListProductListener(new WishListDetailAdapter.OnWishListProductListener() {
                                                    @Override
                                                    public void onAddToCart(int position) {
                                                        Product product = wishListDetailList.get(position);
                                                        product.setAddedToCart(true);

                                                        if (product.getWishListQty() <= 0) {
                                                            product.setWishlistQty(1);
                                                        }

                                                        String notifyMsg = AppConstants.BLANK_STRING;
                                                        if(product.getMaxQuantity() > 0){

                                                            float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                                                            float final_total = current_total;

                                                            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());
                                                            if (cartItem == null) {

                                                                if(product.getWishListQty() > product.getMaxQuantity()){
                                                                    product.setSelectedQuantity(product.getMaxQuantity());
                                                                }else{
                                                                    product.setSelectedQuantity(product.getWishListQty());
                                                                }

                                                                if (product.isOffer()) {
                                                                    final_total = current_total + (Float.parseFloat(product.getOfferedPrice()) * product.getSelectedQuantity());
                                                                } else {
                                                                    final_total = current_total + (Float.parseFloat(product.getActualPrice()) * product.getSelectedQuantity());
                                                                }

                                                                OnlineMartApplication.addToCart(AppUtil.getCartObject(product));
                                                                updateCartProgress(product.isOffer() ? product.getActualPrice() : product.getOfferedPrice(), product.getSelectedQuantity());

                                                            } else {

                                                                cartItem.setMaxQuantity(product.getMaxQuantity());

                                                                int final_qty = cartItem.getSelectedQuantity() + product.getWishListQty();
                                                                int prev_total = Math.round(cartItem.getSelectedQuantity() * Float.parseFloat(cartItem.getActualPrice()));
                                                                if(final_qty > cartItem.getMaxQuantity()){
                                                                    cartItem.setSelectedQuantity(product.getMaxQuantity());
                                                                }else{
                                                                    cartItem.setSelectedQuantity(final_qty);
                                                                }

                                                                cartItem.setActualPrice(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice());

                                                                final_total = final_total - (Float.parseFloat(cartItem.getActualPrice()) * cartItem.getSelectedQuantity());
                                                                if (product.isOffer()) {
                                                                    final_total = final_total + (Float.parseFloat(product.getOfferedPrice()) * product.getSelectedQuantity());
                                                                } else {
                                                                    final_total = final_total + (Float.parseFloat(product.getActualPrice()) * product.getSelectedQuantity());
                                                                }

                                                                OnlineMartApplication.mLocalStore.saveCartTotal(Math.round(OnlineMartApplication.mLocalStore.getCartTotal() - prev_total));
                                                                OnlineMartApplication.updateWishlistFlagOnCart(cartItem.getId(), true);
                                                                OnlineMartApplication.updateQuantityAndrPriceOnCart(cartItem.getId(), cartItem.getSelectedQuantity(), cartItem.getActualPrice());
                                                                updateCartProgress(cartItem.getActualPrice(), cartItem.getSelectedQuantity());
                                                            }

                                                        }else{
                                                            notifyMsg = String.format(getString(R.string.notify_out_of_stock1), product.getLabel());
                                                            AppUtil.showErrorDialog(context, notifyMsg);
                                                        }

                                                    }

                                                    @Override
                                                    public void onRemoveFromWishList(final int position, final Product product) {

                                                        final AppDialogDoubleAction confirmationDialog = new AppDialogDoubleAction(context, getString(R.string.app_name), getString(R.string.wishlist_item_remove_confrmation), getString(R.string.cancel), getString(R.string.ok));
                                                        confirmationDialog.show();
                                                        confirmationDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                            @Override
                                                            public void onLeftActionClick(View view) {
                                                                confirmationDialog.dismiss();
                                                            }

                                                            @Override
                                                            public void onRightActionClick(View view) {
                                                                confirmationDialog.dismiss();
                                                                product.setRemovedFromWishList(true);
                                                                isRemovedFlag = true;
                                                                updateWishList(product, position);
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                txt_update.setVisibility(View.GONE);
                                            }
                                        } else {
                                            loaderLayout.showStatusText();
                                        }
                                    } else {
                                        loaderLayout.showStatusText();
                                    }

                                } else {
                                    loaderLayout.showStatusText();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                loaderLayout.showStatusText();
                            }
                        } else {
                            loaderLayout.showStatusText();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loaderLayout.showStatusText();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AppUtil.requestLogin(context);
        }
    }


}
