package in.zeitgeist.testapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import in.zeitgeist.testapp.utils.BottomNavigationUtils;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class MainActivity extends AppCompatActivity {

    static NavigationController navigationController;
    private final int[] PAGE_IDS = {
            R.id.navigationComponentPageAFragment,
            R.id.Profile_fragment,
            R.id.navigationComponentPageCFragment,
            R.id.Settings_fragment
    };

    private NavController mNavController;

    private PageNavigationView mNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigation = findViewById(R.id.navigation);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        initBottomNavigation(mNavigation);
        setMessage();

    }
    private void initBottomNavigation(PageNavigationView pageNavigationView) {
        navigationController = pageNavigationView.material()
                .addItem(R.drawable.ic_home_black_24dp, "Home")
                .addItem(R.drawable.ic_account_circle_black_24dp, "Profile")
                .addItem(R.drawable.ic_search_black_24dp, "Search")
                .addItem(R.drawable.ic_settings_black_24dp, "Settings")
                .build();
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, mNavController);
    }
    private void setMessage(){
        navigationController.setHasMessage(0,true);
    }

}
