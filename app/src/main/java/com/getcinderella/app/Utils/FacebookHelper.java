package com.getcinderella.app.Utils;


import android.app.Activity;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

public class FacebookHelper {

    private static final String PERMISSION_EMAIL = "email";
    private static final String PERMISSION_PUBLIC_PROFILE = "public_profile";
//    private static final String PERMISSION_USER_GENDER = "user_gender";

    private Activity activity;
    private CallbackManager fbCallbackManager;
    private LoginManager fbLoginManager;

    public FacebookHelper(Activity activity) {
        this.activity = activity;
        initFacebook();
    }

    private void initFacebook() {
        fbCallbackManager = CallbackManager.Factory.create();
        fbLoginManager = LoginManager.getInstance();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void logout() {
        LoginManager.getInstance().logOut();
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
}