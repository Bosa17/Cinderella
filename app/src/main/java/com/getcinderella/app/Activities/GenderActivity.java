package com.getcinderella.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;

public class GenderActivity extends BaseActivity {

    private RadioGroup genderbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataHelper dataHelper=new DataHelper(this);
        setContentView(R.layout.activity_gender);
        genderbtn=findViewById(R.id.genderbtn);
        TextView gender_init=findViewById(R.id.gender_init);
        gender_init.setText("Hi "+dataHelper.getUsername()+", welcome to Cinderella. Please choose your gender carefully as you will not be able to change again. ");
        TextView nextScreen = (TextView) findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = getSelectedGender();
                if (q.matches("")) {
                    Toast.makeText(GenderActivity.this, "You have to choose your gender to continue", Toast.LENGTH_SHORT).show();
                }
                else
                    returnWithGender();
            }
        });
    }

    private String getSelectedGender() {
        String gender="";
        try {
            int radioButtonID = genderbtn.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) genderbtn.findViewById(radioButtonID);
            switch(radioButton.getText().toString()){
                case "Man":gender= "1";
                    break;
                case "Woman": gender= "2";
                    break;
            }
        }catch(Exception e){
            return "";
        }
        return gender;
    }

    private void returnWithGender(){
        Intent intent =new Intent().putExtra(getString(R.string.gender),getSelectedGender());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
