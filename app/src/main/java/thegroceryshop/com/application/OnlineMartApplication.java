package thegroceryshop.com.application;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;

import com.newrelic.agent.android.NewRelic;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.activity.HomeActivity;
import thegroceryshop.com.activity.SplashActivity;
import thegroceryshop.com.checkoutProcess.CheckOutProcessActivity;
import thegroceryshop.com.checkoutProcess.checkoutfragment.TimeSlotService;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.loginModule.VerifyUserFragment;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Region;
import thegroceryshop.com.modal.sildemenu_model.NestedCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.PromotionCategoryItem;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/*
 * Created by rohitg on 12/5/2016.
 */
public class OnlineMartApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public static ApiLocalStore mLocalStore;
    private static OnlineMartApplication onlineMartApplication;
    public static boolean isLiveUrl = false;
    //public static boolean isLiveUrl = true;
    public static int top;
    private static SharedPreferences sharedPref_respond;
    public static List<NestedCategoryItem> nestedCategory = new ArrayList<>();
    public static List<PromotionCategoryItem> promotionCategory = new ArrayList<>();
    public static List<NestedCategoryItem> home_nestedCategory = new ArrayList<>();
    public static List<PromotionCategoryItem> home_promotionCategory = new ArrayList<>();
    public static Parcelable state;

    private static ArrayList<CartItem> list_cart = new ArrayList<>();
    private static Context context;
    public static boolean isApplicationInBackground;
    private Locale locale;
    public static boolean isSplashRunning = false;
    private static int retryCount = 0;
    private static ArrayList<Activity> list_activities = new ArrayList<>();
    private static ArrayList<Region> list_regions = new ArrayList<>();


    /**
     * @return OnlineMartApplication Single Instance
     */
    public static OnlineMartApplication getInstance() {
        return onlineMartApplication;
    }

    public static void openRegionPicker(final Activity activity, String errorMsg) {

        OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
        OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);

        OnlineMartApplication.getCartList().clear();

        OnlineMartApplication.mLocalStore.setSelectedRegion(null);
        OnlineMartApplication.promotionCategory.clear();
        OnlineMartApplication.nestedCategory.clear();

        AppUtil.showErrorDialogWithListener(activity, errorMsg, new OnSingleActionListener() {
            @Override
            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                activity.startActivity(intent);
                activity.finish();
            }
        });


    }

    @Override
    public void onCreate() {
        super.onCreate();

        onlineMartApplication = this;
        if (mLocalStore == null)
            mLocalStore = ApiLocalStore.getInstance(this);

        context = getApplicationContext();
        //NewRelic.withApplicationToken("AAe1183ad3e6954e450f2259ae3b5531c9d450d985").start(this.getApplicationContext());
        NewRelic.withApplicationToken("AA8dd4a326797feded9872a830dc9d1ebd06a77b40").start(this);

        registerActivityLifecycleCallbacks(this);

        initSharedPreferences();
        getintiImageLoaderConfig(getApplicationContext());
        JodaTimeAndroid.init(this);

        mLocalStore.saveAppRunCounter(mLocalStore.getAppRunCounter() + 1);

    }

    @Override
    protected void attachBaseContext(Context base) {
        if (mLocalStore == null)
            mLocalStore = ApiLocalStore.getInstance(base);
        super.attachBaseContext(LocaleHelper.onAttach(base, OnlineMartApplication.mLocalStore.getAppLanguage()));
        MultiDex.install(this);
    }

    /**
     * INITIALIZATION SharedPreferences
     */

    private void initSharedPreferences() {
        try {
            sharedPref_respond = getApplicationContext().getSharedPreferences("app_local_prefs", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to get instance globally of SharedPreferences
     */

    public static synchronized SharedPreferences getSharedPreferences() {
        return sharedPref_respond;
    }

    public void getintiImageLoaderConfig(Context context) {

//        DisplayImageOptions defaultOptions = intitOptions();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        //config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.FIFO);
        config.threadPriority(Thread.MAX_PRIORITY);
        config.threadPoolSize(8);
        config.imageDownloader(new BaseImageDownloader(context){

            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                HttpURLConnection conn = (HttpURLConnection) (new URL(imageUri)).openConnection();
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);

                if (conn instanceof HttpsURLConnection) {
                    ((HttpsURLConnection)conn).setSSLSocketFactory(ApiClient.getSSLSocketFactory1());
                }

                return new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);

            }
        });

        ImageLoader.getInstance().init(config.build());
    }

    public static DisplayImageOptions intitOptions() {

        return new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    public static DisplayImageOptions intitOptions1() {

        //.displayer(new FadeInBitmapDisplayer(300))
        return new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.mipmap.no_image)
                .showImageForEmptyUri(R.mipmap.no_image).build();
    }

    public static DisplayImageOptions intitOptions2() {

        return new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.mipmap.place_holder)
                .showImageForEmptyUri(R.mipmap.place_holder)
                .showImageOnLoading(R.mipmap.place_holder)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    public static DisplayImageOptions intitOptions3() {

        return new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.img_bottle)
                .showImageForEmptyUri(R.drawable.img_bottle)
                .showImageOnLoading(R.drawable.img_bottle)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    public static void addToCart(CartItem cartItem) {

        try {
            JSONObject cartObj = new JSONObject();
            cartObj.put("product_id", cartItem.getId());
            cartObj.put("image", cartItem.getImage());
            cartObj.put("englishName", cartItem.getEnglishLabel());
            cartObj.put("arabicName", cartItem.getArabiclabel());
            cartObj.put("englishBrandName", cartItem.getEnglishbrandName());
            cartObj.put("arabicBrandName", cartItem.getArabicbrandName());
            cartObj.put("english_product_size", cartItem.getEnglishquantity());
            cartObj.put("arabic_product_size", cartItem.getArabicquantity());
            cartObj.put("price", cartItem.getActualPrice().replace(",", ""));
            /*if (cartItem.isOffer()) {
                cartObj.put("price", cartItem.getOfferedPrice());
            } else {
                cartObj.put("price", cartItem.getActualPrice().replace(",",""));
            }*/
            cartObj.put("saved", cartItem.getSavedPrice());
            cartObj.put("tax_charges", cartItem.getTaxCharges());
            cartObj.put("max_qty", cartItem.getMaxQuantity());
            cartObj.put("mark_sold_out", cartItem.getMarkSoldQuantity());
            cartObj.put("is_sold_out", cartItem.isSoldOut());
            cartObj.put("is_door_step_delivery", cartItem.isDoorStepDelivery());
            cartObj.put("currency", cartItem.getCurrency());
            cartObj.put("isOffer", cartItem.isOffer());
            cartObj.put("selected_qty", cartItem.getSelectedQuantity());
            cartObj.put("shipping_hours", cartItem.getShippingHours());
            cartObj.put("shipping_third_party", cartItem.isShippingThirdParty());
            cartObj.put("isAddedToWishList", cartItem.isAddedToWishList());

            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());

            boolean isAlreadyAdded = false;
            for (int i = 0; i < cartArray.length(); i++) {
                if (cartItem.getId().equalsIgnoreCase(((JSONObject) cartArray.get(i)).optString("product_id"))) {
                    increaseORDecreaseQtyOnCart(true, cartItem.getId());
                    isAlreadyAdded = true;
                    break;
                }
            }

            if (!isAlreadyAdded) {
                cartArray.put(cartArray.length(), cartObj);
                OnlineMartApplication.mLocalStore.saveCartData(cartArray.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void increaseORDecreaseQtyOnCart(boolean isIncrease, String id) {

        //ArrayList<CartItem> list_cart = new ArrayList<>();
        try {
            JSONArray newCartArray = new JSONArray();
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            //list_cart.clear();
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
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));
                    cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));

                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    } else {
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    //cartItem.setShippingHours(cartObj.optInt("shipping_hours"));

                    if (cartItem.getId().equalsIgnoreCase(id)) {
                        if (isIncrease) {
                            cartItem.setSelectedQuantity(cartItem.getSelectedQuantity() + 1);
                        } else {
                            cartItem.setSelectedQuantity(cartItem.getSelectedQuantity() - 1);
                        }
                    }

                    if (cartItem.getSelectedQuantity() <= 0) {
                        //list_cart.remove(i);
                    } else {
                        cartObj.put("selected_qty", cartItem.getSelectedQuantity());
                        newCartArray.put(cartObj);
                        //list_cart.add(cartItem);

                    }
                }
                OnlineMartApplication.mLocalStore.saveCartData(newCartArray.toString());
                loadCartList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateQuantityOnCart(String id, int quantity) {

        //ArrayList<CartItem> list_cart = new ArrayList<>();
        try {
            JSONArray newCartArray = new JSONArray();
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            //list_cart.clear();
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
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));
                    cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));

                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    } else {
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    //cartItem.setShippingHours(cartObj.optInt("shipping_hours"));

                    if (cartItem.getId().equalsIgnoreCase(id)) {
                        cartItem.setSelectedQuantity(quantity);
                    }

                    if (cartItem.getSelectedQuantity() <= 0) {
                        //list_cart.remove(i);
                    } else {
                        cartObj.put("selected_qty", cartItem.getSelectedQuantity());
                        newCartArray.put(cartObj);
                        //list_cart.add(cartItem);

                    }
                }
                OnlineMartApplication.mLocalStore.saveCartData(newCartArray.toString());
                loadCartList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateWishlistFlagOnCart(String id, boolean isAddedToWishlist) {

        //ArrayList<CartItem> list_cart = new ArrayList<>();
        try {
            JSONArray newCartArray = new JSONArray();
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            //list_cart.clear();
            if (cartArray.length() != 0) {
                for (int i = 0; i < cartArray.length(); i++) {

                    JSONObject cartObj = cartArray.getJSONObject(i);

                    CartItem cartItem = new CartItem();
                    cartItem.setId(cartObj.optString("product_id"));

                    if (cartItem.getId().equalsIgnoreCase(id)) {
                        cartItem.setAddedToWishList(isAddedToWishlist);
                        cartObj.put("isAddedToWishList", isAddedToWishlist);
                    }

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
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));

                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    } else {
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    newCartArray.put(cartObj);

                }
                OnlineMartApplication.mLocalStore.saveCartData(newCartArray.toString());
                loadCartList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void loadCreditsInWalltet() {

        if (retryCount <= 5) {
            try {

                final JSONObject request = new JSONObject();
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                Call<ResponseBody> call = apiService.getCredits((new ConvertJsonToMap().jsonToMap(request)));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                JSONObject resObj = obj.optJSONObject("response");
                                if (resObj.length() > 0) {

                                    int statusCode = resObj.optInt("status_code", 0);
                                    String status = resObj.optString("status");
                                    if (statusCode == 200 && status != null && status.equalsIgnoreCase("OK")) {

                                        JSONObject dataObj = resObj.optJSONObject("data");
                                        if (dataObj != null) {
                                            String amount = dataObj.optString("user_credit_points");
                                            OnlineMartApplication.mLocalStore.saveUserCredit(Float.parseFloat(amount));
                                        } else {
                                            retryCount++;
                                            loadCreditsInWalltet();
                                        }

                                    } else {
                                        retryCount++;
                                        loadCreditsInWalltet();
                                    }

                                } else {
                                    retryCount++;
                                    loadCreditsInWalltet();
                                }

                            } catch (Exception e) {
                                retryCount++;
                                loadCreditsInWalltet();
                                e.printStackTrace();
                                //AppUtil.showErrorDialog(mContext, response.message());
                            }
                        } else {
                            retryCount++;
                            loadCreditsInWalltet();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        retryCount++;
                        loadCreditsInWalltet();
                    }
                });

            } catch (JSONException e) {
                retryCount++;
                loadCreditsInWalltet();
            }
        }
    }

    public static void updateQuantityAndrPriceOnCart(String id, int quantity, String price) {

        //ArrayList<CartItem> list_cart = new ArrayList<>();
        try {
            JSONArray newCartArray = new JSONArray();
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            //list_cart.clear();
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
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));
                    cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));

                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    } else {
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    //cartItem.setShippingHours(cartObj.optInt("shipping_hours"));

                    if (cartItem.getId().equalsIgnoreCase(id)) {
                        cartItem.setSelectedQuantity(quantity);
                        cartItem.setActualPrice(price);
                    }

                    if (cartItem.getSelectedQuantity() <= 0) {
                        //list_cart.remove(i);
                    } else {
                        cartObj.put("selected_qty", cartItem.getSelectedQuantity());
                        cartObj.put("price", cartItem.getActualPrice());
                        newCartArray.put(cartObj);
                        //list_cart.add(cartItem);

                    }
                }
                OnlineMartApplication.mLocalStore.saveCartData(newCartArray.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeProductFromCart(String id) {

        try {
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            //list_cart.clear();
            ArrayList<CartItem> list_cart = new ArrayList<>();
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
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));
                    cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));

                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    } else {
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    //cartItem.setShippingHours(cartObj.optInt("shipping_hours"));

                    if (!cartItem.getId().equalsIgnoreCase(id)) {
                        list_cart.add(cartItem);
                    }
                }
                updateCartData(list_cart);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateCartData(final ArrayList<CartItem> list_cart) {

        if (list_cart != null) {
            try {
                OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
                JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
                for (int i = 0; i < list_cart.size(); i++) {
                    CartItem cartItem = list_cart.get(i);

                    JSONObject cartObj = new JSONObject();
                    cartObj.put("product_id", cartItem.getId());
                    cartObj.put("image", cartItem.getImage());
                    cartObj.put("englishName", cartItem.getEnglishLabel());
                    cartObj.put("arabicName", cartItem.getArabiclabel());
                    cartObj.put("englishBrandName", cartItem.getEnglishbrandName());
                    cartObj.put("arabicBrandName", cartItem.getArabicbrandName());
                    cartObj.put("english_product_size", cartItem.getEnglishquantity());
                    cartObj.put("arabic_product_size", cartItem.getArabicquantity());
                    cartObj.put("price", cartItem.getActualPrice().replace(",", ""));
                    /*if (cartItem.isOffer()) {
                        cartObj.put("price", cartItem.getOfferedPrice());
                    } else {
                        cartObj.put("price", cartItem.getActualPrice().replace(",",""));
                    }*/
                    cartObj.put("saved", cartItem.getSavedPrice());
                    cartObj.put("tax_charges", cartItem.getTaxCharges());
                    cartObj.put("max_qty", cartItem.getMaxQuantity());
                    cartObj.put("mark_sold_out", cartItem.getMarkSoldQuantity());
                    cartObj.put("is_sold_out", cartItem.isSoldOut());
                    cartObj.put("is_door_step_delivery", cartItem.isDoorStepDelivery());
                    cartObj.put("currency", cartItem.getCurrency());
                    cartObj.put("isOffer", cartItem.isOffer());
                    cartObj.put("selected_qty", cartItem.getSelectedQuantity());
                    cartObj.put("shipping_hours", cartItem.getShippingHours());
                    cartObj.put("shipping_third_party", cartItem.isShippingThirdParty());
                    cartObj.put("isAddedToWishList", cartItem.isAddedToWishList());
                    cartArray.put(cartArray.length(), cartObj);
                }
                OnlineMartApplication.mLocalStore.saveCartData(cartArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isProductInCart(String product_id) {

        boolean isExists = false;
        try {
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());

            if (cartArray.length() != 0) {
                for (int i = 0; i < cartArray.length(); i++) {
                    JSONObject cartObj = cartArray.getJSONObject(i);
                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("product_id"))) {
                        if (cartObj.optString("product_id").equalsIgnoreCase(product_id)) {
                            isExists = true;
                            break;
                        }
                    }
                }
                return isExists;
            }
            return isExists;
        } catch (JSONException e) {
            e.printStackTrace();
            return isExists;
        }
    }

    public static CartItem checkProductInCart(String product_id) {

        try {
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            if (cartArray.length() != 0) {
                for (int i = 0; i < cartArray.length(); i++) {
                    JSONObject cartObj = cartArray.getJSONObject(i);
                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("product_id"))) {
                        if (cartObj.optString("product_id").equalsIgnoreCase(product_id)) {
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
                            //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                            cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                            cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                            cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                            cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                            cartItem.setCurrency(cartObj.optString("currency"));
                            cartItem.setOffer(cartObj.optBoolean("isOffer"));
                            cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                            cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));
                            cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));

                            if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                                cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                            } else {
                                cartItem.setShippingHours(Integer.parseInt("0"));
                            }

                            //cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                            return cartItem;
                        }
                    }
                }
                return null;
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<CartItem> loadCartList() {

        if (list_cart != null) {
            try {
                JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
                list_cart.clear();
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
                    //cartItem.setTaxCharges((float)(cartObj.optDouble("tax_charges")));
                    cartItem.setMaxQuantity(cartObj.optInt("max_qty"));
                    cartItem.setMarkSoldQuantity(cartObj.optInt("mark_sold_out"));
                    cartItem.setSoldOut(cartObj.optBoolean("is_sold_out"));
                    cartItem.setDoorStepDelivery(cartObj.optBoolean("is_door_step_delivery"));
                    cartItem.setCurrency(cartObj.optString("currency"));
                    cartItem.setOffer(cartObj.optBoolean("isOffer"));
                    cartItem.setSelectedQuantity(cartObj.optInt("selected_qty"));
                    cartItem.setShippingThirdParty(cartObj.optBoolean("shipping_third_party"));
                    cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));

                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("shipping_hours"))) {
                        cartItem.setShippingHours(cartObj.optInt("shipping_hours"));
                    } else {
                        cartItem.setShippingHours(Integer.parseInt("0"));
                    }

                    list_cart.add(cartItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list_cart;
    }

    public static ArrayList<CartItem> getCartList() {
        return list_cart;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        list_activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isApplicationInBackground) {
            isApplicationInBackground = false;
            LoadUsualData.loadShippingCharges(this);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        list_activities.remove(activity);
    }

    public static ArrayList<Activity> getActivities() {
        return list_activities;
    }

    @Override
    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isApplicationInBackground = true;
        }
    }

    @SuppressWarnings("deprecation")
    private static Locale getSystemLocale(Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.getLocales().get(0);
        } else {
            return config.locale;
        }
    }

    @SuppressWarnings("deprecation")
    private static void setSystemLocale(Configuration config, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
    }

    @SuppressWarnings("deprecation")
    private void updateConfiguration(Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getBaseContext().createConfigurationContext(config);
        } else {
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public static void restartApplication(Activity currentActivity) {

        Intent intent = new Intent(currentActivity, SplashActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isFromNotification", false);
        currentActivity.startActivity(intent);
        currentActivity.finish();
        Runtime.getRuntime().exit(0);
    }

    public static boolean isAddedToWishlist(String product_id) {

        try {
            JSONArray cartArray = new JSONArray(OnlineMartApplication.mLocalStore.getCartData());
            if (cartArray.length() != 0) {
                for (int i = 0; i < cartArray.length(); i++) {
                    JSONObject cartObj = cartArray.getJSONObject(i);
                    if (!ValidationUtil.isNullOrBlank(cartObj.optString("product_id"))) {
                        if (cartObj.optString("product_id").equalsIgnoreCase(product_id)) {
                            return cartObj.optBoolean("isAddedToWishList", false);
                        }
                    }
                }
                return false;
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void loadUserProfile(final Context context) {
        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
            if (NetworkUtil.networkStatus(context)) {
                try {

                    JSONObject request_data = new JSONObject();
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());

                    ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                    Call<ResponseBody> call = apiService.getUserProfile(new ConvertJsonToMap().jsonToMap(request_data));

                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    JSONObject resObject = jsonObject.getJSONObject("response");
                                    if (resObject.getString("status_code").equalsIgnoreCase("200")) {

                                        JSONObject dataObj = resObject.optJSONObject("data");
                                        if (dataObj != null) {

                                            JSONObject userObj = dataObj.optJSONObject("USer");
                                            if (userObj != null) {

                                                if (!ValidationUtil.isNullOrBlank(userObj.optString("first_name"))) {
                                                    OnlineMartApplication.mLocalStore.saveFirstName(userObj.optString("first_name"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(userObj.optString("last_name"))) {
                                                    OnlineMartApplication.mLocalStore.saveLastName(userObj.optString("last_name"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(userObj.optString("email"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserEmail(userObj.optString("email"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(userObj.optString("dob")) && !userObj.optString("dob").equalsIgnoreCase("0000-00-00")) {
                                                    OnlineMartApplication.mLocalStore.saveUserDob(userObj.optString("dob"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(userObj.optString("mobile"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserPhone(userObj.optString("mobile"));
                                                }

                                                if (!ValidationUtil.isNullOrBlank(userObj.optString("image"))) {
                                                    OnlineMartApplication.mLocalStore.saveUserImage(userObj.optString("image"));
                                                }
                                            }
                                        }

                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void releaseSMSAutoReadReceiver(){
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }

    public void registerSMSAutoReadReceiver(){
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter("otp"));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("otp");
                //Do whatever you want with the code here
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(VerifyUserFragment.pinview != null){
                            VerifyUserFragment.pinview.setValue(message);
                        }else{
                            handler.postDelayed(this, 500);
                        }
                    }
                }, 1000);
            }
        }
    };

    public static ArrayList<Region> getRegions(){
        return list_regions;
    }

    public static void endCheckoutSessionIfCartEmpty(CheckOutProcessActivity checkOutProcessActivity, boolean releaseTimeslot){
        if (OnlineMartApplication.getCartList().size() == 0
                || (checkOutProcessActivity != null
                && checkOutProcessActivity.getOrder() != null
                && checkOutProcessActivity.getOrder().getList_cart() != null
                && checkOutProcessActivity.getOrder().getList_cart().size() == 0)) {

            loadCreditsInWalltet();
            OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
            OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
            OnlineMartApplication.getCartList().clear();

            if(releaseTimeslot){
                if(checkOutProcessActivity.getOrder().getTimeSlot() != null){
                    try {
                        JSONObject request_data = new JSONObject();
                        request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                        request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                        request_data.put("time_slot_id", checkOutProcessActivity.getOrder().getTimeSlot().getId());
                        request_data.put("date", checkOutProcessActivity.getOrder().getDeliveruDate().split(" ")[0]);
                        request_data.put("is_solt_book", "");

                        TimeSlotService.reserveOrReleaseTimeSlot(checkOutProcessActivity, request_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            checkOutProcessActivity.finish();
        }
    }
}