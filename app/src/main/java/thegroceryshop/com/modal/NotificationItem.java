package thegroceryshop.com.modal;

/**
 * Created by rohitg on 4/4/2017.
 */

public class NotificationItem{

    private String message;
    private String datetime;
    private String notificationId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
