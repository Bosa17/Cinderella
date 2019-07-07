package in.cinderella.testapp.Utils;


import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

public class FacebookHelper {

    private static final String PERMISSION_EMAIL = "email";
    private static final String PERMISSION_PUBLIC_PROFILE = "public_profile";
    private static final String PERMISSION_USER_GENDER = "user_gender";

    private DataHelper dataHelper;
    private Activity activity;
    private ProfileTracker fbProfileTracker;
    private AccessTokenTracker fbAccessTokenTracker;
    private CallbackManager fbCallbackManager;
    private LoginManager fbLoginManager;

    public FacebookHelper(Activity activity) {
        this.activity = activity;
        dataHelper=new DataHelper(activity);
        initFacebook();
    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());

        fbCallbackManager = CallbackManager.Factory.create();

        fbLoginManager = LoginManager.getInstance();

        this.fbProfileTracker = new FBProfileTracker();
        this.fbAccessTokenTracker=new FBAccessTokenTracker();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void login(FacebookCallback<LoginResult> facebookLoginCallback) {
        fbLoginManager.registerCallback(fbCallbackManager, facebookLoginCallback);
        fbLoginManager.logInWithReadPermissions(activity, generatePermissionsList());
    }

    public List<String> generatePermissionsList() {
        List<String> permissionsList = new ArrayList<String>();
        permissionsList.add(PERMISSION_EMAIL);
        permissionsList.add(PERMISSION_PUBLIC_PROFILE);
//        permissionsList.add(PERMISSION_USER_GENDER);
        return permissionsList;
    }

    public void onActivityStart() {
        fbProfileTracker.startTracking();
        fbAccessTokenTracker.startTracking();
    }

    public void onActivityStop() {
        fbProfileTracker.stopTracking();
        fbAccessTokenTracker.stopTracking();
    }

    private class FBAccessTokenTracker extends AccessTokenTracker {

        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut();
            }
        }
    }
    private class FBProfileTracker extends ProfileTracker {

        @Override
        protected void onCurrentProfileChanged(
                Profile oldProfile,
                Profile currentProfile) {
            dataHelper.putFb_dp(""+currentProfile.getProfilePictureUri(200, 200));
        }
    }
}