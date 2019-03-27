package in.lolita.testapp.Activities;

import android.Manifest;
import android.content.Context;
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

import in.lolita.testapp.R;
import in.lolita.testapp.Utils.BottomNavigationUtils;
import in.lolita.testapp.Utils.Permissions;
import me.majiajie.pagerbottomtabstrip.MaterialMode;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.Feed_fragment,
            R.id.Recepie_fragment,
            R.id.Search_fragment,
    };

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()== null){
            loginOrRegisterUser();
        }

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

    //starting login activity
    private void loginOrRegisterUser(){
        Intent intent = new Intent(this,User_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    //Initializing Bottom Navigation Components
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_home_black_24dp, "Home")
                .addItem(R.drawable.ic_add_circle_black_24dp,"Chats")
                .addItem(R.drawable.ic_notifications, "Notifications").setMode(MaterialMode.HIDE_TEXT)
                .build();
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, mNavController);
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











