package thegroceryshop.com.dialog;

/*
 * Created by umeshk on 13-03-2018.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import thegroceryshop.com.activity.CreateWishListActivity;
import thegroceryshop.com.adapter.WishListAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.modal.WishListBean;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

public class WishListDialog extends Dialog implements View.OnClickListener {

    private final Context context;

    // View components
    private Button btn_done, btn_cancel;
    private Button btnCreateNewList;
    private RecyclerView recyclerViewWishList;
    private TextView txtViewNoWishList;
    private ArrayList<WishListBean> wishList;
    private WishListAdapter wishListAdapter;
    private String list_id;
    private String product_id;
    private OnAddToWishListLister onAddToWishListLister;
    private int position;
    public static int REQUEST_CREATE_NEW_LIST = 165;

    public WishListDialog(final Context context, int themeResId, ArrayList<WishListBean> listNames, String product_id) {
        super(context, themeResId);
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window mWindow = getWindow();
        setContentView(R.layout.dialog_wishlist);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        wishList = listNames;
        this.product_id = product_id;

        if (mWindow != null) {
            View mView = mWindow.getDecorView();
            mView.setBackgroundResource(android.R.color.transparent);
        }

        recyclerViewWishList = findViewById(R.id.recyclerView_wish_list);
        txtViewNoWishList = findViewById(R.id.txtViewNoWishList);
        btnCreateNewList = findViewById(R.id.btn_create_new_list);
        btn_done = findViewById(R.id.btn_done);
        btn_cancel = findViewById(R.id.btn_cancel);

        loadWishLists();
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btnCreateNewList.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewWishList.setLayoutManager(linearLayoutManager);
        recyclerViewWishList.setHasFixedSize(true);

        if (wishList.size() > 0) {

            if(wishList.get(0).isEnable()){
                wishList.get(0).setSelected(true);
                list_id = wishList.get(0).getId();
            }

            wishListAdapter = new WishListAdapter(context, wishList);
            recyclerViewWishList.setVisibility(View.VISIBLE);
            txtViewNoWishList.setVisibility(View.GONE);
            btn_done.setEnabled(true);
            btn_done.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            wishListAdapter = new WishListAdapter(context, wishList);
            recyclerViewWishList.setVisibility(View.INVISIBLE);
            txtViewNoWishList.setVisibility(View.VISIBLE);
            btn_done.setEnabled(false);
            btn_done.setTextColor(ContextCompat.getColor(context, R.color.gray_50));
        }

        wishListAdapter.setOnWishListSelectedListener(new WishListAdapter.OnWishListSelectedListener() {
            @Override
            public void onWishListSelected(int position) {
                list_id = wishList.get(position).getId();
                for (int i = 0; i < wishList.size(); i++) {
                    WishListBean wishListBean = wishList.get(i);
                    wishListBean.setSelected(position == i);
                }
                wishListAdapter.notifyDataSetChanged();
            }
        });
        recyclerViewWishList.setAdapter(wishListAdapter);
    }

    @Override
    public void onClick(View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AppUtil.hideSoftKeyboard((Activity) context, context);
            }
        });
        if (view == btn_done) {

            if (TextUtils.isEmpty(list_id)) {
                AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), context.getString(R.string.select_wishlist_validation), context.getString(R.string.ok));
            } else {
                addToWishList();
            }

        } else if (view == btn_cancel) {
            dismiss();
        } else if (view == btnCreateNewList) {
            dismiss();
            Intent wishListIntent = new Intent(context, CreateWishListActivity.class);
            wishListIntent.putExtra("product_id", product_id);
            ((Activity)context).startActivityForResult(wishListIntent, REQUEST_CREATE_NEW_LIST);
        }
    }

    private void addToWishList() {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("list_id", list_id);
            request_data.put("product_id", product_id);
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(context)) {
                try {
                    ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
                    Call<ResponseBody> call = apiService.wishListInfo((new ConvertJsonToMap().jsonToMap(request_data)));
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

                                            if(onAddToWishListLister != null){
                                                onAddToWishListLister.onAddedToWishListListener(position, product_id);
                                            }

                                            String responseMessage = resObj.optString("success_message");
                                            AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), responseMessage, context.getString(R.string.ok), new OnSingleActionListener() {
                                                @Override
                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                    dismiss();
                                                    appDialogSingleAction.dismiss();
                                                }
                                            });

                                        } else {
                                            AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), errorMsg, context.getString(R.string.ok));
                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), context.getString(R.string.error_msg) + "(Err-724)", context.getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {
                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(context, context.getString(R.string.title), context.getString(R.string.error_msg) + "(Err-725)", context.getString(R.string.ok));
                                }
                            } else {
                                AppUtil.displaySingleActionAlert(context, context.getString(R.string.app_name), context.getString(R.string.error_msg) + "(Err-726)", context.getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
                            // AppUtil.displaySingleActionAlert(mContext, getString(R.string.app_name), getString(R.string.error_msg), getString(R.string.ok));
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

    private void loadWishLists() {

        if (!TextUtils.isEmpty(OnlineMartApplication.mLocalStore.getUserId())) {
            try {
                JSONObject request = new JSONObject();
                request.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
                request.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, context);
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
                                    if (statusCode == 200) {
                                        if (resObj.optJSONArray("data") != null) {
                                            JSONArray array = resObj.optJSONArray("data");
                                            if (array != null) {
                                                OnlineMartApplication.mLocalStore.setMyWishListNames(array.toString());
                                                updateList();
                                            } else {
                                                loadWishLists();
                                            }
                                        } else {
                                            loadWishLists();
                                        }

                                    } else if(statusCode == 201) {

                                    } else {
                                        loadWishLists();
                                    }

                                } else {
                                    loadWishLists();
                                }

                            } catch (Exception e) {
                                loadWishLists();
                                e.printStackTrace();
                                //AppUtil.showErrorDialog(mContext, response.message());
                            }
                        } else {
                            loadWishLists();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loadWishLists();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList() {

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

                    wishList = listNames;
                    if (wishList.size() > 0) {

                        if(wishList.get(0).isEnable()){
                            wishList.get(0).setSelected(true);
                            list_id = wishList.get(0).getId();
                        }

                        wishListAdapter = new WishListAdapter(context, wishList);
                        recyclerViewWishList.setVisibility(View.VISIBLE);
                        txtViewNoWishList.setVisibility(View.GONE);
                        btn_done.setEnabled(true);
                        btn_done.setTextColor(ContextCompat.getColor(context, R.color.white));
                    } else {
                        wishListAdapter = new WishListAdapter(context, wishList);
                        recyclerViewWishList.setVisibility(View.INVISIBLE);
                        txtViewNoWishList.setVisibility(View.VISIBLE);
                        btn_done.setEnabled(false);
                        btn_done.setTextColor(ContextCompat.getColor(context, R.color.gray_50));
                    }

                    wishListAdapter.setOnWishListSelectedListener(new WishListAdapter.OnWishListSelectedListener() {
                        @Override
                        public void onWishListSelected(int position) {
                            list_id = wishList.get(position).getId();
                            for (int i = 0; i < wishList.size(); i++) {
                                WishListBean wishListBean = wishList.get(i);
                                wishListBean.setSelected(position == i);
                            }
                            wishListAdapter.notifyDataSetChanged();
                        }
                    });

                    recyclerViewWishList.setAdapter(wishListAdapter);

                }else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }

    }

    public void setOnAddToWishListLister(OnAddToWishListLister onAddToWishListLister, int position){
        this.onAddToWishListLister = onAddToWishListLister;
        this.position = position;
    }

    public void setOnAddToWishListLister(OnAddToWishListLister onAddToWishListLister){
        this.onAddToWishListLister = onAddToWishListLister;
    }

    public interface OnAddToWishListLister{
        void onAddedToWishListListener(int position, String product_id);
    }
}
