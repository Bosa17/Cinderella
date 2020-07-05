package com.getcinderella.app.Utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.getcinderella.app.Models.PreferenceModel;
import com.getcinderella.app.R;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;

public class SyncWorker extends Worker {
    private Context mContext;
    public SyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        mContext=context;
        Hawk.init(mContext).build();
    }
    public void putLast_available_at(long last_available_at){ Hawk.put("last_available_at", last_available_at); }
    public long getLast_available_at(){
        return Hawk.get("last_available_at",0L);
    }
    public void destroyAvailable(long t){
        FirebaseDatabase.getInstance().getReference().child("a").child(Hawk.get(mContext.getString(R.string.gender)))
                .child(t+""). removeValue();
    }
    public void putAvailable(){
        String gender= Hawk.get(mContext.getString(R.string.gender));
        if (!gender.equals("") && getUID()!=null) {
            destroyAvailable(getLast_available_at());
            PreferenceModel pm = new PreferenceModel("f", getUID());
            long t=System.currentTimeMillis();
            putLast_available_at(t);
            FirebaseDatabase.getInstance().getReference().child("a")
                    .child(gender)
                    .child(t+"").setValue(pm);
        }
    }
    @Override
    public Result doWork() {
        // Do the work here
        putAvailable();
        return Result.success();
    }

    public String getUID(){
        return Hawk.get(mContext.getString(R.string.uid),"" );
    }
}
