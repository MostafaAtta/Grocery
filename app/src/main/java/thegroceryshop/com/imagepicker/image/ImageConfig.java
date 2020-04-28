package thegroceryshop.com.imagepicker.image;

import android.os.Environment;

import java.io.Serializable;

/*
 * Created by umesh on 08/02/17.
 */
class ImageConfig implements Serializable {

    protected ImagePicker.Extension extension;
    protected ImagePicker.ComperesLevel compressLevel;
    protected ImagePicker.Mode mode;
    protected String directory;
    protected int reqHeight;
    protected int reqWidth;
    protected boolean allowMultiple;
    protected boolean isImgFromCamera;
    protected boolean debug;

    ImageConfig() {
        this.extension = ImagePicker.Extension.PNG;
        this.compressLevel = ImagePicker.ComperesLevel.NONE;
        this.mode = ImagePicker.Mode.CAMERA;
        this.directory = Environment.getExternalStorageDirectory() + ImageTags.Tags.IMAGE_PICKER_DIR;
        this.reqHeight = 0;
        this.reqWidth = 0;
        this.allowMultiple = false;
    }

    @Override
    public String toString() {
        return "ImageConfig{" +
                "extension=" + extension +
                ", compressLevel=" + compressLevel +
                ", mode=" + mode +
                ", directory='" + directory + '\'' +
                ", reqHeight=" + reqHeight +
                ", reqWidth=" + reqWidth +
                ", allowMultiple=" + allowMultiple +
                ", isImgFromCamera=" + isImgFromCamera +
                ", debug=" + debug +
                '}';
    }
}
