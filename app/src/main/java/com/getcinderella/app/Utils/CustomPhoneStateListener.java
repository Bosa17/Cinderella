package com.getcinderella.app.Utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class CustomPhoneStateListener extends PhoneStateListener {

    Context context; //Context to make Toast if required
    ServiceDataHelper dataHelper;
    public static  boolean isOnCall=false;
    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
        dataHelper=new ServiceDataHelper(this.context);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                //when Idle i.e no call
                isOnCall=false;
                dataHelper.putAvailable();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //when Off hook i.e in call
                //Make intent and start your service here
                dataHelper.putUnAvailable();
                isOnCall=true;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                //when Ringing
                isOnCall=true;
                dataHelper.putUnAvailable();
                break;
            default:
                break;
        }
    }
}
