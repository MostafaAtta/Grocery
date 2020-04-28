package thegroceryshop.com.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.adapter.CartListRecyclerViewAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.WishListDialog;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.DeliveryCharges;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.modal.WishListBean;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/29/2017.
 */

public class ReviewCartActivity extends AppCompatActivity {

    public static String DELIVERY_TYPE = "delivery_type";
    public static String IS_USE_CREDITS = "is_use_credits";
    public static String CREDITS_USED_FOR_ORDER = "credits_used_for_order";
    private RecyclerView recyl_cart;
    private LinearLayout cart_lyt_header;
    private TextView cart_total_items, cart_txt_subtotal, cart_txt_total_savings, cart_txt_delivery_fee, cart_txt_total_amount;
    private Context mContext;
    private ApiInterface apiService;
    ArrayList<CartItem> listUpdateCart = new ArrayList<>();
    private CartListRecyclerViewAdapter cartListRecyclerViewAdapter;
    private double deliveryCharge = 0.0f;
    private float subtotal = 0.0f, savings = 0.0f, tax_charges = 0.0f, total_amont = 0.0f;
    private int shipping_hours = 0;
    private boolean isShippingThirdParty, isDoorStepDeliveryAvaliable;
    private float total = 0.0f;
    private TextView no_itme_txt;
    private TextView txt_done;
    private LinearLayout cardItem_layout;
    private boolean isLoading = false, isCartUpdating = false;
    private ArrayList<DeliveryCharges> list_charges = new ArrayList<>();
    private String delivery_type;
    private float credits_to_use;
    private boolean is_use_credits;

