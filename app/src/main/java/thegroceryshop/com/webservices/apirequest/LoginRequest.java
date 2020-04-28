package thegroceryshop.com.webservices.apirequest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rohitg on 12/6/2016.
 */

public class LoginRequest {



    @SerializedName("fullname")
    private String username;

    @SerializedName("otp")
    private String password;




    public LoginRequest( String username, String password) {
      //  this.devicetoken = devicetoken;
        this.username = username;
        this.password = password;
      //  this.devicetype = devicetype;
    }
}
