package in.lolita.testapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import in.lolita.testapp.R;
import in.lolita.testapp.Activities.User_login;

public class Settings extends PreferenceFragment {

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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
