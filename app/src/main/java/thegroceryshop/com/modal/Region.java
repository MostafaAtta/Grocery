package thegroceryshop.com.modal;

import thegroceryshop.com.application.OnlineMartApplication;

public class Region {

    private String regionId;
    private String en_regionName;
    private String ar_regionName;
    private boolean isSelected;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return en_regionName;
        }else{
            return ar_regionName;
        }
    }

    public String getRegionNameEnglish() {
        return en_regionName;
    }

    public String getRegionNameArabic() {
        return  ar_regionName;
    }

    public void setRegionNameEnglish(String en_regionName) {
        this.en_regionName = en_regionName;
    }

    public void setRegionNameArabic(String ar_regionName) {
        this.ar_regionName = ar_regionName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
