package com.getcinderella.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getcinderella.app.R;

public class AliasActivity extends AppCompatActivity {
    private EditText alias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alias);
        alias =(EditText) findViewById(R.id.aliasText);
        InputFilter filtertxt = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        Toast.makeText(AliasActivity.this,"No Space allowed inside Alias",Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };

        alias.setFilters(new InputFilter[]{filtertxt,new InputFilter.LengthFilter(17)});
        TextView nextScreen = (TextView) findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = alias.getText().toString();
                if (q.matches("")) {
                    Toast.makeText(AliasActivity.this, "You did not enter an alias", Toast.LENGTH_SHORT).show();
                }
                else
                    returnWithAlias();
            }
        });
    }
    private void returnWithAlias(){
        Intent intent =new Intent().putExtra(getString(R.string.alias),String.valueOf(alias.getText()));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
