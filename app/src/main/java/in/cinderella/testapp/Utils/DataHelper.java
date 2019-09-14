package in.cinderella.testapp.Utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Random;

import in.cinderella.testapp.Models.RemoteUserConnection;
import in.cinderella.testapp.Models.SceneModel;
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
        saveScene();
        putMask((int)curr_user.getMask());
        putGender("Male");
        putQuote("");
        putLast_sign_at(System.currentTimeMillis());
        putUsername(curr_user.getUsername());
        putFb_dp(curr_user.getFb_dp());
        putIsPrivate(false);
        putIsPremium(false);
        putPartner(0L);
        putCharisma(0L);
        putPixies(30L);
        firebaseHelper.addNewUser(userID,get());
    }
    public void addNewRemoteUser(RemoteUserConnection remoteUser,long conn_no){
        Hawk.put(mContext.getString(R.string.remote)+conn_no,remoteUser);
    }

    public ArrayList<String> getRemoteUserDps(){
        ArrayList<String> filePaths=new ArrayList<>();
        try {
            for (long i = getPartners(); i >= 1; i--) {
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
            for (long i = getPartners(); i >= 1; i--)  {
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
            for (long i = getPartners(); i >= 1; i--)  {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                Quotes.add(remoteTemp.getRemoteUserQuote());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Quotes;
    }

    public ArrayList<String> getRemoteUserIds(){
        ArrayList<String> Ids=new ArrayList<>();
        try {
            for (long i = getPartners(); i >= 1; i--)  {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                Ids.add(remoteTemp.getRemoteUserId());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Ids;
    }

    public void deleteRemoteUser(int index){
        for (long i = getPartners(); i >= index; i--)  {

            RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
        }
    }
    public ArrayList<Long> getRemoteUserCharismas(){
        ArrayList<Long> Charismas=new ArrayList<>();
        try {
            for (long i = getPartners(); i >= 1; i--)  {
                RemoteUserConnection remoteTemp = Hawk.get(mContext.getString(R.string.remote) + i);
                Charismas.add(remoteTemp.getRemoteUserCharisma());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Charismas;
    }
    public void saveScene(){
        try {
            firebaseHelper.getRef().child("scene")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            saveScene(dataSnapshot);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch (Exception ignore){
            putDefaultScenes();
        }
    }

    private void saveScene(DataSnapshot dataSnapshot) {
        for (DataSnapshot sceneSnapshot : dataSnapshot.getChildren()) {
            SceneModel tmp=sceneSnapshot.getValue(SceneModel.class);
            Hawk.put(mContext.getResources().getString(R.string.scene)+sceneSnapshot.getKey(),tmp);
        }

    }


    public ArrayList<SceneModel> getScenes(){
        ArrayList<SceneModel> sceneModels =new ArrayList<>();
        for (int i=1;i<=3;i++){
            sceneModels.add(Hawk.get(mContext.getResources().getString(R.string.scene)+i));
        }
        return sceneModels;
    }

    public void putDefaultScenes(){
        SceneModel defaultScene=new SceneModel("Loading","Please Wait Fetching Scenes Data...","Impaitence","Impaitence");
        for (int i=1;i<=3;i++){
            Hawk.put(mContext.getResources().getString(R.string.scene)+i,defaultScene);
        }
    }
    public void syncWithFirebase(DataSnapshot dataSnapshot){
        UserModel user=dataSnapshot.getValue(UserModel.class);
        putUID(firebaseHelper.getUserID());
        try {
            putCharisma(user.getCharisma());
            putPixies(user.getPixies());
        }catch(Exception e){
            putCharisma(0);
            Toast.makeText(mContext,"Some Error Occured!",Toast.LENGTH_SHORT).show();
        }
    }

    public void updateCharismaWithUid(String uid, long skill){
        firebaseHelper.updateCharismaWithUid(uid,skill);
    }
    public void updatePixieWithUid(String uid, long pixie){
        firebaseHelper.updatePixieWithUid(uid,pixie);
    }
    public void addPixies(int pixies){
        firebaseHelper.updatePixie(getPixies()+pixies);
    }
    public int rewardPixies(){
        int pixies_won=new Random().nextInt(4);
        firebaseHelper.updatePixie(getPixies()+pixies_won);
        return pixies_won;
    }

    public void congoPixies(){
        if (getIsPremium())
            firebaseHelper.updatePixie(getPixies()+7);
        else
            firebaseHelper.updatePixie(getPixies()+3);
    }

    public UserModel get(){
        UserModel user=new UserModel();
        user.setFb_dp(getFb_dp());
        user.setQuote(getQuote());
        user.setCharisma(getCharisma());
        user.setUsername(getUsername());
        user.setMask(getMask());
        user.setPremium(getIsPremium());
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
    public void putPartner(long partners){
        Hawk.put(mContext.getString(R.string.partner), partners);
    }

    public long getPartners(){
        return Hawk.get(mContext.getString(R.string.partner),0L);
    }

    public void putCharisma(long charisma){
        Hawk.put(mContext.getString(R.string.charisma), charisma);
    }

    public long getCharisma(){
        return Hawk.get(mContext.getString(R.string.charisma),0L);
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
        return Hawk.get(mContext.getString(R.string.uid),"" );
    }
    public void putTheme(String theme){
        Hawk.put("theme",theme);
    }
    public String getTheme(){
        return Hawk.get("theme",mContext.getString(R.string.royalty));
    }
    public boolean getIsPrivate(){
        return Hawk.get("isPrivate",true);
    }
    public void selectPrivate(){
        putIsPrivate(!getIsPrivate());
    }
    public void putIsPrivate(boolean isPrivate){
        Hawk.put("isPrivate",isPrivate);
    }
    public boolean getIsPremium(){
        return Hawk.get(mContext.getString(R.string.isPremium),false);
    }
    public void putIsPremium(boolean IsPremium){
        firebaseHelper.updateIsPremium(IsPremium);
        Hawk.put(mContext.getString(R.string.isPremium),IsPremium);
    }
    public void putLast_sign_at(long last_sign_at){
        Hawk.put(mContext.getString(R.string.last_sign_at), last_sign_at);
    }
    public long getLast_sign_at(){
        return Hawk.get(mContext.getString(R.string.last_sign_at),0L);
    }
    public void putAds_watched(int ads_watched){
        Hawk.put("ads_watched", ads_watched);
    }
    public void Ads_watched(){
        Hawk.put("ads_watched", getAds_watched()+1);
    }
    public int getAds_watched(){
        return Hawk.get("ads_watched",0);
    }
    public boolean isTutorialShown(){
        return Hawk.get("isTutorialShown",false);
    }
    public void putTutorialShown(boolean isTutorialShown){
        Hawk.put("isTutorialShown",isTutorialShown);
    }
    public void putScene_timestamp(long scene_timestamp){
        Hawk.put("scene_timestamp", scene_timestamp);
    }

    public long getScene_timestamp(){
        return Hawk.get("scene_timestamp",0L);
    }
    public boolean isRewardPossible(){
        return Hawk.get("isRewardPossible",true);
    }
    public void putIsRewardPossible(boolean isRewardPossible){
        Hawk.put("isRewardPossible",isRewardPossible);
    }
    public void putReferrer(String referrer){
        Hawk.put("referrer", referrer);
    }
    public String getReferrer(){
        return Hawk.get("referrer","");
    }

}

