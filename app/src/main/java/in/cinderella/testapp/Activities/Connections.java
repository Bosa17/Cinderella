package in.cinderella.testapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import in.cinderella.testapp.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Connections extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        ImageView back_btn=(ImageView) findViewById(R.id.connections_back);
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
