package in.lolita.testapp.Utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import in.lolita.testapp.Models.UserModel;
import in.lolita.testapp.R;


public class FirebaseHelper {
    private static String TAG="FirebaseHelper.class";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID="";
    private StorageReference mStorageReference;

    private Context mContext;

    public FirebaseHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }



    public void addNewUser(String userID,UserModel user){

        myRef.child("User")
                .child(userID)
                .setValue(user);

    }

    /**
     * retrieve user data from the firebase database
     * @param dataSnapshot
     * @return
     */
    public UserModel getUser(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserSettings: retrieving user account settings from firebase.");

        UserModel user = new UserModel();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
            if(ds.getKey().equals(mContext.getString(R.string.user_db))) {

                user.setUsername(
                        ds.child(userID)
                                .getValue(UserModel.class)
                                .getUsername()
                );
                user.setFb_dp(
                        ds.child(userID)
                                .getValue(UserModel.class)
                                .getFb_dp()
                );
                user.setKarma(
                        ds.child(userID)
                                .getValue(UserModel.class)
                                .getKarma()
                );

                user.setGender(ds.child(userID)
                        .getValue(UserModel.class)
                        .getGender());
            }
        }
        return user;

    }

}
