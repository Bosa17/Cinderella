package in.lolita.testapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.lolita.testapp.R;
import in.lolita.testapp.Utils.FirebaseHelper;
import in.lolita.testapp.Utils.UniversalImageLoader;


public class Recepie_1 extends Fragment {
    private static final String TAG = "Post_Picture_Next_Fragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;

    //widgets
    private EditText mCaption;
    private ImageView image;

    //vars
    private String mAppend = "file:/";
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_post_picture_next,container,false);
        bundle=this.getArguments();
        image = (ImageView) view.findViewById(R.id.imageShare);
        ImageView shareClose = (ImageView) view.findViewById(R.id.ivBack);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the post fragment.");
                navigateBack();
            }
        });
        firebaseHelper=new FirebaseHelper(getContext());

        TextView share = (TextView) view.findViewById(R.id.ivNext);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                String caption = mCaption.getText().toString();
                uploadPhoto();

            }
        });
        if(bundle!=null)
            setImage();
        return view;
    }


    /**
     * gets the image url from the incoming bundle and displays the chosen image
     */
    private void setImage(){
        imgUrl = bundle.getString(getString(R.string.selected_image));
        Log.d(TAG, "setImage: got new image url: " + imgUrl);
        UniversalImageLoader.setImage(imgUrl, image, null, mAppend,getContext());
    }
    private void navigateBack(){
        Select_picture pp=new Select_picture();
        getFragmentManager().beginTransaction().replace(R.id.nav_main_fragment,pp).commit();
    }

    private void uploadPhoto(){
//        if(bundle!=null){
//            imgUrl = bundle.getString(getString(R.string.selected_image));
//            int count=firebaseHelper.getPostCount();
//            firebaseHelper.uploadNewPost(imgUrl,count,null);
//        }
    }
}


