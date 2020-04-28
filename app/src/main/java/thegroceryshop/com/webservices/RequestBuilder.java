package thegroceryshop.com.webservices;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rohitg on 12/14/2016.
 */

public class RequestBuilder {

    public static String build(Object obj) {

        Gson gson = new Gson();
        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("data", new JSONObject(gson.toJson(obj)));
            return reqObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static JSONObject buildObj(Object obj) {

        Gson gson = new Gson();
        JSONObject reqObj = new JSONObject();
        try {
            return new JSONObject(gson.toJson(obj));
        } catch (JSONException e) {
            e.printStackTrace();
            return reqObj;
        }

    }
}
