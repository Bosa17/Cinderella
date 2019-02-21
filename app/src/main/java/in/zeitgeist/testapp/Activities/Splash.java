package in.zeitgeist.testapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import in.zeitgeist.testapp.R;

import android.content.Intent;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent=new Intent(this,MainActivity.class);
        Thread timer=new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
