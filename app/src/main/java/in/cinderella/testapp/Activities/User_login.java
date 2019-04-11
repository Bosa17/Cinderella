package in.cinderella.testapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import in.cinderella.testapp.Fragments.ProgressDialogFragment;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.FacebookHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    private FacebookHelper facebookHelper;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        userModel=new UserModel();
        facebookHelper=new FacebookHelper(this);
        setContentView(R.layout.activity_user_login);
        firebaseHelper = new FirebaseHelper(this);
        btn=findViewById(R.id.sign_in);

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                facebookHelper.login(new FacebookLoginCallback());
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


    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getFacebookDetails(user.getUid(),token);
                            ProgressDialogFragment.hide(getSupportFragmentManager());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void getFacebookDetails(final String uid,AccessToken token){

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
                            userModel.setFb_dp(""+Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                        }

                        firebaseHelper.addNewUser(uid,userModel);
                        startMainActivity();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        facebookHelper.onActivityResult(requestCode, resultCode, data);
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
