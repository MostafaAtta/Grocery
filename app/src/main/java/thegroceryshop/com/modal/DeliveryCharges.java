package thegroceryshop.com.modal;

public class DeliveryCharges {

    private String id;
    private float start_amount;
    private float end_amount;
    private float charges;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getStart_amount() {
        return start_amount;
    }

    public void setStart_amount(float start_amount) {
        this.start_amount = start_amount;
    }

    public float getEnd_amount() {
        return end_amount;
    }

    public void setEnd_amount(float end_amount) {
        this.end_amount = end_amount;
    }

    public float getCharges() {
        return charges;
    }

    public void setCharges(float charges) {
        this.charges = charges;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}