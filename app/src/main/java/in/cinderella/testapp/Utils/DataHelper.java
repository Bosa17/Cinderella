package in.cinderella.testapp.Utils;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;

public class DataHelper {
    private Context mContext;
    private FirebaseHelper firebaseHelper;

    public DataHelper(Context context){
        mContext=context;
        firebaseHelper=new FirebaseHelper(mContext);
        Hawk.init(mContext).build();
    }

    public void addNewUser(String userID,UserModel curr_user){
        firebaseHelper.addNewUser(userID,curr_user);
        putUID(userID);
        putMask((int)curr_user.getMask());
        putGender("Male");
        putUsername(curr_user.getUsername());
        putFb_dp(curr_user.getFb_dp());
        putKarma(0L);
    }

    public void syncWithFirebase(DataSnapshot dataSnapshot){
        UserModel user=dataSnapshot.getValue(UserModel.class);
        putKarma(user.getKarma());
    }

//    public void update(HashMap Settings){
//        firebaseHelper.updateMask(Settings.)
//    }

    public void put(UserModel curr_user){
        Hawk.put(mContext.getString(R.string.mask), curr_user.getMask());
        Hawk.put(mContext.getString(R.string.username), curr_user.getUsername());
        Hawk.put(mContext.getString(R.string.fb_dp), curr_user.getFb_dp());
        Hawk.put(mContext.getString(R.string.gender), curr_user.getGender());
        Hawk.put(mContext.getString(R.string.karma), curr_user.getKarma());
    }

    public UserModel get(){
        UserModel user=new UserModel();
        user.setFb_dp(getFb_dp());
        user.setGender(getGender());
        user.setKarma(getkarma());
        user.setUsername(getUsername());
        user.setMask(getMask());
        return user;
    }

    public void putMask(int maskID){
        Hawk.put(mContext.getString(R.string.mask), maskID);
        firebaseHelper.updateMask(maskID);
    }
    public int getMask(){
        return Hawk.get(mContext.getString(R.string.mask),R.drawable.dp_1);
    }
    public void putKarma(long karma){
        Hawk.put(mContext.getString(R.string.karma), karma);
    }
    public long getkarma(){
        return Hawk.get(mContext.getString(R.string.karma),0L);
    }
    public void putGender(String gender){
        Hawk.put(mContext.getString(R.string.gender), gender);
    }
    public String getGender(){
        return Hawk.get(mContext.getString(R.string.gender));
    }
    public void putUsername(String username){
        Hawk.put(mContext.getString(R.string.username), username);
    }
    public String getUsername(){
        return Hawk.get(mContext.getString(R.string.username));
    }
    public void putFb_dp(String fb_dp){
        Hawk.put(mContext.getString(R.string.fb_dp), fb_dp);
    }
    public String getFb_dp(){
        return Hawk.get(mContext.getString(R.string.fb_dp));
    }

    public void putUID(String uid){
        Hawk.put(mContext.getString(R.string.uid), uid);
    }
    public String getUID(){
        return firebaseHelper.getUserID();
    }
}
