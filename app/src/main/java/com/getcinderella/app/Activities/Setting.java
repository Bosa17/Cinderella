package com.getcinderella.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.getcinderella.app.Fragments.ProgressDialogFragment;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class Setting extends BaseActivity {

    //widgets
    private TextView faq;
    private ImageView image;
    private EditText quote;
    private TextView invite;
    private TextView feedback;
    private Button temptation;
    private Button royalty;
    private Button babe;
    private Button passion;
    private Button boss;
    private Button vibrant;
    private Button sexy;

    //vars
    private static int RC_SUCCESS=2;
    private boolean isChanged;
    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper=new DataHelper(this);
        setContentView(R.layout.activity_settings);
        temptation=findViewById(R.id.temptation_switch);
        passion=findViewById(R.id.passion_switch);
        royalty=findViewById(R.id.royalty_switch);
        babe=findViewById(R.id.babe_switch);
        boss=findViewById(R.id.boss_switch);
        vibrant=findViewById(R.id.vibrant_switch);
        sexy=findViewById(R.id.sexy_switch);
        quote=(EditText) findViewById(R.id.quoteText);
        faq =(TextView) findViewById(R.id.faq);
        invite =(TextView) findViewById(R.id.invite);
        feedback=(TextView) findViewById(R.id.feedback);
        image = (ImageView) findViewById(R.id.imageShare);
        ImageView back = (ImageView) findViewById(R.id.ivBack);
        if (!dataHelper.getQuote().trim().equals(""))
            quote.setText(dataHelper.getQuote());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSelect_mask();
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInviteClicked();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFeedbackActivity();
            }
        });
        TextView share = (TextView) findViewById(R.id.ivSave);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        InputFilter filtertxt = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isDigit(source.charAt(i))) {
                        Toast.makeText(Setting.this,"No Numbers are allowed inside Quote",Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };

        quote.setFilters(new InputFilter[]{filtertxt,new InputFilter.LengthFilter(40)});
        int position = quote.getText().length();
        Editable editObj= quote.getText();
        Selection.setSelection(editObj, position);
        quote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChanged=true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        temptation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.temptation));
                onResume();
            }
        });
        royalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.royalty));
                onResume();
            }
        });
        babe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.babe));
                onResume();
            }
        });
        boss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.boss));
                onResume();
            }
        });
        vibrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.vibrant));
                onResume();
            }
        });
        sexy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.sexy));
                onResume();
            }
        });
        passion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.putTheme(getString(R.string.passion));
                onResume();
            }
        });

        isChanged=false;
        setMask(dataHelper.getMask());
    }

    private void startSelect_mask(){
        startActivityForResult(new Intent(this,Select_mask.class),RC_SUCCESS);
    }
    /**
     * gets the image url from the activity_partner_call bundle and displays the chosen image
     */
    private void setMask(int maskID){
        image.setImageResource(maskID);
        image.setTag(maskID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SUCCESS) {
            if(resultCode == Activity.RESULT_OK){
                isChanged=true;
                setMask(data.getIntExtra(getString(R.string.mask),R.drawable.dp_1));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    private void startFeedbackActivity(){
        Intent intent =new Intent(this, ContactSupport.class);
        startActivity(intent);
    }
    private void saveData(){
        String q = quote.getText().toString();
        if (q.matches("")) {
            Toast.makeText(Setting.this, "You did not enter a quote", Toast.LENGTH_SHORT).show();
        }
        else {
            dataHelper.putSetting2Firebase((Integer) image.getTag(),String.valueOf(quote.getText()));
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            saveData();
        }
        else
            super.onBackPressed();
    }

    private void onInviteClicked() {
        ProgressDialogFragment.show(getSupportFragmentManager());
        String uid=dataHelper.getUID();
        String sharelinktext  = "https://cinderella1234.page.link/?"+
                "link=https://www.getcinderella.com/?invitedby="+uid+
                "&apn="+ "com.getcinderella.app"+
                "&st="+"Cinderella Invitation Link"+
                "&sd="+"Get 5 Pixies Reward"+
                "&si="+"https://cinderellaapp.000webhostapp.com/ic_launcher.png";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, "Step into the magical world of Cinderella where your persona is only restricted by your imagination. Put on a mask and join the world's first social role-playing game. \n\nBy Clicking this link you will be taken to download the app and on signing up, both of us will get a reward \n\n"+ shortLink.toString());
                            intent.setType("text/plain");
                            Intent chooser = Intent.createChooser(intent, "Share");
                            startActivity(chooser);
                            ProgressDialogFragment.hide(getSupportFragmentManager());
                        } else {
                            // Error
                            // ...
                            ProgressDialogFragment.hide(getSupportFragmentManager());
                            Toast.makeText(Setting.this,"Unexpected error ",Toast.LENGTH_SHORT).show();
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });

    }

}
