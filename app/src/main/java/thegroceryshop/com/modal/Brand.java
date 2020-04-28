package thegroceryshop.com.modal;

import thegroceryshop.com.dialog.SelectItemUtil;

/**
 * Created by rohitg on 4/4/2017.
 */

public class Brand implements SelectItemUtil.KeyValuePair<Brand>{

    private String brandName;

    private String brandId;

    private String productCount;

    private boolean isSelected;

    @Override
    public String getKey() {
        return brandId;
    }

    @Override
    public Brand getValue() {
        return this;
    }

    @Override
    public String getString() {
        return brandName;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandId() {
         return brandId;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getProductCount() {
        return productCount;
    }

    public void setProductCount(String productCount) {
        this.productCount = productCount;
    }

    public Brand(String brandId, String brandName, boolean isSelected, String productCount) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.isSelected = isSelected;
        this.productCount = productCount;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
