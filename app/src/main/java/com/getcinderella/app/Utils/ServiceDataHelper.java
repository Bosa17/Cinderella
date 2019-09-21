package com.getcinderella.app.Utils;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Map;

import com.getcinderella.app.Models.BlockUserModel;
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
    }

    public ArrayList<String> getBlockUserCallerId(){
        ArrayList<String> call_ids=new ArrayList<>();
        try {
            for (long i = getBlocked(); i >= 1; i--)  {
                BlockUserModel blocked = Hawk.get(mContext.getString(R.string.blockUser) + i);
                call_ids.add(blocked.getCaller_id());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return call_ids;
    }
    private void putBlocked(long partners){
        Hawk.put(mContext.getString(R.string.blocked), partners);
    }
    private long getBlocked(){
        return Hawk.get(mContext.getString(R.string.blocked),0L);
    }
    public void blockUser(BlockUserModel user){
        Hawk.put(mContext.getString(R.string.blockUser)+(getBlocked()+1),user);
        putBlocked(getBlocked()+1);
    }

    public long getPixies(){
        return Hawk.get(mContext.getString(R.string.pixies),0L);
    }
}
