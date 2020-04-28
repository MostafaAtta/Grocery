/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package thegroceryshop.com.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.custom.NetworkUtil;
import thegroceryshop.com.custom.StrikeTextView;
import thegroceryshop.com.custom.ValidationUtil;
import thegroceryshop.com.custom.barcode.BarcodeDetectionListener;
import thegroceryshop.com.custom.barcode.BarcodeGraphic;
import thegroceryshop.com.custom.barcode.BarcodeTrackerFactory;
import thegroceryshop.com.custom.barcode.CameraSource;
import thegroceryshop.com.custom.barcode.CameraSourcePreview;
import thegroceryshop.com.custom.barcode.GraphicOverlay;
import thegroceryshop.com.custom.loader.LoaderLayout;
import thegroceryshop.com.dialog.AppDialogSingleAction;
import thegroceryshop.com.dialog.BarCodeDialog;
import thegroceryshop.com.dialog.OnDoubleActionListener;
import thegroceryshop.com.dialog.OnSingleActionListener;
import thegroceryshop.com.dialog.WishListDialog;
import thegroceryshop.com.modal.CartItem;
import thegroceryshop.com.modal.Product;
import thegroceryshop.com.modal.WishListBean;
import thegroceryshop.com.utils.LocaleHelper;
import thegroceryshop.com.webservices.APIHelper;
import thegroceryshop.com.webservices.ApiClient;
import thegroceryshop.com.webservices.ApiInterface;
import thegroceryshop.com.webservices.ConvertJsonToMap;

