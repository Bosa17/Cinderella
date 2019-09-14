package in.cinderella.testapp.Utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
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
    public void updateIsPremium(boolean isPremium){
        myRef.child(mContext.getString(R.string.user_db))
                .child(getUserID())
                .child(mContext.getString(R.string.isPremium))
                .setValue(isPremium);
    }
    public void updatePixie(long pixie){
        myRef.child(mContext.getString(R.string.user_db))
                .child(getUserID())
                .child(mContext.getString(R.string.pixies))
                .setValue(pixie);
    }
    public void updateCharismaWithUid(String uid, long pixie){
        myRef.child(mContext.getString(R.string.user_db))
                .child(uid)
                .child(mContext.getString(R.string.charisma))
                .setValue(pixie);
    }
    public void updatePixieWithUid(String uid, long charisma){
        myRef.child(mContext.getString(R.string.user_db))
                .child(uid)
                .child(mContext.getString(R.string.pixies))
                .setValue(charisma);
    }
    public void addUserToChannel(String chapter,String partnerPreference){
        myRef.child(chapter)
                .child(userID).setValue(partnerPreference);
    }

    public void removeUserFromChannel(String chapter){
        myRef.child(chapter)
                .child(userID).removeValue();
    }


    public void updateMask(long mask){
        myRef.child(mContext.getString(R.string.user_db))
                .child(getUserID())
                .child(mContext.getString(R.string.mask))
                .setValue(mask);
    }

    public void updateQuote(String quote){
        myRef.child(mContext.getString(R.string.user_db))
                .child(getUserID())
                .child(mContext.getString(R.string.quote))
                .setValue(quote);
    }


}
