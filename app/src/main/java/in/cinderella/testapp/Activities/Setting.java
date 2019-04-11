package in.cinderella.testapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import in.cinderella.testapp.R;

public class Setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Display the fragment as the main content.
        ImageView back_btn=(ImageView) findViewById(R.id.settings_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });
    }
    private void startMainActivity(){
        super.onBackPressed();
    }
}
