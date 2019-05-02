package in.cinderella.testapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.BottomNavigationUtils;
import in.cinderella.testapp.Utils.Permissions;
import me.majiajie.pagerbottomtabstrip.MaterialMode;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class MainActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.Feed_fragment,
            R.id.Call_fragment,
            R.id.Search_fragment,
    };

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        PageNavigationView mNavigation = findViewById(R.id.navigation);

        mNavController = Navigation.findNavController(this, R.id.nav_main_fragment);

        initBottomNavigation(mNavigation);

    }
    //checking to see if user is already logged in else goes to user login activity
    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }
    //Initializing Bottom Navigation Components
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_home, "",getResources().getColor(R.color.colorAccent,getTheme()))
                .addItem(R.drawable.ic_call,"",getResources().getColor(R.color.colorAccent,getTheme()))
                .addItem(R.drawable.ic_notifications, "",getResources().getColor(R.color.colorAccent,getTheme())).setDefaultColor(getResources().getColor(R.color.white,getTheme()))
                .build();
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, mNavController);
    }


    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) ){
            Toast.makeText(getApplicationContext(),"Accept all the permissions",Toast.LENGTH_SHORT).show();

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











