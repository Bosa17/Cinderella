package in.cinderella.testapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import in.cinderella.testapp.Fragments.ProgressDialogFragment;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.ConnectivityUtils;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FacebookHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class User_login extends AppCompatActivity {

    private static final String TAG = "User_login Activity";
    private Button btn;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth mAuth;
    private DataHelper dataHelper;
    private FacebookHelper facebookHelper;
    private UserModel userModel;
    private static int RC_SUCCESS=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
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
        facebookHelper.onActivityStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        facebookHelper.onActivityStop();
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            boolean isNewUser=true;
                            // Sign in success, update UI with the signed-in user's information
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "signInWithCredential:success");
                            if (isNewUser) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                getFacebookDetails(user.getUid(), token);
                                ProgressDialogFragment.hide(getSupportFragmentManager());
                            }
                            else{
                                startMainActivity();
                                ProgressDialogFragment.hide(getSupportFragmentManager());
                            }
                            ProgressDialogFragment.hide(getSupportFragmentManager());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
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

                        String email = object.getString("email");
                        String name = object.getString("name");
//                        String gender=object.getString("gender");

                        userModel.setUsername(name);
//                        userModel.setGender(gender);

                        if (Profile.getCurrentProfile()!=null)
                        {
                            userModel.setFb_link(""+Profile.getCurrentProfile().getLinkUri());
                            userModel.setFb_dp(""+Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                        }
                        dataHelper.addNewUser(uid,userModel);
                        startSelect_mask();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private void startSelect_mask(){
        startActivityForResult(new Intent(this,Select_mask.class),RC_SUCCESS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SUCCESS){
            dataHelper.putMask(data.getIntExtra(getString(R.string.mask),R.drawable.dp_1));
            startMainActivity();
        }
        else {
            // Pass the activity result back to the Facebook SDK
            facebookHelper.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void startMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
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
        }

        @Override
        public void onError(FacebookException error) {
            ProgressDialogFragment.hide(getSupportFragmentManager());
        }
    }

}
