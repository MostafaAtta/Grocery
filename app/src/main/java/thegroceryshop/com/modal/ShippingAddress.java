package thegroceryshop.com.modal;

/*
 * Created by umeshk on 4/7/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class ShippingAddress implements Parcelable {

    private String ship_id;
    private String user_id;
    private String lang_id;
    private String floor_number;
    private String unit_number;
    private String building_name;
    private String street_name;
    private String area_id;
    private String city_id;
    private String country_id;
    private String address_instruction;
    private String first_name;
    private String last_name;
    private String address_name;
    private double latitude;
    private double longitude;
    private boolean isSelected;
    private String area;
    private String country;
    private String city;
    private String inputFrom;
    private String regionId;
    private String regionName;
    private boolean isEnable;

    public ShippingAddress() {}

    protected ShippingAddress(Parcel in) {
        ship_id = in.readString();
        user_id = in.readString();
        lang_id = in.readString();
        floor_number = in.readString();
        unit_number = in.readString();
        building_name = in.readString();
        area_id = in.readString();
        city_id = in.readString();
        country_id = in.readString();
        address_instruction = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        address_name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        area = in.readString();
        country = in.readString();
        city = in.readString();
        inputFrom = in.readString();
        street_name = in.readString();
        regionId = in.readString();
        regionName = in.readString();
        isEnable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ship_id);
        dest.writeString(user_id);
        dest.writeString(lang_id);
        dest.writeString(floor_number);
        dest.writeString(unit_number);
        dest.writeString(building_name);
        dest.writeString(area_id);
        dest.writeString(city_id);
        dest.writeString(country_id);
        dest.writeString(address_instruction);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(address_name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(area);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(inputFrom);
        dest.writeString(street_name);
        dest.writeString(regionId);
        dest.writeString(regionName);
        dest.writeByte((byte) (isEnable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShippingAddress> CREATOR = new Creator<ShippingAddress>() {
        @Override
        public ShippingAddress createFromParcel(Parcel in) {
            return new ShippingAddress(in);
        }

        @Override
        public ShippingAddress[] newArray(int size) {
            return new ShippingAddress[size];
        }
    };

    public String getShip_id() {
        return ship_id;
    }

    public void setShip_id(String ship_id) {
        this.ship_id = ship_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLang_id() {
        return lang_id;
    }

    public void setLang_id(String lang_id) {
        this.lang_id = lang_id;
    }

    public String getFloor_number() {
        return floor_number;
    }

    public void setFloor_number(String floor_number) {
        this.floor_number = floor_number;
    }

    public String getUnit_number() {
        return unit_number;
    }

    public void setUnit_number(String unit_number) {
        this.unit_number = unit_number;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getAddress_instruction() {
        return address_instruction;
    }

    public void setAddress_instruction(String address_instruction) {
        this.address_instruction = address_instruction;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInputFrom() {
        return inputFrom;
    }

    public void setInputFrom(String inputFrom) {
        this.inputFrom = inputFrom;
    }

    public void setStreetName(String streetName){
        this.street_name = streetName;
    }

    public String getStreetName(){
        return street_name;
    }

    public void setRegionId(String warehouse_id){
        this.regionId = warehouse_id;
    }

    public String getRegionId(){
        return regionId;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
