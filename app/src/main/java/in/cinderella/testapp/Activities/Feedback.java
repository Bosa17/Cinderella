package in.cinderella.testapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import in.cinderella.testapp.R;

public class Feedback extends BaseActivity {
    private  final String DEVICE = "Device: ";
    private  final String SDK_VERSION = "SDK version: ";
    private  final String MODEL = "Model: ";
    private  final String APP_VERSION = "Cinderella build version: ";
    private  final String NEW_LINE = "\n";
    private  final String DIVIDER_STRING = "----------";
    private  final String TYPE_OF_EMAIL = "message/rfc822";

    public  StringBuilder getDeviseInfoForFeedback() {
        StringBuilder infoStringBuilder = new StringBuilder();
        infoStringBuilder.append(DEVICE).append(android.os.Build.DEVICE);
        infoStringBuilder.append(NEW_LINE);
        infoStringBuilder.append(SDK_VERSION).append(Build.VERSION.SDK_INT);
        infoStringBuilder.append(NEW_LINE);
        infoStringBuilder.append(MODEL).append(android.os.Build.MODEL);
        infoStringBuilder.append(NEW_LINE);
        infoStringBuilder.append(APP_VERSION).append("v1.0");
        infoStringBuilder.append(NEW_LINE);
        infoStringBuilder.append(DIVIDER_STRING);
        infoStringBuilder.append(NEW_LINE);
        return infoStringBuilder;
    }
    private RadioGroup feedbackTypesRadioGroup;
    private TextView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedbackTypesRadioGroup= findViewById(R.id.feedback_types_radiogroup);
        send=findViewById(R.id.tvNext);
        ImageView back = (ImageView) findViewById(R.id.ivBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedbackEmail( getSelectedFeedbackType());
            }
        });
    }

    public void sendFeedbackEmail( String feedbackType) {
        Resources resources = getResources();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{resources.getText(R.string.feedback_support_email)
                .toString()});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, feedbackType);
        intentEmail.putExtra(Intent.EXTRA_TEXT,
                (java.io.Serializable) getDeviseInfoForFeedback());
        intentEmail.setType(TYPE_OF_EMAIL);
        startActivity(Intent.createChooser(intentEmail, resources.getText(
                R.string.feedback_choose_email_provider)));
    }

    private String getSelectedFeedbackType() {
        int radioButtonID = feedbackTypesRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) feedbackTypesRadioGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }
}
