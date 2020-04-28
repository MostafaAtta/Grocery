package thegroceryshop.com.utils;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.loginModule.LoginFragment;

/**
 * Created by rohitg on 12/7/2016.
 */

public class FacebookUtil {

    public static void loginWithFacebook(final Fragment mFragment, CallbackManager facebook_login_callback) {
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(mFragment, Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(facebook_login_callback, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {

                        if (response.getError() != null) {
                            // handle error
                            System.out.println("ERROR : " + response.getError());
                        } else {
                            try {
                                String email = "";
                                // {"id":"10205816488090765","name":"Rohit Gupta","email":"rgohit@gmail.com","gender":"male"}
                                System.out.println("JSON Result" + json);
                                if (json.has("email")) {
                                    email = json.getString("email");
                                }
                                String facebook_id = json.getString("id");
                                String first_name = json.getString("first_name");
                                String last_name = json.getString("last_name");
                                String userName = json.getString("name");
                                //String image = json.getJSONObject("picture").getJSONObject("data").getString("url");
                                Uri image = Uri.parse("https://graph.facebook.com/" + facebook_id + "/picture?width=800&height=800");
                                if (mFragment instanceof LoginFragment) {

                                    ((LoginFragment) mFragment).social_email = email;
                                    ((LoginFragment) mFragment).social_id = facebook_id;
                                    ((LoginFragment) mFragment).social_first_name = first_name;
                                    ((LoginFragment) mFragment).social_last_name = last_name;
                                    ((LoginFragment) mFragment).social_img = image.toString();

                                    ((LoginFragment) mFragment).socialLogin(facebook_id, AppConstants.TYPE_SOCIAL_FACEBOOK);
                                }

                                logout();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, first_name, last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("Facebook Error in On Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
            }
        });
    }

    private static void logout() {

        try {

            if (AccessToken.getCurrentAccessToken() == null) {
                return; // already logged out
            }

            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();

                }
            }).executeAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
