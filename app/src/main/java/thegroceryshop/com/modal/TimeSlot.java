package thegroceryshop.com.modal;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

/**
 * Created by rohitg on 4/19/2017.
 */

public class TimeSlot implements Parcelable{

    private String id;
    private DateTime startTime;
    private DateTime endTime;
    private boolean isSlotAvailable;
    private boolean isSlotSelected;

    public TimeSlot(){}

    protected TimeSlot(Parcel in) {
        id = in.readString();
        isSlotAvailable = in.readByte() != 0;
        isSlotSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (isSlotAvailable ? 1 : 0));
        dest.writeByte((byte) (isSlotSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeSlot> CREATOR = new Creator<TimeSlot>() {
        @Override
        public TimeSlot createFromParcel(Parcel in) {
            return new TimeSlot(in);
        }

        @Override
        public TimeSlot[] newArray(int size) {
            return new TimeSlot[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isSlotAvailable() {
        return isSlotAvailable;
    }

    public void setSlotAvailable(boolean slotAvailable) {
        isSlotAvailable = slotAvailable;
    }

    public boolean isSlotSelected() {
        return isSlotSelected;
    }

    public void setSlotSelected(boolean slotSelected) {
        isSlotSelected = slotSelected;
    }
}
