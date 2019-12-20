package com.getcinderella.app.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new CustomPhoneStateListener(context), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class CustomPhoneStateListener extends PhoneStateListener {

        Context context; //Context to make Toast if required
        ServiceDataHelper dataHelper;
        private  boolean isOnCall=false;
        CustomPhoneStateListener(Context context) {
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
                    Log.d("callState","Not on Call");
                    isOnCall=false;
                    dataHelper.putIsOnCall(isOnCall);
                    dataHelper.putAvailable();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //when Off hook i.e in call
                    //Make intent and start your service here
                case TelephonyManager.CALL_STATE_RINGING:
                    //when Ringing
                    Log.d("callState","On Call");
                    isOnCall=true;
                    dataHelper.putIsOnCall(isOnCall);
                    dataHelper.putUnAvailable();
                    break;
                default:
                    break;
            }
        }
    }

}