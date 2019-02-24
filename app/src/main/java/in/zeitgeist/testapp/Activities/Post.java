package in.zeitgeist.testapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.util.Log;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import in.zeitgeist.testapp.R;
import in.zeitgeist.testapp.Utils.BottomNavigationUtils;
import in.zeitgeist.testapp.Utils.Permissions;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;


public class Post extends AppCompatActivity {

    private static String TAG="Post Fragment";
    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.Post_picture_fragment
    };

    private NavController mNavController;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Log.d(TAG, "onCreateView: started.");
        PageNavigationView mNavigation = findViewById(R.id.navigation);

        mNavController = Navigation.findNavController(this, R.id.nav_post_fragment);

        initBottomNavigation(mNavigation);

    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermission();
    }

    //Initializing Bottom Navigation Components
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_landscape_black_24dp,"Image")
                .build();
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, mNavController);
    }


    private void checkPermission() {
        // give whatever permission you want. for example i am taking--Manifest.permission.READ_PHONE_STATE

        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) ){

            requestPermissions(Permissions.READ_STORAGE_PERMISSION, 2);
        }else {
            // if permission already granted
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