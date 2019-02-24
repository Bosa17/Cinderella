package in.zeitgeist.testapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import in.zeitgeist.testapp.R;
import in.zeitgeist.testapp.Activities.User_login;

public class Settings extends PreferenceFragmentCompat{
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference preference = preferenceScreen.findPreference("checkbox_preference");
        Preference myPref=preferenceScreen.findPreference("logout");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().signOut();
                startSigninActivity();
                getActivity().finish();
                return false;
            }
        });
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
    }
    private void startSigninActivity(){
        Intent intent =new Intent(getActivity(), User_login.class);
        startActivity(intent);
    }
}
