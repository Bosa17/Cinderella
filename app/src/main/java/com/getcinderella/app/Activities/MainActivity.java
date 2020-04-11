package com.getcinderella.app.Activities;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.BottomNavigationUtils;
import com.getcinderella.app.Utils.DataHelper;
import com.getcinderella.app.Utils.Permissions;

import com.getcinderella.app.Utils.NavigationController;
import com.getcinderella.app.Utils.PageNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore myRef;
    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.Home_fragment,
            R.id.Partner_fragment,
            R.id.Pixies_fragment,
    };
    private DataHelper dataHelper;
    private NavController mNavController;
    private static OnDataChangedListener onDataChangedListener;
    public void setOnDataChangedListener(OnDataChangedListener listener) {
        onDataChangedListener = listener;
    }

    public interface OnDataChangedListener {
        void onDataChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRef = FirebaseFirestore.getInstance();
        dataHelper= new DataHelper(this);
        PageNavigationView mNavigation = findViewById(R.id.navigation);

        mNavController = Navigation.findNavController(this, R.id.nav_main_fragment);

        initBottomNavigation(mNavigation);
        checkPermission();
        myRef.collection(getString(R.string.user_db))
                .document(dataHelper.getUID())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (snapshot != null && snapshot.exists()) {
                            dataHelper.syncWithFirebase(snapshot);
                            try {
                                onDataChangedListener.onDataChanged();
                            }catch (Exception exc){
//                                Ignore
                            }
                        }
                    }
                });
    }


    //Initializing Bottom Navigation Components
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        TypedValue typedValue=new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int color = typedValue.data;
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_home, "Home",color)
                .addItem(R.drawable.ic_partners,"Partners",color)
                .addItem(R.drawable.ic_pixie_buy, "Pixie",color).setDefaultColor(getResources().getColor(R.color.white))
                .build();
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, mNavController);
    }

    public  void navigateToPixie(){
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();
        mNavController.navigate(PAGE_IDS[2],null,options);
    }

    public  void refreshHome(){
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();
        mNavController.navigate(PAGE_IDS[0],null,options);
    }



    private void checkPermission() {
        if (!Permissions.hasAllPermissions(this) ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(Permissions.PERMISSIONS, 2);
            }
        }else{

        }

        if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this) && !dataHelper.getAutostart()){
            new MaterialDialog.Builder(MainActivity.this).title("Enable AutoStart")
                    .content(
                            " VERY IMPORTANT!!! Please allow Cinderella to always run in the background,else your partner will not be able to use \"ChatCall\" to connect with you! Settings->Autostart and enable Cinderella.")
                    .theme(Theme.DARK)
                    .positiveText("ALLOW")
                    .positiveColor(getResources().getColor(R.color.the_royalty))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            AutoStartPermissionHelper.getInstance().getAutoStartPermission(MainActivity.this);
                        }
                    })
                    .negativeText("ALREADY ALLOWED")
                    .negativeColor(getResources().getColor(R.color.the_royalty))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dataHelper.putAutoStart(true);
                        }
                    })
                    .show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            Log.i("resultcode",""+requestCode);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("resultcode",""+requestCode);
            }
            else {
                Toast.makeText(getApplicationContext(),  "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

