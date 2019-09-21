package com.getcinderella.app.Utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;


public class FirebaseHelper {
    private static String TAG="FirebaseHelper.class";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference myRef;
    private String userID="";

    private Context mContext;

    public FirebaseHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mContext = context;
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }
    public DatabaseReference getRef(){return myRef;}
    public CollectionReference getUserRef(){
        return  db.collection(mContext.getString(R.string.user_db));
    }
    public String getUserID(){
        return mAuth.getCurrentUser().getUid();
    }

    public void addNewUser(String userID,UserModel user){

        db.collection(mContext.getString(R.string.user_db))
                .document(userID)
                .set(user);

    }
    public void updateIsPremium(boolean isPremium){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.isPremium),isPremium);

    }
    public void updatePixie(long pixie){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.pixies),pixie);
    }
    public void updateCharismaWithUid(String uid, long charisma){
        db.collection(mContext.getString(R.string.user_db))
                .document(uid)
                .update(mContext.getString(R.string.charisma),charisma);

    }
    public void updatePixieWithUid(String uid, long pixie){
        db.collection(mContext.getString(R.string.user_db))
                .document(uid)
                .update(mContext.getString(R.string.pixies),pixie);

    }
    public void addUserToChannel(String scene,String partnerPreference){
        myRef.child(scene)
                .child(userID).setValue(partnerPreference);
    }

    public void removeUserFromChannel(String scene){
        myRef.child(scene)
                .child(userID).removeValue();
        myRef.child("m")
                .child(userID).removeValue();
    }
    public void removeUserFromMatched(){
        myRef.child("m")
                .child(userID).removeValue();
    }

    public void updateSettings(long mask, String quote){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(
                        mContext.getString(R.string.mask),mask,
                        mContext.getString(R.string.quote),quote
                );
    }

    public void updateMask(long mask){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.mask),mask);

    }

    public void updateQuote(String quote){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.quote),quote);

    }


}
