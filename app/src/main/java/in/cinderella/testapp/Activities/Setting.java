package in.cinderella.testapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;

import in.cinderella.testapp.Fragments.TwoButtonsDialogFragment;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;

public class Setting extends BaseActivity {

    //widgets
    private TextView logout;
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
    private static int REQUEST_INVITE=100;
    private static int RC_SUCCESS=2;
    private boolean isChanged=false;
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
        logout=(TextView) findViewById(R.id.logout);
        invite=(TextView) findViewById(R.id.invite);
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startSigninActivity();
                finish();
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


        setMask(dataHelper.getMask());
    }

    private void startSelect_mask(){
        startActivityForResult(new Intent(this,Select_mask.class),RC_SUCCESS);
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Cinderella App invitation")
                .setMessage("Come to Cinderella")
                .setCallToActionText("Lets go!")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
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
    }//onActivityResult

    private void startSigninActivity(){
        Intent intent =new Intent(this, User_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startFeedbackActivity(){
        Intent intent =new Intent(this, Feedback.class);
        startActivity(intent);
    }
    private void saveData(){
        dataHelper.putQuote(String.valueOf(quote.getText()));
        dataHelper.putMask((Integer) image.getTag());
        Toast.makeText(this,"Saved Settings Successfully",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            TwoButtonsDialogFragment.show(
                    getSupportFragmentManager(),
                    getString(R.string.dlg_save_confirm),
                    new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            saveData();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            dataHelper.putTheme(dataHelper.getPrevTheme());
                            Setting.super.onBackPressed();
                        }
                    });
        }
        else
            super.onBackPressed();
    }
}
