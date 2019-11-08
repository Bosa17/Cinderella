package com.getcinderella.app.Utils;

import android.content.Context;

import com.getcinderella.app.Models.PreferenceModel;
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
        PreferenceModel pm=new PreferenceModel(partnerPreference,System.currentTimeMillis());
        myRef.child(scene)
                .child(userID)
                .setValue(pm);
    }

    public void removeUserFromChannel(String scene){
        myRef.child(scene)
                .child(userID).removeValue();
        removeUserFromMatched();
    }
    public void removeUserFromMatched(){
        myRef.child("m")
                .child(userID). removeValue();
    }
    public void removeinitChat(){
        myRef.child("n")
                .child(userID).removeValue();
    }
    public void removeRoom(String roomId){
        myRef.child("h")
                .child(roomId).removeValue();
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

    public void updateT(String T){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update("t",T);

    }

    public void updateQuote(String quote){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.quote),quote);

    }


    public void setPrivate(String roomId, String participId,boolean isPrivate){
        String pvt=isPrivate?"t":"f";
        myRef.child("h")
                .child(roomId)
                .child("p"+participId)
                .setValue(pvt);
    }

    public void setTyping(String roomId, String participId, boolean isTyping){
        String t=isTyping?"t":"f";
        myRef.child("h")
                .child(roomId)
                .child("t"+participId)
                .setValue(t);
    }

}
