package thegroceryshop.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import thegroceryshop.com.custom.SimpleSideDrawer;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.dialog.AppDialogDoubleAction;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.WishListDialog;
import thegroceryshop.com.fragments.RegionPickerFragment;
import thegroceryshop.com.loginModule.LoginModuleActivity;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.DeliveryCharges;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.modal.Region;
import thegroceryshop.com.modal.WishListBean;
import thegroceryshop.com.modal.sildemenu_model.CategoryItem;
import thegroceryshop.com.modal.sildemenu_model.NestedCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.PromotionCategoryItem;
import thegroceryshop.com.modal.sildemenu_model.SequenceNumberSorter;
import thegroceryshop.com.orders.MyOrdersActivity;
import thegroceryshop.com.service.LoadUsualData;
import thegroceryshop.com.slideMenu.SideMenuListAdapter;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.R.string.update;
import static thegroceryshop.com.application.OnlineMartApplication.state;


/**
 * Created by rohitg on 4/12/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public Toolbar toolbar;
    private RelativeLayout lyt_search;
    public DrawerLayout lyt_drawer;
    public ListView listview_side;
    private FrameLayout lyt_showcase;
    private Button btn_keep_shopping;
    protected TextView txt_title;
    private TextView txt_title_left;
    private boolean headerShouldBeinLeft = false;

    private View drawer_header_view, drawer_footer_view;
    private LinearLayout lyt_menu_home, lyt_menu_user, lyt_menu_notification, lyt_promotions, lyt_categories;
    private LinearLayout lyt_menu_account;
    private LinearLayout lyt_my_list;
    private LinearLayout lyt_menu_orders;
    private LinearLayout lyt_menu_promotions;
    private LinearLayout lyt_menu_invite;
    private LinearLayout lyt_menu_feedback, application_tour;
    private LinearLayout cart_lyt_header;
    private View.OnClickListener staticMenuClickListener;
    private TextView txt_region_name;
    private LinearLayout lyt_region_picker;
    private LinearLayout base_region_picker;
    private ImageView img_close_region_picker;
    private RecyclerView recyl_regions;

    private SimpleSideDrawer cartDrawer;
    private ImageView cart_btn_back, imageViewBarCode;
    private RecyclerView recyl_cart;
    private RelativeLayout lyt_checkout;
    private TextView cart_total_items, cart_txt_subtotal, cart_txt_total_savings, cart_txt_delivery_fee, cart_txt_tax_charges, cart_txt_total_amount, cart_text_checkout_total;
    private BaseActivity mContext;
    private ApiInterface apiService;
    ArrayList<CartItem> listUpdateCart = new ArrayList<>();
    private CartListRecyclerViewAdapter cartListRecyclerViewAdapter;
    private Activity activity;
    private double deliveryCharge = 0.0f;
    private float subtotal = 0.0f, savings = 0.0f, tax_charges = 0.0f, total_amont = 0.0f;
    private float total = 0.0f;
    private TextView no_itme_txt;
    private LinearLayout cardItem_layout;
    private boolean isHeaderLoaded = false;
    private boolean isForceUpdate, isUpdateAvailable, isDataLoaded;
    private SideMenuListAdapter sideMenuAdapter;
    private Call<ResponseBody> updateShippingChargesCall;
    private ArrayList<DeliveryCharges> list_charges = new ArrayList<>();
    private boolean isUserActive;

    private WishListDialog.OnAddToWishListLister onAddToWishListLister;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver wishListUpdateedReceiver;
    private RegionPickerFragment regionPickerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_title = findViewById(R.id.base_txt_title);
        txt_title_left = findViewById(R.id.base_txt_title_left);
        lyt_search = findViewById(R.id.serach_view_layout);
        lyt_showcase = findViewById(R.id.base_lyt_showCaseViwe);
        btn_keep_shopping = findViewById(R.id.base_btn_keep_shopping);
        imageViewBarCode = findViewById(R.id.imageViewBarCode);
        txt_region_name = findViewById(R.id.base_txt_region_name);
        lyt_region_picker = findViewById(R.id.base_lyt_region_picker);

        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShowCaseText())) {
            TextView txt_showcase = findViewById(R.id.txt_showcase);
            txt_showcase.setText(OnlineMartApplication.mLocalStore.getShowCaseText());
        } else {
            lyt_showcase.setVisibility(View.GONE);
        }

        /*txt_region_name.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txt_region_name.getLayoutParams();
                if(params.width == LinearLayout.LayoutParams.WRAP_CONTENT){
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    txt_region_name.setLayoutParams(params);
                }
            }
        });*/

        AppUtil.hideSoftKeyboard(this, this);

        mContext = this;
        apiService = ApiClient.createService(ApiInterface.class, mContext);

        initDrawerMenu();
        initCartView();

        if (OnlineMartApplication.getSharedPreferences().getBoolean("showCase", false)) {
            lyt_showcase.setVisibility(View.GONE);
        } else {
            lyt_showcase.setVisibility(View.VISIBLE);
        }

        wishListUpdateedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                // Get extra data included in the Intent
                String product_id = intent.getStringExtra("product_id");
                boolean isAddedToWishList = intent.getBooleanExtra("isAddedToWishList", false);

                ArrayList<Activity> list = OnlineMartApplication.getActivities();
                for(int i = 0; i < list.size(); i++){
                    if(list.get(i) instanceof ProductMoreDetailsActivity){

                        ProductMoreDetailsActivity productMoreDetailsActivity = (ProductMoreDetailsActivity)list.get(i);

                        if(productMoreDetailsActivity.product != null){
                            productMoreDetailsActivity.product.setAddedToWishList(isAddedToWishList);
                        }

                        if(productMoreDetailsActivity.list_same_brands_products != null && productMoreDetailsActivity.list_same_brands_products.size()>0){
                            for(int j=0; j<productMoreDetailsActivity.list_same_brands_products.size(); j++){
                                if (productMoreDetailsActivity.list_same_brands_products.get(j) != null) {
                                    if(product_id.equalsIgnoreCase(productMoreDetailsActivity.list_same_brands_products.get(j).getId())){
                                        productMoreDetailsActivity.list_same_brands_products.get(j).setAddedToWishList(isAddedToWishList);
                                        OnlineMartApplication.updateWishlistFlagOnCart(product_id, isAddedToWishList);
                                    }
                                }
                            }
                        }

                        if(productMoreDetailsActivity.list_similer_products != null && productMoreDetailsActivity.list_similer_products.size()>0) {
                            for (int j = 0; j < productMoreDetailsActivity.list_similer_products.size(); j++) {
                                if (productMoreDetailsActivity.list_similer_products.get(j) != null) {
                                    if (product_id.equalsIgnoreCase(productMoreDetailsActivity.list_similer_products.get(j).getId())) {
                                        productMoreDetailsActivity.list_similer_products.get(j).setAddedToWishList(isAddedToWishList);
                                        OnlineMartApplication.updateWishlistFlagOnCart(product_id, isAddedToWishList);
                                    }
                                }
                            }
                        }

                    }else if(list.get(i) instanceof ProductListActivity){

                        ProductListActivity productListActivity = (ProductListActivity)list.get(i);
                        if(productListActivity.list_products != null && productListActivity.list_products.size()>0){
                            for(int j=0; j<productListActivity.list_products.size(); j++){
                                if(productListActivity.list_products.get(j) != null){
                                    if(product_id.equalsIgnoreCase(productListActivity.list_products.get(j).getId())){
                                        productListActivity.list_products.get(j).setAddedToWishList(isAddedToWishList);
                                        OnlineMartApplication.updateWishlistFlagOnCart(product_id, isAddedToWishList);
                                    }
                                }
                            }
                        }

                    }else if(list.get(i) instanceof BarcodeCaptureActivity){

                        BarcodeCaptureActivity barcodeCaptureActivity = (BarcodeCaptureActivity)list.get(i);

                        if(barcodeCaptureActivity.product != null){
                            barcodeCaptureActivity.product.setAddedToWishList(isAddedToWishList);
                            OnlineMartApplication.updateWishlistFlagOnCart(product_id, isAddedToWishList);
                        }

                    }
                }
            }
        };

        lyt_showcase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineMartApplication.getSharedPreferences().edit().putBoolean("showCase", true).apply();
                lyt_showcase.setVisibility(View.GONE);
            }
        });

        btn_keep_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnlineMartApplication.getSharedPreferences().edit().putBoolean("showCase", true).apply();
                lyt_showcase.setVisibility(View.GONE);
            }
        });

        lyt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent searchIntent = new Intent(mContext, SearchingActivity.class);
                startActivity(searchIntent);

            }
        });

        onAddToWishListLister = new WishListDialog.OnAddToWishListLister() {
            @Override
            public void onAddedToWishListListener(int position, final String product_id) {

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
                    OnlineMartApplication.updateWishlistFlagOnCart(OnlineMartApplication.getCartList().get(position).getId(), true);
                    //OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());

                    if(cartListRecyclerViewAdapter != null){
                        cartListRecyclerViewAdapter.notifyItemChanged(position);
                    }
                }

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (activity != null) {
                            if (activity instanceof ProductListActivity){
                                ((ProductListActivity) activity).notifyData(product_id);
                            }else if (activity instanceof ProductMoreDetailsActivity) {
                                ((ProductMoreDetailsActivity) activity).notifyData(product_id);
                            }
                        }
                    }
                });
            }
        };

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtil.hideSoftKeyboard(BaseActivity.this, mContext);
                if (toolbar.getTag() != null && !ValidationUtil.isNullOrBlank(toolbar.getTag().toString())) {
                    String tag = (String) toolbar.getTag();
                    if (tag.equalsIgnoreCase(getString(R.string.back))) {
                        onBackPressed();
                    } else {
                        if (lyt_drawer != null) {
                            if (lyt_drawer.isDrawerOpen(GravityCompat.START)) {
                                lyt_drawer.closeDrawers();
                            } else {
                                lyt_drawer.openDrawer(GravityCompat.START);
                            }
                        }
                    }
                }
            }
        });
        imageViewBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent barIntent = new Intent(mContext, TestActivity.class);
                Intent barIntent = new Intent(mContext, BarcodeCaptureActivity.class);
                startActivity(barIntent);

            }
        });
    }

    private void initDrawerMenu() {

        lyt_drawer = findViewById(R.id.base_lyt_drawer);
        listview_side = findViewById(R.id.base_side_list_sidemenu);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                lyt_drawer,
                toolbar,
                R.string.app_name,
                R.string.app_name);

        lyt_drawer.addDrawerListener(actionBarDrawerToggle);
        lyt_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        actionBarDrawerToggle.syncState();

        /**
         * Set Silde HeaderView
         */

        drawer_header_view = LayoutInflater.from(mContext).inflate(R.layout.slide_header_view, null, false);
        lyt_menu_user = drawer_header_view.findViewById(R.id.user_item);
        lyt_menu_orders = drawer_header_view.findViewById(R.id.my_orders_item);
        lyt_menu_home = drawer_header_view.findViewById(R.id.home_item);
        lyt_menu_account = drawer_header_view.findViewById(R.id.my_account_item);
        lyt_my_list = drawer_header_view.findViewById(R.id.my_list_item);
        lyt_promotions = drawer_header_view.findViewById(R.id.lyt_promotions);
        lyt_categories = drawer_header_view.findViewById(R.id.categories_item);
        lyt_menu_promotions = drawer_header_view.findViewById(R.id.promotionItme_view);
        lyt_menu_notification = drawer_header_view.findViewById(R.id.notification_item);


        /**
         * Set Silde FooterView
         */
        drawer_footer_view = LayoutInflater.from(mContext).inflate(R.layout.slide_footer_view, null, false);
        lyt_menu_invite = drawer_footer_view.findViewById(R.id.invite_view);
        lyt_menu_feedback = drawer_footer_view.findViewById(R.id.feedbackView);
        application_tour = drawer_footer_view.findViewById(R.id.applocation_tour);
        listview_side.addFooterView(drawer_footer_view, null, false);

        listview_side.post(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (OnlineMartApplication.state != null) {
                            listview_side.onRestoreInstanceState(state);
                        }
                    }
                });
            }
        });

        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
            //resetSelection();
            lyt_menu_user.setVisibility(View.VISIBLE);
            lyt_my_list.setVisibility(View.VISIBLE);
            lyt_menu_orders.setVisibility(View.VISIBLE);

            String name = AppConstants.BLANK_STRING;
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getFirstName())) {
                name = OnlineMartApplication.mLocalStore.getFirstName();
            }

            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getLastName())) {
                if (name.length() == 0) {
                    name = OnlineMartApplication.mLocalStore.getLastName();
                } else {
                    name = name + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getLastName();
                }
            }

            TextView txt_name = drawer_header_view.findViewById(R.id.home_txt_name_of_user);
            txt_name.setText(name);
        } else {
            lyt_menu_user.setVisibility(View.GONE);
            lyt_my_list.setVisibility(View.GONE);
            lyt_menu_orders.setVisibility(View.GONE);
        }

        /**
         * Set Silde PromotionValue
         */
        setPromotionValueMenus();

        /**
         * SetClick Event On Widget
         */

        staticMenuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppUtil.hideSoftKeyboard(BaseActivity.this, mContext);
                lyt_drawer.closeDrawer(GravityCompat.START);
                resetSelection();
                switch (view.getId()) {
                    case R.id.home_item:
                        Intent intentStore = new Intent(BaseActivity.this, HomeActivity.class);
                        intentStore.putExtra("isFromInternal", true);
                        intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentStore);
                        break;
                    case R.id.user_item:
                        intentStore = new Intent(BaseActivity.this, AccountDetailsActivity.class);
                        intentStore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentStore);
                        break;
                    case R.id.my_list_item:
                        Intent intentOrderHistory;
                        intentOrderHistory = new Intent(BaseActivity.this, MySavedListActivity.class);
                        startActivity(intentOrderHistory);
                        break;
                    case R.id.my_account_item:
                        startActivity(new Intent(mContext, MyAccountActivity.class));
                        break;
                    case R.id.my_orders_item:
                        intentOrderHistory = new Intent(BaseActivity.this, MyOrdersActivity.class);
                        startActivity(intentOrderHistory);
                        break;
                    case R.id.notification_item:
                        startActivity(new Intent(mContext, NotificationListActivity.class));
                        break;
                    case R.id.invite_view:

                        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
                            try {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.title));

                                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                                    String strShareMessage = "\nLet me recommend you this application\n\n";
                                    strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName()
                                            + "\n You can use my referral code \"" + OnlineMartApplication.mLocalStore.getReferralCode() + "\" to register and enjoy the benefits.";

                                    i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
                                    startActivity(Intent.createChooser(i, getString(R.string.invite_friends)));
                                } else {
                                    String strShareMessage = "\nاسمحوا لي أن أوصي لكم هذا التطبيق\n\n";
                                    strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName()
                                            + "\n يمكنك استخدام كود الدعوة الخاص بي \"" + OnlineMartApplication.mLocalStore.getReferralCode() + "\" لتسجيل والتمتع بالفوائد";

                                    i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
                                    startActivity(Intent.createChooser(i, getString(R.string.invite_friends)));
                                }

                            } catch (Exception e) {
                                //e.toString();
                                e.printStackTrace();
                            }
                        } else {
                            AppUtil.requestLogin(mContext);
                        }
                        break;
                    case R.id.feedbackView:

                        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                            Intent intent = new Intent(BaseActivity.this, FeedbackActivity.class);
                            startActivity(intent);

                        } else {
                            AppUtil.requestLogin(BaseActivity.this);
                        }
                        break;

                    case R.id.applocation_tour:
                        startActivity(new Intent(BaseActivity.this, AppIntro_Activity.class).putExtra("from", "slide"));
                        break;
                }
            }
        };

        lyt_menu_home.setOnClickListener(staticMenuClickListener);
        lyt_menu_user.setOnClickListener(staticMenuClickListener);
        lyt_menu_orders.setOnClickListener(staticMenuClickListener);
        lyt_menu_account.setOnClickListener(staticMenuClickListener);
        lyt_my_list.setOnClickListener(staticMenuClickListener);
        lyt_menu_invite.setOnClickListener(staticMenuClickListener);
        lyt_menu_feedback.setOnClickListener(staticMenuClickListener);
        lyt_menu_notification.setOnClickListener(staticMenuClickListener);
        application_tour.setOnClickListener(staticMenuClickListener);

    }

    private void setPromotionValueMenus() {

        if (OnlineMartApplication.promotionCategory.size() > 0) {

            lyt_promotions.setVisibility(View.VISIBLE);
            for (int promoIndex = 0; promoIndex < OnlineMartApplication.promotionCategory.size(); promoIndex++) {

                View pormoView = LayoutInflater.from(mContext).inflate(R.layout.promotion_item_view, null);
                pormoView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.side_menu_color));

                TextView promotion_name_txt = pormoView.findViewById(R.id.promotion_name_txt);
                promotion_name_txt.setText(OnlineMartApplication.promotionCategory.get(promoIndex).getName());

                ImageView img_promotion = pormoView.findViewById(R.id.promotion_img);
                img_promotion.setVisibility(View.INVISIBLE);

                pormoView.setTag(promoIndex);
                pormoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = (int) v.getTag();
                        if (OnlineMartApplication.promotionCategory != null && OnlineMartApplication.promotionCategory.size() > position) {

                            Intent productListintent = new Intent(mContext, ProductListActivity.class);
                            productListintent.putExtra(ProductListActivity.PROMOTION_ID, OnlineMartApplication.promotionCategory.get(position).getId());
                            productListintent.putExtra(ProductListActivity.PROMOTION_NAME, OnlineMartApplication.promotionCategory.get(position).getName());
                            productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);

                            startActivity(productListintent);
                        }
                    }
                });


                switch (OnlineMartApplication.promotionCategory.get(promoIndex).getApp_ican_id()) {

                    case 2:
                        img_promotion.setImageResource(R.drawable.img_2);
                        break;
                    case 3:
                        img_promotion.setImageResource(R.drawable.img_3);
                        break;
                    case 4:
                        img_promotion.setImageResource(R.drawable.img_4);
                        break;
                    case 5:
                        img_promotion.setImageResource(R.drawable.img_5);
                        break;
                    case 6:
                        img_promotion.setImageResource(R.drawable.img_6);
                        break;
                    case 7:
                        img_promotion.setImageResource(R.drawable.img_7);
                        break;
                    case 8:
                        img_promotion.setImageResource(R.drawable.img_8);
                        break;
                    case 9:
                        img_promotion.setImageResource(R.drawable.img_9);
                        break;
                    case 10:
                        img_promotion.setImageResource(R.drawable.img_10);
                        break;
                    case 11:
                        img_promotion.setImageResource(R.drawable.img_11);
                        break;
                    case 12:
                        img_promotion.setImageResource(R.drawable.img_12);
                        break;
                    case 13:
                        img_promotion.setImageResource(R.drawable.img_13);
                        break;
                    case 14:
                        img_promotion.setImageResource(R.drawable.img_14);
                        break;
                    case 15:
                        img_promotion.setImageResource(R.drawable.img_15);
                        break;
                    case 16:
                        img_promotion.setImageResource(R.drawable.img_16);
                        break;
                    case 17:
                        img_promotion.setImageResource(R.drawable.img_17);
                        break;
                    case 18:
                        img_promotion.setImageResource(R.drawable.img_18);
                        break;
                    case 19:
                        img_promotion.setImageResource(R.drawable.img_19);
                        break;
                    case 20:
                        img_promotion.setImageResource(R.drawable.img_20);
                        break;
                    case 21:
                        img_promotion.setImageResource(R.drawable.img_21);
                        break;
                    case 22:
                        img_promotion.setImageResource(R.drawable.img_22);
                        break;
                    case 23:
                        img_promotion.setImageResource(R.drawable.img_23);
                        break;
                    case 24:
                        img_promotion.setImageResource(R.drawable.img_24);
                        break;
                    default:
                        img_promotion.setImageResource(R.drawable.img_default);

                }
                lyt_menu_promotions.addView(pormoView);
            }
        } else {
            lyt_promotions.setVisibility(View.GONE);
        }

        listview_side.addHeaderView(drawer_header_view, null, false);

        if(OnlineMartApplication.nestedCategory != null && OnlineMartApplication.nestedCategory.size() > 0){
            lyt_categories.setVisibility(View.VISIBLE);
        }else{
            lyt_categories.setVisibility(View.GONE);
        }
        sideMenuAdapter = new SideMenuListAdapter(this);
        listview_side.setAdapter(sideMenuAdapter);

    }

    private void initCartView() {

        cartDrawer = new SimpleSideDrawer(this);
        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            cartDrawer.setRightBehindContentView(R.layout.layout_shopping_cart_list);
        } else {
            cartDrawer.setLeftBehindContentView(R.layout.layout_shopping_cart_list);
        }
        recyl_cart = cartDrawer.findViewById(R.id.recyclerViewCartList);
        lyt_checkout = cartDrawer.findViewById(R.id.mCheckoutBtn);
        cart_btn_back = cartDrawer.findViewById(R.id.back_btn);

        cart_total_items = cartDrawer.findViewById(R.id.cart_txt_total_items);
        cart_txt_subtotal = cartDrawer.findViewById(R.id.cart_txt_subtotal_val);
        cart_txt_total_savings = cartDrawer.findViewById(R.id.cart_txt_total_savings_val);
        cart_txt_delivery_fee = cartDrawer.findViewById(R.id.cart_txt_delivery_fee_val);
        cart_txt_tax_charges = cartDrawer.findViewById(R.id.cart_txt_tax_charges);
        cart_txt_total_amount = cartDrawer.findViewById(R.id.cart_txt_total_amount);
        cart_text_checkout_total = cartDrawer.findViewById(R.id.cart_txt_checkout_total);
        cart_lyt_header = cartDrawer.findViewById(R.id.cart_lyt_header);
        no_itme_txt = cartDrawer.findViewById(R.id.no_itme_txt);
        cardItem_layout = cartDrawer.findViewById(R.id.cardItem_layout);

        recyl_cart.setHasFixedSize(true);
        recyl_cart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        cart_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    cartDrawer.toggleRightDrawer();
                } else {
                    cartDrawer.toggleLeftDrawer();
                }
            }
        });

        lyt_region_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegionPicker(true, OnlineMartApplication.getActivities().get(OnlineMartApplication.getActivities().size() - 1));
            }
        });

        lyt_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                    OnlineMartApplication.loadUserProfile(BaseActivity.this);
                    if (isHeaderLoaded && total_amont != 0.0f) {
                        if (OnlineMartApplication.getCartList().size() > 0) {

                            String ids = AppConstants.BLANK_STRING;
                            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                if (i == OnlineMartApplication.getCartList().size() - 1) {
                                    ids = ids + OnlineMartApplication.getCartList().get(i).getId();
                                } else {
                                    ids = ids + OnlineMartApplication.getCartList().get(i).getId() + ",";
                                }
                            }
                            updateCartProducts(ids);
                        }
                    }

                } else {

                    final AppDialogDoubleAction loginAlertDialog = new AppDialogDoubleAction(
                            mContext,
                            getString(R.string.title),
                            getString(R.string.do_you_want_to_login_into_OnlineMart),
                            getString(R.string.cancel),
                            getString(R.string.login));

                    loginAlertDialog.show();
                    loginAlertDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                        @Override
                        public void onLeftActionClick(View view) {
                            loginAlertDialog.dismiss();
                        }

                        @Override
                        public void onRightActionClick(View view) {
                            loginAlertDialog.dismiss();
                            startActivity(new Intent(mContext, LoginModuleActivity.class));
                        }
                    });

                }

            }
        });

        if (hasNavBar()) {

            RelativeLayout relativeLayoutSlide = findViewById(R.id.relativeLayoutSlide);
            LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) relativeLayoutSlide.getLayoutParams();
            relativeParams.setMargins(0, 0, 0, getSoftButtonsBarHeight());  // left, top, right, bottom
            relativeLayoutSlide.setLayoutParams(relativeParams);

        }
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
                        try {
                            AppUtil.showProgress(mContext);
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

                                                            listUpdateCart.clear();
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

                                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optDouble("offer_percent", 0.0f))) {
                                                                        //product.setOfferString(jsonObject.optString("offer_percent") + "% OFF");
                                                                        product.setOfferString(Math.round(jsonObject.optDouble("offer_percent", 0.0f)) + " %" + getString(R.string.off).toUpperCase());
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

                                                                    listUpdateCart.add(AppUtil.getCartObject(product));
                                                                }
                                                            }

                                                            String notifyMsg = getString(R.string.pls_note_the_following);
                                                            boolean isRemoved = false;
                                                            outer:
                                                            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                                                inner:
                                                                for (int j = 0; j < listUpdateCart.size(); j++) {
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

                                                            if(listUpdateCart != null && listUpdateCart.size()>0){

                                                                OnlineMartApplication.getCartList().clear();
                                                                OnlineMartApplication.getCartList().addAll(listUpdateCart);
                                                                OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                                                                listUpdateCart.clear();

                                                                if (cartListRecyclerViewAdapter != null) {
                                                                    cartListRecyclerViewAdapter.notifyDataSetChanged();
                                                                }
                                                                updateCartHeader();

                                                                if (!notifyMsg.equalsIgnoreCase(getString(R.string.pls_note_the_following))) {

                                                                    new Handler().post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (activity != null) {
                                                                                if (activity instanceof ProductListActivity)
                                                                                    ((ProductListActivity) activity).notifyData();
                                                                                else if (activity instanceof ProductMoreDetailsActivity) {
                                                                                    ((ProductMoreDetailsActivity) activity).notifyData();
                                                                                }
                                                                            }
                                                                        }
                                                                    });

                                                                    if (OnlineMartApplication.getCartList().size() > 0) {
                                                                        final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(mContext, getString(R.string.app_name), notifyMsg, getString(R.string.cancel), getString(R.string.checkout));
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

                                                                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                                                                                    if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                                                                                        cartDrawer.toggleRightDrawer();
                                                                                    } else {
                                                                                        cartDrawer.toggleLeftDrawer();
                                                                                    }

                                                                                    Intent intent = new Intent(mContext, CheckOutProcessActivity.class);
                                                                                    intent.putExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE, deliveryCharge);
                                                                                    intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS, OnlineMartApplication.getCartList().size());
                                                                                    intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT, total);
                                                                                    intent.putExtra(CheckOutProcessActivity.KEY_SUBTOTAL, subtotal);
                                                                                    intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS, savings);

                                                                                    startActivity(intent);

                                                                                } else {

                                                                                    final AppDialogDoubleAction loginAlertDialog = new AppDialogDoubleAction(
                                                                                            mContext,
                                                                                            getString(R.string.title),
                                                                                            getString(R.string.do_you_want_to_login_into_OnlineMart),
                                                                                            getString(R.string.cancel),
                                                                                            getString(R.string.login));

                                                                                    loginAlertDialog.show();
                                                                                    loginAlertDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                                                        @Override
                                                                                        public void onLeftActionClick(View view) {
                                                                                            loginAlertDialog.dismiss();
                                                                                        }

                                                                                        @Override
                                                                                        public void onRightActionClick(View view) {
                                                                                            loginAlertDialog.dismiss();
                                                                                            //cartDrawer.toggleRightDrawer();
                                                                                            startActivity(new Intent(mContext, LoginModuleActivity.class));
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        final AppDialogSingleAction appDialogDoubleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), notifyMsg, getString(R.string.cancel));
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
                                                                } else {
                                                                    if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                                                                        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                                                                            cartDrawer.toggleRightDrawer();
                                                                        } else {
                                                                            cartDrawer.toggleLeftDrawer();
                                                                        }

                                                                        Intent intent = new Intent(mContext, CheckOutProcessActivity.class);
                                                                        intent.putExtra(CheckOutProcessActivity.KEY_DELIVERY_FEE, deliveryCharge);
                                                                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_ITEMS, OnlineMartApplication.getCartList().size());
                                                                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_AMOUNT, total);
                                                                        intent.putExtra(CheckOutProcessActivity.KEY_SUBTOTAL, subtotal);
                                                                        intent.putExtra(CheckOutProcessActivity.KEY_TOTAL_SAVINGS, savings);

                                                                        startActivity(intent);

                                                                    } else {

                                                                        final AppDialogDoubleAction loginAlertDialog = new AppDialogDoubleAction(
                                                                                mContext,
                                                                                getString(R.string.title),
                                                                                getString(R.string.do_you_want_to_login_into_OnlineMart),
                                                                                getString(R.string.cancel),
                                                                                getString(R.string.login));

                                                                        loginAlertDialog.show();
                                                                        loginAlertDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                                            @Override
                                                                            public void onLeftActionClick(View view) {
                                                                                loginAlertDialog.dismiss();
                                                                            }

                                                                            @Override
                                                                            public void onRightActionClick(View view) {
                                                                                loginAlertDialog.dismiss();
                                                                                //cartDrawer.toggleRightDrawer();
                                                                                startActivity(new Intent(mContext, LoginModuleActivity.class));
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }else{
                                                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                                                                    OnlineMartApplication.loadUserProfile(BaseActivity.this);
                                                                    if (isHeaderLoaded && total_amont != 0.0f) {
                                                                        if (OnlineMartApplication.getCartList().size() > 0) {

                                                                            String ids = AppConstants.BLANK_STRING;
                                                                            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                                                                if (i == OnlineMartApplication.getCartList().size() - 1) {
                                                                                    ids = ids + OnlineMartApplication.getCartList().get(i).getId();
                                                                                } else {
                                                                                    ids = ids + OnlineMartApplication.getCartList().get(i).getId() + ",";
                                                                                }
                                                                            }
                                                                            updateCartProducts(ids);
                                                                        }
                                                                    }

                                                                } else {

                                                                    final AppDialogDoubleAction loginAlertDialog = new AppDialogDoubleAction(
                                                                            mContext,
                                                                            getString(R.string.title),
                                                                            getString(R.string.do_you_want_to_login_into_OnlineMart),
                                                                            getString(R.string.cancel),
                                                                            getString(R.string.login));

                                                                    loginAlertDialog.show();
                                                                    loginAlertDialog.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                                                        @Override
                                                                        public void onLeftActionClick(View view) {
                                                                            loginAlertDialog.dismiss();
                                                                        }

                                                                        @Override
                                                                        public void onRightActionClick(View view) {
                                                                            loginAlertDialog.dismiss();
                                                                            startActivity(new Intent(mContext, LoginModuleActivity.class));
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        AppUtil.showErrorDialog(mContext, responseMessage);
                                                    }

                                                } else {
                                                    AppUtil.showErrorDialog(mContext, errorMsg);
                                                }

                                            } else {
                                                AppUtil.showErrorDialog(mContext, getString(R.string.some_error_occoured));
                                            }

                                        } catch (Exception e) {//(JSONException | IOException e) {
                                            e.printStackTrace();
                                            AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-744)");
                                        }
                                    } else {
                                        AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-745)");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    AppUtil.hideProgress();
                                    AppUtil.showErrorDialog(mContext, getString(R.string.error_msg) + "(Err-746)");

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
            }
        });
    }

    public boolean hasNavBar() {
        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && getResources().getBoolean(id);
    }

    @SuppressLint("NewApi")
    private int getSoftButtonsBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    protected void setPageTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
        }

        if (headerShouldBeinLeft) {
            if(title != null){
                txt_title_left.setText(title.toUpperCase());
            }
            txt_title.setText(AppConstants.BLANK_STRING);
            txt_title.setVisibility(View.GONE);
            txt_title_left.setVisibility(View.VISIBLE);
            txt_title_left.setSelected(true);
        } else {
            txt_title.setText(title.toUpperCase());
            txt_title_left.setText(AppConstants.BLANK_STRING);
            txt_title_left.setVisibility(View.GONE);
            txt_title.setVisibility(View.VISIBLE);
        }

        lyt_region_picker.setVisibility(View.GONE);
        txt_region_name.setText(AppConstants.BLANK_STRING);
    }

    protected void setPageTitle(String title, boolean showRegionPicker) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
        }

        if(showRegionPicker){

            lyt_region_picker.setVisibility(View.VISIBLE);

            if(OnlineMartApplication.mLocalStore.getSelectedRegionId().equalsIgnoreCase(AppConstants.BLANK_STRING)){
                lyt_region_picker.setVisibility(View.GONE);
                txt_region_name.setText(AppConstants.BLANK_STRING);
            }else{
                lyt_region_picker.setVisibility(View.VISIBLE);
                txt_region_name.setText(OnlineMartApplication.mLocalStore.getSelectedRegionName());
                txt_region_name.setSelected(true);
            }

            txt_title_left.setVisibility(View.GONE);
            txt_title.setVisibility(View.GONE);

        }else{

            lyt_region_picker.setVisibility(View.GONE);
            txt_region_name.setText(AppConstants.BLANK_STRING);

            if (headerShouldBeinLeft) {

                txt_title_left.setText(title.toUpperCase());
                txt_title.setText(AppConstants.BLANK_STRING);
                txt_title.setVisibility(View.GONE);
                txt_title_left.setVisibility(View.VISIBLE);
                txt_title_left.setSelected(true);

            } else {

                txt_title.setText(title.toUpperCase());
                txt_title_left.setText(AppConstants.BLANK_STRING);
                txt_title_left.setVisibility(View.GONE);
                txt_title.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cartMenu:

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
                            cartItem.setAddedToWishList(cartObj.optBoolean("isAddedToWishList", false));
                            OnlineMartApplication.getCartList().add(cartItem);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (OnlineMartApplication.getCartList().size() > 0) {
                    cartListRecyclerViewAdapter = new CartListRecyclerViewAdapter(this, OnlineMartApplication.getCartList());
                    recyl_cart.setAdapter(cartListRecyclerViewAdapter);
                    no_itme_txt.setVisibility(View.GONE);
                    cardItem_layout.setVisibility(View.VISIBLE);
                    lyt_checkout.setVisibility(View.VISIBLE);
                } else {
                    no_itme_txt.setVisibility(View.VISIBLE);
                    cardItem_layout.setVisibility(View.GONE);
                    lyt_checkout.setVisibility(View.GONE);
                }

                if (cartListRecyclerViewAdapter != null) {
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
                                            final WishListDialog wishListDialog = new WishListDialog(BaseActivity.this, R.style.AddressDialog, listNames, OnlineMartApplication.getCartList().get(position).getId());
                                            wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                            wishListDialog.show();
                                        }else{
                                            final WishListDialog wishListDialog = new WishListDialog(BaseActivity.this, R.style.AddressDialog, listNames, OnlineMartApplication.getCartList().get(position).getId());
                                            wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                            wishListDialog.show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    final WishListDialog wishListDialog = new WishListDialog(BaseActivity.this, R.style.AddressDialog, listNames, OnlineMartApplication.getCartList().get(position).getId());
                                    wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                    wishListDialog.show();
                                }
                            } else {
                                AppUtil.requestLogin(BaseActivity.this);
                            }
                        }

                        @Override
                        public void onAddedToWishList(int position) {
                        }
                    });

                    cartListRecyclerViewAdapter.setOnQuantityChangedListener(new CartListRecyclerViewAdapter.OnQuantityChangedListener() {
                        @Override
                        public void onQuantityChanged(boolean isIncreased, final int position, final CartListRecyclerViewAdapter.ViewHolder holder) {

                            if (OnlineMartApplication.getCartList().size() > position) {
                                if (isIncreased) {
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (position < OnlineMartApplication.getCartList().size()) {
                                                if (OnlineMartApplication.getCartList().get(position).getSelectedQuantity() < OnlineMartApplication.getCartList().get(position).getMaxQuantity()) {
                                                    OnlineMartApplication.getCartList().get(position).setSelectedQuantity(OnlineMartApplication.getCartList().get(position).getSelectedQuantity() + 1);
                                                } else {
                                                    AppUtil.showErrorDialog(BaseActivity.this, String.format(getString(R.string.max_quantity_reached_msg), OnlineMartApplication.getCartList().get(position).getMaxQuantity() + AppConstants.BLANK_STRING));
                                                }

                                                cartListRecyclerViewAdapter.updateUI(holder, position);

                                                invalidateOptionsMenu();
                                                updateCartHeader();
                                                OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                                            }
                                        }
                                    });
                                } else {
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (position < OnlineMartApplication.getCartList().size()) {
                                                OnlineMartApplication.getCartList().get(position).setSelectedQuantity(OnlineMartApplication.getCartList().get(position).getSelectedQuantity() - 1);

                                                if (OnlineMartApplication.getCartList().get(position).getSelectedQuantity() <= 0) {
                                                    OnlineMartApplication.getCartList().remove(position);
                                                    cartListRecyclerViewAdapter.notifyDataSetChanged();
                                                } else {
                                                    cartListRecyclerViewAdapter.updateUI(holder, position);
                                                }

                                                invalidateOptionsMenu();
                                                updateCartHeader();
                                                OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                                            }
                                        }
                                    });
                                }

                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (activity != null) {
                                            if (activity instanceof ProductListActivity){
                                                ((ProductListActivity) activity).notifyData();
                                            }else if (activity instanceof ProductMoreDetailsActivity) {
                                                ((ProductMoreDetailsActivity) activity).notifyData();
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    });
                }

                updateCartHeader();

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    cartDrawer.toggleRightDrawer();
                } else {
                    cartDrawer.toggleLeftDrawer();
                }

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCartHeader() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                if (OnlineMartApplication.getCartList().size() == 0) {
                    invalidateOptionsMenu();
                    cart_lyt_header.setVisibility(View.GONE);
                    recyl_cart.setVisibility(View.GONE);
                    if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                        cart_text_checkout_total.setText(getString(R.string.egp) + AppConstants.SPACE + 0.00);
                    } else {
                        cart_text_checkout_total.setText(AppConstants.SPACE + 0.00 + AppConstants.SPACE + getString(R.string.egp));
                    }
                    no_itme_txt.setVisibility(View.VISIBLE);
                    cardItem_layout.setVisibility(View.GONE);
                    lyt_checkout.setVisibility(View.GONE);
                    OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);
                } else {
                    cart_lyt_header.setVisibility(View.VISIBLE);
                    recyl_cart.setVisibility(View.VISIBLE);

                    no_itme_txt.setVisibility(View.GONE);
                    cardItem_layout.setVisibility(View.VISIBLE);
                    lyt_checkout.setVisibility(View.VISIBLE);

                    subtotal = 0.0f;
                    savings = 0.0f;
                    tax_charges = 0.0f;
                    total_amont = 0.0f;
                    deliveryCharge = 0.0f;
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

                    cart_total_items.setText(OnlineMartApplication.getCartList().size() + AppConstants.BLANK_STRING);

                    if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                        cart_txt_subtotal.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + subtotal));
                        cart_txt_total_savings.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + savings));
                    } else {
                        cart_txt_subtotal.setText(AppUtil.mSetRoundUpPrice("" + subtotal) + AppConstants.SPACE + getString(R.string.egp));
                        cart_txt_total_savings.setText(AppUtil.mSetRoundUpPrice("" + savings) + AppConstants.SPACE + getString(R.string.egp));
                    }

                    OnlineMartApplication.mLocalStore.saveCartTotal(Math.round(subtotal));

                    if (total_amont != 0.00) {
                        updateShippingCharges(total_amont);
                    } else {
                        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                            cart_txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + total_amont));
                            cart_text_checkout_total.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + total_amont));
                            cart_txt_delivery_fee.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + 0.00f));
                        } else {
                            cart_txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + total_amont) + AppConstants.SPACE + getString(R.string.egp));
                            cart_text_checkout_total.setText(AppUtil.mSetRoundUpPrice("" + total_amont) + AppConstants.SPACE + getString(R.string.egp));
                            cart_txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice("" + 0.00f) + AppConstants.SPACE + getString(R.string.egp));
                        }
                    }
                }
            }
        });
    }

    public float getDeliveryCharges(float subtotal) {

        if (list_charges != null) {
            if (list_charges.size() > 0) {
                for (int i = 0; i < list_charges.size(); i++) {
                    if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && "2" .equalsIgnoreCase(list_charges.get(i).getType())) {
                        return list_charges.get(i).getCharges();
                    }
                }
                return 0.0f;
            } else {
                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())) {
                    try {
                        JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                        if (chargesArray != null) {
                            list_charges.clear();
                            for (int i = 0; i < chargesArray.length(); i++) {
                                JSONObject object = chargesArray.optJSONObject(i);
                                if (object != null) {

                                    DeliveryCharges deliveryCharges = new DeliveryCharges();
                                    deliveryCharges.setId(object.optString("id"));
                                    deliveryCharges.setType(object.optString("type"));
                                    deliveryCharges.setStart_amount((float) (object.optDouble("start_amt", 0.0)));
                                    deliveryCharges.setEnd_amount((float) (object.optDouble("end_amt", 0.0)));
                                    deliveryCharges.setCharges((float) (object.optDouble("d_charge", 0.0)));
                                    list_charges.add(deliveryCharges);
                                }
                            }

                            if (list_charges != null) {
                                for (int i = 0; i < list_charges.size(); i++) {
                                    if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && "2" .equalsIgnoreCase(list_charges.get(i).getType())) {
                                        return list_charges.get(i).getCharges();
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }
        } else {
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getShippingCharges())) {
                try {
                    JSONArray chargesArray = new JSONArray(OnlineMartApplication.mLocalStore.getShippingCharges());
                    if (chargesArray != null) {
                        list_charges.clear();
                        for (int i = 0; i < chargesArray.length(); i++) {
                            JSONObject object = chargesArray.optJSONObject(i);
                            if (object != null) {

                                DeliveryCharges deliveryCharges = new DeliveryCharges();
                                deliveryCharges.setId(object.optString("id"));
                                deliveryCharges.setType(object.optString("type"));
                                deliveryCharges.setStart_amount((float) (object.optDouble("start_amt", 0.0)));
                                deliveryCharges.setEnd_amount((float) (object.optDouble("end_amt", 0.0)));
                                deliveryCharges.setCharges((float) (object.optDouble("d_charge", 0.0)));
                                list_charges.add(deliveryCharges);
                            }
                        }

                        if (list_charges != null) {
                            for (int i = 0; i < list_charges.size(); i++) {
                                if (subtotal >= list_charges.get(i).getStart_amount() && subtotal <= list_charges.get(i).getEnd_amount() && "2" .equalsIgnoreCase(list_charges.get(i).getType())) {
                                    return list_charges.get(i).getCharges();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

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

                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    cart_txt_delivery_fee.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + deliveryCharge));
                    cart_txt_total_amount.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + total));
                    cart_text_checkout_total.setText(getString(R.string.egp) + AppConstants.SPACE + AppUtil.mSetRoundUpPrice("" + total));
                } else {
                    cart_txt_delivery_fee.setText(AppUtil.mSetRoundUpPrice("" + deliveryCharge) + AppConstants.SPACE + getString(R.string.egp));
                    cart_txt_total_amount.setText(AppUtil.mSetRoundUpPrice("" + total) + AppConstants.SPACE + getString(R.string.egp));
                    cart_text_checkout_total.setText(AppUtil.mSetRoundUpPrice("" + total) + AppConstants.SPACE + getString(R.string.egp));
                }
                isHeaderLoaded = true;
            }
        });
    }

    private void verifyCheckoutProcess(final String ids) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject request_data = new JSONObject();
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("product_id", ids);
                    request_data.put("user_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                    if (NetworkUtil.networkStatus(mContext)) {
                        try {
                            //AppUtil.showProgress(mContext);
                            Call<ResponseBody> call = apiService.updateCartData((new ConvertJsonToMap().jsonToMap(request_data)));

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

                                                                    if (!ValidationUtil.isNullOrBlank(jsonObject.optDouble("offer_percent", 0.0f))) {
                                                                        //product.setOfferString(jsonObject.optString("offer_percent") + "% OFF");
                                                                        product.setOfferString(Math.round(jsonObject.optDouble("offer_percent", 0.0f)) + " %" + getString(R.string.off).toUpperCase());
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

                                                                    listUpdateCart.add(AppUtil.getCartObject(product));
                                                                }
                                                            }

                                                            outer:
                                                            for (int i = 0; i < OnlineMartApplication.getCartList().size(); i++) {
                                                                inner:
                                                                for (int j = 0; j < listUpdateCart.size(); j++) {
                                                                    if (listUpdateCart.get(j).getId().equalsIgnoreCase(OnlineMartApplication.getCartList().get(i).getId())) {
                                                                        if (listUpdateCart.get(i).getMaxQuantity() == 0) {
                                                                            listUpdateCart.remove(i);
                                                                            OnlineMartApplication.removeProductFromCart(listUpdateCart.get(i).getId());
                                                                            //listUpdateCart.get(i).setSelectedQuantity(OnlineMartApplication.getCartList().get(i).getSelectedQuantity());
                                                                        } else {
                                                                            if (OnlineMartApplication.getCartList().get(i).getSelectedQuantity() > listUpdateCart.get(i).getMaxQuantity()) {
                                                                                listUpdateCart.get(i).setSelectedQuantity(listUpdateCart.get(i).getMaxQuantity());
                                                                            } else {
                                                                                listUpdateCart.get(i).setSelectedQuantity(OnlineMartApplication.getCartList().get(i).getSelectedQuantity());
                                                                            }
                                                                        }
                                                                        break inner;
                                                                    }
                                                                }
                                                            }

                                                            OnlineMartApplication.getCartList().clear();

                                                            for (int i = 0; i < listUpdateCart.size(); i++) {
                                                                if (listUpdateCart.get(i).getSelectedQuantity() == 0) {
                                                                    listUpdateCart.remove(i);
                                                                }
                                                            }

                                                            OnlineMartApplication.getCartList().addAll(listUpdateCart);
                                                            OnlineMartApplication.updateCartData(OnlineMartApplication.getCartList());
                                                            listUpdateCart.clear();

                                                            if (cartListRecyclerViewAdapter != null) {
                                                                cartListRecyclerViewAdapter.notifyDataSetChanged();
                                                            }
                                                            updateCartHeader();

                                                        }

                                                    } else {
                                                        //AppUtil.showErrorDialog(mContext, errorMsg);
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
                                    //loader_root.setStatuText(getString(R.string.no_product_found));
                                    //loader_root.showStatusText();
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
        });


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cartMenu);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        final TextView txt_badge_count = rootView.findViewById(R.id.menu_badge);

        int badge_count = OnlineMartApplication.loadCartList().size();
        if (badge_count > 0) {
            txt_badge_count.setText(badge_count + AppConstants.BLANK_STRING);
            txt_badge_count.setVisibility(View.VISIBLE);
        } else {
            txt_badge_count.setVisibility(View.INVISIBLE);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        getLocalBroadCastManager().registerReceiver(wishListUpdateedReceiver, new IntentFilter("WISHLIST_UPDATED"));

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (OnlineMartApplication.nestedCategory.size() == 0) {
                    loadSideMenuData(false, false);
                }
            }
        });

        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
            lyt_menu_user.setVisibility(View.VISIBLE);
            lyt_my_list.setVisibility(View.VISIBLE);
            lyt_menu_orders.setVisibility(View.VISIBLE);

            String name = AppConstants.BLANK_STRING;
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getFirstName())) {
                name = OnlineMartApplication.mLocalStore.getFirstName();
            }

            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getLastName())) {
                if (name.length() == 0) {
                    name = OnlineMartApplication.mLocalStore.getLastName();
                } else {
                    name = name + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getLastName();
                }
            }

            TextView txt_name = drawer_header_view.findViewById(R.id.home_txt_name_of_user);
            txt_name.setText(name);
        } else {
            lyt_menu_user.setVisibility(View.GONE);
            lyt_my_list.setVisibility(View.GONE);
            lyt_menu_orders.setVisibility(View.GONE);
        }
    }

    public void loadSideMenuData(final boolean isFromNotificaton, boolean shouldShowLoading) {

        if (NetworkUtil.networkStatus(BaseActivity.this)) {

            try {
                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("device_type", AppConstants.DEVICE_TYPE);
                request_data.put("os_version", DeviceUtil.getOsVersion());
                request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request_data.put("app_version", DeviceUtil.getVersionName(this));
                request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, BaseActivity.this);
                Call<ResponseBody> call = apiService.mGetSildeMenuCategory((new ConvertJsonToMap().jsonToMap(request_data)));

                if(shouldShowLoading){
                    AppUtil.showProgress(BaseActivity.this);
                }

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                    //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            AppUtil.hideProgress();
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getJSONObject("response").getInt("status_code") == 200) {

                                isUserActive = jsonObject.getJSONObject("response").optBoolean("user_active", true);
                                OnlineMartApplication.mLocalStore.saveUserActive(isUserActive);
                                updateUserUI();

                                isForceUpdate = jsonObject.getJSONObject("response").optBoolean("is_force_update", false);
                                isUpdateAvailable = jsonObject.getJSONObject("response").optBoolean("is_update_available", false);
                                isDataLoaded = true;

                                if (!isFromNotificaton) {

                                    if (isForceUpdate) {
                                        final AppDialogDoubleAction appDialogDoubleAction = new AppDialogDoubleAction(BaseActivity.this, getString(R.string.title), getString(R.string.app_force_update_message), getString(R.string.later).toUpperCase(), getString(update).toUpperCase());
                                        appDialogDoubleAction.show();
                                        appDialogDoubleAction.setCancelable(false);
                                        appDialogDoubleAction.setCanceledOnTouchOutside(false);
                                        appDialogDoubleAction.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                                        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                            @Override
                                            public void onLeftActionClick(View view) {
                                                appDialogDoubleAction.dismiss();
                                                finish();
                                            }

                                            @Override
                                            public void onRightActionClick(View view) {
                                                appDialogDoubleAction.dismiss();
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                                } catch (ActivityNotFoundException e) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_APP_STORE)));
                                                }

                                            }
                                        });
                                    } else {

                                        OnlineMartApplication.promotionCategory.clear();

                                        JSONObject dataJsonObject = jsonObject.getJSONObject("response").getJSONObject("data");

                                        if (dataJsonObject.get("PromotionCategory") instanceof JSONArray) {
                                            JSONArray promotionJsonArray = dataJsonObject.getJSONArray("PromotionCategory");

                                            for (int promoIndex = 0; promoIndex < promotionJsonArray.length(); promoIndex++) {
                                                JSONObject promoData = promotionJsonArray.getJSONObject(promoIndex);

                                                PromotionCategoryItem promotionCategoryItem = new PromotionCategoryItem();
                                                promotionCategoryItem.setId(promoData.optString("id"));
                                                promotionCategoryItem.setName(promoData.optString("name"));
                                                promotionCategoryItem.setImage(promoData.optString("image"));
                                                promotionCategoryItem.setApp_ican_id(promoData.optInt("app_ican_id"));
                                                OnlineMartApplication.promotionCategory.add(promotionCategoryItem);
                                            }
                                        }

                                        updateSideMenuPromotions();

                                        OnlineMartApplication.nestedCategory.clear();

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
                                                nestedCategoryItem.setParentId(nestedData.optString("parent_id"));
                                                nestedCategoryItem.setAdminCategoryLevel(nestedData.optString("admin_category_level"));
                                                nestedCategoryItem.setProductCount(nestedData.optString("product_count"));
                                                nestedCategoryItem.setApp_ican_id(nestedData.optInt("app_ican_id"));

                                                if (nestedData.has("Category")) {
                                                    if (nestedData.get("Category") instanceof JSONArray) {
                                                        JSONArray categoryJsonArray = nestedData.getJSONArray("Category");
                                                        List<CategoryItem> category = new ArrayList<>();

                                                        for (int catIndex = 0; catIndex < categoryJsonArray.length(); catIndex++) {
                                                            JSONObject catData = categoryJsonArray.getJSONObject(catIndex);
                                                            CategoryItem categoryItem = new CategoryItem();
                                                            categoryItem.setName(catData.optString("name"));
                                                            categoryItem.setImage(catData.optString("image"));
                                                            categoryItem.setLevel(catData.optString("level"));
                                                            categoryItem.setAdminCategoryLevel(catData.optString("admin_category_level"));
                                                            categoryItem.setParentId(catData.optString("parent_id"));
                                                            categoryItem.setProduct_count(catData.optString("product_count"));
                                                            categoryItem.setCategoryId(catData.optString("category_id"));


                                                            if (catData.has("Category")) {
                                                                JSONArray subcategoryJsonArray = catData.getJSONArray("Category");
                                                                List<CategoryItem> sub_category = new ArrayList<>();

                                                                for (int subIndex = 0; subIndex < subcategoryJsonArray.length(); subIndex++) {
                                                                    JSONObject subcatData = subcategoryJsonArray.getJSONObject(subIndex);
                                                                    CategoryItem subcategoryItem = new CategoryItem();
                                                                    subcategoryItem.setName(subcatData.optString("name"));
                                                                    subcategoryItem.setImage(subcatData.optString("image"));
                                                                    subcategoryItem.setLevel(subcatData.optString("level"));
                                                                    subcategoryItem.setAdminCategoryLevel(subcatData.optString("admin_category_level"));
                                                                    subcategoryItem.setParentId(subcatData.optString("parent_id"));
                                                                    subcategoryItem.setProduct_count(subcatData.optString("product_count"));
                                                                    subcategoryItem.setCategoryId(subcatData.optString("category_id"));

                                                                    sub_category.add(subcategoryItem);
                                                                }
                                                                categoryItem.setCategory(sub_category);
                                                            }

                                                            category.add(categoryItem);
                                                        }
                                                        nestedCategoryItem.setCategory(category);
                                                    }
                                                }

                                                //OnlineMartApplication.nestedCategory.add(nestedCategoryItem);
                                                if (!nestedCategoryItem.getProduct_count().equalsIgnoreCase("0")) {
                                                    OnlineMartApplication.nestedCategory.add(nestedCategoryItem);
                                                }
                                            }
                                        }

                                        Collections.sort(OnlineMartApplication.nestedCategory, new SequenceNumberSorter());

                                        if(OnlineMartApplication.nestedCategory != null && OnlineMartApplication.nestedCategory.size() > 0){
                                            lyt_categories.setVisibility(View.VISIBLE);
                                        }else{
                                            lyt_categories.setVisibility(View.GONE);
                                        }

                                        if (OnlineMartApplication.nestedCategory.size() != 0 && sideMenuAdapter != null) {
                                            sideMenuAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                if (!isUserActive) {
                                    logoutUser();
                                }
                            }
                        } catch (Exception e) {
                            AppUtil.hideProgress();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppUtil.hideProgress();
                    }
                });
            } catch (Exception e) {

            }
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

    public void updateUserUI() {

        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {
            lyt_menu_user.setVisibility(View.VISIBLE);
            lyt_my_list.setVisibility(View.VISIBLE);
            lyt_menu_orders.setVisibility(View.VISIBLE);

            String name = AppConstants.BLANK_STRING;
            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getFirstName())) {
                name = OnlineMartApplication.mLocalStore.getFirstName();
            }

            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getLastName())) {
                if (name.length() == 0) {
                    name = OnlineMartApplication.mLocalStore.getLastName();
                } else {
                    name = name + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getLastName();
                }
            }

            TextView txt_name = drawer_header_view.findViewById(R.id.home_txt_name_of_user);
            txt_name.setText(name);
        } else {
            lyt_menu_user.setVisibility(View.GONE);
            lyt_my_list.setVisibility(View.GONE);
            lyt_menu_orders.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();
    }

    protected void setSearchBarVisiblity(boolean isVisible) {
        if (isVisible) {
            lyt_search.setVisibility(View.VISIBLE);
        } else {
            lyt_search.setVisibility(View.GONE);
        }
    }

    protected void setToolbarTag(String tag) {
        toolbar.setTag(tag);
        if (tag.equalsIgnoreCase(getString(R.string.back))) {

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                toolbar.setNavigationIcon(R.mipmap.top_back);
            } else {
                toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
            }
        }
    }

    @Override
    public void onBackPressed() {

        AppUtil.hideSoftKeyboard(this, mContext);
        if (lyt_drawer != null && lyt_drawer.isDrawerOpen(GravityCompat.START)) {
            lyt_drawer.closeDrawers();
        } else {
            if (cartDrawer != null && !cartDrawer.isClosed()) {
                if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                    cartDrawer.toggleRightDrawer();
                } else {
                    cartDrawer.toggleLeftDrawer();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(this, mContext);
        if(wishListUpdateedReceiver != null){
            getLocalBroadCastManager().unregisterReceiver(wishListUpdateedReceiver);
        }

        OnlineMartApplication.getSharedPreferences().edit().putBoolean("showCase", true).apply();
        lyt_showcase.setVisibility(View.GONE);
        OnlineMartApplication.state = listview_side.onSaveInstanceState();

        if (lyt_drawer != null && lyt_drawer.isDrawerOpen(GravityCompat.START)) {
            lyt_drawer.closeDrawers();
        }

        if(regionPickerFragment != null && regionPickerFragment.isVisible()){
            regionPickerFragment.dismiss();
        }
    }

    protected void headerShouldBeInLeft(boolean headerShouldBeinLeft) {
        this.headerShouldBeinLeft = headerShouldBeinLeft;
    }

    protected void setCurrentActivity(Activity activity) {
        this.activity = activity;
    }

    public void resetSelection() {

        ((TextView) (lyt_menu_home.findViewById(R.id.label))).setTextColor(ContextCompat.getColor(mContext, R.color.white));
        //((TextView) (lyt_menu_home.findViewById(R.id.label))).setTypeface(Typeface.MONOSPACE);
        //((ImageView)(lyt_menu_home.findViewById(R.id.icon))).setColorFilter(R.color.menu_white, PorterDuff.Mode.DST);
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            ((TextView) (lyt_menu_home.findViewById(R.id.label))).setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_regular));
        } else {
            ((TextView) (lyt_menu_home.findViewById(R.id.label))).setTypeface(ResourcesCompat.getFont(this, R.font.ge_ss_two_medium));
        }

        if (OnlineMartApplication.nestedCategory != null && OnlineMartApplication.nestedCategory.size() > 0) {
            for (int i = 0; i < OnlineMartApplication.nestedCategory.size(); i++) {
                OnlineMartApplication.nestedCategory.get(i).setSelected(false);
                if (OnlineMartApplication.nestedCategory.get(i).getCategory() != null && OnlineMartApplication.nestedCategory.get(i).getCategory().size() > 0) {
                    for (int j = 0; j < OnlineMartApplication.nestedCategory.get(i).getCategory().size(); j++) {
                        OnlineMartApplication.nestedCategory.get(i).getCategory().get(j).setSelected(false);
                    }
                }
            }
        }
    }

    public void setHomeSelected() {
        if (lyt_menu_home != null) {
            //lyt_menu_home.setBackgroundColor(ContextCompat.getColor(mContext, R.color.darker_green));
            //((TextView) (lyt_menu_home.findViewById(R.id.label))).setTypeface(Typeface.SERIF);
            if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                ((TextView) (lyt_menu_home.findViewById(R.id.label))).setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_bold));
            } else {
                ((TextView) (lyt_menu_home.findViewById(R.id.label))).setTypeface(ResourcesCompat.getFont(this, R.font.ge_ss_two_bold));
            }
            //((ImageView)(lyt_menu_home.findViewById(R.id.icon))).setColorFilter(R.color.menu_red, PorterDuff.Mode.DST);
            ((TextView) (lyt_menu_home.findViewById(R.id.label))).setTextColor(ContextCompat.getColor(mContext, R.color.menu_selected));
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == WishListDialog.REQUEST_CREATE_NEW_LIST){
            if(resultCode == Activity.RESULT_OK){
                if(data != null){
                    if(data.getStringExtra("product_id") != null){

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
                                    final WishListDialog wishListDialog = new WishListDialog(BaseActivity.this, R.style.AddressDialog, listNames, data.getStringExtra("product_id"));
                                    wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                    wishListDialog.show();
                                }else{
                                    final WishListDialog wishListDialog = new WishListDialog(BaseActivity.this, R.style.AddressDialog, listNames, data.getStringExtra("product_id"));
                                    wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                    wishListDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            final WishListDialog wishListDialog = new WishListDialog(BaseActivity.this, R.style.AddressDialog, listNames, data.getStringExtra("product_id"));
                            wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                            wishListDialog.show();
                        }
                    }
                }
            }
        }
    }

    LocalBroadcastManager getLocalBroadCastManager(){
        if(localBroadcastManager != null){
            return localBroadcastManager;
        }else{
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            return localBroadcastManager;
        }
    }

    protected void openRegionPicker(boolean isCancelable, final Activity activity){

        if(regionPickerFragment != null){
            if(regionPickerFragment.getDialog() != null){
                if(!regionPickerFragment.getDialog().isShowing()){
                    regionPickerFragment = new RegionPickerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isCancelable", isCancelable);
                    regionPickerFragment.setArguments(bundle);
                    regionPickerFragment.setActivity(activity);
                    regionPickerFragment.setCancelable(isCancelable);
                    regionPickerFragment.show(getSupportFragmentManager(), "regionPicker");

                    regionPickerFragment.setOnRegionSelectedListener(new RegionPickerFragment.OnRegionSelectedListener() {
                        @Override
                        public void onRegionSelected(Region selected_region, Activity activity) {
                            updateRegionChanges(selected_region, activity);
                            regionPickerFragment.updateRegionAdapter();
                        }
                    });
                }
            }else{
                regionPickerFragment = new RegionPickerFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isCancelable", isCancelable);
                regionPickerFragment.setArguments(bundle);
                regionPickerFragment.setActivity(activity);
                regionPickerFragment.setCancelable(isCancelable);
                regionPickerFragment.show(getSupportFragmentManager(), "regionPicker");

                regionPickerFragment.setOnRegionSelectedListener(new RegionPickerFragment.OnRegionSelectedListener() {
                    @Override
                    public void onRegionSelected(Region selected_region, Activity activity) {
                        updateRegionChanges(selected_region, activity);
                        regionPickerFragment.updateRegionAdapter();
                    }
                });
            }

        }else{
            regionPickerFragment = new RegionPickerFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isCancelable", isCancelable);
            regionPickerFragment.setArguments(bundle);
            regionPickerFragment.setActivity(activity);
            regionPickerFragment.setCancelable(isCancelable);
            regionPickerFragment.show(getSupportFragmentManager(), "regionPicker");

            regionPickerFragment.setOnRegionSelectedListener(new RegionPickerFragment.OnRegionSelectedListener() {
                @Override
                public void onRegionSelected(Region selected_region, final Activity activity) {
                    updateRegionChanges(selected_region, activity);
                    regionPickerFragment.updateRegionAdapter();
                }
            });
        }
    }

    public void updateRegionChanges(Region selected_region, final Activity activity){

        OnlineMartApplication.mLocalStore.saveCartData(AppConstants.BLANK_STRING);
        OnlineMartApplication.mLocalStore.saveCartTotal(0.0f);

        OnlineMartApplication.getCartList().clear();
        invalidateOptionsMenu();
        updateCartHeader();

        OnlineMartApplication.mLocalStore.setSelectedRegion(selected_region);

        setPageTitle(OnlineMartApplication.mLocalStore.getSelectedRegionName(), true);

        lyt_region_picker.invalidate();

        OnlineMartApplication.promotionCategory.clear();
        OnlineMartApplication.nestedCategory.clear();

        OnlineMartApplication.home_nestedCategory.clear();
        OnlineMartApplication.home_promotionCategory.clear();

        if (sideMenuAdapter != null) {
            sideMenuAdapter.notifyDataSetChanged();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                loadSideMenuData(false, true);
                if(activity instanceof HomeActivity){
                    ((HomeActivity)activity).loadCategoriesForHome();
                }
                LoadUsualData.loadShippingCharges(BaseActivity.this);
            }
        }, 500);

    }

    public void updateSideMenuPromotions(){

        if (OnlineMartApplication.promotionCategory.size() > 0) {

            lyt_promotions.setVisibility(View.VISIBLE);
            if(lyt_menu_promotions != null){
                lyt_menu_promotions.removeAllViews();
            }
            for (int promoIndex = 0; promoIndex < OnlineMartApplication.promotionCategory.size(); promoIndex++) {

                View pormoView = LayoutInflater.from(mContext).inflate(R.layout.promotion_item_view, null);
                pormoView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.side_menu_color));

                TextView promotion_name_txt = pormoView.findViewById(R.id.promotion_name_txt);
                promotion_name_txt.setText(OnlineMartApplication.promotionCategory.get(promoIndex).getName());

                ImageView img_promotion = pormoView.findViewById(R.id.promotion_img);
                img_promotion.setVisibility(View.INVISIBLE);

                pormoView.setTag(promoIndex);
                pormoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = (int) v.getTag();
                        if (OnlineMartApplication.promotionCategory != null && OnlineMartApplication.promotionCategory.size() > position) {

                            Intent productListintent = new Intent(mContext, ProductListActivity.class);
                            productListintent.putExtra(ProductListActivity.PROMOTION_ID, OnlineMartApplication.promotionCategory.get(position).getId());
                            productListintent.putExtra(ProductListActivity.PROMOTION_NAME, OnlineMartApplication.promotionCategory.get(position).getName());
                            productListintent.putExtra(ProductListActivity.IS_LOAD_CATEGORY, false);

                            startActivity(productListintent);
                        }
                    }
                });


                switch (OnlineMartApplication.promotionCategory.get(promoIndex).getApp_ican_id()) {

                    case 2:
                        img_promotion.setImageResource(R.drawable.img_2);
                        break;
                    case 3:
                        img_promotion.setImageResource(R.drawable.img_3);
                        break;
                    case 4:
                        img_promotion.setImageResource(R.drawable.img_4);
                        break;
                    case 5:
                        img_promotion.setImageResource(R.drawable.img_5);
                        break;
                    case 6:
                        img_promotion.setImageResource(R.drawable.img_6);
                        break;
                    case 7:
                        img_promotion.setImageResource(R.drawable.img_7);
                        break;
                    case 8:
                        img_promotion.setImageResource(R.drawable.img_8);
                        break;
                    case 9:
                        img_promotion.setImageResource(R.drawable.img_9);
                        break;
                    case 10:
                        img_promotion.setImageResource(R.drawable.img_10);
                        break;
                    case 11:
                        img_promotion.setImageResource(R.drawable.img_11);
                        break;
                    case 12:
                        img_promotion.setImageResource(R.drawable.img_12);
                        break;
                    case 13:
                        img_promotion.setImageResource(R.drawable.img_13);
                        break;
                    case 14:
                        img_promotion.setImageResource(R.drawable.img_14);
                        break;
                    case 15:
                        img_promotion.setImageResource(R.drawable.img_15);
                        break;
                    case 16:
                        img_promotion.setImageResource(R.drawable.img_16);
                        break;
                    case 17:
                        img_promotion.setImageResource(R.drawable.img_17);
                        break;
                    case 18:
                        img_promotion.setImageResource(R.drawable.img_18);
                        break;
                    case 19:
                        img_promotion.setImageResource(R.drawable.img_19);
                        break;
                    case 20:
                        img_promotion.setImageResource(R.drawable.img_20);
                        break;
                    case 21:
                        img_promotion.setImageResource(R.drawable.img_21);
                        break;
                    case 22:
                        img_promotion.setImageResource(R.drawable.img_22);
                        break;
                    case 23:
                        img_promotion.setImageResource(R.drawable.img_23);
                        break;
                    case 24:
                        img_promotion.setImageResource(R.drawable.img_24);
                        break;
                    default:
                        img_promotion.setImageResource(R.drawable.img_default);

                }
                lyt_menu_promotions.addView(pormoView);
            }
        } else {
            lyt_promotions.setVisibility(View.GONE);
        }
    }
}
