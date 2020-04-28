package thegroceryshop.com.imagepicker;

import java.io.File;
import java.io.Serializable;

/*
 * Created by umeshk on 8/3/2017.
 */

public class ImageBean implements Serializable {

    private String imagePath;
    private File selectedFile;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public ImageBean() {
    }
}
