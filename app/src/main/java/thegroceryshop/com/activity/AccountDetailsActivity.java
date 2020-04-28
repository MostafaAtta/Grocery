package thegroceryshop.com.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.BuildConfig;
import thegroceryshop.com.R;
import thegroceryshop.com.application.ApiLocalStore;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.DateTimePickUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.RippleButton;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.imagepicker.ImageBean;
import thegroceryshop.com.imagepicker.image.ImagePicker;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

/*
 * Created by umeshk on 20-Mar-17.
 */

public class AccountDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txt_title;
    private AppCompatEditText editTextFirstName;
    private AppCompatEditText editTextLastName;
    private AppCompatEditText editTextEmailAddress;
    private AppCompatEditText editTextMobileNumber;
    private AppCompatEditText editTextCountryCode;
    private AppCompatTextView editTextDOB;
    private RippleButton buttonUpdateProfile;
    private RippleButton buttonUpdatePassword;
    private Activity mActivity;
    private String dob = "";
    DateTime selectedDateTime = DateTime.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateTimePickUtil.SLASH_DATE);
    DateTimeFormatter dateTimeFormatter1 = DateTimeFormat.forPattern("yyyy-MM-dd");

    //for picture add
    private String selected_image_option = "";
    private CircleImageView img_user;
    public static final int RESULT_LOAD_IMAGE_FROM_GALLERY = 9988;
    public static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 9999;
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 126;
    private String userChosenTask;
    private String captured_image_path = "";
    private String user_id = "";
    private File chooseFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_account_details);
        mActivity = this;
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        txt_title = findViewById(R.id.txt_title);
        txt_title.setText(getString(R.string.account_details).toUpperCase());

        setSupportActionBar(toolbar);

        /*DateTimeFormatter dateTimeFormatter1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime datetime = dateTimeFormatter1.parseDateTime("2018-48-27 12:48:22");

        Format dateFormat = android.text.format.DateFormat.getTimeFormat(this);
        String pattern = ((SimpleDateFormat) dateFormat).toLocalizedPattern();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
        dateTimeFormatter.print(datetime);*/

        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            toolbar.setNavigationIcon(R.mipmap.top_back);
        }else{
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        getSupportActionBar().setTitle(AppConstants.BLANK_STRING);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextCountryCode = findViewById(R.id.editTextCountryCode);
        editTextDOB = findViewById(R.id.editTextDOB);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);
        img_user = findViewById(R.id.account_detail_img_user);

        editTextEmailAddress.setEnabled(false);
        editTextMobileNumber.setEnabled(false);
        editTextCountryCode.setEnabled(false);
        user_id = ApiLocalStore.getInstance(mActivity).getUserId();

        editTextEmailAddress.setText(ApiLocalStore.getInstance(mActivity).getUserEmail());
        editTextMobileNumber.setText(ApiLocalStore.getInstance(mActivity).getUserPhone());
        editTextCountryCode.setText("+" + ApiLocalStore.getInstance(mActivity).getUserCountryCode());
        editTextFirstName.setText(ApiLocalStore.getInstance(mActivity).getFirstName());
        editTextLastName.setText(ApiLocalStore.getInstance(mActivity).getLastName());

        dob = ApiLocalStore.getInstance(mActivity).getUserDob();
        if (!dob.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            selectedDateTime = formatter.parseDateTime(dob);
            editTextDOB.setText(dateTimeFormatter.print(selectedDateTime));
        }

        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserImage())) {
            ImageLoader.getInstance().displayImage(OnlineMartApplication.mLocalStore.getUserImage(), img_user, OnlineMartApplication.intitOptions1());
        } else {
            ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, img_user, OnlineMartApplication.intitOptions1());
        }

        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(AccountDetailsActivity.this, AccountDetailsActivity.this);
                if (!ValidationUtil.isNullOrBlank(mActivity, editTextFirstName.getText().toString().trim(), editTextFirstName, "firstName")
                        && !ValidationUtil.isNullOrBlank(mActivity, editTextLastName.getText().toString().trim(), editTextLastName, "lastName")
                        /*&& ValidationUtil.isValidDateOfBirth(mActivity, editTextDOB)*/) {

                    /*if (chooseFile == null) {
                        updateProfileWebService();
                    } else {
                        updateProfile(chooseFile);
                    }*/

                    updateProfile(chooseFile);

                }
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(AccountDetailsActivity.this, AccountDetailsActivity.this);

                //chooseImageDialog();

                new ImagePicker.Builder(AccountDetailsActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.PNG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();

            }
        });

        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.hideSoftKeyboard(AccountDetailsActivity.this, AccountDetailsActivity.this);
                startActivity(new Intent(AccountDetailsActivity.this, UpdatePasswordActivity.class));
            }
        });

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtil.hideSoftKeyboard(AccountDetailsActivity.this, AccountDetailsActivity.this);
                if (selectedDateTime == null) {
                    DateTimePickUtil.showDateSelection(mActivity, DateTime.now(), DateTime.now(), DateTime.now().minusYears(90), new DateTimePickUtil.DateTimePickListener() {
                        @Override
                        public void onDateTimeSelected(DateTime selected) {
                            selectedDateTime = selected;
                            String selected_date = dateTimeFormatter.print(selected);
                            editTextDOB.setText(selected_date);
                        }
                    });
                } else {
                    DateTimePickUtil.showDateSelection(mActivity, selectedDateTime, DateTime.now(), DateTime.now().minusYears(90), new DateTimePickUtil.DateTimePickListener() {
                        @Override
                        public void onDateTimeSelected(DateTime selected) {
                            selectedDateTime = selected;
                            String selected_date = dateTimeFormatter.print(selected);
                            editTextDOB.setText(selected_date);
                        }
                    });
                }
            }
        });
    }

    //image add work
    private void chooseImageDialog() {

        // Creating and Building the Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.choose_from));
        final CharSequence[] choices;

        choices = new CharSequence[2];
        choices[0] = getString(R.string.gallery);
        choices[1] = getString(R.string.camera);
        selected_image_option = choices[0] + "";

        builder.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        selected_image_option = getString(R.string.gallery);
                        break;
                    case 1:
                        selected_image_option = getString(R.string.camera);
                        break;
                }
            }
        });

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (selected_image_option.equalsIgnoreCase(getString(R.string.gallery))) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkReadWriteGalleryPermission()) {
                            openGallery();
                        }
                    } else {
                        openGallery();
                    }
                } else if (selected_image_option.equalsIgnoreCase(getString(R.string.camera))) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkReadWriteCameraPermission(true)) {
                            startCamera();
                        }
                    } else {
                        startCamera();
                    }
                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Window dialogWindow = builder.create().getWindow();
        if (dialogWindow != null) {
            ColorDrawable dialogColor = new ColorDrawable(Color.parseColor("#003849"));
            dialogColor.setAlpha((int) (255 * 0.4f));
            dialogWindow.setBackgroundDrawable(dialogColor);
            dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        builder.create().show();
    }

    private void loadProfile() {
        if (NetworkUtil.networkStatus(mActivity)) {
            //AppUtil.showProgress(mActivity);
            try {

                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("user_id", user_id);

                ApiInterface apiService = ApiClient.createService(ApiInterface.class, mActivity);
                Call<ResponseBody> call = apiService.getUserProfile(new ConvertJsonToMap().jsonToMap(request_data));

                APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                //call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //AppUtil.hideProgress();
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

                                            editTextEmailAddress.setText(ApiLocalStore.getInstance(mActivity).getUserEmail());
                                            editTextMobileNumber.setText(ApiLocalStore.getInstance(mActivity).getUserPhone());
                                            editTextCountryCode.setText("+" + ApiLocalStore.getInstance(mActivity).getUserCountryCode());
                                            editTextFirstName.setText(ApiLocalStore.getInstance(mActivity).getFirstName());
                                            editTextLastName.setText(ApiLocalStore.getInstance(mActivity).getLastName());

                                            dob = ApiLocalStore.getInstance(mActivity).getUserDob();
                                            if (!dob.isEmpty()) {
                                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                                                selectedDateTime = formatter.parseDateTime(dob);
                                                editTextDOB.setText(dateTimeFormatter.print(selectedDateTime));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserImage())) {
                                                ImageLoader.getInstance().displayImage(OnlineMartApplication.mLocalStore.getUserImage(), img_user, OnlineMartApplication.intitOptions1());
                                            } else {
                                                ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, img_user, OnlineMartApplication.intitOptions1());
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
                        //AppUtil.hideProgress();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                //AppUtil.hideProgress();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkReadWriteCameraPermission(boolean isCameraRequired) {

        List<String> permissionsList = new ArrayList<>();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        userChosenTask = getString(R.string.camera);

        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
        return permissionsList.size() == 0;
    }


    private boolean checkReadWriteGalleryPermission() {

        List<String> permissionsList = new ArrayList<>();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        userChosenTask = getString(R.string.gallery);

        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
        return permissionsList.size() == 0;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            int permissionCount = 0;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    permissionCount++;
                }
            }

            if (permissionCount == grantResults.length) {
                if (userChosenTask.equalsIgnoreCase(getString(R.string.camera))) {
                    startCamera();
                } else {
                    openGallery();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<ImageBean> mPaths = (List<ImageBean>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Bitmap bit = BitmapFactory.decodeFile(mPaths.get(0).getImagePath());
            chooseFile = mPaths.get(0).getSelectedFile();
            ImageLoader.getInstance().displayImage(Uri.fromFile(mPaths.get(0).getSelectedFile()).toString(), img_user, OnlineMartApplication.intitOptions1());
        }

        /*if (requestCode == RESULT_LOAD_IMAGE_FROM_GALLERY && resultCode == FragmentActivity.RESULT_OK) {

            // Get the Image from data
            //BitmapFactory.Options options = new BitmapFactory.Options();

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Move to first row
            try {
                int columnIndex;
                String imgDecodableString = null;
                if (cursor != null) {
                    cursor.moveToFirst();
                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                }
                if (imgDecodableString != null) {
                    Bitmap bit = AppUtil.getBitmapFromPath(imgDecodableString);
                    //img_user.setImageBitmap(bit);
                    chooseFile = saveImage(bit);
                    ImageLoader.getInstance().displayImage(Uri.fromFile(chooseFile).toString(), img_user, OnlineMartApplication.intitOptions1());
                }
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA && resultCode == FragmentActivity.RESULT_OK) {

            File camFile = new File(captured_image_path);

            if (!camFile.exists()) {
                try {
                    camFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Bitmap bit = AppUtil.getBitmapFromPath(captured_image_path);
            img_user.setImageBitmap(bit);
            chooseFile = saveImage(bit);
        }*/
    }

    private File saveImage(Bitmap finalBitmap) {

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        //String root = Environment.getExternalStorageDirectory().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "profile_pic_" + timeStamp;
        File myDir = new File(storageDir + "/profile_pic");
        myDir.mkdirs();
        File file = new File(myDir, imageFileName + ".jpg");
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private void startCamera() {
        Intent takePicIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePicIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    try{
                        photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    }catch(Exception e){
                        e.printStackTrace();
                        photoURI = Uri.fromFile(photoFile);
                    }
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
                takePicIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicIntent, RESULT_LOAD_IMAGE_FROM_CAMERA);
            }
        }
    }

    private void openGallery() {
        try {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(pickImageIntent, RESULT_LOAD_IMAGE_FROM_GALLERY);
        } catch (ActivityNotFoundException exp) {
            exp.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en"));
        String timeStamp = simpleDateFormat.format(new Date());
        String imageFileName = "group_image_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        File image = new File(storageDir, imageFileName + ".jpg");
        image.createNewFile();
        captured_image_path = image.getAbsolutePath();
        return image;
    }

    public void updateProfile(File file) {

        MultipartBody.Part body = null;
        if(file != null){
            RequestBody reqFile;
            reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        }

        RequestBody lang_id = RequestBody.create(MediaType.parse("text/plain"), OnlineMartApplication.mLocalStore.getAppLangId());
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), OnlineMartApplication.mLocalStore.getUserId());
        RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), editTextFirstName.getText().toString().trim());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), editTextLastName.getText().toString().trim());
        RequestBody dob1 = RequestBody.create(MediaType.parse("text/plain"), dateTimeFormatter1.print(selectedDateTime));

        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, this);
        AppUtil.showProgress(this);
        Call<ResponseBody> call = apiInterface.updateProfile(body, lang_id, id, firstName, lastName, dob1);

        APIHelper.enqueueWithRetry(call, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
        //call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AppUtil.hideProgress();

                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject resObject = jsonObject.optJSONObject("response");


                        if (resObject.getString("status_code").equalsIgnoreCase("200")) {

                            JSONObject userObj = resObject.optJSONObject("data");
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

                                editTextEmailAddress.setText(ApiLocalStore.getInstance(mActivity).getUserEmail());
                                editTextCountryCode.setText("+" + ApiLocalStore.getInstance(mActivity).getUserCountryCode());
                                editTextMobileNumber.setText(ApiLocalStore.getInstance(mActivity).getUserPhone());
                                editTextFirstName.setText(ApiLocalStore.getInstance(mActivity).getFirstName());
                                editTextLastName.setText(ApiLocalStore.getInstance(mActivity).getLastName());

                                dob = ApiLocalStore.getInstance(mActivity).getUserDob();
                                if (!dob.isEmpty()) {
                                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                                    selectedDateTime = formatter.parseDateTime(dob);
                                    editTextDOB.setText(dateTimeFormatter.print(selectedDateTime));
                                }

                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserImage())) {
                                    ImageLoader.getInstance().displayImage(OnlineMartApplication.mLocalStore.getUserImage(), img_user, OnlineMartApplication.intitOptions1());
                                } else {
                                    ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, img_user, OnlineMartApplication.intitOptions1());
                                }

                                AppUtil.displaySingleActionAlert(mActivity, AppConstants.BLANK_STRING, resObject.optString("success_message"), "OK");


                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppUtil.hideProgress();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadProfile();
        if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getLoginType()) && OnlineMartApplication.mLocalStore.getLoginType().equalsIgnoreCase(AppConstants.TYPE_LOGIN_EMAIL)) {
            buttonUpdatePassword.setVisibility(View.VISIBLE);
        } else {
            buttonUpdatePassword.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtil.hideSoftKeyboard(AccountDetailsActivity.this, AccountDetailsActivity.this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
