package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import thegroceryshop.com.adapter.MySavedListAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.modal.WishListBean;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.application.OnlineMartApplication.mLocalStore;

public class MySavedListActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView txt_title;
    private LoaderLayout loaderLayout;
    private RecyclerView recyclerWishList;
    private Button btnCreateNewList;
    private Context context;
    private MySavedListAdapter mySavedListAdapter;
    ArrayList<WishListBean> listNames = new ArrayList<>();
    private ApiInterface apiService;

    private MenuItem mSearchMenu;

    private LinearLayout lyt_add_to_cart;
    private TextView txt_item_add_or_remove;
    private TextView txt_cart_capecity;
    private LinearLayout lyt_progress;
    private ProgressBar progress_cart;
    private TextView txt_eligible;
    private Handler cart_progress_handler;
    private Runnable cart_progress_runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        setSupportActionBar(toolbar);
        txt_title.setText(getString(R.string.my_list).toUpperCase());
        context = this;
        apiService = ApiClient.createService(ApiInterface.class, context);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.setPageTitle(getString(R.string.my_list));
        super.setSearchBarVisiblity(false);
        super.setToolbarTag(getString(R.string.back));

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            toolbar.setNavigationIcon(R.mipmap.top_back);
        } else {
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.serach_menu:
                Intent searchIntent = new Intent(MySavedListActivity.this, SearchingActivity.class);
                startActivity(searchIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchMenu = menu.findItem(R.id.serach_menu);
        mSearchMenu.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    private void initView() {
        recyclerWishList = findViewById(R.id.recyclerWishList);
        btnCreateNewList = findViewById(R.id.btn_create_new_list);
        loaderLayout = findViewById(R.id.loaderLayout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerWishList.setLayoutManager(linearLayoutManager);
        recyclerWishList.setHasFixedSize(true);

        btnCreateNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wishListIntent = new Intent(context, CreateWishListActivity.class);
                startActivityForResult(wishListIntent, 1001);
            }
        });

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

        cart_progress_runnable = new Runnable() {
            @Override
            public void run() {
                lyt_add_to_cart.setVisibility(View.GONE);
            }
        };

        loadWishLists();
    }

    private void loadWishLists() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                loaderLayout.showProgress();
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                Call<ResponseBody> call = apiService.getSavedWishLists((new ConvertJsonToMap().jsonToMap(request)));
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
                                        listNames.clear();
                                        //String responseMessage = resObj.optString("success_message");
                                        if (resObj.optJSONArray("data") != null) {
                                            JSONArray array = resObj.optJSONArray("data");
                                            OnlineMartApplication.mLocalStore.setMyWishListNames(array.toString());
                                            for (int i = 0; i < array.length(); i++) {
                                                WishListBean wishListBean = new WishListBean();
                                                JSONObject dataObject = array.getJSONObject(i);
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
                                            mySavedListAdapter = new MySavedListAdapter(context, listNames);
                                            recyclerWishList.setAdapter(mySavedListAdapter);
                                            mySavedListAdapter.setOnWishListSelectedListener(new MySavedListAdapter.OnWishListSelectedListener() {
                                                @Override
                                                public void onWishListSelected(int position) {
                                                    if (listNames.get(position).getId() != null) {
                                                        Intent intent = new Intent(context, MyWishListDetailActivity.class);
                                                        intent.putExtra("list_id", listNames.get(position).getId());
                                                        intent.putExtra("list_name", listNames.get(position).getName());
                                                        intent.putExtra("list_description", listNames.get(position).getDescription());
                                                        startActivityForResult(intent, 1002);
                                                    }
                                                }

                                                @Override
                                                public void onAddWishListToCart(int position, View btn) {
                                                    AppUtil.showProgress(MySavedListActivity.this, getString(R.string.adding_wishlist_to_cart));
                                                    addWishListToCart(listNames.get(position).getId(), btn);
                                                }

                                                @Override
                                                public void onMenuItemClicked(final int position, View anchor, final String list_id) {

                                                    PopupMenu popup = new PopupMenu(context, anchor);
                                                    MenuInflater inflater = popup.getMenuInflater();
                                                    inflater.inflate(R.menu.menu_wishlist, popup.getMenu());
                                                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                        public boolean onMenuItemClick(MenuItem item) {

                                                            if(item.getItemId() == R.id.edit_wishlist){

                                                                Intent wishListIntent = new Intent(context, CreateWishListActivity.class);
                                                                wishListIntent.putExtra(CreateWishListActivity.KEY_WISHLIST_ID, listNames.get(position).getId());
                                                                wishListIntent.putExtra(CreateWishListActivity.KEY_WISHLIST_NAME, listNames.get(position).getName());
                                                                wishListIntent.putExtra(CreateWishListActivity.KEY_WISHLIST_DESCRIPTION, listNames.get(position).getDescription());
                                                                startActivityForResult(wishListIntent, 1001);

                                                            } else if(item.getItemId() == R.id.remove_wishlist){
                                                                final AppDialogDoubleAction confirmationDialog = new AppDialogDoubleAction(context, getString(R.string.app_name), getString(R.string.wishlist_remove_confrmation), getString(R.string.cancel), getString(R.string.ok));
                                                                confirmationDialog.show();
                                                                confirmationDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                                    @Override
                                                                    public void onLeftActionClick(View view) {
                                                                        confirmationDialog.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onRightActionClick(View view) {
                                                                        confirmationDialog.dismiss();
                                                                        removeWishList(position, list_id);
                                                                    }
                                                                });
                                                            }

                                                            return true;
                                                        }
                                                    });
                                                    popup.show();

                                                }
                                            });
                                            loaderLayout.showContent();
                                        }
                                    } else {

                                        loaderLayout.showContent();
                                        listNames = new ArrayList<>();
                                        mySavedListAdapter = new MySavedListAdapter(context, listNames);
                                        recyclerWishList.setAdapter(mySavedListAdapter);
                                    }

                                } else {

                                    loaderLayout.showContent();
                                    listNames = new ArrayList<>();
                                    mySavedListAdapter = new MySavedListAdapter(context, listNames);
                                    recyclerWishList.setAdapter(mySavedListAdapter);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();

                                loaderLayout.showContent();
                                listNames = new ArrayList<>();
                                mySavedListAdapter = new MySavedListAdapter(context, listNames);
                                recyclerWishList.setAdapter(mySavedListAdapter);
                            }
                        } else {

                            loaderLayout.showContent();
                            listNames = new ArrayList<>();
                            mySavedListAdapter = new MySavedListAdapter(context, listNames);
                            recyclerWishList.setAdapter(mySavedListAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        loaderLayout.showContent();
                        listNames = new ArrayList<>();
                        mySavedListAdapter = new MySavedListAdapter(context, listNames);
                        recyclerWishList.setAdapter(mySavedListAdapter);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AppUtil.requestLogin(context);
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
                }
            });
        }

    }

    public void showAddToCartSliderNotification(int no_of_products_added){
        if(no_of_products_added > 1){
            txt_item_add_or_remove.setText(String.format(getString(R.string.products_added_to_cart), no_of_products_added + AppConstants.BLANK_STRING).toUpperCase());
        }else{
            txt_item_add_or_remove.setText(String.format(getString(R.string.product_added_to_cart), no_of_products_added + AppConstants.BLANK_STRING).toUpperCase());
        }

        if (cart_progress_handler == null) {
            cart_progress_handler = new Handler();
        }

        lyt_add_to_cart.setVisibility(View.VISIBLE);
        int remaingEGP = Math.round(OnlineMartApplication.mLocalStore.getFreeShippingAmount() - OnlineMartApplication.mLocalStore.getCartTotal());

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), getString(R.string.egp) + AppConstants.SPACE + remaingEGP));
        }else{
            txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), remaingEGP + AppConstants.SPACE + getString(R.string.egp)));
        }

        //txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), getString(R.string.egp) + AppConstants.SPACE + remaingEGP).toUpperCase());
        cart_progress_handler.postDelayed(cart_progress_runnable, 4000);
    }

    private void addWishListToCart(String list_id, final View btn) {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
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

                                        JSONObject dataObject = resObj.optJSONObject("data");
                                        JSONArray productsArray = dataObject.optJSONArray("products");

                                        String notifyMsg = getString(R.string.notify_out_of_stock_new);
                                        int out_of_stock_counter = 0;
                                        int counter = 0;
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
                                                //product.setOfferString(object.optString("product_offer_percent") + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
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

                                            product.setAddedToCart(true);

                                            if (product.getWishListQty() <= 0) {
                                                product.setWishlistQty(1);
                                            }

                                            if(product.getMaxQuantity() > 0){

                                                float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                                                float final_total = current_total;

                                                CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());
                                                if (cartItem == null) {

                                                    if(product.getWishListQty() > product.getMaxQuantity()){
                                                        notifyMsg = notifyMsg + "\n- " + String.format(getString(R.string.max_quantity_reached_msg_new), product.getLabel());
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
                                                    invalidateOptionsMenu();

                                                } else {

                                                    cartItem.setMaxQuantity(product.getMaxQuantity());
                                                    cartItem.setAddedToWishList(true);

                                                    int final_qty = cartItem.getSelectedQuantity() + product.getWishListQty();
                                                    int prev_total = Math.round(cartItem.getSelectedQuantity() * Float.parseFloat(cartItem.getActualPrice()));

                                                    if(final_qty > cartItem.getMaxQuantity()){
                                                        notifyMsg = notifyMsg + "\n- " + String.format(getString(R.string.max_quantity_reached_msg_new), product.getLabel());
                                                        cartItem.setSelectedQuantity(product.getMaxQuantity());
                                                    }else{
                                                        cartItem.setSelectedQuantity(final_qty);
                                                    }

                                                    String product_price = (product.isOffer() ? product.getOfferedPrice() : product.getActualPrice());
                                                    cartItem.setActualPrice(product_price);

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
                                                    invalidateOptionsMenu();
                                                }
                                                counter++;

                                            }else{
                                                out_of_stock_counter++;
                                                notifyMsg = notifyMsg + "\n- " + String.format(getString(R.string.noti_out_of_stock_new), product.getLabel());
                                            }
                                        }

                                        AppUtil.hideProgress();

                                        if(!notifyMsg.equalsIgnoreCase(getString(R.string.notify_out_of_stock_new))){
                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(context, getString(R.string.app_name), notifyMsg, getString(R.string.ok));
                                            appDialogSingleAction.setCancelable(false);
                                            appDialogSingleAction.setCanceledOnTouchOutside(false);
                                            appDialogSingleAction.setStartAligned(true);
                                            appDialogSingleAction.show();
                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                }
                                            });

                                            //AppUtil.showErrorDialog(context, notifyMsg);
                                        }

                                        if(counter != 0){
                                            showAddToCartSliderNotification(counter);
                                        }

                                    } else {
                                        AppUtil.hideProgress();
                                        AppUtil.displaySingleActionAlert(context, getString(R.string.title), errorMsg, getString(R.string.ok));
                                    }

                                } else {
                                    AppUtil.hideProgress();
                                    AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-713)", getString(R.string.ok));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                AppUtil.hideProgress();
                                AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-714)", getString(R.string.ok));
                            }
                        } else {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-715)", getString(R.string.ok));
                        }
                        btn.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        btn.setEnabled(true);
                        AppUtil.hideProgress();
                        AppUtil.displaySingleActionAlert(context, getString(R.string.title), getString(R.string.error_msg) + "(Err-716)", getString(R.string.ok));
                    }
                });

            } catch (JSONException e) {
                AppUtil.hideProgress();
                btn.setEnabled(true);
                e.printStackTrace();
            }
        } else {
            AppUtil.hideProgress();
            btn.setEnabled(true);
            AppUtil.requestLogin(context);
        }
    }

    private void removeWishList(final int position, String list_id) {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("list_id", list_id);

            if (NetworkUtil.networkStatus(context)) {
                try {
                    ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                    Call<ResponseBody> call = apiService.removeWishList((new ConvertJsonToMap().jsonToMap(request_data)));
                    AppUtil.showProgress(context);
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
                                            AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), responseMessage, context.getString(R.string.ok), new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    appDialogSingleAction.dismiss();
                                                    if (mySavedListAdapter != null) {
                                                        LoadUsualData.loadWishLists(context);
                                                        mySavedListAdapter.updateWishList(position);
                                                        if(mySavedListAdapter.getItemCount() == 0){
                                                            mySavedListAdapter = new MySavedListAdapter(context, listNames);
                                                            recyclerWishList.setAdapter(mySavedListAdapter);
                                                        }
                                                    }

                                                }
                                            });

                                        } else {
                                            AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), errorMsg, context.getString(R.string.ok));
                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), context.getString(R.string.error_msg) + "(Err-717)", context.getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), context.getString(R.string.error_msg) + "(Err-718)", context.getString(R.string.ok));
                                }
                            } else {
                                AppUtil.displaySingleActionAlert(context, context.getString(R.string.app_name), context.getString(R.string.error_msg) + "(Err-719)", context.getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadWishLists();
        } else if (requestCode == 1002 && resultCode == RESULT_OK) {
            loadWishLists();
        }
    }
}
