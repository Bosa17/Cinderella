package in.cinderella.testapp.Utils;


import android.app.Activity;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

public class FacebookHelper {

    private static final String PERMISSION_EMAIL = "email";
    private static final String PERMISSION_PUBLIC_PROFILE = "public_profile";
    private static final String PERMISSION_USER_GENDER = "user_gender";

    private Activity activity;
    private CallbackManager fbCallbackManager;
    private LoginManager fbLoginManager;

    public FacebookHelper(Activity activity,FacebookCallback<LoginResult> facebookLoginCallback) {
        this.activity = activity;
        initFacebook( facebookLoginCallback);
    }

    private void initFacebook(FacebookCallback<LoginResult> facebookLoginCallback) {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());

        fbCallbackManager = CallbackManager.Factory.create();

        fbLoginManager = LoginManager.getInstance();
        fbLoginManager.registerCallback(fbCallbackManager, facebookLoginCallback);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void login() {
        fbLoginManager.logInWithReadPermissions(activity, generatePermissionsList());
    }

    public List<String> generatePermissionsList() {
        List<String> permissionsList = new ArrayList<String>();
        permissionsList.add(PERMISSION_EMAIL);
        permissionsList.add(PERMISSION_PUBLIC_PROFILE);
//        permissionsList.add(PERMISSION_USER_GENDER);
        return permissionsList;
    }
}