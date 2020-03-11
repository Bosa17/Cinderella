package com.getcinderella.app.Activities;

import androidx.annotation.NonNull;

import com.getcinderella.app.Fragments.ProgressDialogFragment;
import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.ConnectivityUtils;
import com.getcinderella.app.Utils.DataHelper;
import com.getcinderella.app.Utils.FacebookHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONException;
import org.json.JSONObject;


public class User_login extends BaseActivity {

    private static final String TAG = "User_login Activity";
    private Button btn;
    private FirebaseAuth mAuth;
    private DataHelper dataHelper;
    private FacebookHelper facebookHelper;
    private UserModel userModel;
    private static int RC_SUCCESS_MASK=2;
    private static int RC_SUCCESS_QUOTE=3;
    private static int RC_SUCCESS_GENDER=4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        checkCurrentUser(mAuth.getCurrentUser());

        userModel=new UserModel();
        dataHelper=new DataHelper(this);
        facebookHelper=new FacebookHelper(this);

        setContentView(R.layout.activity_user_login);
        btn=findViewById(R.id.sign_in);

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(ConnectivityUtils.isNetworkAvailable(getApplicationContext()))
                    facebookHelper.login(new FacebookLoginCallback());
                else
                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged com.");

        if(user != null){
            startMainActivity();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dataHelper.saveScene();
//                            boolean isNewUser=true;
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "signInWithCredential:success");
                            if (isNewUser) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                getDynamicLink();
                                getFacebookDetails(user.getUid(), token);
                                ProgressDialogFragment.hide(getSupportFragmentManager());
                            }
                            else{
                                if (dataHelper.getScene_timestamp()==0){
                                    dataHelper.addPrevUser(mAuth.getCurrentUser().getUid());
                                }
                                startMainActivity();
                                ProgressDialogFragment.hide(getSupportFragmentManager());
                            }
                            ProgressDialogFragment.hide(getSupportFragmentManager());

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(User_login.this, "Unexpected Problem during Sign Up! Try Again later!", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }
    private void getDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed com and the pending Dynamic Link is
                        // an invitation, sign com the user anonymously, and record the
                        // referrer's UID.
                        //
                        if (deepLink != null) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            if (dataHelper.isRewardPossible()){
                                dataHelper.putReferrer(referrerUid);
                                dataHelper.putIsRewardPossible(false);
                            }
                        }
                    }
                });
    }

    private void getFacebookDetails(String uid,AccessToken token){

        GraphRequest request = GraphRequest.newMeRequest(
            token,
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // Application code
                    try {
                        Log.i("Response",response.toString());

                        String name = object.getString("name");

                        userModel.setUsername(name);

                        if (Profile.getCurrentProfile()!=null)
                        {
                            userModel.setFb_dp(""+Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                        }
                        dataHelper.addNewUser(uid,userModel);
                        startGenderActivity();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void startSelect_mask(){
        startActivityForResult(new Intent(this,Select_mask.class),RC_SUCCESS_MASK);
    }
    private void startQuoteActivity(){
        startActivityForResult(new Intent(this,QuoteActivity.class),RC_SUCCESS_QUOTE);
    }
    private void startGenderActivity(){
        startActivityForResult(new Intent(this,GenderActivity.class),RC_SUCCESS_GENDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==RC_SUCCESS_MASK){
            dataHelper.putMask2Firebase(data.getIntExtra(getString(R.string.mask),R.drawable.dp_1));
            startMainActivity();
        }
        else if (requestCode==RC_SUCCESS_QUOTE){
            dataHelper.putQuote2Firebase(data.getStringExtra(getString(R.string.quote)));
            startSelect_mask();
        }
        else if (requestCode==RC_SUCCESS_GENDER){
            dataHelper.putGender2Firebase(data.getStringExtra(getString(R.string.gender)));
            startQuoteActivity();
        }
        facebookHelper.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            ProgressDialogFragment.show(getSupportFragmentManager());
            handleFacebookAccessToken(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            ProgressDialogFragment.hide(getSupportFragmentManager());
            if (AccessToken.getCurrentAccessToken()!= null) {
                handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
            }
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("lol",error.toString());
            Toast.makeText(User_login.this, "Unexpected Problem during Sign Up! Try Again!", Toast.LENGTH_SHORT).show();
            ProgressDialogFragment.hide(getSupportFragmentManager());
        }
    }

}
