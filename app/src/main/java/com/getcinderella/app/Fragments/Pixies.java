package com.getcinderella.app.Fragments;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class Pixies extends Fragment  {
    private DataHelper dataHelper;
    private Button getpixies;
    private Button buypixies;
    private Button inviteNEarn;
    private LinearLayout pixies_layout;
    private AnimationDrawable animationDrawable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_pixies, container, false);

        dataHelper=new DataHelper(getContext());
        pixies_layout=view.findViewById(R.id.pixies_layout);
        getpixies=view.findViewById(R.id.getpixie);
        getpixies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataHelper.getAds_watched()<3)
                    new RewardedVideoAdDialog.Builder(getContext()).build().show();
                else
                    Toast.makeText(getContext(), "You can only watch 3 ads every hour", Toast.LENGTH_SHORT).show();
            }
        });
        buypixies=view.findViewById(R.id.buypixie);
        buypixies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PurchaseDialog.Builder(getContext()).build().show();
            }
        });
        inviteNEarn=view.findViewById(R.id.inviteNEarn);
        inviteNEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInviteClicked();
            }
        });
        animationDrawable=(AnimationDrawable) pixies_layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
    }

    private void onInviteClicked() {
        ProgressDialogFragment.show(getFragmentManager());
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
                .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
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
                            ProgressDialogFragment.hide(getFragmentManager());
                        } else {
                            // Error
                            // ...
                            ProgressDialogFragment.hide(getFragmentManager());
                            Toast.makeText(getActivity(),"Unexpected error ",Toast.LENGTH_SHORT).show();
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });

    }

}