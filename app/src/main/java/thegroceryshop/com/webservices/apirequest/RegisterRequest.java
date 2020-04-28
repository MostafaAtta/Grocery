package thegroceryshop.com.webservices.apirequest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rohitg on 12/6/2016.
 */

public class RegisterRequest {


    @SerializedName("phone_no")
    private String phone_no;

    @SerializedName("device_token")
    private String device_token;

    @SerializedName("device_name")
    private String device_name;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("password")
    private String password;

    @SerializedName("os_version")
    private String os_version;

    @SerializedName("device_id")
    private String device_id;




    public RegisterRequest(String device_token, String full_name, String password, String phone_no,String os_verion,String device_id,String device_name) {
        this.device_token = device_token;
        this.fullname = full_name;
        this.password = password;
        this.phone_no = phone_no;
        this.os_version=os_verion;
        this.device_id= device_id;
        this.device_name= device_name;
    }
}
