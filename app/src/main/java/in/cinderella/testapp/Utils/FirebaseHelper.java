package in.cinderella.testapp.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;


public class FirebaseHelper {
    private static String TAG="FirebaseHelper.class";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID="";

    private Context mContext;

    public FirebaseHelper(Context context) {
        FirebaseApp.initializeApp(context);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public DatabaseReference getRef(){return myRef;}

    public String getUserID(){
        return mAuth.getCurrentUser().getUid();
    }

    public void addNewUser(String userID,UserModel user){
        myRef.child(mContext.getString(R.string.user_db))
                .child(userID)
                .setValue(user);

    }
    public void addKarma(long karma){
        myRef.child(mContext.getString(R.string.user_db))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.karma))
                .setValue(karma);
    }
    public void updateKarmaWithUid(String uid,long karma){
        myRef.child(mContext.getString(R.string.user_db))
                .child(uid)
                .child(mContext.getString(R.string.karma))
                .setValue(karma);
    }

    public void addUserToChannel(){
        myRef.child(mContext.getString(R.string.channel))
                .child(userID).setValue(1);
    }

    public void removeUserFromChannel(){
        myRef.child(mContext.getString(R.string.channel))
                .child(userID).removeValue();
    }


    public void updateMask(long mask){
        myRef.child(mContext.getString(R.string.user_db))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.mask))
                .setValue(mask);
    }



}
