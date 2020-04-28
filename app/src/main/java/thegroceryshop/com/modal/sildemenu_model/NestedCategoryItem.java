package thegroceryshop.com.modal.sildemenu_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class NestedCategoryItem {

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("Category")
    @Expose
    private List<CategoryItem> category;

    @SerializedName("category_id")
    @Expose
    private String categoryId;

    @SerializedName("level")
    @Expose
    private String level;

    @SerializedName("admin_category_level")
    @Expose
    private String adminCategoryLevel;

    @SerializedName("parent_id")
    @Expose
    private Object parentId;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("product_count")
    @Expose
    private String product_count;

    @SerializedName("app_ican_id")
    @Expose
    private int app_ican_id;

    private boolean isSelected;

    private boolean isOpened;

    private boolean flag;

    private String sequence_number;

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setCategory(List<CategoryItem> category) {
        this.category = category;
    }

    public List<CategoryItem> getCategory() {
        return category;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setAdminCategoryLevel(String adminCategoryLevel) {
        this.adminCategoryLevel = adminCategoryLevel;
    }

    public String getAdminCategoryLevel() {
        return adminCategoryLevel;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProductCount(String product_count) {
        this.product_count = product_count;
    }

    public String getProduct_count() {
        return product_count;
    }

    public int getApp_ican_id() {
        return app_ican_id;
    }

    public void setApp_ican_id(int app_ican_id) {
        this.app_ican_id = app_ican_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getSequenceNumber() {
        return sequence_number;
    }

    public void setSequenceNumber(String sequence_number) {
        this.sequence_number = sequence_number;
    }

    @Override
    public String toString() {
        return
                "NestedCategoryItem{" +
                        "image = '" + image + '\'' +
                        ",category = '" + category + '\'' +
                        ",category_id = '" + categoryId + '\'' +
                        ",level = '" + level + '\'' +
                        ",parent_id = '" + parentId + '\'' +
                        ",app_ican_id = '" + app_ican_id + '\'' +
                        ",product_count = '" + product_count + '\'' +
                        ",name = '" + name + '\'' +
                        "}";
    }

}