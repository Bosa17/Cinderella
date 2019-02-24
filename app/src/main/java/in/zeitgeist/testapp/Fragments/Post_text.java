package in.zeitgeist.testapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.zip.Inflater;

import in.zeitgeist.testapp.Activities.MainActivity;
import in.zeitgeist.testapp.R;


public class Post_text extends Fragment {
    private static String TAG="Post_text_fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_post_text,container,false);
        EditText post =(EditText) view.findViewById(R.id.post_text);
        ImageView shareClose = (ImageView) view.findViewById(R.id.ivClosePost);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the post fragment.");
                startMainActivity();
                getActivity().finish();
            }
        });
        return view;
    }
    private void startMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
