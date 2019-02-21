package in.zeitgeist.testapp.Utils;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.zeitgeist.testapp.Models.UserModel;
import in.zeitgeist.testapp.R;


public class FirebaseHelper {
    private static String TAG="FirebaseHelper.class";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID="";

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



    public void addNewUser(String userID,String username){
        long dp=(long)(Math.random()*10);
        UserModel user = new UserModel(username,dp);

        myRef.child("User")
                .child(userID)
                .setValue(user);

    }

    public void getUsername(){

    }

    public void getDP(){

    }

}
