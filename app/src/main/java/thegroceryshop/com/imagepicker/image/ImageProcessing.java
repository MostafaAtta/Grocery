package thegroceryshop.com.imagepicker.image;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import thegroceryshop.com.imagepicker.FileProcessing;

/**
 * Created by umeshk on 8/15/16.
 * MediaPicker
 */
class ImageProcessing {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static List<String> processMultiImage(Context context, Intent data) {
        List<String> listOfImages = new ArrayList<>();
        if (null == data.getData()) {
            ClipData clipdata = data.getClipData();
            if (clipdata != null) {
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    Uri selectedImage = clipdata.getItemAt(i).getUri();
                    String selectedImagePath = FileProcessing.getPath(context, selectedImage);
                    listOfImages.add(selectedImagePath);
                }
            }
        }
        return listOfImages;
    }
}
