package thegroceryshop.com.modal;

public class Item{

    private String itemId;
    private String itemLabel;
    private boolean isChecked;
    private int counter;

    public Item(String itemId, String itemLabel, boolean isChecked) {
        this.itemId = itemId;
        this.itemLabel = itemLabel;
        this.isChecked = isChecked;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}