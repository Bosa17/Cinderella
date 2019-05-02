package in.cinderella.testapp.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;


public class FirebaseHelper {
    private static String TAG="FirebaseHelper.class";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID="";
    private long karma;
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

    public String getUserID(){
        return userID;
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

    public long getKarmaWithUid(String uid){
        myRef.child(mContext.getString(R.string.user_db))
                .child(uid)
                .child(mContext.getString(R.string.karma)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //TODO
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return karma;
    }


    public void updateMask(long mask){
        myRef.child(mContext.getString(R.string.user_db))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.mask))
                .setValue(mask);
    }

    /**
     * retrieve user data from the firebase database
     * @param dataSnapshot
     * @return
     */
    public UserModel getUser(DataSnapshot dataSnapshot){

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
