package in.cinderella.testapp.Utils;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.Map;

import in.cinderella.testapp.Models.SceneModel;
import in.cinderella.testapp.R;

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
        long timestamp=Long.valueOf(data.get("timestamp").toString());
        Hawk.put("scene_timestamp",timestamp);
        for (int i=1;i<4;i++) {
            SceneModel tmp = new SceneModel();
            tmp.setDesc(desc[i-1]);
            tmp.setName(names[i-1]);
            tmp.setOption0(option0[i-1]);
            tmp.setOption1(option1[i-1]);
            Hawk.put("scene"+i,tmp);
        }
        try {
            onSceneChangedListener.onSceneChanged();
        }catch (Exception ignore){

        }
    }
}