import static thegroceryshop.com.R.id.product_txt_add_to_cart;
import static thegroceryshop.com.application.OnlineMartApplication.mLocalStore;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureActivity extends AppCompatActivity {
    private static final String TAG = "Barcode-reader";

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;


    private ImageView productItemImg;
    private TextView productTxtName;
    private TextView productTxtBrandName;
    private TextView productTxtQuantity;
    private StrikeTextView productItemTxtActualPrice;
    private TextView productItemTxtFinalPrice;
    private TextView productTxtShippedIn;
    private TextView productTxtSoldOut;
    private TextView productTxtOffer;
    private LoaderLayout list_product_loader_products;
    private TextView productItemTxtAddToCart;
    private LinearLayout productItemLytQuantity;
    private TextView productItemTxtPlusQuantity;
    private TextView productItemTxtOrderQuantity;
    private TextView productItemTxtMinusQuantity;
    private TextView productItemTxtSaveToList;
    private Call<ResponseBody> call;
    private ApiInterface apiService;
    private TextView txt_add_to_cart;
    private TextView txt_plus, txt_minus;
    private TextView txt_add_or_remove, txt_eligible, txt_cart_capecity;
    private TextView txt_selected_quantity, txt_title;
    private TextView txt_item_remaining;
    private TextView txt_save_to_list;
    private RelativeLayout lyt_quantity;
    Product product;
    private LinearLayout lyt_add_to_cart, lyt_progress;
    private ProgressBar progress_cart;
    private Handler cart_progress_handler;
    private Runnable cart_progress_runnable;
    private Toolbar toolbar;
    private boolean isDialogShowing = false;
    private LoaderLayout loader_quantity;
    private Context mContext;
    private Call<ResponseBody> loadInfoCall;
    private ArrayList<String> list_alert = new ArrayList<>();
    private LoaderLayout loader_img;
    private BarCodeDialog appDialogDoubleAction;
    private SimpleRatingBar productRating;
    private TextView txt_rating_count;
    private ImageView img_save_to_list;

    private WishListDialog.OnAddToWishListLister onAddToWishListLister;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_code_activity);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt_title = findViewById(R.id.base_txt_title);
        txt_title.setText(getResources().getString(R.string.product_scan_title).toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AppConstants.BLANK_STRING);
        apiService = ApiClient.createService(ApiInterface.class, this);
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);

        appDialogDoubleAction = new BarCodeDialog(BarcodeCaptureActivity.this, getString(R.string.title), getString(R.string.no_product_on_barcode), getString(R.string.send_us_feedback), getString(R.string.scan_again).toUpperCase());

        if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("2")) {
            toolbar.setNavigationIcon(R.mipmap.top_back_arabic);
        }

        list_product_loader_products = findViewById(R.id.list_product_loader_products);
        list_product_loader_products.setStatuText(getString(R.string.pls_scan_barcode));
        list_product_loader_products.showStatusText();

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            initView();
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        onAddToWishListLister = new WishListDialog.OnAddToWishListLister() {
            @Override
            public void onAddedToWishListListener(int position, String product_id) {
                if(product != null){
                    img_save_to_list.setImageDrawable(ContextCompat.getDrawable(BarcodeCaptureActivity.this, R.drawable.icon_heart_filled));
                    product.setAddedToWishList(true);
                    OnlineMartApplication.updateWishlistFlagOnCart(product.getId(), true);
                }
            }
        };
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        //findViewById(R.id.topLayout).setOnClickListener(listener);
        /*Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
        barcodeFactory.setBarcodeDetectionListener(new BarcodeDetectionListener() {
            @Override
            public void onBarcodeDetected(final String barcode) {

                //Toast.makeText(BarcodeCaptureActivity.this, barcode, Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBarCodeSearchProduct(barcode);
                    }
                });

                /*if(mPreview != null){
                    mPreview.stop();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startCameraSource();
                        }
                    }, 1000);
                }*/
            }
        });

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            initView();
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        //float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        //float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        float x = mPreview.getWidth();
        float y = mPreview.getHeight();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode != null) {
                if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                    // Exact hit, no need to keep looking.
                    best = barcode;
                    break;
                }
                float dx = x - barcode.getBoundingBox().centerX();
                float dy = y - barcode.getBoundingBox().centerY();
                float distance = (dx * dx) + (dy * dy);  // actually squared distance
                if (distance < bestDistance) {
                    best = barcode;
                    bestDistance = distance;
                }
            }
        }

        return best != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    private void initView() {
        //contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        productItemImg = findViewById(R.id.product_item_img);
        productTxtName = findViewById(R.id.product_txt_name);
        productTxtBrandName = findViewById(R.id.product_txt_brand_name);
        productTxtQuantity = findViewById(R.id.product_txt_quantity);
        productItemTxtActualPrice = findViewById(R.id.product_item_txt_actual_price);
        productItemTxtFinalPrice = findViewById(R.id.product_item_txt_final_price);
        productTxtShippedIn = findViewById(R.id.product_txt_shipped_in);
        productTxtSoldOut = findViewById(R.id.product_txt_sold_out);
        productTxtOffer = findViewById(R.id.product_txt_offer);
        loader_quantity = findViewById(R.id.product_lyt_loader_quantity);
        loader_img = findViewById(R.id.loader_img);
        txt_item_remaining = findViewById(R.id.product_txt_remaining);
        img_save_to_list = findViewById(R.id.product_img_save_to_list);

        txt_item_remaining.setVisibility(View.GONE);

        //ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        //mScannerView = new ZBarScannerView(this);
        //contentFrame.addView(mScannerView);

        productItemTxtAddToCart = findViewById(R.id.product_item_txt_add_to_cart);
        productItemLytQuantity = findViewById(R.id.product_item_lyt_quantity);
        productItemTxtPlusQuantity = findViewById(R.id.product_item_txt_plus_quantity);
        productItemTxtOrderQuantity = findViewById(R.id.product_item_txt_order_quantity);
        productItemTxtMinusQuantity = findViewById(R.id.product_item_txt_minus_quantity);
        img_save_to_list = findViewById(R.id.product_img_save_to_list);
        productRating = findViewById(R.id.product_rating);
        txt_rating_count = findViewById(R.id.product_text_review_count);


        txt_add_to_cart = findViewById(product_txt_add_to_cart);
        txt_plus = findViewById(R.id.product_txt_plus_quantity);
        txt_minus = findViewById(R.id.product_txt_minus_quantity);
        txt_selected_quantity = findViewById(R.id.product_txt_order_quantity);
        lyt_quantity = findViewById(R.id.product_lyt_quantity);


        txt_cart_capecity = findViewById(R.id.product_txt_cart_capecity);
        lyt_progress = findViewById(R.id.product_lyt_progress);
        progress_cart = findViewById(R.id.product_progress_cart);
        txt_eligible = findViewById(R.id.product_txt_eligible);
        txt_add_or_remove = findViewById(R.id.product_txt_cart_add_or_remove);

        lyt_add_to_cart = findViewById(R.id.product_lyt_add_cart);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.cart_progress);
        progress_cart.setProgressDrawable(drawable);
        progress_cart.setMax((int) mLocalStore.getFreeShippingAmount());
        txt_eligible.setVisibility(View.GONE);

        productItemTxtAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productItemLytQuantity.setVisibility(View.VISIBLE);
            }
        });

        img_save_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                if(product != null){
                                    final WishListDialog wishListDialog = new WishListDialog(BarcodeCaptureActivity.this, R.style.AddressDialog, listNames, product.getId());
                                    wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                    wishListDialog.show();
                                }
                            }else{
                                if(product != null){
                                    final WishListDialog wishListDialog = new WishListDialog(BarcodeCaptureActivity.this, R.style.AddressDialog, listNames, product.getId());
                                    wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                                    wishListDialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(product != null){
                            final WishListDialog wishListDialog = new WishListDialog(BarcodeCaptureActivity.this, R.style.AddressDialog, listNames, product.getId());
                            wishListDialog.setOnAddToWishListLister(onAddToWishListLister);
                            wishListDialog.show();
                        }
                    }
                } else {
                    AppUtil.requestLogin(BarcodeCaptureActivity.this);
                }

            }
        });


        txt_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product != null) {

                    if (txt_add_to_cart.getText().toString().equalsIgnoreCase(mContext.getString(R.string.notify_me))) {
                        if (!ValidationUtil.isNullOrBlank(mLocalStore.getUserId())) {
                            notifyme(product.getId());
                        } else {
                            AppUtil.requestLogin(mContext);
                        }
                    } else {
                        if (product.getSelectedQuantity() == 0) {

                            if (!product.isSoldOut()) {

                                txt_add_to_cart.setVisibility(View.GONE);
                                product.setAddedToCart(true);
                                loader_quantity.setVisibility(View.VISIBLE);
                                //loader_quantity.showProgress();
                                OnlineMartApplication.addToCart(AppUtil.getCartObject(product));
                                lyt_quantity.setVisibility(View.VISIBLE);
                                txt_selected_quantity.setVisibility(View.VISIBLE);
                                txt_selected_quantity.setText(product.getSelectedQuantity() + "");

                                txt_add_to_cart.setVisibility(View.GONE);
                                loader_quantity.setVisibility(View.VISIBLE);
                                //loader_quantity.showProgress();

                                loadProductInfo(product.getId(), -1, "addToCart");

                                loader_quantity.showContent();

                                if (product.getMaxQuantity() >= 1) {
                                    OnlineMartApplication.addToCart(AppUtil.getCartObject(product));
                                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);
                                    updateCartProgress(
                                            product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                            true);
                                    updateData();
                                } else {

                                }

                                invalidateOptionsMenu();

                            }
                        }
                    }

                }


            }
        });

        txt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product != null && product.getSelectedQuantity() < product.getMaxQuantity()) {

                    txt_add_to_cart.setVisibility(View.GONE);
                    product.setAddedToCart(true);
                    loader_quantity.setVisibility(View.VISIBLE);
                    //loader_quantity.showProgress();

                    product.setSelectedQuantity(product.getSelectedQuantity() + 1);
                    lyt_quantity.setVisibility(View.VISIBLE);
                    loader_quantity.setVisibility(View.VISIBLE);
                    txt_selected_quantity.setVisibility(View.VISIBLE);
                    txt_selected_quantity.setText(product.getSelectedQuantity() + "");

                    txt_add_to_cart.setVisibility(View.GONE);
                    loader_quantity.setVisibility(View.VISIBLE);
                    product.setQuantityUpdated(true);
                    //loader_quantity.showProgress();

                    loadProductInfo(product.getId(), -1, "add");

                    loader_quantity.showContent();
                    if (cart_progress_handler != null) {
                        lyt_add_to_cart.setVisibility(View.GONE);
                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                    }

                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {
                        OnlineMartApplication.increaseORDecreaseQtyOnCart(true, product.getId());
                        updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), true);
                    } else {

                    }

                    txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);

                } else {
                    AppUtil.showErrorDialog(BarcodeCaptureActivity.this, String.format(getString(R.string.max_quantity_reached_msg) + AppConstants.BLANK_STRING, product.getMaxQuantity()));
                }

                invalidateOptionsMenu();
            }
        });

        txt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product != null) {

                    txt_add_to_cart.setVisibility(View.GONE);
                    loader_quantity.setVisibility(View.VISIBLE);
                    //loader_quantity.showProgress();
                    loadProductInfo(product.getId(), -1, "remove");

                    loader_quantity.showContent();
                    if (cart_progress_handler != null) {
                        lyt_add_to_cart.setVisibility(View.GONE);
                        cart_progress_handler.removeCallbacks(cart_progress_runnable);
                    }

                    if (product.getSelectedQuantity() <= product.getMaxQuantity()) {

                        if (product.getSelectedQuantity() <= 1) {
                            product.setAddedToCart(false);
                            //product.setSelectedQuantity(0);
                            lyt_add_to_cart.setVisibility(View.GONE);

                            if (cart_progress_handler != null) {
                                lyt_add_to_cart.setVisibility(View.GONE);
                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                            }

                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, product.getId());
                            updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), false);
                        }

                        if (product.getSelectedQuantity() != 0) {
                            product.setSelectedQuantity(product.getSelectedQuantity() - 1);

                            if (cart_progress_handler != null) {
                                lyt_add_to_cart.setVisibility(View.GONE);
                                cart_progress_handler.removeCallbacks(cart_progress_runnable);
                            }

                            if (product.getSelectedQuantity() == 0) {
                                txt_add_to_cart.setVisibility(View.VISIBLE);
                                loader_quantity.setVisibility(View.GONE);
                                lyt_quantity.setVisibility(View.GONE);
                                product.setAddedToCart(false);
                                product.setSelectedQuantity(0);
                            }

                            OnlineMartApplication.increaseORDecreaseQtyOnCart(false, product.getId());
                            updateCartProgress(product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(), false);
                            txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                        }
                    } else {

                    }

                    txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                }
                invalidateOptionsMenu();
            }
        });

        cart_progress_runnable = new Runnable() {
            @Override
            public void run() {
                lyt_add_to_cart.setVisibility(View.GONE);
            }
        };
    }

    private void notifyme(String product_id) {

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", product_id);
            request_data.put("user_id", mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(BarcodeCaptureActivity.this)) {
                try {
                    Call<ResponseBody> call = apiService.notifyMe((new ConvertJsonToMap().jsonToMap(request_data)));
                    AppUtil.showProgress(BarcodeCaptureActivity.this);

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
                                            AppUtil.displaySingleActionAlert(BarcodeCaptureActivity.this, getString(R.string.title), responseMessage, getString(R.string.ok));

                                        } else {
                                            AppUtil.displaySingleActionAlert(BarcodeCaptureActivity.this, getString(R.string.title), errorMsg, getString(R.string.ok));
                                        }

                                    } else {
                                        AppUtil.displaySingleActionAlert(BarcodeCaptureActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-679)", getString(R.string.ok));
                                    }

                                } catch (Exception e) {//(JSONException | IOException e) {

                                    e.printStackTrace();
                                    AppUtil.displaySingleActionAlert(BarcodeCaptureActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-680)", getString(R.string.ok));
                                }
                            } else {
                                AppUtil.displaySingleActionAlert(BarcodeCaptureActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-681)", getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AppUtil.hideProgress();
                            AppUtil.displaySingleActionAlert(BarcodeCaptureActivity.this, getString(R.string.title), getString(R.string.error_msg) + "(Err-682)", getString(R.string.ok));
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

    private void mBarCodeSearchProduct(String code_id) {
        if (!isDialogShowing) {
            try {

                list_product_loader_products = findViewById(R.id.list_product_loader_products);
                list_product_loader_products.setStatuText(getString(R.string.pls_scan_barcode));

                list_product_loader_products.showProgress();

                if (mPreview != null) {
                    mPreview.stop();
                }

                JSONObject request_data = new JSONObject();
                request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
                request_data.put("prodcut_bar_code", code_id);
                request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

                call = apiService.loadBarCodeProducts((new ConvertJsonToMap().jsonToMap(request_data)));

                call.enqueue(new Callback<ResponseBody>() {
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
                                        startCameraSource();
                                        list_product_loader_products.showContent();
                                        String responseMessage = resObj.optString("success_message");
                                        JSONObject dataObj = resObj.optJSONObject("data");
                                        JSONObject productObject = dataObj.getJSONObject("Product");

                                        product = new Product();

                                        if (productObject != null) {

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("product_id"))) {
                                                product.setId(productObject.optString("product_id"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("english_product_name"))) {
                                                product.setEnglishName(productObject.optString("english_product_name"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("arabic_product_name"))) {
                                                product.setArabicName(productObject.optString("arabic_product_name"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("english_product_size"))) {
                                                product.setEnglishQuantity(productObject.optString("english_product_size"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("arabic_product_size"))) {
                                                product.setArabicQuantity(productObject.optString("arabic_product_size"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("english_brand_name"))) {
                                                product.setEnglishBrandName(productObject.optString("english_brand_name"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("arabic_brand_name"))) {
                                                product.setArabicBrandName(productObject.optString("arabic_brand_name"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("image"))) {
                                                product.setImage(productObject.optString("image"));
                                            }

                                            product.setAddedToWishList(productObject.optBoolean("in_wishlist", false));

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("currency"))) {
                                                product.setCurrency(productObject.optString("currency"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("product_acutal_price"))) {
                                                product.setActualPrice(productObject.optString("product_acutal_price"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("product_offer_price"))) {
                                                product.setOfferedPrice(productObject.optString("product_offer_price"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("brand_id"))) {
                                                product.setBrandId(productObject.optString("brand_id"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optDouble("tax"))) {
                                                product.setTaxCharges((float) productObject.optDouble("tax", 0.00f));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optInt("product_average_rating"))) {
                                                product.setAvgRating(productObject.optInt("product_average_rating", 0));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optInt("product_rating_count"))) {
                                                product.setRatingCount(productObject.optInt("product_rating_count", 0));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optInt("product_average_rating"))) {
                                                product.setAvgRating(productObject.optInt("product_average_rating", 0));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optInt("product_rating_count"))) {
                                                product.setRatingCount(productObject.optInt("product_rating_count", 0));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("door_step"))) {
                                                if (productObject.optString("door_step").equalsIgnoreCase("1")) {
                                                    product.setDoorStepDelivery(true);
                                                } else {
                                                    product.setDoorStepDelivery(false);
                                                }
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optBoolean("offered_price_status", false))) {
                                                product.setOffer(productObject.optBoolean("offered_price_status", false));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("product_offer_price"))) {
                                                product.setOfferedPrice(productObject.optString("product_offer_price"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optDouble("product_offer_percent", 0.0f))) {
                                                //product.setOfferString(productObject.optString("product_offer_percent") + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                                product.setOfferString(Math.round(productObject.optDouble("product_offer_percent", 0.0f)) + "%" + AppConstants.SPACE + getString(R.string.off).toUpperCase());
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optBoolean("shipping_third_party", false))) {
                                                product.setShippingThirdParty(productObject.optBoolean("shipping_third_party", false));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("shipping_hours"))) {
                                                product.setShippingHours(productObject.optString("shipping_hours"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("mark_sold_qty"))) {
                                                product.setMarkSoldQuantity(productObject.optInt("mark_sold_qty"));
                                            }

                                            if (!ValidationUtil.isNullOrBlank(productObject.optString("qty"))) {
                                                if (productObject.optInt("qty") < 0) {
                                                    product.setMaxQuantity(0);
                                                } else {
                                                    product.setMaxQuantity(productObject.optInt("qty"));
                                                }
                                            } else {
                                                product.setMaxQuantity(0);
                                            }

                                            if (product.getMaxQuantity() <= 0) {
                                                product.setSoldOut(true);
                                            } else {
                                                product.setSoldOut(false);
                                            }
                                            mSetProductDetails(product);
                                        }
                                    } else if(statusCode == 102){

                                        isDialogShowing = true;

                                        if (mPreview != null) {
                                            mPreview.stop();
                                        }

                                        final AppDialogSingleAction appDialogSingleAction1 = new AppDialogSingleAction(BarcodeCaptureActivity.this, getString(R.string.app_name), errorMsg, getString(R.string.ok));
                                        appDialogSingleAction1.show();
                                        appDialogSingleAction1.setOnSingleActionListener(new OnSingleActionListener() {
                                            @Override
                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                isDialogShowing = false;
                                                appDialogSingleAction1.dismiss();
                                                startCameraSource();
                                            }
                                        });

                                        appDialogSingleAction1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                isDialogShowing = false;
                                                startCameraSource();
                                                //mScannerView.startCamera();
                                            }
                                        });

                                    } else {
                                        isDialogShowing = true;

                                        if (mPreview != null) {
                                            mPreview.stop();
                                        }

                                        //mScannerView.stopCamera();
                                        if(!appDialogDoubleAction.isShowing()){
                                            appDialogDoubleAction.show();
                                        }
                                        appDialogDoubleAction.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                isDialogShowing = false;
                                                startCameraSource();
                                                //mScannerView.startCamera();
                                            }
                                        });
                                        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                            @Override
                                            public void onLeftActionClick(View view) {
                                                isDialogShowing = false;
                                                appDialogDoubleAction.dismiss();

                                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                                                    Intent intent = new Intent(BarcodeCaptureActivity.this, FeedbackActivity.class);
                                                    startActivity(intent);

                                                } else {
                                                    AppUtil.requestLogin(BarcodeCaptureActivity.this);
                                                }
                                            }

                                            @Override
                                            public void onRightActionClick(View view) {
                                                isDialogShowing = false;
                                                appDialogDoubleAction.dismiss();
                                                startCameraSource();

                                                //mScannerView.startCamera();
                                            }
                                        });

                                        list_product_loader_products.showStatusText();
                                    }
                                } else {
                                    startCameraSource();
                                }
                            } catch (Exception e) {
                                list_product_loader_products.showStatusText();
                                isDialogShowing = true;

                                e.printStackTrace();
                                //mScannerView.stopCamera();
                                if (mPreview != null) {
                                    mPreview.stop();
                                }
                                try {
                                    if(!appDialogDoubleAction.isShowing()){
                                        appDialogDoubleAction.show();
                                    }
                                    appDialogDoubleAction.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            isDialogShowing = false;
                                            startCameraSource();
                                            //mScannerView.startCamera();
                                        }
                                    });
                                    appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                                        @Override
                                        public void onLeftActionClick(View view) {
                                            isDialogShowing = false;
                                            appDialogDoubleAction.dismiss();

                                            if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                                                Intent intent = new Intent(BarcodeCaptureActivity.this, FeedbackActivity.class);
                                                startActivity(intent);

                                            } else {
                                                AppUtil.requestLogin(BarcodeCaptureActivity.this);
                                            }
                                        }

                                        @Override
                                        public void onRightActionClick(View view) {
                                            isDialogShowing = false;
                                            appDialogDoubleAction.dismiss();
                                            startCameraSource();

                                            //mScannerView.startCamera();
                                        }
                                    });
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    startCameraSource();
                                }
                            }
                        } else {
                            startCameraSource();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        list_product_loader_products.showStatusText();
                        isDialogShowing = true;

                        //mScannerView.stopCamera();

                        if (mPreview != null) {
                            mPreview.stop();
                        }
                        if(!appDialogDoubleAction.isShowing()){
                            appDialogDoubleAction.show();
                        }
                        appDialogDoubleAction.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                isDialogShowing = false;
                                startCameraSource();
                                //mScannerView.startCamera();
                            }
                        });
                        appDialogDoubleAction.setOnDoubleActionsListener(new OnDoubleActionListener() {
                            @Override
                            public void onLeftActionClick(View view) {
                                isDialogShowing = false;
                                appDialogDoubleAction.dismiss();

                                if (!ValidationUtil.isNullOrBlank(OnlineMartApplication.mLocalStore.getUserId())) {

                                    Intent intent = new Intent(BarcodeCaptureActivity.this, FeedbackActivity.class);
                                    startActivity(intent);

                                } else {
                                    AppUtil.requestLogin(BarcodeCaptureActivity.this);
                                }
                            }

                            @Override
                            public void onRightActionClick(View view) {
                                isDialogShowing = false;
                                appDialogDoubleAction.dismiss();
                                startCameraSource();

                                //mScannerView.startCamera();
                            }
                        });

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void mSetProductDetails(Product product) {

        if (product.getAvgRating() <= 0 || product.getRatingCount() <= 0) {
            productRating.setVisibility(View.GONE);
            txt_rating_count.setVisibility(View.GONE);
        } else {
            productRating.setVisibility(View.VISIBLE);
            txt_rating_count.setVisibility(View.VISIBLE);
            productRating.setRating(product.getAvgRating());
            txt_rating_count.setText("(" + product.getRatingCount() + " " + getString(R.string.review_caps) + ")");
        }

        if (!ValidationUtil.isNullOrBlank(product.getLabel())) {
            productTxtName.setText(product.getLabel());
        } else {
            productTxtName.setText(AppConstants.BLANK_STRING);
        }

        if (!ValidationUtil.isNullOrBlank(product.getBrandName())) {
            productTxtBrandName.setText(product.getBrandName());
        } else {
            productTxtBrandName.setText(AppConstants.BLANK_STRING);
        }

        img_save_to_list.setImageDrawable(ContextCompat.getDrawable(BarcodeCaptureActivity.this, R.drawable.icon_heart_filled));

        if (!ValidationUtil.isNullOrBlank(product.getQuantity())) {
            productTxtQuantity.setText(product.getQuantity());
        } else {
            productTxtQuantity.setText(AppConstants.BLANK_STRING);
        }

        if (product.isOffer()) {
            productItemTxtActualPrice.setText(product.getActualPrice());
            productItemTxtActualPrice.setVisibility(View.VISIBLE);

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                productItemTxtFinalPrice.setText(getString(R.string.egp) + AppConstants.SPACE + product.getOfferedPrice());
            } else {
                productItemTxtFinalPrice.setText(product.getOfferedPrice() + AppConstants.SPACE + getString(R.string.egp));
            }

        } else {
            productItemTxtActualPrice.setVisibility(View.GONE);

            if (OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")) {
                productItemTxtFinalPrice.setText(getString(R.string.egp) + AppConstants.SPACE + product.getActualPrice());
            } else {
                productItemTxtFinalPrice.setText(product.getActualPrice() + AppConstants.SPACE + getString(R.string.egp));
            }

        }

        if (!ValidationUtil.isNullOrBlank(product.getOfferString()) && product.isOffer()) {
            productTxtOffer.setVisibility(View.VISIBLE);
            productTxtOffer.setText(product.getOfferString());
        } else {
            productTxtOffer.setVisibility(View.INVISIBLE);
        }

        CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());
        loader_quantity.showContent();

        if (cartItem != null) {
            product.setAddedToCart(true);
            product.setSelectedQuantity(cartItem.getSelectedQuantity());
            txt_add_to_cart.setVisibility(View.GONE);
            lyt_quantity.setVisibility(View.VISIBLE);
            loader_quantity.setVisibility(View.VISIBLE);
            txt_selected_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);
        } else {
            txt_add_to_cart.setVisibility(View.VISIBLE);
            loader_quantity.setVisibility(View.GONE);
            lyt_quantity.setVisibility(View.GONE);
        }


        if (product.isShippingThirdParty()) {
            productTxtShippedIn.setVisibility(View.VISIBLE);
            productTxtShippedIn.setText(String.format(getResources().getString(R.string.shipping_in_n_hours), product.getShippingHours()));
        } else {
            productTxtShippedIn.setVisibility(View.GONE);
        }

        if (product.isSoldOut()) {
            productTxtSoldOut.setVisibility(View.VISIBLE);
            txt_add_to_cart.setText(getResources().getString(R.string.notify_me).toUpperCase());
            txt_item_remaining.setVisibility(View.GONE);
        } else {
            productTxtSoldOut.setVisibility(View.GONE);
            txt_add_to_cart.setText(getResources().getString(R.string.add_to_cart_caps).toUpperCase());

            if (product.getMaxQuantity() <= product.getMarkSoldQuantity()) {
                txt_item_remaining.setVisibility(View.VISIBLE);
                txt_item_remaining.setText(String.format(getString(R.string.remaining).toUpperCase(),  product.getMaxQuantity() + AppConstants.BLANK_STRING));
            } else {
                txt_item_remaining.setVisibility(View.GONE);
            }
        }


        productItemImg.setMinimumHeight(loader_img.getHeight());

        loader_img.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) productItemImg.getLayoutParams();
                layoutParams.height = loader_img.getHeight();
                layoutParams.width = loader_img.getHeight();
                productItemImg.setLayoutParams(layoutParams);
            }
        });


        if (!ValidationUtil.isNullOrBlank(product.getImage())) {
            final Drawable alternate_image = ContextCompat.getDrawable(BarcodeCaptureActivity.this, R.mipmap.place_holder);
            ImageLoader.getInstance().displayImage(product.getImage(), productItemImg, OnlineMartApplication.intitOptions(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    loader_img.showProgress();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    loader_img.showContent();
                    productItemImg.setImageDrawable(alternate_image);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loader_img.showContent();
                            productItemImg.setImageBitmap(loadedImage);
                        }
                    }, 500);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    loader_img.showContent();
                    productItemImg.setImageDrawable(alternate_image);
                }
            });
        } else {
            ImageLoader.getInstance().displayImage(AppConstants.BLANK_STRING, productItemImg, OnlineMartApplication.intitOptions());
        }
    }

    private void updateCartProgress(final String priceString, final boolean isAddToCart) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                String updatedTotal = null;
                if (isAddToCart) {
                    txt_add_or_remove.setText(String.format(getString(R.string.product_added_to_cart), "1" + AppConstants.BLANK_STRING).toUpperCase());
                    updatedTotal = " "+ getString(R.string.egp) +" " + Math.round(current_total + price);
                } else {
                    txt_add_or_remove.setText(getString(R.string.product_removed_from_cart));
                    updatedTotal = " "+ getString(R.string.egp) +" " + Math.round(current_total - price);
                }

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

                //txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), remaingEGP + AppConstants.BLANK_STRING).toUpperCase());
                cart_progress_handler.postDelayed(cart_progress_runnable, 4000);

            }
        });

    }

    private void updateData() {


        if (product != null) {

            if (!ValidationUtil.isNullOrBlank(product.getQuantity())) {
                productTxtQuantity.setText(product.getQuantity());
            } else {
                productTxtQuantity.setText(AppConstants.BLANK_STRING);
            }

            if (product.isOffer()) {
                productItemTxtActualPrice.setVisibility(View.VISIBLE);
                productItemTxtActualPrice.setText(product.getActualPrice()/* + AppConstants.SPACE + product.getCurrency()*/);
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    productItemTxtFinalPrice.setText(getString(R.string.egp)+ AppConstants.SPACE +product.getOfferedPrice());
                }else{
                    productItemTxtFinalPrice.setText(product.getOfferedPrice() + AppConstants.SPACE + getString(R.string.egp));
                }
                //productItemTxtFinalPrice.setText(getString(R.string.egp) + AppConstants.SPACE + product.getOfferedPrice());
            } else {
                productItemTxtActualPrice.setVisibility(View.GONE);
                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    productItemTxtFinalPrice.setText(getString(R.string.egp)+ AppConstants.SPACE +product.getActualPrice());
                }else{
                    productItemTxtFinalPrice.setText(product.getActualPrice() + AppConstants.SPACE + getString(R.string.egp));
                }

                //productItemTxtFinalPrice.setText(getString(R.string.egp) + AppConstants.SPACE + product.getActualPrice());
            }

            img_save_to_list.setImageDrawable(ContextCompat.getDrawable(BarcodeCaptureActivity.this, R.drawable.icon_heart_filled));

            CartItem cartItem = OnlineMartApplication.checkProductInCart(product.getId());

            if (cartItem != null) {
                product.setAddedToCart(true);
                product.setSelectedQuantity(cartItem.getSelectedQuantity());
                txt_add_to_cart.setVisibility(View.GONE);
                lyt_quantity.setVisibility(View.VISIBLE);
                loader_quantity.setVisibility(View.VISIBLE);

                if (cartItem.getSelectedQuantity() > product.getMaxQuantity()) {
                    product.setSelectedQuantity(product.getMaxQuantity());
                    OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());

                    AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.notify_out_of_stock), product.getLabel()), getString(R.string.ok));
                    appDialogSingleAction.show();
                    appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                        @Override
                        public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                            appDialogSingleAction.dismiss();
                        }
                    });
                }

                txt_selected_quantity.setText(cartItem.getSelectedQuantity() + AppConstants.BLANK_STRING);


            } else {
                txt_add_to_cart.setVisibility(View.VISIBLE);
                lyt_quantity.setVisibility(View.GONE);
                loader_quantity.setVisibility(View.GONE);
            }

            if (product.isShippingThirdParty()) {
                productTxtShippedIn.setVisibility(View.VISIBLE);
                productTxtShippedIn.setText(String.format(mContext.getString(R.string.shipping_in_n_hours), product.getShippingHours()));
            } else {
                productTxtShippedIn.setVisibility(View.GONE);
            }

            if (product.isSoldOut()) {
                productTxtSoldOut.setVisibility(View.VISIBLE);
                productTxtShippedIn.setVisibility(View.GONE);
                txt_add_to_cart.setText(mContext.getString(R.string.notify_me).toUpperCase());
                txt_item_remaining.setVisibility(View.GONE);

            } else {
                productTxtSoldOut.setVisibility(View.GONE);

                if (product.getMaxQuantity() <= product.getMarkSoldQuantity()) {
                    txt_item_remaining.setVisibility(View.VISIBLE);
                    txt_item_remaining.setText(String.format(getString(R.string.remaining).toUpperCase(),  product.getMaxQuantity() + AppConstants.BLANK_STRING));
                } else {
                    txt_item_remaining.setVisibility(View.GONE);
                }
            }

            if (product.isOffer() && !ValidationUtil.isNullOrBlank(product.getOfferString())) {
                productTxtOffer.setVisibility(View.VISIBLE);
                productTxtOffer.setText(product.getOfferString());
            } else {
                productTxtOffer.setVisibility(View.GONE);
            }
        }

    }

    private void updateCartProgress(final String priceString, final int quantity) {

        if (quantity != 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    float current_total = OnlineMartApplication.mLocalStore.getCartTotal();
                    float price = Float.parseFloat(priceString.replace(",", AppConstants.BLANK_STRING));
                    String updatedTotal = null;
                    txt_add_or_remove.setText(getString(R.string.product_removed_from_cart));
                    updatedTotal = " "+ getString(R.string.egp) +" " + Math.round((current_total - price) * quantity);

                    OnlineMartApplication.mLocalStore.saveCartTotal(Float.parseFloat(updatedTotal.trim().split(" ")[1]));

                    if (OnlineMartApplication.mLocalStore.getFreeShippingAmount() > Float.parseFloat(updatedTotal.trim().split(" ")[1])) {
                        lyt_progress.setVisibility(View.VISIBLE);
                        txt_eligible.setVisibility(View.GONE);


                        float perCent = (Float.parseFloat(updatedTotal.trim().split(" ")[1]) * 100 / OnlineMartApplication.mLocalStore.getFreeShippingAmount());

                        progress_cart.setProgress((int) perCent);
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

                    //txt_cart_capecity.setText(String.format(getString(R.string.to_free_delivery), remaingEGP + AppConstants.BLANK_STRING).toUpperCase());
                    cart_progress_handler.postDelayed(cart_progress_runnable, 4000);

                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProductInfo(String id, final int position, final String quantityMode) {

        /* Sources
        1   -   Same Brand List
        2   -   Similar Prodcuts List
        3   -   Product Detail
         */

        try {
            JSONObject request_data = new JSONObject();
            request_data.put("lang_id", OnlineMartApplication.mLocalStore.getAppLangId());
            request_data.put("product_id", id);
            request_data.put("user_id", OnlineMartApplication.mLocalStore.getUserId());
            request_data.put("warehouse_id", OnlineMartApplication.mLocalStore.getSelectedRegionId());

            if (NetworkUtil.networkStatus(mContext)) {
                try {

                    if (loadInfoCall != null) {
                        loadInfoCall.cancel();
                    }

                    loadInfoCall = apiService.updateCartData((new ConvertJsonToMap().jsonToMap(request_data)));

                    APIHelper.enqueueWithRetry(loadInfoCall, AppConstants.API_RETRY_COUNT, new Callback<ResponseBody>() {
                        //call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //AppUtil.hideProgress();

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

                                                            product = BarcodeCaptureActivity.this.product;

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

                                                            int maxQty = 0;
                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("qty"))) {
                                                                if (jsonObject.optInt("qty") < 0) {
                                                                    maxQty = 0;
                                                                } else {
                                                                    maxQty = jsonObject.optInt("qty");
                                                                }
                                                            } else {
                                                                maxQty = 0;
                                                            }

                                                            if (product.getMaxQuantity() == maxQty) {
                                                                product.setQuantityUpdated(product.isQuantityUpdated());
                                                            } else {
                                                                product.setQuantityUpdated(false);
                                                            }

                                                            product.setMaxQuantity(maxQty);

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("currency"))) {
                                                                product.setCurrency(jsonObject.optString("currency"));
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optDouble("offer_percent", 0.0f))) {
                                                                //product.setOfferString(jsonObject.optDouble("offer_percent") + "% OFF");
                                                                product.setOfferString(Math.round(jsonObject.optDouble("offer_percent", 0.0f)) + "% " + getString(R.string.off).toUpperCase());
                                                            }

                                                            product.setShippingThirdParty(jsonObject.optBoolean("shipping_third_party", false));

                                                            if (product.isShippingThirdParty()) {
                                                                if (!ValidationUtil.isNullOrBlank(jsonObject.optString("shipping_hours"))) {
                                                                    product.setShippingHours(jsonObject.optString("shipping_hours"));
                                                                }
                                                            }

                                                            if (!ValidationUtil.isNullOrBlank(jsonObject.optString("mark_sold_qty"))) {
                                                                product.setMarkSoldQuantity(jsonObject.optInt("mark_sold_qty"));
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

                                                            if (quantityMode.equalsIgnoreCase("addToCart")) {

                                                                loader_quantity.showContent();
                                                                if (product.getMaxQuantity() >= 1) {

                                                                } else {

                                                                    product.setSoldOut(true);
                                                                    product.setSelectedQuantity(0);

                                                                    updateCartProgress(
                                                                            product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                            product.getSelectedQuantity() - product.getMaxQuantity());
                                                                    product.setSelectedQuantity(product.getMaxQuantity());
                                                                    OnlineMartApplication.removeProductFromCart(product.getId());
                                                                    //Toast.makeText(mContext, String.format(getString(R.string.notify_out_of_stock), product.getLabel()), Toast.LENGTH_SHORT).show();

                                                                    String tag = product.getId() + "_" + String.format(getString(R.string.notify_out_of_stock), product.getLabel());
                                                                    if (!list_alert.contains(tag)) {
                                                                        AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.notify_out_of_stock), product.getLabel()), getString(R.string.ok));
                                                                        appDialogSingleAction.show();
                                                                        list_alert.add(tag);
                                                                        appDialogSingleAction.setTag(tag);
                                                                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                            @Override
                                                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                appDialogSingleAction.dismiss();
                                                                                list_alert.remove(appDialogSingleAction.getTag());
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                invalidateOptionsMenu();


                                                            } else if (quantityMode.equalsIgnoreCase("add")) {

                                                                loader_quantity.showContent();
                                                                if (cart_progress_handler != null) {
                                                                    lyt_add_to_cart.setVisibility(View.GONE);
                                                                    cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                }

                                                                if (product.getSelectedQuantity() < product.getMaxQuantity()) {

                                                                } else {

                                                                    if (!product.isQuantityUpdated()) {
                                                                        updateCartProgress(
                                                                                product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                                product.getSelectedQuantity() - product.getMaxQuantity());
                                                                        product.setSelectedQuantity(product.getMaxQuantity());
                                                                        OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());

                                                                        String[] arr = new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING + AppConstants.BLANK_STRING, product.getLabel()};
                                                                        String tag = product.getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), arr);
                                                                        if (!list_alert.contains(tag)) {
                                                                            AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
                                                                            appDialogSingleAction.show();
                                                                            list_alert.add(tag);
                                                                            appDialogSingleAction.setTag(tag);
                                                                            appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                                @Override
                                                                                public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                    appDialogSingleAction.dismiss();
                                                                                    list_alert.remove(appDialogSingleAction.getTag());
                                                                                }
                                                                            });

                                                                        }
                                                                    }
                                                                }

                                                                txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);

                                                            } else if (quantityMode.equalsIgnoreCase("remove")) {

                                                                loader_quantity.showContent();
                                                                if (cart_progress_handler != null) {
                                                                    lyt_add_to_cart.setVisibility(View.GONE);
                                                                    cart_progress_handler.removeCallbacks(cart_progress_runnable);
                                                                }

                                                                if (product.getSelectedQuantity() < product.getMaxQuantity()) {


                                                                } else {
                                                                    updateCartProgress(
                                                                            product.isOffer() ? product.getOfferedPrice() : product.getActualPrice(),
                                                                            product.getSelectedQuantity() - product.getMaxQuantity());
                                                                    product.setSelectedQuantity(product.getMaxQuantity());
                                                                    OnlineMartApplication.updateQuantityOnCart(product.getId(), product.getSelectedQuantity());

                                                                    String tag = product.getId() + "_" + String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()});
                                                                    if (!list_alert.contains(tag)) {

                                                                        AppDialogSingleAction appDialogSingleAction = new AppDialogSingleAction(mContext, getString(R.string.app_name), String.format(getString(R.string.max_quantity_reached_msg1), new String[]{product.getMaxQuantity() + AppConstants.BLANK_STRING, product.getLabel()}), getString(R.string.ok));
                                                                        appDialogSingleAction.show();
                                                                        list_alert.add(tag);
                                                                        appDialogSingleAction.setTag(tag);
                                                                        appDialogSingleAction.setOnSingleActionListener(new OnSingleActionListener() {
                                                                            @Override
                                                                            public void onActionClick(View view, AppDialogSingleAction appDialogSingleAction) {
                                                                                appDialogSingleAction.dismiss();
                                                                                list_alert.remove(appDialogSingleAction.getTag());
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                txt_selected_quantity.setText(product.getSelectedQuantity() + AppConstants.BLANK_STRING);
                                                            }

                                                        }
                                                    }

                                                }

                                            } else {

                                            }

                                        } else {

                                        }

                                    } else {

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
                            if (!call.isCanceled()) {

                            }
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
