package com.getcinderella.app.Utils;

import android.content.Context;

import com.getcinderella.app.Models.PreferenceModel;
import com.getcinderella.app.Models.RemoteUserConnection;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Map;

import com.getcinderella.app.Models.SceneModel;
import com.getcinderella.app.R;

public class ServiceDataHelper {
    private Context mContext;
    private static OnSceneChangedListener onSceneChangedListener;
    public void setOnSceneChangedListener(OnSceneChangedListener listener) {
        onSceneChangedListener = listener;
    }

    public interface OnSceneChangedListener {
        void onSceneChanged();
    }
    public ServiceDataHelper(Context context){
        mContext=context;
        Hawk.init(mContext).build();
    }
    public String getUID(){
        return Hawk.get(mContext.getString(R.string.uid),"" );
    }

    public void saveSituationsFromFCM(Map data){
        String[] names = data.get("name").toString().split("@");
        String[] desc = data.get("desc").toString().split("@");
        String[] option0 = data.get("option0").toString().split("@");
        String[] option1 = data.get("option1").toString().split("@");
        String[] scene_no = data.get("scene_no").toString().split("@");
        long timestamp=Long.valueOf(data.get("timestamp").toString());
        Hawk.put("scene_timestamp",timestamp);
        try {
            for (int i=1;i<4;i++) {
                SceneModel tmp = new SceneModel();
                tmp.setDesc(desc[i-1]);
                tmp.setName(names[i-1]);
                tmp.setOption0(option0[i-1]);
                tmp.setOption1(option1[i-1]);
                tmp.setScene_no(scene_no[i-1]);
                Hawk.put("scene"+i,tmp);
            }
        }catch (Exception e){
            Hawk.put("scene_timestamp",0);
        }
        try {
            onSceneChangedListener.onSceneChanged();
        }catch (Exception ignore){

        }
        putAvailable();
    }

    public ArrayList<String> getBlockUserCallerId(){
        return Hawk.get(mContext.getString(R.string.blocked),new ArrayList<>());
    }

    private void putBlocked(String uid){
        ArrayList<String> ids=getBlockUserCallerId();
        ids.add(uid);
        Hawk.put(mContext.getString(R.string.blocked),ids);
    }

    public void blockUser(String userID){
        putBlocked(userID);
    }

    public long getPixies(){
        return Hawk.get(mContext.getString(R.string.pixies),0L);
    }

    public void putAvailable() {
        PreferenceModel pm = new PreferenceModel("f",System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference().child("a")
                .child(Hawk.get(mContext.getString(R.string.gender)))
                .child(getUID()).setValue(pm);
    }
    public void putUnAvailable() {
        PreferenceModel pm = new PreferenceModel("t",System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference().child("a")
                .child(Hawk.get(mContext.getString(R.string.gender)))
                .child(getUID()).setValue(pm);
    }

    public SceneModel getScene(String scene_no){
        for (int i=1;i<=3;i++){
            SceneModel scene=(SceneModel)Hawk.get(mContext.getResources().getString(R.string.scene)+i);
            if(scene.getScene_no().equals(scene_no))
            {
                return scene;
            }
        }
        return null;
    }

    public boolean getIsPrivate(){
        return Hawk.get("isPrivate");
    }

    public void saveRemoteTmp(RemoteUserConnection remoteTmp){
        Hawk.put("remoteTmp",remoteTmp);
    }
}
