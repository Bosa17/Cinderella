package in.zeitgeist.testapp.Fragments;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.zeitgeist.testapp.Activities.MainActivity;
import in.zeitgeist.testapp.R;
import in.zeitgeist.testapp.Utils.FirebaseHelper;
import in.zeitgeist.testapp.Utils.UniversalImageLoader;


public class Post_picture_next extends Fragment {
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
    private int imageCount = 0;
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
        if(bundle!=null)
            setImage();
        return view;
    }


//    /**
//     * gets the image url from the incoming bundle and displays the chosen image
//     */
    private void setImage(){
        imgUrl = bundle.getString(getString(R.string.selected_image));
        Log.d(TAG, "setImage: got new image url: " + imgUrl);
        UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
    }
    private void navigateBack(){
        Post_picture pp=new Post_picture();
        getFragmentManager().beginTransaction().replace(R.id.nav_post_fragment,pp).commit();
    }
}


