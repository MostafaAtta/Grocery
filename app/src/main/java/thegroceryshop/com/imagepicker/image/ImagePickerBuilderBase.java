package thegroceryshop.com.imagepicker.image;

/**
 * Created by umeshk on 08/03/17.
 * MediaPicker
 */
interface ImagePickerBuilderBase {

    ImagePicker.Builder compressLevel(ImagePicker.ComperesLevel compressLevel);

    ImagePicker.Builder mode(ImagePicker.Mode mode);

    ImagePicker.Builder directory(String directory);

    ImagePicker.Builder directory(ImagePicker.Directory directory);

    ImagePicker.Builder extension(ImagePicker.Extension extension);

    ImagePicker.Builder scale(int minWidth, int minHeight);

    ImagePicker.Builder allowMultipleImages(boolean allowMultiple);

    ImagePicker.Builder enableDebuggingMode(boolean debug);

    ImagePicker build();

}
