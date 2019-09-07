package in.cinderella.testapp.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;

public class RewardedVideoAdDialog extends BlurPopupWindow implements RewardedVideoAdListener {
    public RewardedVideoAdDialog(@NonNull Context context) {
        super(context);
    }
    private DataHelper dataHelper;
    private RewardedVideoAd mRewardedVideoAd;
    private LinearLayout adLoading;
    private LinearLayout congo;
    private LinearLayout ohoh;
    private TextView congo_pixies;
    private TextView msg;
    private Button watch_more_btn;
    private Button cont;
    private Button cont2;
    private boolean rewarded=false;
    private int pixies_won;

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_ad_dialog, parent, false);
        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        dataHelper=new DataHelper(getContext());
        congo= view.findViewById(R.id.congo);
        ohoh=view.findViewById(R.id.ohoh);
        msg=view.findViewById(R.id.msg);
        adLoading=view.findViewById(R.id.adLoading);
        ImageView closeDialog= view.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bye();
            }
        });
        congo_pixies= view.findViewById(R.id.congo_pixies);
        watch_more_btn=view.findViewById(R.id.watch_more);
        watch_more_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (dataHelper.getAds_watched()<=3)
                    watch_more();
                else
                    Toast.makeText(getContext(), "lol", Toast.LENGTH_SHORT).show();
            }
        });
        cont=view.findViewById(R.id.cont);
        cont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bye();
            }
        });
        cont2=view.findViewById(R.id.cont2);
        cont2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bye();
            }
        });
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setVisibility(INVISIBLE);
        return view;
    }
    private void bye(){
        mRewardedVideoAd.destroy(getContext());
        dismiss();
    }
    public void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }


    @Override
    protected void onShow() {
        super.onShow();
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);

                getContentView().setVisibility(VISIBLE);
                int height = getContentView().getMeasuredHeight();
                ObjectAnimator.ofFloat(getContentView(), "translationY", height, 0).setDuration(getAnimationDuration()).start();
            }
        });
    }

    private void congo(){
        adLoading.setVisibility(GONE);
        congo.setVisibility(VISIBLE);
        if (pixies_won==0)
            pixies_won=1;
        congo_pixies.setText(String.valueOf(pixies_won));

    }
    private void failed2load(){
        adLoading.setVisibility(GONE);
        ohoh.setVisibility(VISIBLE);
    }
    private void watch_more(){
        congo.setVisibility(GONE);
        adLoading.setVisibility(VISIBLE);
        loadRewardedVideoAd();
    }
    private void watchfull(){
        adLoading.setVisibility(GONE);
        ohoh.setVisibility(VISIBLE);
        msg.setText("Wath the full ad to earn pixies");
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (rewarded){
            congo();
            dataHelper.Ads_watched();
        }
        else
            watchfull();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        rewarded=true;
        pixies_won=dataHelper.rewardPixies();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        failed2load();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    protected ObjectAnimator createShowAnimator() {
        return null;
    }

    @Override
    protected ObjectAnimator createDismissAnimator() {
        int height = getContentView().getMeasuredHeight();
        return ObjectAnimator.ofFloat(getContentView(), "translationY", 0, height).setDuration(getAnimationDuration());
    }

    public static class Builder extends BlurPopupWindow.Builder<RewardedVideoAdDialog> {
        public Builder(Context context) {
            super(context);
            this.setScaleRatio(0.25f).setBlurRadius(0).setTintColor(context.getColor(R.color.colorPrimary)).setDismissOnClickBack(false)
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected RewardedVideoAdDialog createPopupWindow() {
            return new RewardedVideoAdDialog(mContext);
        }
    }
}
