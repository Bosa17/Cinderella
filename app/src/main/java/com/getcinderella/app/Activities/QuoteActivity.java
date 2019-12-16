package com.getcinderella.app.Activities;

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

public class QuoteActivity extends BaseActivity {
    private EditText quote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        quote=(EditText) findViewById(R.id.quoteText);
        InputFilter filtertxt = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isDigit(source.charAt(i))) {
                        Toast.makeText(QuoteActivity.this,"No Numbers are allowed inside Quote",Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };

        quote.setFilters(new InputFilter[]{filtertxt,new InputFilter.LengthFilter(40)});
        TextView nextScreen = (TextView) findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = quote.getText().toString();
                if (q.matches("")) {
                    Toast.makeText(QuoteActivity.this, "You did not enter a quote", Toast.LENGTH_SHORT).show();
                }
                else
                    returnWithQuote();
            }
        });
    }
    private void returnWithQuote(){
        Intent intent =new Intent().putExtra(getString(R.string.quote),String.valueOf(quote.getText()));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
