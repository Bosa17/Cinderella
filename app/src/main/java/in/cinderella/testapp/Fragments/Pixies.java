package in.cinderella.testapp.Fragments;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.ConnectivityUtils;
import in.cinderella.testapp.Utils.DataHelper;
import com.google.android.gms.ads.reward.RewardedVideoAd;

public class Pixies extends Fragment  {
    private DataHelper dataHelper;
    private Button getpixies;
    private Button buypixies;
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
                if (dataHelper.getAds_watched()<=3)
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

}