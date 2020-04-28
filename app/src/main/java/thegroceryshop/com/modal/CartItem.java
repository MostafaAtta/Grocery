package thegroceryshop.com.modal;

import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;

/**
 * Created by rohitg on 3/30/2017.
 */

public class CartItem implements Cloneable {

    private String id = AppConstants.BLANK_STRING;
    private String englishlabel = AppConstants.BLANK_STRING;
    private String arabiclabel = AppConstants.BLANK_STRING;
    private String image = AppConstants.BLANK_STRING;
    private String actualPrice = AppConstants.BLANK_STRING;
    private String offeredPrice = AppConstants.BLANK_STRING;
    private String savedPrice = AppConstants.BLANK_STRING;
    private float taxCharges = 0.00f;
    private String englishquantity = AppConstants.BLANK_STRING;
    private String arabicquantity = AppConstants.BLANK_STRING;
    private int maxQuantity = 1;
    private boolean isOffer = false;
    private boolean isSoldOut = false;
    private int selectedQuantity = 0;
    private String currency = AppConstants.BLANK_STRING;
    private int mark_sold_quantity = 0;
    private String arabicbrandName;
    private String englishbrandName;
    private boolean isDoorStepDelivery;
    private int shippingHours;
    private boolean isShippingThirdParty = false;
    private boolean isAddedToWishList;
    private boolean shouldRemoveFromCart = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return englishlabel;
        }else{
            return arabiclabel;
        }
    }

    public String getEnglishLabel() {
        return englishlabel;
    }

    public String getArabiclabel() {
        return arabiclabel;
    }

    public void setEnglishLabel(String englishlabel) {
        this.englishlabel = englishlabel;
    }

    public void setArabicLabel(String arabiclabel) {
        this.arabiclabel = arabiclabel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public String getQuantity() {
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return englishquantity;
        }else{
            return arabicquantity;
        }
    }

    public String getEnglishquantity() {
        return englishquantity;
    }

    public String getArabicquantity() {
        return arabicquantity;
    }

    public void setArabicQuantity(String arabicquantity) {
        this.arabicquantity = arabicquantity;
    }

    public void setEnglishQuantity(String englishquantity) {
        this.englishquantity = englishquantity;
    }

    public boolean isOffer() {
        return isOffer;
    }

    public void setOffer(boolean isOffer) {
        this.isOffer = isOffer;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public String getCurrency() {
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return "EGP";
        }else{
            return "جنيه";
        }

        //return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(String offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public int getMarkSoldQuantity() {
        return mark_sold_quantity;
    }

    public void setMarkSoldQuantity(int mark_sold_quantity) {
        this.mark_sold_quantity = mark_sold_quantity;
    }

    public String getBrandName() {
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return englishbrandName;
        }else{
            return arabicbrandName;
        }
    }

    public String getEnglishbrandName() {
        return englishbrandName;
    }

    public String getArabicbrandName() {
        return arabicbrandName;
    }

    public void setEnglishBrandName(String englishbrandName) {
        this.englishbrandName = englishbrandName;
    }

    public void setArabicBrandName(String arabicbrandName) {
        this.arabicbrandName = arabicbrandName;
    }

    public String getSavedPrice() {
        return savedPrice;
    }

    public void setSavedPrice(String savedPrice) {
        this.savedPrice = savedPrice;
    }

    public float getTaxCharges() {
        return taxCharges;
    }

    public void setTaxCharges(float taxCharges) {
        this.taxCharges = taxCharges;
    }

    public boolean isDoorStepDelivery() {
        return isDoorStepDelivery;
    }

    public void setDoorStepDelivery(boolean doorStepDelivery) {
        isDoorStepDelivery = doorStepDelivery;
    }

    public int getShippingHours() {
        return shippingHours;
    }

    public void setShippingHours(int shippingHours) {
        this.shippingHours = shippingHours;
    }

    public boolean isShippingThirdParty() {
        return isShippingThirdParty;
    }

    public void setShippingThirdParty(boolean shippingThirdParty) {
        isShippingThirdParty = shippingThirdParty;
    }

    public void setAddedToWishList(boolean isAddedToWishList) {
        this.isAddedToWishList = isAddedToWishList;
    }
    
    public boolean isAddedToWishList(){
        return isAddedToWishList;
    }

    public void setRemovedFromCart(boolean shouldRemoveFromCart){
        this.shouldRemoveFromCart = shouldRemoveFromCart;
    }

    public boolean shouldRemovedFromCart(){
        return shouldRemoveFromCart;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
