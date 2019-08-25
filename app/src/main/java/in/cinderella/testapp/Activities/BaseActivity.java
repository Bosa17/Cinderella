package in.cinderella.testapp.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.SinchService;

public class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private DataHelper dataHelper;
    private SinchService.SinchServiceInterface mSinchServiceInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
        dataHelper=new DataHelper(this);
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
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }



    private void bindService() {
        Intent serviceIntent = new Intent(this, SinchService.class);
        getApplicationContext().bindService(serviceIntent, this, BIND_AUTO_CREATE);

    }
    protected void unbindService(){
        getApplicationContext().unbindService(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        String themeName=String.valueOf(outValue.string);
        if(!themeName.equals(dataHelper.getTheme())){
            dataHelper.putPrevTheme(themeName);
            recreate();
        }
    }
}
