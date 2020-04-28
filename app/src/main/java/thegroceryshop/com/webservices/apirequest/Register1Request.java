package thegroceryshop.com.webservices.apirequest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rohitg on 12/6/2016.
 */

public class Register1Request {


  /*  @SerializedName("phone_no")
    private String phone_no;

    @SerializedName("device_token")
    private String device_token;

    @SerializedName("device_name")
    private String device_name;*/

    @SerializedName("complaint")
    private String complaint;

    @SerializedName("address")
    private String address;

    @SerializedName("image")
    private String image;

    @SerializedName("user_id")
    private String user_id;




    public Register1Request(String complaint, String address, String image, String user_id){//}, String os_verion, String device_id, String device_name) {
        this.complaint = complaint;
        this.address = address;
        this.image = image;
        this.user_id = user_id;

    }
}
