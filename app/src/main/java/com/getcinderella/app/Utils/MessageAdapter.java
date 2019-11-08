package com.getcinderella.app.Utils;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.getcinderella.app.R;
import com.sinch.android.rtc.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 1;

    public static final int DIRECTION_OUTGOING = 2;

    private boolean isRecurring=false;
    private int last_direction=0;
    private List<Pair<Message, Integer>> mMessages;

    private String incoming_name;
    private String outgoing_name;
    private @DrawableRes int incoming_mask;

    private LayoutInflater mInflater;

    public MessageAdapter(Activity activity) {
        mInflater = activity.getLayoutInflater();
        mMessages = new ArrayList<Pair<Message, Integer>>();
    }

    public void addMessage(Message message, int direction) {
        if (last_direction==0) {
            last_direction=direction;
        }
        else if (last_direction!=direction){
            last_direction=direction;
            isRecurring=false;
        }
        else if (last_direction==direction)
            isRecurring=true;
        direction=isRecurring?direction*10+1:direction*10;
        Log.e("yolohiya",""+direction);
        mMessages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    public void addUser(String name){
        this.outgoing_name=name;
    }

    public void addOppUser(String name, @DrawableRes int mask){
        this.incoming_name =name;
        this.incoming_mask =mask;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return mMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 22;
    }

    @Override
    public int getItemViewType(int i) {
        return mMessages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);
        Log.e("yolohiya",""+direction);
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING*10+1) {
                res = R.layout.message_incoming_recurring;
            }
            else if (direction == DIRECTION_INCOMING*10) {
                res = R.layout.message_incoming;
            }
            else if (direction == DIRECTION_OUTGOING*10+1) {
                res = R.layout.message_outgoing_recurring;
            }
            else if (direction == DIRECTION_OUTGOING*10) {
                res = R.layout.message_outgoing;
            }
            convertView = mInflater.inflate(res, viewGroup, false);
        }

        if (direction == DIRECTION_INCOMING*10) {
            TextView message_body=convertView.findViewById(R.id.message_body);
            Message message = mMessages.get(i).first;
            message_body.setText(message.getTextBody());
            ((TextView)convertView.findViewById(R.id.name)).setText(incoming_name);
            convertView.findViewById(R.id.avatar).setBackgroundResource(incoming_mask);

        } else if (direction == DIRECTION_OUTGOING*10) {
            TextView message_body=convertView.findViewById(R.id.message_body);
            Message message = mMessages.get(i).first;
            message_body.setText(message.getTextBody());
            if (outgoing_name.equals(""))
                ((TextView)convertView.findViewById(R.id.name)).setVisibility(View.GONE);
            else
                ((TextView)convertView.findViewById(R.id.name)).setText(outgoing_name);
        }
        else {
            TextView message_body=convertView.findViewById(R.id.message_body);
            Message message = mMessages.get(i).first;
            message_body.setText(message.getTextBody());

        }

        return convertView;
    }
}
