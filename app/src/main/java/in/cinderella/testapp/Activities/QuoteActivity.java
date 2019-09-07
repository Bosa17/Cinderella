package in.cinderella.testapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.cinderella.testapp.R;

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
                    returnWithSelectedImage();
            }
        });
    }
    private void returnWithSelectedImage(){
        Intent intent =new Intent().putExtra(getString(R.string.quote),String.valueOf(quote.getText()));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}