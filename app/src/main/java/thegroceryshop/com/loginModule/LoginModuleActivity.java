package thegroceryshop.com.loginModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import org.json.JSONObject;

import thegroceryshop.com.R;
import thegroceryshop.com.constant.AppConstants;
import thegroceryshop.com.custom.AppUtil;
import thegroceryshop.com.utils.LocaleHelper;

/**
 * Created by mohitd on 15-Feb-17.
 */

public class LoginModuleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //public Toolbar toolbar;
    //public TextView txt_title;

    private FragmentManager fragmentManager;
    public GoogleApiClient mGoogleApiClient;
    public FrameLayout containerLayout;
    private Context mContext;

    private JSONObject social_request_data = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_module);
        mContext = LoginModuleActivity.this;

        fragmentManager = getSupportFragmentManager();
        containerLayout = findViewById(R.id.login_module_lyt_container);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        switchToFragment(LoginFragment.newInstance());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.login_module_lyt_container);
        if (currentFragment != null) {
            if (currentFragment instanceof LoginFragment && (requestCode == 140 || requestCode == AppConstants.REQUEST_GOOGLE_SIGN_IN)) {
                currentFragment.onActivityResult(requestCode, resultCode, data);
            } else if (currentFragment instanceof RegistrationFragment) {
                currentFragment.onActivityResult(requestCode, resultCode, data);
            } else if(currentFragment instanceof ForgotPasswordFragment){
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppConstants.REQUEST_FACEBOOK_SIGN_IN) {
            facebook_login_callback.onActivityResult(requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == AppConstants.REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("OnConnectionFailed", "Connection Failed");
    }

    // [START signOut]
    public void signOutGoogle() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            //updateUI(false);
                        }
                    });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    public void switchToFragment(Fragment mFragment) {

        AppUtil.hideSoftKeyboard(this, this);
        if (mFragment != null) {

            Fragment currentFragment = fragmentManager.findFragmentById(R.id.login_module_lyt_container);
            if (currentFragment == null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.login_module_lyt_container, mFragment)
                        .commitAllowingStateLoss();
            } else {
                if (currentFragment.getClass() != mFragment.getClass()) {
                    fragmentManager.beginTransaction()
                            //.setCustomAnimations(R.anim.enter_from_right, 0)
                            .replace(R.id.login_module_lyt_container, mFragment)
                            //.addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }

        } else {
            // error in creating fragment
            Log.e("LoginModule", "Error in creating fragment");
        }
    }

    public void switchToFragment(Fragment mFragment, boolean isAddToBack) {

        AppUtil.hideSoftKeyboard(this, this);
        if (mFragment != null) {
            if (getCurrentFragment() != null && getCurrentFragment().getClass() != mFragment.getClass()) {
                if (isAddToBack) {
                    fragmentManager.beginTransaction()
                            //.setCustomAnimations(R.anim.enter_from_right, 0)
                            .replace(R.id.login_module_lyt_container, mFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                } else {
                    fragmentManager.beginTransaction()
                            //.setCustomAnimations(R.anim.enter_from_right, 0)
                            .replace(R.id.login_module_lyt_container, mFragment)
                            .commitAllowingStateLoss();
                }

            } else {
                fragmentManager.beginTransaction()
                        //.setCustomAnimations(R.anim.enter_from_right, 0)
                        .replace(R.id.login_module_lyt_container, mFragment)
                        .commitAllowingStateLoss();
            }

        } else {
            // error in creating fragment
            Log.e("LoginModule", "Error in creating fragment");
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.login_module_lyt_container);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}

