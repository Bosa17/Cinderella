package in.cinderella.testapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper=new NotificationHelper(context);
        notificationHelper.createClaimPixieNotification();

    }

}