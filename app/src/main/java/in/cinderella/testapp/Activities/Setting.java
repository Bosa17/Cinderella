package in.cinderella.testapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.cinderella.testapp.Fragments.TwoButtonsDialogFragment;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;

public class Setting extends AppCompatActivity {
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;

    //widgets
    private Button logout;
    private EditText mCaption;
    private ImageView image;
    private DataHelper dataHelper;

    //vars
    private static int RC_SUCCESS=2;
    private Integer imgUrl;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dataHelper=new DataHelper(this);
        logout=(Button) findViewById(R.id.logout);
        image = (ImageView) findViewById(R.id.imageShare);
        ImageView back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        firebaseHelper=new FirebaseHelper(this);
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
        TextView share = (TextView) findViewById(R.id.ivSave);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        setMask(dataHelper.getMask());
    }

    private void startSelect_mask(){
        startActivityForResult(new Intent(this,Select_mask.class),RC_SUCCESS);
    }
    /**
     * gets the image url from the incoming bundle and displays the chosen image
     */
    private void setMask(int maskID){
        image.setImageResource(maskID);
        image.setTag(maskID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SUCCESS) {
            if(resultCode == Activity.RESULT_OK){
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

    private void saveData(){
        dataHelper.putMask((Integer) image.getTag());
        Toast.makeText(this,"Saved Settings Successfully",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
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
                        Setting.super.onBackPressed();
                    }
                });
    }
}
