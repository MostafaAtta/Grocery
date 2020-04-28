package thegroceryshop.com.modal;

import java.util.ArrayList;

/**
 * Created by rohitg on 4/17/2017.
 */

public class OrderData {

    private String orderId;
    private String deliveryDate;
    private TimeSlot deliveryTimeSlot;
    private String deliveryTimeInString;
    private String orderPlaceDate;
    private String noOfItems;
    private String orderType; // 1-Schedule, 2-Express
    private String orderAmount;
    private String paymentType; // 1-COD, 2-CARD, 3-CREDITS, 4-CARD & CREDITS, 5-CASH & CREDITS
    private String orderStatus; // 1 = ACKNOWLEDGE , 2 = PICKING , 3=PICKED , 4=PACKING , 5=PACKED, 6=IN TRANSIT , 7=DELIVERED , 8=CCARETURN , 9 = QCRETURN

    private ShippingAddress shippingAddress;
    private String orderMobileNo;
    private String orderCountryCode;
    private String cardNo;
    private String orderSubTotal;
    private String orderDeliveryFee;
    //promo discount
    private String orderPromoDiscount;
    private String orderCouponCode;

    private OrderCaptain orderCaptain;
    private ArrayList<Product> listProducts;
    private String amountPaidByUser;
    private String amountPaidByCredits;
    private String auth_hash;
    private String regionName;


    public String getOrderPromoDiscount() {
        return orderPromoDiscount;
    }

    public void setOrderPromoDiscount(String orderPromoDiscount) {
        this.orderPromoDiscount = orderPromoDiscount;
    }



    public String getOrderCouponCode() {
        return orderCouponCode;
    }

    public void setOrderCouponCode(String orderCouponCode) {
        this.orderCouponCode = orderCouponCode;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public TimeSlot getDeliveryTimeSlot() {
        return deliveryTimeSlot;
    }

    public void setDeliveryTimeSlot(TimeSlot deliveryTimeSlot) {
        this.deliveryTimeSlot = deliveryTimeSlot;
    }

    public String getOrderPlaceDate() {
        return orderPlaceDate;
    }

    public void setOrderPlaceDate(String orderPlaceDate) {
        this.orderPlaceDate = orderPlaceDate;
    }

    public String getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        this.noOfItems = noOfItems;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliveryTimeInString() {
        return deliveryTimeInString;
    }

    public void setDeliveryTimeInString(String deliveryTimeInString) {
        this.deliveryTimeInString = deliveryTimeInString;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderMobileNo() {
        return orderMobileNo;
    }

    public void setOrderMobileNo(String orderMobileNo) {
        this.orderMobileNo = orderMobileNo;
    }

    public String getOrderCountryCode() {
        return orderCountryCode;
    }

    public void setOrderCountryCode(String orderCountryCode) {
        this.orderCountryCode = orderCountryCode;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(String orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public String getOrderDeliveryFee() {
        return orderDeliveryFee;
    }

    public void setOrderDeliveryFee(String orderDeliveryFee) {
        this.orderDeliveryFee = orderDeliveryFee;
    }

    public OrderCaptain getOrderCaptain() {
        return orderCaptain;
    }

    public void setOrderCaptain(OrderCaptain orderCaptain) {
        this.orderCaptain = orderCaptain;
    }

    public ArrayList<Product> getListProducts() {
        return listProducts;
    }

    public void setListProducts(ArrayList<Product> listProducts) {
        this.listProducts = listProducts;
    }

    public String getAmountPaidByUser() {
        return amountPaidByUser;
    }

    public void setAmountPaidByUser(String amountPaidByUser) {
        this.amountPaidByUser = amountPaidByUser;
    }

    public String getAmountPaidByCredits() {
        return amountPaidByCredits;
    }

    public void setAmountPaidByCredits(String amountPaidByCredits) {
        this.amountPaidByCredits = amountPaidByCredits;
    }

    public String getAuth_hash() {
        return auth_hash;
    }

    public void setAuth_hash(String auth_hash) {
        this.auth_hash = auth_hash;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
