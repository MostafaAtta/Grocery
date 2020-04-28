package thegroceryshop.com.modal.sildemenu_model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class CategoryItem implements Parcelable{

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
    private String parentId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("app_ican_id")
    @Expose
    private int app_ican_id;

    @SerializedName("product_count")
    @Expose
    private String product_count;

    private boolean isSelected;

    public CategoryItem(){}

    protected CategoryItem(Parcel in) {
        image = in.readString();
        category = in.createTypedArrayList(CategoryItem.CREATOR);
        categoryId = in.readString();
        level = in.readString();
        adminCategoryLevel = in.readString();
        parentId = in.readString();
        name = in.readString();
        app_ican_id = in.readInt();
        product_count = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<CategoryItem> CREATOR = new Creator<CategoryItem>() {
        @Override
        public CategoryItem createFromParcel(Parcel in) {
            return new CategoryItem(in);
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };

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

    public void setAdminCategoryLevel(String adminCategoryLevel) {
        this.adminCategoryLevel = adminCategoryLevel;
    }

    public String getAdminCategoryLevel() {
        return adminCategoryLevel;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getApp_ican_id() {
        return app_ican_id;
    }

    public void setApp_ican_id(int app_ican_id) {
        this.app_ican_id = app_ican_id;
    }

    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return
                "CategoryItem{" +
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeTypedList(category);
        dest.writeString(categoryId);
        dest.writeString(level);
        dest.writeString(adminCategoryLevel);
        dest.writeString(parentId);
        dest.writeString(name);
        dest.writeInt(app_ican_id);
        dest.writeString(product_count);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}