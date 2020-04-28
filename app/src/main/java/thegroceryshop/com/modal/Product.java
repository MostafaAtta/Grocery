package thegroceryshop.com.modal;

import java.util.ArrayList;

import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.constant.AppConstants;

/**
 * Created by rohitg on 3/30/2017.
 */

public class Product implements Cloneable{

    private String id = AppConstants.BLANK_STRING;
    private String englishName = AppConstants.BLANK_STRING;
    private String arabicName = AppConstants.BLANK_STRING;
    private String image = AppConstants.BLANK_STRING;
    private String actualPrice = AppConstants.BLANK_STRING;
    private String offeredPrice = AppConstants.BLANK_STRING;
    private String englishQuantity = AppConstants.BLANK_STRING;
    private String arabicQuantity = AppConstants.BLANK_STRING;
    private int maxQuantity = 1;
    private boolean isAddedToCart = false;
    private boolean isOffer = false;
    private boolean isSoldOut = false;
    private boolean isShippingThirdParty = false;
    private int selectedQuantity = 0;
    private String currency = AppConstants.BLANK_STRING;
    private String offerString = AppConstants.BLANK_STRING;
    private String shippingHours = AppConstants.BLANK_STRING;
    private int mark_sold_quantity = 0;
    private String producDescription = AppConstants.BLANK_STRING;
    private ArrayList<String> listImages;
    private ArrayList<String> listOrignalImages;
    private String englishBrandName;
    private String arabicBrandName;
    private String brandId;
    private float taxCharges;
    private boolean isDoorStepDelivery;
    private boolean isLoadingInfo = false;
    private boolean isQuantityUpdated = false;
    private boolean shouldLoad = true;
    private int wishlistQty;
    private boolean isRemovedFromWishList;
    private int product_avg_rating;
    private int product_rating_count;
    private boolean isAddedToWishList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    private String getArabicName() {
        return arabicName;
    }

    public String getLabel(){
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return getEnglishName();
        }else{
            return getArabicName();
        }
    }

    public String getEnglishLabel(){
        return getEnglishName();
    }

    public String getArabicLabel(){
        return getArabicName();
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
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

    public boolean isAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }

    public String getEnglishQuantity() {
        return englishQuantity;
    }

    public void setEnglishQuantity(String englishQuantity) {
        this.englishQuantity = englishQuantity;
    }

    public String getArabicQuantity() {
        return arabicQuantity;
    }

    public void setArabicQuantity(String arabicQuantity) {
        this.arabicQuantity = arabicQuantity;
    }

    public String getQuantity(){
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return getEnglishQuantity();
        }else{
            return getArabicQuantity();
        }
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

    public boolean isShippingThirdParty() {
        return isShippingThirdParty;
    }

    public void setShippingThirdParty(boolean isShippingThirdParty) {
        this.isShippingThirdParty = isShippingThirdParty;
    }

    public String getCurrency() {
        return currency;
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

    public String getOfferString() {
        return offerString;
    }

    public void setOfferString(String offerString) {
        this.offerString = offerString;
    }

    public String getShippingHours() {
        return shippingHours;
    }

    public void setShippingHours(String shippingHours) {
        this.shippingHours = shippingHours;
    }

    public int getMarkSoldQuantity() {
        return mark_sold_quantity;
    }

    public void setMarkSoldQuantity(int mark_sold_quantity) {
        this.mark_sold_quantity = mark_sold_quantity;
    }

    public String getProducDescription() {
        return producDescription;
    }

    public void setProducDescription(String producDescription) {
        this.producDescription = producDescription;
    }

    public ArrayList<String> getListImages() {
        return listImages;
    }

    public void setListImages(ArrayList<String> listImages) {
        this.listImages = listImages;
    }

    public ArrayList<String> getListorignalImages() {
        return listOrignalImages;
    }

    public void setListOrignalImages(ArrayList<String> listOrignalImages) {
        this.listOrignalImages = listOrignalImages;
    }

    public String getEnglishBrandName() {
        return englishBrandName;
    }

    public void setEnglishBrandName(String englishBrandName) {
        this.englishBrandName = englishBrandName;
    }

    public String getArabicBrandName() {
        return arabicBrandName;
    }

    public String getBrandName(){
        if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
            return getEnglishBrandName();
        }else{
            return getArabicBrandName();
        }
    }

    public void setArabicBrandName(String arabicBrandName) {
        this.arabicBrandName = arabicBrandName;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
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

    public boolean isLoadingInfo() {
        return isLoadingInfo;
    }

    public void setLoadingInfo(boolean loadingInfo) {
        isLoadingInfo = false;
    }

    public boolean isQuantityUpdated() {
        return isQuantityUpdated;
    }

    public void setQuantityUpdated(boolean quantityUpdated) {
        isQuantityUpdated = quantityUpdated;
    }

    public void shouldImageLoad(boolean shouldLoad) {
        this.shouldLoad = shouldLoad;
    }

    public boolean isLoadImage(){
        return this.shouldLoad;
    }

    public void setWishlistQty(int wishlistQty) {
        this.wishlistQty = wishlistQty;
    }

    public int getWishListQty(){
        return wishlistQty;
    }

    public boolean isRemovedFromWishList() {
        return isRemovedFromWishList;
    }

    public void setRemovedFromWishList(boolean isRemovedFromWishList){
        this.isRemovedFromWishList = isRemovedFromWishList;
    }

    public void setAvgRating(int avg_rating){
        product_avg_rating = avg_rating;
    }

    public int getAvgRating(){
        return product_avg_rating;
    }

    public void setRatingCount(int ratingCount){
        product_rating_count = ratingCount;
    }

    public int getRatingCount(){
        return product_rating_count;
    }

    public void setAddedToWishList(boolean isAddedToWishList) {
        this.isAddedToWishList = isAddedToWishList;
    }

    public boolean isAddedToWishList(){
        return isAddedToWishList;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
