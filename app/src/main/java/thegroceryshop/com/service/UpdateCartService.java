package thegroceryshop.com.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class UpdateCartService extends IntentService {

    private static final String ACTION_UPDATE = "services.action.updatecatrt";
    private ApiInterface apiService;

    public UpdateCartService() {
        super("UpdateCartService");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startUpdateCartService(Context context) {

        Intent intent = new Intent(context, UpdateCartService.class);
        intent.setAction(ACTION_UPDATE);
        if(!OnlineMartApplication.isApplicationInBackground){
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            apiService = ApiClient.createService(ApiInterface.class, this);
            final String action = intent.getAction();
            if (ACTION_UPDATE.equalsIgnoreCase(action)) {
                if(!OnlineMartApplication.mLocalStore.isCartUpdated())
                    loadCartInformation();
                else
                    stopSelf();
            }else
                stopSelf();
        }else
            stopSelf();
    }

    public void loadCartInformation(){

        final ArrayList<CartItem> list_cart = getShoppingCartList();

        if (list_cart.size() > 0) {

            String ids = AppConstants.BLANK_STRING;
            for (int i = 0; i < list_cart.size(); i++) {
                if (i == OnlineMartApplication.getCartList().size() - 1) {
                    ids = ids + list_cart.get(i).getId();
                } else {
                    ids = ids + list_cart.get(i).getId() + ",";
                }
            }

            try {
                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("product_id", ids);
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                final ArrayList<Product> list_cart_products = new ArrayList<>();
                if (NetworkUtil.networkStatus(UpdateCartService.this)) {
                    try {
                        ApiInterface apiService = ApiClient.createService(ApiInterface.class, UpdateCartService.this);
                        Call<ResponseBody> call = apiService.updateCartData((new ConvertJsonToMap().jsonToMap(request_data)));
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

                                                                product.setAddedToWishList(jsonObject.optBoolean("in_wishlist", false));

                                                                if (!ValidationUtil.isNullOrBlank(jsonObject.optString("arabic_product_size"))) {
                                                                    product.setArabicQuantity(jsonObject.optString("arabic_product_size"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(jsonObject.optString("english_brand_name"))) {
                                                                    product.setEnglishBrandName(jsonObject.optString("english_brand_name"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(jsonObject.optString("arabic_brand_name"))) {
                                                                    product.setArabicBrandName(jsonObject.optString("arabic_brand_name"));
                                                                }

                                                                if (!ValidationUtil.isNullOrBlank(jsonObject.optString("currency"))) {
                                                                    product.setCurrency(jsonObject.optString("currency"));
                                                                }

                                                                list_cart_products.add(product);
                                                            }
                                                        }

                                                        ArrayList<CartItem> newList = new ArrayList<>();
                                                        if(list_cart_products.size() > 0){

                                                            for(int i=0; i<list_cart_products.size(); i++){
                                                                inner : for(int j=0; j<list_cart.size(); j++){
                                                                    if(list_cart_products.get(i).getId().equalsIgnoreCase(list_cart.get(j).getId())){
                                                                        CartItem cartItem = list_cart.get(j);
                                                                        cartItem.setEnglishLabel(list_cart_products.get(i).getEnglishLabel());
                                                                        cartItem.setArabicLabel(list_cart_products.get(i).getArabicLabel());
                                                                        cartItem.setEnglishBrandName(list_cart_products.get(i).getEnglishBrandName());
                                                                        cartItem.setArabicBrandName(list_cart_products.get(i).getArabicBrandName());
                                                                        cartItem.setEnglishQuantity(list_cart_products.get(i).getEnglishQuantity());
                                                                        cartItem.setArabicQuantity(list_cart_products.get(i).getArabicQuantity());
                                                                        newList.add(cartItem);
                                                                        break inner;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        if(newList.size() > 0){
                                                            OnlineMartApplication.updateCartData(newList);
                                                            OnlineMartApplication.loadCartList();
                                                            OnlineMartApplication.mLocalStore.setCartUpdated(true);
                                                        }
                                                    }

                                                    stopSelf();

                                                } else {
                                                    //AppUtil.showErrorDialog(mContext, responseMessage);
                                                }

                                            } else {
                                                //AppUtil.showErrorDialog(mContext, errorMsg);
                                            }

                                        } else {
                                            //AppUtil.showErrorDialog(mContext, getString(R.string.some_error_occoured));
                                        }

                                    } catch (Exception e) {//(JSONException | IOException e) {

                                        e.printStackTrace();
                                        //AppUtil.showErrorDialog(mContext, response.message());
                                    }
                                } else {
                                    //AppUtil.showErrorDialog(mContext, response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                AppUtil.hideProgress();
                                //loader_root.setStatuText(getString(R.string.no_product_found));
                                //loader_root.showStatusText();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppUtil.hideProgress();
                    }

                } else {
                    //Snackbar.make(term_condtn_checkBox, getResources().getString(R.string.error_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.try_again), networkCallBack1).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            stopSelf();
        }

    }

    private ArrayList<CartItem> getShoppingCartList() {

        ArrayList<CartItem> list_cart = new ArrayList<>();
        try {
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            if(cartArray.length() != 0){
                list_cart.clear();
                for(int  i=0; i<cartArray.length(); i++){

                    JSONObject cartObj = cartArray.getJSONObject(i);

                    CartItem cartItem = new CartItem();
                    cartItem.setId(cartObj.optString("product_id"));
                    cartItem.setImage(cartObj.optString("image"));
                    cartItem.setEnglishLabel(cartObj.optString("name"));
                    cartItem.setEnglishBrandName(cartObj.optString("brand"));
                    cartItem.setEnglishQuantity(cartObj.optString("product_size"));
                    cartItem.setActualPrice(cartObj.optString("price"));
                    cartItem.setMaxQuantity(cartObj.optInt("qty"));
                    cartItem.setSavedPrice(cartObj.optString("saved"));
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));

                    if(!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))){
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    }else{
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    list_cart.add(cartItem);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return list_cart;
        }

        return list_cart;
    }
}