package com.getcinderella.app.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.getcinderella.app.Activities.MatchActivity;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Map;

import com.getcinderella.app.Activities.PartnerChatActivity;

public class ChatService extends Service {

    public static final String CHAT_TYPE = "CHAT_TYPE";
    static final String TAG = ChatService.class.getSimpleName();

    private ServiceDataHelper dataHelper;
    private ChatServiceInterface mChatServiceInterface = new ChatServiceInterface();
    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new ServiceDataHelper(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mChatServiceInterface;
    }

    public class ChatServiceInterface extends Binder {

        public void initChatWithPayload(final Map payload){
            Log.d("insideChatService","lol");
            if(!dataHelper.getBlockUserCallerId().contains( payload.get("uID").toString())){
                final Intent intent;
                if (dataHelper.getIsOnCall())
                {
                    switch (payload.get("type").toString()){
                        case "1":FirebaseDatabase.getInstance().getReference().child("h").child( payload.get("rID").toString())
                                .removeValue();
                                break;
                        case "0":String roomId= StringUtils.extractRoomIdandParticipID(payload.get("comboID").toString())[0];
                            FirebaseDatabase.getInstance().getReference().child("h").child(roomId)
                                    .removeValue();
                            break;
                    }
                }
                else {
                    if (payload.get("type").toString().equals("1")) {
                        intent = new Intent(ChatService.this, PartnerChatActivity.class);
                        intent.putExtra("roomId", payload.get("rID").toString());
                        intent.putExtra("remoteUser", payload.get("uID").toString());
                    }
                    else {
                        intent = new Intent(ChatService.this, MatchActivity.class);
                        intent.putExtra("scene", payload.get("sid").toString());
                        intent.putExtra("comboID", payload.get("comboID").toString());
                        intent.putExtra("isPrivate",dataHelper.getIsPrivate());
                    }
                    intent.putExtra(CHAT_TYPE, "0");
                    intent.putExtra("pixies", dataHelper.getPixies());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ChatService.this.startActivity(intent);
                }
            }
            else {
                switch (payload.get("type").toString()){
                    case "1":FirebaseDatabase.getInstance().getReference().child("h").child( payload.get("rID").toString()).child("1p")
                            .setValue("b");
                        FirebaseDatabase.getInstance().getReference().child("h").child( payload.get("rID").toString())
                                .removeValue();
                        break;
                    case "0":String roomId= StringUtils.extractRoomIdandParticipID(payload.get("comboID").toString())[0];
                        FirebaseDatabase.getInstance().getReference().child("h").child(roomId)
                                .removeValue();
                        break;
                }
            }
        }
    }
}
