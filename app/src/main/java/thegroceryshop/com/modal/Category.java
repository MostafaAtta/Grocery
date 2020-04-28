package thegroceryshop.com.modal;

/**
 * Created by rohitg on 3/30/2017.
 */

public class Category {

    private String id;
    private String label;
    private int productCount;
    private int categoryLevel;
    private int adminCategoryLevel;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public int getAdminCategoryLevel() {
        return adminCategoryLevel;
    }

    public void setAdminCategoryLevel(int adminCategoryLevel) {
        this.adminCategoryLevel = adminCategoryLevel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
