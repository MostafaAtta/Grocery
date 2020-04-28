package thegroceryshop.com.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.BuildConfig;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.utils.DeviceUtil;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;

/**
 * Created by mohitd on 24-Feb-17.
 */

public class FeedbackActivity extends AppCompatActivity {

    private LinearLayout lyt_send;
    private Toolbar toolbar;
    private TextView txt_title;
    private TextView baseTxtTitle;
    private LinearLayout header;
    private AppCompatTextView editTextListName;
    private ImageView imageView3;
    private AppCompatEditText editTextList;
    private LinearLayout mCheckoutBtn;
    private TextView textViewCheckOut;
    private int checkItem = -1;
    private String subject = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedback);
        initView();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        txt_title.setText(getResources().getString(R.string.feedback).toUpperCase());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
            toolbar.setNavigationIcon(R.mipmap.top_back);
        } else {
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOption();
            }
        });

        editTextListName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOption();
            }
        });

        lyt_send = findViewById(R.id.mCheckoutBtn);

        lyt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(FeedbackActivity.this, ContactZendeskActivity.class);
                startActivity(intent);*/

                if (subject.isEmpty()) {
                    AppUtil.showErrorDialog(FeedbackActivity.this, getString(R.string.pls_select_an_option));
                } else if (editTextList.getText().toString().trim().isEmpty()) {
                    AppUtil.showErrorDialog(FeedbackActivity.this, getString(R.string.pls_enter_your_feedback_here));
                } else {

                    ApiInterface service = ApiClient.createHelpDeskRetrofitService(FeedbackActivity.this).create(ApiInterface.class);

                    JSONObject operationObject = new JSONObject();
                    JSONObject detailsObject = new JSONObject();
                    JSONObject dataObject = new JSONObject();
                    try {
                        dataObject.put("requester", OnlineMartApplication.mLocalStore.getFirstName() + " " + OnlineMartApplication.mLocalStore.getLastName());
                        dataObject.put("description", editTextList.getText().toString());
                        dataObject.put("requesttemplate", "Unable to browse");
                        dataObject.put("priority", "High");
                        dataObject.put("email", OnlineMartApplication.mLocalStore.getUserEmail());
                        dataObject.put("requesteremail", OnlineMartApplication.mLocalStore.getUserEmail());
                        dataObject.put("site", "Android App");
                        dataObject.put("group", "Call Center agents");

                        if(subject != null && subject.equalsIgnoreCase(getString(R.string.technical))){
                            dataObject.put("category", "Android Technical Issue");
                            dataObject.put("subject", "Technical Issue");
                        }

                        if(subject != null && subject.equalsIgnoreCase(getString(R.string.feedback))){
                            dataObject.put("category", "Feedback");
                            dataObject.put("subject", "Feedback");
                        }

                        if(subject != null && subject.equalsIgnoreCase(getString(R.string.suggestions))){
                            dataObject.put("category", "Suggestions");
                            dataObject.put("subject", "Suggestions");
                        }

                        if(subject != null && subject.equalsIgnoreCase(getString(R.string.complaint))){
                            dataObject.put("category", "Complaint");
                            dataObject.put("subject", "Complaint");
                        }

                        if(subject != null && subject.equalsIgnoreCase(getString(R.string.other))){
                            dataObject.put("category", "Other");
                            dataObject.put("subject", "Other");
                        }

                        dataObject.put("level", "Tier 3");
                        dataObject.put("status", "open");
                        dataObject.put("service", "Email");

                        detailsObject.put("details", dataObject);

                        operationObject.put("operation", detailsObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Call<ResponseBody> call = service.createRequest("json", "ADD_REQUEST", "5BC5F3AB-FA27-4B11-BDC9-94092FF585BC", operationObject.toString());

                    AppUtil.showProgress(FeedbackActivity.this);
                    APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //AppUtil.hideProgress();
                            if (response.code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    JSONObject operationObject = jsonObject.optJSONObject("operation");

                                    if(operationObject != null && operationObject.length() >0 ){

                                        JSONObject resultObject = operationObject.optJSONObject("result");
                                        JSONObject detailsObject = operationObject.optJSONObject("Details");

                                        if (resultObject != null && resultObject.length() > 0 && resultObject.getString("status").equalsIgnoreCase("Success")) {

                                            String status = resultObject.optString("status");
                                            if(status != null && status .equalsIgnoreCase("Success")){

                                                if(detailsObject != null && detailsObject.length() > 0){
                                                    String request_id = detailsObject.optString("WORKORDERID");
                                                    if(request_id != null && request_id.length()>0){
                                                        addNotestoRequest(request_id);

                                                        /*AppUtil.displaySingleActionAlert(FeedbackActivity.this, "", getString(R.string.feedback_submited_successful), getString(R.string.ok), new OnSingleActionListener() {
                                                            @Override
                                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                appDialogSingleAction.dismiss();
                                                                finish();
                                                            }
                                                        });*/

                                                    }else{
                                                        AppUtil.hideProgress();
                                                        AppUtil.displaySingleActionAlert(FeedbackActivity.this, "", getString(R.string.feedback_submited_successful), getString(R.string.ok), new OnSingleActionListener() {
                                                            @Override
                                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                appDialogSingleAction.dismiss();
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                }else{
                                                    AppUtil.hideProgress();
                                                    AppUtil.displaySingleActionAlert(FeedbackActivity.this, "", getString(R.string.feedback_submited_successful), getString(R.string.ok), new OnSingleActionListener() {
                                                        @Override
                                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                            appDialogSingleAction.dismiss();
                                                            finish();
                                                        }
                                                    });
                                                }

                                                /*AppUtil.displaySingleActionAlert(FeedbackActivity.this, "", getString(R.string.feedback_submited_successful), getString(R.string.ok), new OnSingleActionListener() {
                                                    @Override
                                                    public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                        appDialogSingleAction.dismiss();
                                                        finish();
                                                    }
                                                });*/

                                            }else{
                                                AppUtil.hideProgress();
                                                AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-741)");
                                            }
                                        }else{
                                            AppUtil.hideProgress();
                                            AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-740)");
                                        }
                                    }else{
                                        AppUtil.hideProgress();
                                        AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-731)");
                                    }

                                } catch (JSONException e) {
                                    AppUtil.hideProgress();
                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-732)");
                                } catch (IOException e) {
                                    AppUtil.hideProgress();
                                    e.printStackTrace();
                                    AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-733)");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            AppUtil.hideProgress();
                            AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-734)");
                        }
                    });
                }
            }
        });
    }

    private void addNotestoRequest(String request_id) {

        ApiInterface service = ApiClient.createHelpDeskRetrofitService(FeedbackActivity.this).create(ApiInterface.class);

        JSONObject operationObject = new JSONObject();
        JSONObject detailsObject = new JSONObject();
        JSONObject notesObject = new JSONObject();
        JSONObject noteObject = new JSONObject();
        JSONObject object = new JSONObject();

        try {
            object.put("ispublic", false);
            object.put("notestext", getNotesText());

            noteObject.put("note", object);
            notesObject.put("notes", noteObject);
            detailsObject.put("details", notesObject);
            operationObject.put("operation", detailsObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = service.addNotes("json", "ADD_NOTE", request_id, "5BC5F3AB-FA27-4B11-BDC9-94092FF585BC", operationObject.toString());
        APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
            //call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AppUtil.hideProgress();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject operationObject = jsonObject.optJSONObject("operation");

                        if(operationObject != null && operationObject.length() >0 ){

                            JSONObject resultObject = operationObject.optJSONObject("result");

                            if (resultObject != null && resultObject.length() > 0 && resultObject.getString("status").equalsIgnoreCase("Success")) {

                                String status = resultObject.optString("status");
                                if(status != null && status .equalsIgnoreCase("Success")){

                                    AppUtil.displaySingleActionAlert(FeedbackActivity.this, "", getString(R.string.feedback_submited_successful), getString(R.string.ok), new OnSingleActionListener() {
                                        @Override
                                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                            appDialogSingleAction.dismiss();
                                            finish();
                                        }
                                    });

                                }else{
                                    AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-739)");
                                }
                            }else{
                                AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-738)");
                            }
                        }else{
                            AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-737)");
                        }

                    } catch (JSONException e) {
                        AppUtil.hideProgress();
                        e.printStackTrace();
                        AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-736)");
                    } catch (IOException e) {
                        AppUtil.hideProgress();
                        e.printStackTrace();
                        AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-735)");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                AppUtil.hideProgress();
                AppUtil.showErrorDialog(FeedbackActivity.this,getString(R.string.error_msg) + "(ERR-736)");
            }
        });


    }

    private String getNotesText() {

        String type = AppConstants.BLANK_STRING;
        switch (OnlineMartApplication.mLocalStore.getLoginType()){
            case "1" :
                type = "Email";
                break;
            case "2" :
                type = "Google";
                break;
            case "3" :
                type = "Facebook";
                break;
        }

        return "Mobile Number : +" + OnlineMartApplication.mLocalStore.getUserCountryCode() + AppConstants.SPACE + OnlineMartApplication.mLocalStore.getUserPhone()
                + "  |  Login Type : " + type
                + "  |  OS Version : " + DeviceUtil.getOsVersion()
                + "  |  App Version : " + BuildConfig.VERSION_NAME;
    }

    private void showOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence items[] = new CharSequence[]{getString(R.string.feedback), getString(R.string.complaint), getString(R.string.technical), getString(R.string.suggestions), getString(R.string.other)};
        builder.setSingleChoiceItems(items, checkItem, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                // ...
                checkItem = position;
                subject = items[position].toString();
                editTextListName.setText(subject);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_caps), null);
        builder.setTitle(null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void initView() {
        baseTxtTitle = findViewById(R.id.base_txt_title);
        header = findViewById(R.id.header);
        editTextListName = findViewById(R.id.editTextListName);
        imageView3 = findViewById(R.id.imageView3);
        editTextList = findViewById(R.id.editTextList);
        mCheckoutBtn = findViewById(R.id.mCheckoutBtn);
        textViewCheckOut = findViewById(R.id.textViewCheckOut);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
