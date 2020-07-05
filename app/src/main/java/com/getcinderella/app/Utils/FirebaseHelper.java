package com.getcinderella.app.Utils;

import android.content.Context;
import android.util.Log;

import com.getcinderella.app.Models.PreferenceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.orhanobut.hawk.Hawk;

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
    public String getGender(){
        return Hawk.get(mContext.getString(R.string.gender), "");
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
    public void updateAlias(String alias){
        Log.d("lol","updateFired! "+alias);
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.alias),alias);
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
                .child(userID)
                .setValue(partnerPreference);
    }

    public void endChat(String roomId){
        removeRoom(roomId);
        setAvailable();
    }
    public void putLast_available_at(long last_available_at){ Hawk.put("last_available_at", last_available_at); }
    public long getLast_available_at(){
        return Hawk.get("last_available_at",0L);
    }
    public void destroyAvailable(long t){
        myRef.child("a")
                .child(getGender())
                .child(t+""). removeValue();
    }
    public void setAvailable(){
        destroyAvailable(getLast_available_at());
        if ( getUserID()!=null) {
            PreferenceModel pm = new PreferenceModel("f", getUserID());
            long t=System.currentTimeMillis();
            putLast_available_at(t);
            myRef.child("a")
                    .child(getGender())
                    .child(t+"").setValue(pm);
        }
    }

    public void setUnavailable(){
        destroyAvailable(getLast_available_at());
        if ( getUserID()!=null) {
            PreferenceModel pm = new PreferenceModel("t", getUserID());
            long t=System.currentTimeMillis();
            putLast_available_at(t);
            myRef.child("a")
                    .child(getGender())
                    .child(t+"").setValue(pm);
        }
    }
    public void removeUserFromChannel(String scene){
        myRef.child(scene)
                .child(userID).removeValue();
        removeUserFromMatched();
        removeUserFromMatchedInit();
    }
    public void removeUserFromMatchedInit(){
        myRef.child("o")
                .child(userID). removeValue();
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

    public void updateSettings(long mask, String quote,String alias){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(
                mContext.getString(R.string.mask),mask,
                mContext.getString(R.string.quote),quote,
                mContext.getString(R.string.alias),alias
        );
    }

    public void updateMask(long mask){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.mask),mask);

    }

    public void updateGender(String gender){
        db.collection(mContext.getString(R.string.user_db))
                .document(getUserID()).update(mContext.getString(R.string.gender),gender);

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