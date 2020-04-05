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

    @Override
    public Result doWork() {
        // Do the work here
        PreferenceModel pm = new PreferenceModel("f",System.currentTimeMillis());
        String gender= Hawk.get(mContext.getString(R.string.gender));
        if (!getUID().equals("") && !gender.equals(""))
            FirebaseDatabase.getInstance().getReference().child("a")
                    .child(gender)
                    .child(getUID()).setValue(pm);
        return Result.success();
    }

    public String getUID(){
        return Hawk.get(mContext.getString(R.string.uid),"" );
    }
}
