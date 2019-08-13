package in.cinderella.testapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import in.cinderella.testapp.Models.RemoteUserConnection;
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
        putUID(userID);
        putMask((int)curr_user.getMask());
        putGender("Male");
        putQuote("");
        putUsername(curr_user.getUsername());
        putFb_link(curr_user.getFb_link());
        putFb_dp(curr_user.getFb_dp());
        putConnection(0L);
        putKarma(0L);
        putPixies(0L);
        firebaseHelper.addNewUser(userID,get());
    }
    public void addNewRemoteUser(RemoteUserConnection remoteUser,long conn_no){
        Hawk.put(mContext.getString(R.string.remote)+conn_no,remoteUser);
    }

    public ArrayList<String> getRemoteUserDps(){
        ArrayList<String> filePaths=new ArrayList<>();
        try {
            for (long i = getConnection(); i >= 1; i--) {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                filePaths.add(remoteTemp.getRemoteUserDp());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return filePaths;
    }

    public ArrayList<String> getRemoteUserNames(){
        ArrayList<String> Names=new ArrayList<>();
        try {
            for (long i = getConnection(); i >= 1; i--)  {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                Names.add(remoteTemp.getRemoteUserName());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Names;
    }

    public ArrayList<String> getRemoteUserQuotes(){
        ArrayList<String> Quotes=new ArrayList<>();
        try {
            for (long i = getConnection(); i >= 1; i--)  {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                Quotes.add(remoteTemp.getRemoteUserQuote());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Quotes;
    }


    public ArrayList<Long> getRemoteUserKarmas(){
        ArrayList<Long> Karmas=new ArrayList<>();
        try {
            for (long i = getConnection(); i >= 1; i--)  {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                Karmas.add(remoteTemp.getRemoteUserKarma());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Karmas;
    }

    public void syncWithFirebase(DataSnapshot dataSnapshot){
        UserModel user=dataSnapshot.getValue(UserModel.class);
        try {
            putKarma(user.getKarma());
            putUsername(user.getUsername());
            putPixies(user.getPixies());
        }catch(Exception e){
            putKarma(0);
            Toast.makeText(mContext,"Some Error Occured!",Toast.LENGTH_SHORT).show();
        }
    }

    public void updateKarmaWithUid(String uid,long karma){
        firebaseHelper.updateKarmaWithUid(uid,karma);
    }

    public UserModel get(){
        UserModel user=new UserModel();
        user.setFb_dp(getFb_dp());
        user.setFb_link(getFb_link());
        user.setGender(getGender());
        user.setQuote(getQuote());
        user.setKarma(getKarma());
        user.setUsername(getUsername());
        user.setMask(getMask());
        user.setPixies(getPixies());
        return user;
    }

    public void putMask(int maskID){
        Hawk.put(mContext.getString(R.string.mask), maskID);
        firebaseHelper.updateMask(maskID);
    }
    public int getMask(){
        return Hawk.get(mContext.getString(R.string.mask),R.drawable.dp_1);
    }
    public void putConnection(long connection){
        Hawk.put(mContext.getString(R.string.connection), connection);
    }

    public long getConnection(){
        return Hawk.get(mContext.getString(R.string.connection),0L);
    }

    public void putKarma(long karma){
        Hawk.put(mContext.getString(R.string.karma), karma);
    }

    public long getKarma(){
        return Hawk.get(mContext.getString(R.string.karma),0L);
    }
    public long getPixies(){
        return Hawk.get(mContext.getString(R.string.pixies),0L);
    }
    public void putPixies(long pixies){
        Hawk.put(mContext.getString(R.string.pixies), pixies);
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

    public void putFb_link(String fb_link){
        Hawk.put(mContext.getString(R.string.fb_link), fb_link);
    }
    public String getFb_link(){
        return Hawk.get(mContext.getString(R.string.fb_link));
    }

    public void putQuote(String quote){
        Hawk.put(mContext.getString(R.string.quote), quote);
        firebaseHelper.updateQuote(quote);
    }
    public String getQuote(){
        return Hawk.get(mContext.getString(R.string.quote),"");
    }

    public void putUID(String uid){
        Hawk.put(mContext.getString(R.string.uid), uid);
    }
    public String getUID(){
        return Hawk.get(mContext.getString(R.string.uid));
    }
    public void putPrevTheme(String theme){
        Hawk.put("prev_theme",theme);
    }
    public String getPrevTheme(){
        return Hawk.get("prev_theme",mContext.getString(R.string.royalty));
    }
    public void putTheme(String theme){
        Hawk.put("theme",theme);
    }
    public String getTheme(){
        return Hawk.get("theme",mContext.getString(R.string.royalty));
    }
}
