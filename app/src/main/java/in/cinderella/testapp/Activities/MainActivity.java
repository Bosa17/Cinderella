package in.cinderella.testapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.BottomNavigationUtils;
import in.cinderella.testapp.Utils.Permissions;
import me.majiajie.pagerbottomtabstrip.MaterialMode;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.Home_fragment,
            R.id.Partner_fragment,
            R.id.Pixies_fragment,
    };

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PageNavigationView mNavigation = findViewById(R.id.navigation);

        mNavController = Navigation.findNavController(this, R.id.nav_main_fragment);

        initBottomNavigation(mNavigation);
        checkPermission();

    }


    //Initializing Bottom Navigation Components
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        TypedValue typedValue=new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int color = typedValue.data;
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_home, "",color)
                .addItem(R.drawable.ic_partners,"",color)
                .addItem(R.drawable.ic_pixie_buy, "",color).setDefaultColor(getResources().getColor(R.color.white,getTheme()))
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
            requestPermissions(Permissions.PERMISSIONS, 2);
        }else {
            //write your code here. if permission already granted


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











