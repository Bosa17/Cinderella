package com.getcinderella.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;


public class ContactSupport extends BaseActivity {
    private  final String DEVICE = "Device: ";
    private  final String SDK_VERSION = "SDK version: ";
    private  final String MODEL = "Model: ";
    private  final String APP_VERSION = "Cinderella build version: ";
    private  final String NEW_LINE = "\n";
    private  final String DIVIDER_STRING = "----------";
    private  final String TYPE_OF_EMAIL = "message/rfc822";
    private  final String USER_ID = "User ID: ";
    private DataHelper dataHelper;

    public  StringBuilder getDeviseInfoForCustomerSupport() {
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
        infoStringBuilder.append(USER_ID).append(dataHelper.getUID());
        return infoStringBuilder;
    }
    private RadioGroup customerSupportTypesRadioGroup;
    private TextView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        dataHelper=new DataHelper(this);
        customerSupportTypesRadioGroup = findViewById(R.id.customer_support_types_radiogroup);
        send=findViewById(R.id.tvNext);
        Context mContext=this;
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
                sendContactSupportEmail( getSelectedCustomerSupportType());
            }
        });
    }

    public void sendContactSupportEmail(String feedbackType) {
        Resources resources = getResources();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{resources.getText(R.string.support_email)
                .toString()});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, feedbackType);
        intentEmail.putExtra(Intent.EXTRA_TEXT,
                (java.io.Serializable) getDeviseInfoForCustomerSupport());
        intentEmail.setType(TYPE_OF_EMAIL);
        startActivity(Intent.createChooser(intentEmail, resources.getText(
                R.string.choose_email_provider)));
    }

    private String getSelectedCustomerSupportType() {
        int radioButtonID = customerSupportTypesRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) customerSupportTypesRadioGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }
}