    private WishListDialog.OnAddToWishListLister onAddToWishListLister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_cart);

        mContext = this;
        apiService = ApiClient.createService(ApiInterface.class, mContext);
        setResult(Activity.RESULT_CANCELED);

        recyl_cart = findViewById(R.id.recyclerViewCartList);
        txt_done = findViewById(R.id.review_cart_txt_done);

        if(getIntent() != null){
            delivery_type = getIntent().getStringExtra(DELIVERY_TYPE);
            is_use_credits = getIntent().getBooleanExtra(IS_USE_CREDITS, false);
            credits_to_use = getIntent().getFloatExtra(CREDITS_USED_FOR_ORDER, 0.0f);
        }

        cart_total_items = findViewById(R.id.cart_txt_total_items);
        cart_txt_subtotal = findViewById(R.id.cart_txt_subtotal_val);
        cart_txt_total_savings = findViewById(R.id.cart_txt_total_savings_val);
        cart_txt_delivery_fee = findViewById(R.id.cart_txt_delivery_fee_val);
        cart_txt_total_amount = findViewById(R.id.cart_txt_total_amount);
        cart_lyt_header = findViewById(R.id.cart_lyt_header);

        no_itme_txt = findViewById(R.id.no_itme_txt);
        cardItem_layout = findViewById(R.id.cardItem_layout);

        recyl_cart.setHasFixedSize(true);
        recyl_cart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        onAddToWishListLister = new WishListDialog.OnAddToWishListLister() {
            @Override
            public void onAddedToWishListListener(int position, String product_id) {
                position = -1;
                if(OnlineMartApplication.getCartList().size() > 0){
                    for(int i=0; i<OnlineMartApplication.getCartList().size(); i++){
                        if(OnlineMartApplication.getCartList().get(i).getId().equalsIgnoreCase(product_id)){
                            position = i;
                            break;
                        }
                    }
                }

                if(OnlineMartApplication.getCartList().size() > position && position != -1){
                    OnlineMartApplication.getCartList().get(position).setAddedToWishList(true);
                    OnlineMartApplication.updateWishlistFlagOnCart(product_id, true);
                    if(cartListRecyclerViewAdapter != null){
                        cartListRecyclerViewAdapter.notifyItemChanged(position);
                    }
                }
            }
        };

        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isLoading){

                    float available_credits = OnlineMartApplication.mLocalStore.getUserCredit();
                    float total_amount = total;
                    credits_to_use = 0.0f;

                    if (total_amount <= available_credits) {
                        credits_to_use = total_amount;
                    } else {
                        credits_to_use = available_credits;
                    }

                    boolean isUpdatingCartProducts = false;
                    if(OnlineMartApplication.getCartList().size() > 0 && total_amont != 0.0f){
                        if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())){

                            String ids = AppConstants.BLANK_STRING;
                            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                if (i == OnlineMartApplication.getCartList().size() - 1) {
                                    ids = ids + OnlineMartApplication.getCartList().get(i).getId();
                                } else {
                                    ids = ids + OnlineMartApplication.getCartList().get(i).getId() + ",";
                                }
                            }
                            isUpdatingCartProducts = true;
                            updateCartProducts(ids);

                        }
                    }

                    if(!isUpdatingCartProducts){

                        Intent intent = new Intent();
                        intent.putExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE, deliveryCharge);
                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS, OnlineMartApplication.getCartList().size());
                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT, total);
                        intent.putExtra(CheckOutProcessActivity.KEY_SUBTOTAL, subtotal);
                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SHIPPING_HOURS, shipping_hours);
                        intent.putExtra(CheckOutProcessActivity.KEY_IS_DOOR_STEP_DELIVERY_AVAILABLE, isDoorStepDeliveryAvaliable);
                        intent.putExtra(CheckOutProcessActivity.KEY_IS_SHIPPING_TO_THIRD_PARTY, isShippingThirdParty);
                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS, savings);
                        intent.putExtra(CheckOutProcessActivity.KEY_CREDITS_CAN_USE, credits_to_use);

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume() {
        super.onResume();

        try {
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            OnlineMartApplication.getCartList().clear();
            if (cartArray.length() != 0) {
                for (int i = 0; i < cartArray.length(); i++) {

                    JSONObject cartObj = cartArray.getJSONObject(i);
                    CartItem cartItem = new CartItem();
                    cartItem.setId(cartObj.optString("product_id"));
                    cartItem.setImage(cartObj.optString("image"));
                    cartItem.setEnglishLabel(cartObj.optString("englishName"));
                    cartItem.setArabicLabel(cartObj.optString("arabicName"));
                    cartItem.setEnglishBrandName(cartObj.optString("englishBrandName"));
                    cartItem.setArabicBrandName(cartObj.optString("arabicBrandName"));
                    cartItem.setEnglishQuantity(cartObj.optString("english_product_size"));
                    cartItem.setArabicQuantity(cartObj.optString("arabic_product_size"));
                    cartItem.setActualPrice(cartObj.optString("price"));
                    cartItem.setMaxQuantity(cartObj.optInt("qty"));
                    cartItem.setSavedPrice(cartObj.optString("saved"));
                    //cartItem.setTaxCharges((float) (cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    OnlineMartApplication.getCartList().add(cartItem);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        cartListRecyclerViewAdapter = new CartListRecyclerViewAdapter(this, OnlineMartApplication.getCartList());
        recyl_cart.setAdapter(cartListRecyclerViewAdapter);

        cartListRecyclerViewAdapter.setOnAddToListListener(new CartListRecyclerViewAdapter.OnAddToListListener() {
            @Override
            public void onAddToWishList(int position) {
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
                                final WishListDialog wishListDialog = new WishListDialog(ReviewCartActivity.this, R.style.AddressDialog, listNames, OnlineMartApplication.getCartList().get(position).getId());
                                wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                wishListDialog.show();
                            }else{
                                final WishListDialog wishListDialog = new WishListDialog(ReviewCartActivity.this, R.style.AddressDialog, listNames, OnlineMartApplication.getCartList().get(position).getId());
                                wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                wishListDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        final WishListDialog wishListDialog = new WishListDialog(ReviewCartActivity.this, R.style.AddressDialog, listNames, OnlineMartApplication.getCartList().get(position).getId());
                        wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                        wishListDialog.show();
                    }
                } else {
                    AppUtil.requestLogin(ReviewCartActivity.this);
                }
            }

            @Override
            public void onAddedToWishList(int position) {

            }
        });

        cartListRecyclerViewAdapter.setOnQuantityChangedListener(new CartListRecyclerViewAdapter.OnQuantityChangedListener() {
            @Override
            public void onQuantityChanged(boolean isIncreased, final int position, final CartListRecyclerViewAdapter.ViewHolder holder) {

                if (isIncreased) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (OnlineMartApplication.getCartList().get(position).getSelectedQuantity() < OnlineMartApplication.getCartList().get(position).getMaxQuantity()) {
                                OnlineMartApplication.getCartList().get(position).setSelectedQuantity(OnlineMartApplication.getCartList().get(position).getSelectedQuantity() + 1);
                            }else{
                                AppUtil.showErrorDialog(ReviewCartActivity.this, String.format(getString(R.string.max_quantity_reached_msg), OnlineMartApplication.getCartList().get(position).getMaxQuantity() + ""));
                            }

                            cartListRecyclerViewAdapter.updateUI(holder, position);

                            updateCartHeader();
                            OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                        }
                    });
                } else {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            OnlineMartApplication.getCartList().get(position).setSelectedQuantity(OnlineMartApplication.getCartList().get(position).getSelectedQuantity() - 1);

                            if (OnlineMartApplication.getCartList().get(position).getSelectedQuantity() <= 0) {
                                OnlineMartApplication.getCartList().remove(position);
                                cartListRecyclerViewAdapter.notifyDataSetChanged();
                            }else{
                                cartListRecyclerViewAdapter.updateUI(holder, position);
                            }

                            updateCartHeader();
                            OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                        }
                    });
                }
            }
        });

        updateCartHeader();

        if (OnlineMartApplication.getCartList() != null && OnlineMartApplication.getCartList().size() > 0) {
            String ids = AppConstants.BLANK_STRING;
            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                if (i == OnlineMartApplication.getCartList().size() - 1) {
                    ids = ids + OnlineMartApplication.getCartList().get(i).getId();
                } else {
                    ids = ids + OnlineMartApplication.getCartList().get(i).getId() + ",";
                }
            }
            //updateCartProducts(ids);
        }
    }

    private void updateCartHeader() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                if(OnlineMartApplication.getCartList().size() == 0) {
                    cart_lyt_header.setVisibility(View.GONE);
                    recyl_cart.setVisibility(View.GONE);
                    no_itme_txt.setVisibility(View.VISIBLE);
                    cardItem_layout.setVisibility(View.GONE);
                    OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);

                }else{
                    no_itme_txt.setVisibility(View.GONE);
                    cardItem_layout.setVisibility(View.VISIBLE);

                    cart_lyt_header.setVisibility(View.VISIBLE);
                    recyl_cart.setVisibility(View.VISIBLE);

                    subtotal = 0.0f; savings = 0.0f; tax_charges = 0.0f; total_amont = 0.0f; deliveryCharge = 0.0f;
                    if (OnlineMartApplication.getCartList() != null && OnlineMartApplication.getCartList().size() > 0) {
                        for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {

                            CartItem cartItem = OnlineMartApplication.getCartList().get(i);

                            if (!ValidationUtil.isNullOrBlank(cartItem.getActualPrice())) {
                                subtotal = subtotal + (Float.parseFloat(cartItem.getActualPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                            }

                            if (cartItem.isOffer()) {
                                savings = savings + (Float.parseFloat(cartItem.getSavedPrice().replace(",", AppConstants.BLANK_STRING)) * cartItem.getSelectedQuantity());
                            }
                            //tax_charges = tax_charges + (cartItem.getTaxCharges() * cartItem.getSelectedQuantity());
                            total_amont = subtotal + tax_charges;

                        }
                    }

                    int shippingHours = 0;
                    boolean isShippingThirdParty = false;
                    if (OnlineMartApplication.getCartList() != null && OnlineMartApplication.getCartList().size() > 0) {

                        boolean isDoorStepFinal = false;
                        for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {

                            if(OnlineMartApplication.getCartList().get(i).isShippingThirdParty()){
                                isShippingThirdParty = true;
                            }

                            if (OnlineMartApplication.getCartList().get(i).getShippingHours() > shippingHours) {
                                shippingHours = OnlineMartApplication.getCartList().get(i).getShippingHours();
                            }

                            if (!OnlineMartApplication.getCartList().get(i).isDoorStepDelivery()) {
                                isDoorStepDeliveryAvaliable = false;
                                isDoorStepFinal = true;
                            }else{
                                if(!isDoorStepFinal){
                                    isDoorStepDeliveryAvaliable = true;
                                }
                            }
                        }

                        ReviewCartActivity.this.isShippingThirdParty = isShippingThirdParty;
                    }else{
                        ReviewCartActivity.this.isShippingThirdParty = false;
                        //getOrder().setShippinghirdParty(false);
                    }

                    ReviewCartActivity.this.shipping_hours = shippingHours;
                    //getOrder().setOrderShippingHours(shippingHours);

                    cart_total_items.setText(OnlineMartApplication.getCartList().size() + AppConstants.BLANK_STRING);

                    if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                        cart_txt_subtotal.setText(getString(R.string.egp)+ AppConstants.SPACE +AppUtil.mSetRoundUpPrice(""+subtotal));
                        cart_txt_total_savings.setText(getString(R.string.egp)+ AppConstants.SPACE +AppUtil.mSetRoundUpPrice(""+savings));
                        cart_txt_delivery_fee.setText(getString(R.string.egp)+ AppConstants.SPACE +AppUtil.mSetRoundUpPrice(""+0.00));
                        cart_txt_total_amount.setText(getString(R.string.egp)+ AppConstants.SPACE +AppUtil.mSetRoundUpPrice(""+total_amont));
                    }else{
                        cart_txt_subtotal.setText(AppUtil.mSetRoundUpPrice(""+subtotal) + AppConstants.SPACE + getString(R.string.egp));
                        cart_txt_total_savings.setText(AppUtil.mSetRoundUpPrice(""+savings) + AppConstants.SPACE + getString(R.string.egp));
                        cart_txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice(""+0.00) + AppConstants.SPACE + getString(R.string.egp));
                        cart_txt_total_amount.setText(AppUtil.mSetRoundUpPrice(""+total_amont) + AppConstants.SPACE + getString(R.string.egp));
                    }

                    OnlineMartApplication.mLocalStore.saveCartTotal(Math.round(subtotal));

                    if (total_amont != 0.00) {
                        updateShippingCharges(total_amont);
                    }
                }
            }
        });
    }

    public float getDeliveryCharges(float subtotal){

        if(list_charges != null){
            if(list_charges.size() > 0){
                for(int i=0; i<list_charges.size(); i++){
                    if(subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && list_charges.get(i).getType().equalsIgnoreCase(delivery_type)){
                        return list_charges.get(i).getCharges();
                    }
                }
                return 0.0f;
            }else{
                if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())){
                    try {
                        JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                        if(chargesArray != null){
                            list_charges.clear();
                            for(int i=0; i<chargesArray.length(); i++){
                                JSONObject object = chargesArray.optJSONObject(i);
                                if(object != null){

                                    DeliveryCharges deliveryCharges = new DeliveryCharges();
                                    deliveryCharges.setId(object.optString("id"));
                                    deliveryCharges.setType(object.optString("type"));
                                    deliveryCharges.setStart_amount((float)(object.optDouble("start_amt", 0.0)));
                                    deliveryCharges.setEnd_amount((float)(object.optDouble("end_amt", 0.0)));
                                    deliveryCharges.setCharges((float)(object.optDouble("d_charge", 0.0)));
                                    list_charges.add(deliveryCharges);
                                }
                            }

                            if(list_charges != null){
                                for(int i=0; i<list_charges.size(); i++){
                                    if(subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && list_charges.get(i).getType().equalsIgnoreCase(delivery_type)){
                                        return list_charges.get(i).getCharges();
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{

                }
            }
        }else{
            if(!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())){
                try {
                    JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                    if(chargesArray != null){
                        list_charges.clear();
                        for(int i=0; i<chargesArray.length(); i++){
                            JSONObject object = chargesArray.optJSONObject(i);
                            if(object != null){

                                DeliveryCharges deliveryCharges = new DeliveryCharges();
                                deliveryCharges.setId(object.optString("id"));
                                deliveryCharges.setType(object.optString("type"));
                                deliveryCharges.setStart_amount((float)(object.optDouble("start_amt", 0.0)));
                                deliveryCharges.setEnd_amount((float)(object.optDouble("end_amt", 0.0)));
                                deliveryCharges.setCharges((float)(object.optDouble("d_charge", 0.0)));
                                list_charges.add(deliveryCharges);
                            }
                        }

                        if(list_charges != null){
                            for(int i=0; i<list_charges.size(); i++){
                                if(subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && list_charges.get(i).getType().equalsIgnoreCase(delivery_type)){
                                    return list_charges.get(i).getCharges();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{

            }
        }
        return 0.0f;
    }

    private void updateShippingCharges(final float total_amount) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                deliveryCharge = getDeliveryCharges(subtotal);
                total = (float) (total_amount + deliveryCharge);

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    cart_txt_delivery_fee.setText(getString(R.string.egp) + AppConstants.SPACE +AppUtil.mSetRoundUpPrice(""+deliveryCharge));
                    cart_txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE +AppUtil.mSetRoundUpPrice(""+total));
                }else{
                    cart_txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice(""+deliveryCharge) + AppConstants.SPACE + getString(R.string.egp));
                    cart_txt_total_amount.setText(AppUtil.mSetRoundUpPrice(""+total) + AppConstants.SPACE + getString(R.string.egp));
                }

                isLoading = false;
            }
        });
    }

    private void updateCartProducts(final String ids) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject request_data = new JSONObject();
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("product_id", ids);
                    request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                    request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                    if (NetworkUtil.networkStatus(mContext)) {
                        isCartUpdating = false;
                        try {
                            AppUtil.showProgress(mContext);
                            Call<ResponseBody> call = apiService.updateCartData((new ConvertJsonToMap().jsonToMap(request_data)));
                            APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                                //call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    AppUtil.hideProgress();
                                    isCartUpdating = true;
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

                                                            listUpdateCart.clear();
                                                            for (int i = 0; i < products_arr.length(); i++) {

                                                                JSONObject jsonObject = products_arr.getJSONObject(i);
                                                                if (jsonObject != null) {
                                                                    Product product = new Product();

                                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("product_id"))) {
                                                                        product.setId(jsonObject.optString("product_id"));
                                                                    }

                                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optString("image"))) {
                                                                        product.setImage(jsonObject.optString("image"));
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

                                                                    JSONArray array_images = jsonObject.optJSONArray("File");
                                                                    if (array_images != null && array_images.length() > 0) {
                                                                        ArrayList<String> list_images = new ArrayList<>();
                                                                        for (int j = 0; j < array_images.length(); j++) {
                                                                            JSONObject imageObj = array_images.getJSONObject(j);
                                                                            if (imageObj != null) {
                                                                                if (!ValidationUtil.isNullOrBlank(imageObj.optString("image"))) {

                                                                                    if (j == 0) {
                                                                                        product.setImage(imageObj.optString("image"));
                                                                                    }

                                                                                    list_images.add(imageObj.optString("image"));
                                                                                }
                                                                            }
                                                                        }

                                                                        product.setListImages(list_images);
                                                                    }

                                                                    if (product.getMaxQuantity() <= 0) {
                                                                        product.setSoldOut(true);
                                                                    } else {
                                                                        product.setSoldOut(false);
                                                                    }

                                                                    listUpdateCart.add(AppUtil.getCartObject(product));
                                                                }
                                                            }

                                                            String notifyMsg = getString(R.string.pls_note_the_following);
                                                            boolean isRemoved = false;
                                                            outer : for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                                                inner : for (int j = 0; j < listUpdateCart.size(); j++) {
                                                                    if (listUpdateCart.get(j).getId().equalsIgnoreCase(OnlineMartApplication.getCartList().get(i).getId())) {
                                                                        if (listUpdateCart.get(j).getMaxQuantity() == 0) {
                                                                            notifyMsg = notifyMsg + "\n- " + String.format(getString(R.string.noti_out_of_stock_new), listUpdateCart.get(j).getLabel());

                                                                            OnlineMartApplication.removeProductFromCart(listUpdateCart.get(j).getId());
                                                                            isRemoved = true;
                                                                            listUpdateCart.remove(j);
                                                                        } else {
                                                                            if (OnlineMartApplication.getCartList().get(i).getSelectedQuantity() > listUpdateCart.get(j).getMaxQuantity()) {
                                                                                notifyMsg = notifyMsg + "\n- " + String.format(getString(R.string.max_quantity_reached_msg_new), new String[]{OnlineMartApplication.getCartList().get(i).getLabel()});

                                                                                listUpdateCart.get(j).setSelectedQuantity(listUpdateCart.get(j).getMaxQuantity());
                                                                            } else {
                                                                                listUpdateCart.get(j).setSelectedQuantity(OnlineMartApplication.getCartList().get(i).getSelectedQuantity());
                                                                            }
                                                                        }

                                                                        if (!isRemoved) {
                                                                            if (listUpdateCart.size() > j) {

                                                                                String new_product_price = ((listUpdateCart.get(j).isOffer()) ? listUpdateCart.get(j).getOfferedPrice() : listUpdateCart.get(j).getActualPrice()).replace(",", AppConstants.BLANK_STRING);
                                                                                if (!(new_product_price.equalsIgnoreCase(OnlineMartApplication.getCartList().get(i).getActualPrice()))) {
                                                                                    notifyMsg = notifyMsg + "\n- " + String.format(getString(R.string.price_has_updated), listUpdateCart.get(j).getLabel());
                                                                                }
                                                                            }
                                                                        }

                                                                        break inner;
                                                                    }
                                                                }
                                                            }

                                                            OnlineMartApplication.getCartList().clear();
                                                            OnlineMartApplication.getCartList().addAll(listUpdateCart);
                                                            OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                                                            listUpdateCart.clear();

                                                            if (cartListRecyclerViewAdapter != null) {
                                                                cartListRecyclerViewAdapter.notifyDataSetChanged();
                                                            }
                                                            updateCartHeader();

                                                            if(!notifyMsg.equalsIgnoreCase(getString(R.string.pls_note_the_following))){

                                                                if(OnlineMartApplication.getCartList().size() > 0){
                                                                    final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(mContext, getString(R.string.app_name), notifyMsg, getString(R.string.cancel), getString(R.string.done_caps));
                                                                    appDialogDoubleAction.setCancelable(false);
                                                                    appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                                                    appDialogDoubleAction.setStartAligned(true);
                                                                    appDialogDoubleAction.show();
                                                                    appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                                        @Override
                                                                        public void onLeftActionClick(View view) {
                                                                            appDialogDoubleAction.dismiss();
                                                                        }

                                                                        @Override
                                                                        public void onRightActionClick(View view) {
                                                                            appDialogDoubleAction.dismiss();

                                                                            Intent intent = new Intent();
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE, deliveryCharge);
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS, OnlineMartApplication.getCartList().size());
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT, total);
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_SUBTOTAL, subtotal);
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SHIPPING_HOURS, shipping_hours);
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_IS_DOOR_STEP_DELIVERY_AVAILABLE, isDoorStepDeliveryAvaliable);
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_IS_SHIPPING_TO_THIRD_PARTY, isShippingThirdParty);
                                                                            intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS, savings);

                                                                            setResult(Activity.RESULT_OK, intent);
                                                                            finish();
                                                                        }
                                                                    });
                                                                }else{
                                                                    final AppDialogSingleAction appDialogDoubleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), notifyMsg, getString(R.string.ok));
                                                                    appDialogDoubleAction.setCancelable(false);
                                                                    appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                                                    appDialogDoubleAction.setStartAligned(true);
                                                                    appDialogDoubleAction.show();
                                                                    appDialogDoubleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                        @Override
                                                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                            appDialogDoubleAction.dismiss();
                                                                        }
                                                                    });
                                                                }


                                                            }else{
                                                                Intent intent = new Intent();
                                                                intent.putExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE, deliveryCharge);
                                                                intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS, OnlineMartApplication.getCartList().size());
                                                                intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT, total);
                                                                intent.putExtra(CheckOutProcessActivity.KEY_SUBTOTAL, subtotal);
                                                                intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SHIPPING_HOURS, shipping_hours);
                                                                intent.putExtra(CheckOutProcessActivity.KEY_IS_DOOR_STEP_DELIVERY_AVAILABLE, isDoorStepDeliveryAvaliable);
                                                                intent.putExtra(CheckOutProcessActivity.KEY_IS_SHIPPING_TO_THIRD_PARTY, isShippingThirdParty);
                                                                intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS, savings);

                                                                setResult(Activity.RESULT_OK, intent);
                                                                finish();
                                                            }

                                                        }

                                                    } else {
                                                        AppUtil.showErrorDialog(mContext, responseMessage);
                                                    }

                                                } else {
                                                }

                                            } else {
                                            }

                                        } catch (Exception e) {//(JSONException | IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        isCartUpdating = true;
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    AppUtil.hideProgress();
                                    isCartUpdating = true;
                                }
                            });
                        } catch (JSONException e) {
                            AppUtil.hideProgress();
                            e.printStackTrace();
                            isCartUpdating = true;
                        }

                    } else {
                        isCartUpdating = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCartUpdating = true;
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
