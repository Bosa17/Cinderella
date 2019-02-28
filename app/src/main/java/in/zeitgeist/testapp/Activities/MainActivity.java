package in.zeitgeist.testapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import in.zeitgeist.testapp.R;
import in.zeitgeist.testapp.Utils.BottomNavigationUtils;
import in.zeitgeist.testapp.Utils.Permissions;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.Feed_fragment,
            R.id.Post_activity,
            R.id.Search_fragment,
    };

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        PageNavigationView mNavigation = findViewById(R.id.navigation);

        mNavController = Navigation.findNavController(this, R.id.nav_main_fragment);

        initBottomNavigation(mNavigation);
        setMessage();

    }
    //checking to see if user is already logged in else goes to user login activity
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser== null){
            loginOrRegisterUser();
        }
        checkPermission();
    }
    //starting login activity
    private void loginOrRegisterUser(){
        Intent intent = new Intent(this,User_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    //Initializing Bottom Navigation Components
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_home_black_24dp, "Home")
                .addItem(R.drawable.ic_add_circle_black_24dp,"Post")
                .addItem(R.drawable.ic_explore_black_24dp, "Explore")
                .build();
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, mNavController);
    }
    private void setMessage(){
        navigationController.setHasMessage(0,true);
    }


    private void checkPermission() {
        // give whatever permission you want. for example i am taking--Manifest.permission.READ_PHONE_STATE

        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) ){

            requestPermissions(Permissions.READ_STORAGE_PERMISSION, 2);
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
//                Toast.makeText(getApplicationContext(),  "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}











