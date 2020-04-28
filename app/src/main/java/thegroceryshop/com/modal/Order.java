package thegroceryshop.com.modal;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by rohitg on 4/17/2017.
 */

public class Order {

    private String userId;
    private ShippingAddress address;
    private String countryCode;
    private String contactNo;
    private String instructions;
    private boolean isDoorStepAvailable;
    private boolean isDoorStep;
    private String deliveyType; // 1-Schedule, 2-Express
    private String deliveruDate;
    //payment modes: 1-COD, 2-CARD, 3-CREDITS, 4-CARD & CREDITS, 5-COD & CREDITS, 6-VALU, 7-VALU & CREDITS
    private String paymentMode;
    private String cardId;
    private int noOfItems;
    private float subTotal;
    private float deliveryCharges = 0.0f;
    private float tax;
    private float totalAmountToPay;
    private ArrayList<CartItem> list_cart;
    private int orderShippingHours;
    private DateTime date;
    private TimeSlot timeSlot;
    private PaymentCard selectedCard;
    private float totalSavings;
    private boolean isShippinghirdParty;
    private boolean useTGSCredits;
    private float amountByTgsCredits;
    private float amountByUser;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ShippingAddress getAddress() {
        return address;
    }

    public void setAddress(ShippingAddress address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public boolean isDoorStep() {
        return isDoorStep;
    }

    public void setDoorStep(boolean doorStep) {
        isDoorStep = doorStep;
    }

    public boolean isDoorStepAvailable() {
        return isDoorStepAvailable;
    }

    public void setDoorStepAvailable(boolean doorStepAvailable) {
        isDoorStepAvailable = doorStepAvailable;
    }

    public String getDeliveyType() {
        return deliveyType;
    }

    public void setDeliveyType(String deliveyType) {
        this.deliveyType = deliveyType;
    }

    public String getDeliveruDate() {
        return deliveruDate;
    }

    public void setDeliveruDate(String deliveruDate) {
        this.deliveruDate = deliveruDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }

    public float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getTotalAmountToPay() {
        return totalAmountToPay;
    }

    public void setTotalAmountToPay(float totalAmountToPay) {
        this.totalAmountToPay = totalAmountToPay;
    }

    public ArrayList<CartItem> getList_cart() {
        return list_cart;
    }

    public void setList_cart(ArrayList<CartItem> list_cart) {
        this.list_cart = list_cart;
    }

    public int getOrderShippingHours() {
        return orderShippingHours;
    }

    public void setOrderShippingHours(int orderShippingHours) {
        this.orderShippingHours = orderShippingHours;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public PaymentCard getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(PaymentCard selectedCard) {
        this.selectedCard = selectedCard;
    }

    public float getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(float totalSavings) {
        this.totalSavings = totalSavings;
    }

    public boolean isShippinghirdParty() {
        return isShippinghirdParty;
    }

    public void setShippinghirdParty(boolean shippinghirdParty) {
        isShippinghirdParty = shippinghirdParty;
    }

    public boolean isUseTGSCredits() {
        return useTGSCredits;
    }

    public void setUseTGSCredits(boolean useTGSCredits) {
        this.useTGSCredits = useTGSCredits;
    }

    public float getAmountByTgsCredits() {
        return amountByTgsCredits;
    }

    public void setAmountByTgsCredits(float amountByTgsCredits) {
        this.amountByTgsCredits = amountByTgsCredits;
    }

    public float getAmountByUser() {
        return amountByUser;
    }

    public void setAmountByUser(float amountByUser) {
        this.amountByUser = amountByUser;
    }
}
