package in.cinderella.testapp.Utils;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import in.cinderella.testapp.R;

public class ServiceDataHelper {
    private Context mContext;
    public ServiceDataHelper(Context context){
        mContext=context;
        Hawk.init(mContext).build();
    }
    public String getUID(){
        return Hawk.get(mContext.getString(R.string.uid),"" );
    }

}
