package in.cinderella.testapp.Utils;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class AdUtil {
    private RewardedVideoAd mRewardedVideoAd;
    public AdUtil(Context context){
        MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(new mRewardedVideoAdListener());
    }
    public void setmRewardedVideoAdListener(RewardedVideoAdListener listener){
        mRewardedVideoAd.setRewardedVideoAdListener(listener);
    }
    public void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }
    public void showmRewardVideoAd(){
        mRewardedVideoAd.show();
    }
    private class mRewardedVideoAdListener implements RewardedVideoAdListener{
        @Override
        public void onRewardedVideoAdLoaded() {
            showmRewardVideoAd();
        }

        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoStarted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {

        }

        @Override
        public void onRewarded(RewardItem rewardItem) {

        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {

        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    }
}
