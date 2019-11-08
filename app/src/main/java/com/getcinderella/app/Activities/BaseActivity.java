package com.getcinderella.app.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;


import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;
import com.getcinderella.app.Utils.ChatService;

public class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private DataHelper dataHelper;
    private ChatService.ChatServiceInterface mChatServiceInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper=new DataHelper(this);
        bindService();
        if (dataHelper.getTheme().equals(getString(R.string.temptation))){
            setTheme(R.style.ThemeTemptation);
        }
        else if (dataHelper.getTheme().equals(getString(R.string.royalty))){
            setTheme(R.style.ThemeRoyalty);
        }
        else if (dataHelper.getTheme().equals(getString(R.string.passion))){
            setTheme(R.style.ThemePassion);
        }
        else if (dataHelper.getTheme().equals(getString(R.string.vibrant))){
            setTheme(R.style.ThemeVibrant);
        }
        else if (dataHelper.getTheme().equals(getString(R.string.boss))){
            setTheme(R.style.ThemeBoss);
        }
        else if (dataHelper.getTheme().equals(getString(R.string.babe))){
            setTheme(R.style.ThemeBabe);
        }
        else if (dataHelper.getTheme().equals(getString(R.string.sexy))){
            setTheme(R.style.ThemeSexy);
        }
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (ChatService.class.getName().equals(componentName.getClassName())) {
            mChatServiceInterface = (ChatService.ChatServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (ChatService.class.getName().equals(componentName.getClassName())) {
            mChatServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected ChatService.ChatServiceInterface getSinchServiceInterface() {
        return mChatServiceInterface;
    }



    private void bindService() {
        Intent serviceIntent = new Intent(this, ChatService.class);
        getApplicationContext().bindService(serviceIntent, this, BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        String themeName=String.valueOf(outValue.string);
        if(!themeName.equals(dataHelper.getTheme())){
            recreate();
        }
    }
}
