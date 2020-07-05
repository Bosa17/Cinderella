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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.getcinderella.app.Activities.MainActivity;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;
import java.util.List;

public class Pixies extends Fragment  {
    private DataHelper dataHelper;
    private Button getpixies;
    private Button inviteNEarn;
    private Button getpixie_100;
    private Button getpixie_200;
    private Button getpixie_300;
    private BillingClient billingClient;
    List<String> skuList;
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
        skuList = new ArrayList<>();
        skuList.add("100_pixies");
        skuList.add("200_pixies");
        skuList.add("300_pixies");
        dataHelper=new DataHelper(getContext());
        ImageView closeDialog= view.findViewById(R.id.closeDialog);
        getpixie_100=view.findViewById(R.id.getpixie_100);
        getpixie_200=view.findViewById(R.id.getpixie_200);
        getpixie_300=view.findViewById(R.id.getpixie_300);
        inviteNEarn=view.findViewById(R.id.inviteNEarn);
        inviteNEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInviteClicked();
            }
        });
        setupBilling();
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

    public void setupBilling(){
        billingClient = BillingClient.newBuilder(getContext())
                .setChildDirected(BillingClient.ChildDirected.CHILD_DIRECTED)
                .enablePendingPurchases()
                .setUnderAgeOfConsent(BillingClient.UnderAgeOfConsent.UNDER_AGE_OF_CONSENT)
                .setListener(new MyPurchaseUpdateListener())
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("lol","Billing Client connected");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    Log.d("lol",billingResult.getResponseCode()+"");
                                    Log.d("lol",skuDetailsList.toString());
                                    // Process the result.
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            Log.d("lol",sku);
                                            switch(sku){
                                                case "100_pixies":getpixie_100.setText(price);
                                                    getpixie_100.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            billingClient.launchBillingFlow(getActivity(), BillingFlowParams.newBuilder()
                                                                    .setSkuDetails(skuDetails)
                                                                    .build());

                                                        }
                                                    });
                                                    break;
                                                case "200_pixies":getpixie_200.setText(price);
                                                    getpixie_200.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            billingClient.launchBillingFlow(getActivity(),BillingFlowParams.newBuilder()
                                                                    .setSkuDetails(skuDetails)
                                                                    .build());

                                                        }
                                                    });
                                                    break;
                                                case "300_pixies":getpixie_300.setText(price);
                                                    getpixie_300.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            billingClient.launchBillingFlow(getActivity(),BillingFlowParams.newBuilder()
                                                                    .setSkuDetails(skuDetails)
                                                                    .build());

                                                        }
                                                    });
                                                    break;
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    public void handlePurchase(Purchase purchase){
        Log.d("lol",purchase.getPurchaseState()+"");
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            switch(purchase.getSku()){
                case"100_pixies": dataHelper.addPixies(100);
                    new BillingAckDialog.Builder(getContext(),false,100).build().show();
                    ((MainActivity)getActivity()).refreshHome();
                    break;
                case"200_pixies": dataHelper.addPixies(200);
                    new BillingAckDialog.Builder(getContext(),false,200).build().show();
                    ((MainActivity)getActivity()).refreshHome();
                    break;
                case"300_pixies": dataHelper.addPixies(300);
                    new BillingAckDialog.Builder(getContext(),false,300).build().show();
                    ((MainActivity)getActivity()).refreshHome();
                    break;
            }
            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(BillingResult billingResult, String s) {

                    }
                });
            }
        } else{
            Log.d("lol","failed transaction");
            new BillingAckDialog.Builder(getContext(),true,0).build().show();
        }
    }

    public class MyPurchaseUpdateListener implements PurchasesUpdatedListener {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                new BillingAckDialog.Builder(getContext(),true,0).build().show();
            } else {
                new BillingAckDialog.Builder(getContext(),true,0).build().show();
            }
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