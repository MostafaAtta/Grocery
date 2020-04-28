package thegroceryshop.com.modal;

/*
 * Created by umeshk on 3/24/2017.
 */

public class PaymentCard {

    private String card_number;
    private boolean isSelected;
    private String card_id;
    private String card_token;
    private String card_subType;
    private String card_month;
    private String card_year;
    private String merchant_id;
    private String card_name;

    public String getCardNumber() {
        return card_number;
    }

    public void setCardNumber(String card_number) {
        this.card_number = card_number;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCardId() {
        return card_id;
    }

    public void setCardId(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_token() {
        return card_token;
    }

    public void setCard_token(String card_token) {
        this.card_token = card_token;
    }

    public String getCard_subType() {
        return card_subType;
    }

    public void setCard_subType(String card_subType) {
        this.card_subType = card_subType;
    }

    public String getCard_month() {
        return card_month;
    }

    public void setCard_month(String card_month) {
        this.card_month = card_month;
    }

    public String getCard_year() {
        return card_year;
    }

    public void setCard_year(String card_year) {
        this.card_year = card_year;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }
}
