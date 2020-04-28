package thegroceryshop.com.modal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rohitg on 4/27/2017.
 */

public class OrderCaptain implements Parcelable {

    private String id;
    private String name;
    private String mobileNo;
    private String countryCode;
    private double latitude;
    private double longitude;

    public OrderCaptain() {}

    protected OrderCaptain(Parcel in) {
        id = in.readString();
        name = in.readString();
        mobileNo = in.readString();
        countryCode = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(mobileNo);
        dest.writeString(countryCode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderCaptain> CREATOR = new Creator<OrderCaptain>() {
        @Override
        public OrderCaptain createFromParcel(Parcel in) {
            return new OrderCaptain(in);
        }

        @Override
        public OrderCaptain[] newArray(int size) {
            return new OrderCaptain[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
