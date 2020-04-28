package thegroceryshop.com.imagepicker.image;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import thegroceryshop.com.BuildConfig;
import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.imagepicker.FileProcessing;
import thegroceryshop.com.imagepicker.ImageBean;
import thegroceryshop.com.imagepicker.Utility;
import thegroceryshop.com.utils.LocaleHelper;

/**
 * Created by umeshk on 08/02/17.
 * MediaPicker
 */
public class ImageActivity extends AppCompatActivity {

    private File destination;
    private Uri mImageUri;
    private ImageConfig mImgConfig;
    private List<String> listOfImages;

    public static Intent getCallingIntent(Context activity, ImageConfig imageConfig) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra(ImageTags.Tags.IMG_CONFIG, imageConfig);
        return intent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    /*@Override
    protected void attachBaseContext(Context base) {
        //super.attachBaseContext(updateBaseContextLocale(base));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mImgConfig = (ImageConfig) intent.getSerializableExtra(ImageTags.Tags.IMG_CONFIG);
        }

        if (savedInstanceState == null) {
            pickImage();
            listOfImages = new ArrayList<>();
        }
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, mImgConfig.toString());
    }

    private void pickImage() {
        Utility.createFolder(mImgConfig.directory);
        destination = new File(mImgConfig.directory, Utility.getRandomString() + mImgConfig.extension.getValue());
        switch (mImgConfig.mode) {
            case CAMERA:
                pickImageFromCamera();
                //startActivityFromCamera();
                break;
            case GALLERY:
                pickImageFromGallery();
                /*if (mImgConfig.allowMultiple)
                    startActivityFromGalleryMultiImg();
                else
                    startActivityFromGallery();*/
                break;
            case CAMERA_AND_GALLERY:
                showFromCameraOrGalleryBottomSheet();
                //showFromCameraOrGalleryAlert();
                break;
            default:
                break;
        }
    }


    private void showFromCameraOrGalleryBottomSheet() {

        final CharSequence[] options = {getResources().getString(R.string.camera), getResources().getString(R.string.gallery), getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.choose_from));
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getResources().getString(R.string.camera))) {
                    dialog.dismiss();
                    if (mImgConfig.debug)
                        Log.d(ImageTags.Tags.TAG, "Start From Camera");
                    pickImageFromCamera();
                } else if (options[which].equals(getResources().getString(R.string.gallery))) {
                    dialog.dismiss();
                    if (mImgConfig.debug)
                        Log.d(ImageTags.Tags.TAG, "Start From Gallery");
                    pickImageFromGallery();

                } else if (options[which].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                    finish();
                }
            }
        });

        // builder.setOnDismissListener(dialogInterface -> finish());
        builder.show();
    }


    private void startActivityFromGallery() {
        mImgConfig.isImgFromCamera = false;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "Gallery Start with Single Image mode");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startActivityFromGalleryMultiImg() {
        mImgConfig.isImgFromCamera = false;
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, AppConstants.BLANK_STRING), ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "Gallery Start with Multiple Images mode");
    }

    private void startActivityFromCamera() {
        mImgConfig.isImgFromCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (destination != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                mImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", destination);
            } else {
                mImageUri = Uri.fromFile(destination);
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(Intent.createChooser(intent, AppConstants.BLANK_STRING), ImageTags.IntentCode.CAMERA_REQUEST);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "Camera Start");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageUri != null) {
            outState.putString(ImageTags.Tags.CAMERA_IMAGE_URI, mImageUri.toString());
            outState.putSerializable(ImageTags.Tags.IMG_CONFIG, mImgConfig);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(ImageTags.Tags.CAMERA_IMAGE_URI)) {
            mImageUri = Uri.parse(savedInstanceState.getString(ImageTags.Tags.CAMERA_IMAGE_URI));
            destination = new File(mImageUri.getPath());
            mImgConfig = (ImageConfig) savedInstanceState.getSerializable(ImageTags.Tags.IMG_CONFIG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(ImageTags.Tags.TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageTags.IntentCode.CAMERA_REQUEST:
                    new CompressImageTask(destination.getAbsolutePath(), mImgConfig, ImageActivity.this).execute();
                    break;

                case ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO:
                    processOneImage(data);
                    break;

                case ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO:
                    //Check if the intent contain only one image
                    if (data.getClipData() == null) {
                        processOneImage(data);
                    } else {
                        //intent has multi images
                        listOfImages = ImageProcessing.processMultiImage(this, data);
                        new CompressImageTask(listOfImages, mImgConfig, ImageActivity.this).execute();
                    }
                    break;
                default:
                    break;
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(ImageTags.Tags.PICK_ERROR, "user did not select any image");
            sendBroadcast(intent);
            finish();
        }
    }

    private void processOneImage(Intent data) {
        try {
            Uri selectedImage = data.getData();
            String selectedImagePath = FileProcessing.getPath(this, selectedImage);
            new CompressImageTask(selectedImagePath, mImgConfig, ImageActivity.this).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void finishActivity(List<ImageBean> path) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ImagePicker.EXTRA_IMAGE_PATH, (Serializable) path);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void pickImageFromCamera() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                            builder.setTitle(AppConstants.BLANK_STRING);
                            builder.setMessage(getResources().getString(R.string.msg_txt));

                            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 1001);
                                }
                            });

                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (report.areAllPermissionsGranted()) {
                            startActivityFromCamera();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void pickImageFromGallery() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                            builder.setTitle(AppConstants.BLANK_STRING);
                            builder.setMessage(getResources().getString(R.string.msg_txt));

                            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 1001);
                                }
                            });

                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (report.areAllPermissionsGranted()) {
                            if (mImgConfig.allowMultiple)
                                startActivityFromGalleryMultiImg();
                            else
                                startActivityFromGallery();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private static class CompressImageTask extends AsyncTask<Void, Void, Void> {

        private final ImageConfig mImgConfig;
        private final List<String> listOfImages;
        private List<ImageBean> destinationPaths;
        private WeakReference<ImageActivity> mContext;


        CompressImageTask(List<String> listOfImages, ImageConfig imageConfig, ImageActivity context) {
            this.listOfImages = listOfImages;
            this.mContext = new WeakReference<>(context);
            this.mImgConfig = imageConfig;
            this.destinationPaths = new ArrayList<>();
        }

        CompressImageTask(String absolutePath, ImageConfig imageConfig, ImageActivity context) {
            List<String> list = new ArrayList<>();
            list.add(absolutePath);
            this.listOfImages = list;
            this.mContext = new WeakReference<>(context);
            this.destinationPaths = new ArrayList<>();
            this.mImgConfig = imageConfig;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (String mPath : listOfImages) {
                File file = new File(mPath);
                File destinationFile;
                if (mImgConfig.isImgFromCamera) {
                    destinationFile = file;
                } else {
                    destinationFile = new File(mImgConfig.directory, Utility.getRandomString() + mImgConfig.extension.getValue());
                }

                ImageBean imageBean = new ImageBean();
                imageBean.setImagePath(destinationFile.getAbsolutePath());
                imageBean.setSelectedFile(destinationFile);
                destinationPaths.add(imageBean);//destinationFile.getAbsolutePath());
                try {
                    Utility.compressAndRotateIfNeeded(file, destinationFile, mImgConfig.compressLevel.getValue(), mImgConfig.reqWidth, mImgConfig.reqHeight);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ImageActivity context = mContext.get();
            if (context != null) {
                context.finishActivity(destinationPaths);
                Intent intent = new Intent();
                intent.putExtra(ImageTags.Tags.IMAGE_PATH, (Serializable) destinationPaths);
                context.sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}