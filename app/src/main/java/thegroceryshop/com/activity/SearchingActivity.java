package thegroceryshop.com.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import thegroceryshop.com.adapter.SearchSuggestionListAdapter;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/**
 * Created by rohitg on 4/24/2017.
 */
public class SearchingActivity extends AppCompatActivity {

    private EditText edt_search;
    private TextView txt_cancel;
    private LoaderLayout loader_search;
    private RecyclerView recyl_search;
    private ArrayList<String> list_string = new ArrayList<>();
    private Context mContext;
    private ApiInterface apiService;
    private SearchSuggestionListAdapter searchSuggestionListAdapter;
    private Call<ResponseBody> suggestionCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        mContext = this;
        apiService = ApiClient.createService(ApiInterface.class, mContext);

        edt_search = findViewById(R.id.search_edt_serach_box);
        txt_cancel = findViewById(R.id.search_txt_cancel);
        loader_search = findViewById(R.id.search_loader);
        recyl_search = findViewById(R.id.search_recyl_suggestions);
        edt_search.setHint(AppConstants.SPACE + getString(R.string.search_product_here));
        loader_search.showContent();
        loader_search.setHideContentOnLoad(true);
        loader_search.setStatuText(getString(R.string.no_product_found));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyl_search.setLayoutManager(linearLayoutManager);

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(!ValidationUtil.isNullOrBlank(edt_search.getText().toString()) && edt_search.getText().toString().length() >=3){

                        /*Intent searchIntent = new Intent(mContext, ProductListActivity.class);
                        searchIntent.putExtra(ProductListActivity.SEARCH_KEYWORD, edt_search.getText().toString());
                        startActivity(searchIntent);*/

                        Intent searchIntent = new Intent(mContext, ProductListActivity.class);
                        searchIntent.putExtra(ProductListActivity.SEARCH_KEYWORD, edt_search.getText().toString());
                        startActivity(searchIntent);

                        finish();
                    }
                }

                return false;
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                list_string.clear();
                edt_search.setText(AppConstants.BLANK_STRING);
                if(searchSuggestionListAdapter != null){
                    loader_search.showStatusText();

                    searchSuggestionListAdapter = new SearchSuggestionListAdapter(mContext, list_string);
                    recyl_search.setAdapter(searchSuggestionListAdapter);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchString, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchString = editable.toString();
                if(searchString.length() >=3){
                    loadSuggestions(searchString);
                }else if(searchString.length() == 0){
                    if(list_string != null){

                        if(suggestionCall != null){
                            suggestionCall.cancel();
                        }
                        loader_search.showStatusText();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                list_string.clear();
                                loader_search.showStatusText();
                                recyl_search.setAdapter(null);
                            }
                        }, 500);

                    }
                }

            }
        });
    }

    private void loadSuggestions(final String search_string) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                if(suggestionCall != null){
                    suggestionCall.cancel();
                }

                try {
                    JSONObject request_data = new JSONObject();
                    request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                    request_data.put("keywords", search_string);
                    request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                    if (NetworkUtil.networkStatus(mContext)) {
                        try {
                            loader_search.showProgress();
                            suggestionCall = apiService.loadSuggestions((new ConvertJsonToMap().jsonToMap(request_data)));
                            APIHelper.enqueueWithRetry(suggestionCall, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                                //call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    loader_search.showContent();
                                    if (response.code() == 200) {
                                        try {
                                            JSONObject obj = new JSONObject(response.body().string());
                                            JSONObject resObj = obj.optJSONObject("response");
                                            if (resObj.length() > 0) {

                                                int statusCode = resObj.optInt("status_code", 0);
                                                String errorMsg = resObj.optString("error_message");
                                                if (statusCode == 200) {

                                                    String responseMessage = resObj.optString("success_message");
                                                    JSONArray dataArray = resObj.optJSONArray("data");
                                                    list_string.clear();
                                                    if (dataArray != null && dataArray.length()>0) {

                                                        for(int i=0; i<dataArray.length(); i++){

                                                            JSONObject dataObj = dataArray.getJSONObject(i);
                                                            if(dataObj != null){
                                                                if(!ValidationUtil.isNullOrBlank(dataObj.optString("name"))){
                                                                    list_string.add(dataObj.optString("name"));
                                                                }
                                                            }
                                                        }

                                                        searchSuggestionListAdapter = new SearchSuggestionListAdapter(mContext, list_string);
                                                        recyl_search.setAdapter(searchSuggestionListAdapter);

                                                        searchSuggestionListAdapter.setOnSuggestionClickListener(new SearchSuggestionListAdapter.OnSuggesionClickListener() {
                                                            @Override
                                                            public void onSuggestionClick(int position) {
                                                                if(list_string != null && list_string.size() > position){

                                                                    Intent searchIntent = new Intent(mContext, ProductListActivity.class);
                                                                    searchIntent.putExtra(ProductListActivity.SEARCH_KEYWORD, list_string.get(position));
                                                                    startActivity(searchIntent);

                                                                    finish();
                                                                }
                                                            }
                                                        });


                                                    } else {
                                                        loader_search.showStatusText();
                                                    }

                                                } else {
                                                    loader_search.showStatusText();
                                                }

                                            } else {
                                                loader_search.showStatusText();
                                            }

                                        } catch (Exception e) {

                                            loader_search.showStatusText();
                                            e.printStackTrace();
                                        }
                                    } else {
                                        loader_search.showStatusText();
                                        searchSuggestionListAdapter = new SearchSuggestionListAdapter(mContext, list_string);
                                        recyl_search.setAdapter(searchSuggestionListAdapter);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    if(!call.isCanceled()){
                                        loader_search.showStatusText();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loader_search.showStatusText();
                        }

                    } else {
                        loader_search.showContent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loader_search.showContent();
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(SearchingActivity.this, mContext);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
