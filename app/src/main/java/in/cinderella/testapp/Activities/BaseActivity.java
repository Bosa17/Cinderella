package in.cinderella.testapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;

public class BaseActivity extends AppCompatActivity {
    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
