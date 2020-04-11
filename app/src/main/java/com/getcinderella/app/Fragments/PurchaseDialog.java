package com.getcinderella.app.Fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.getcinderella.app.Activities.MainActivity;
import com.getcinderella.app.Utils.DataHelper;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import com.getcinderella.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurchaseDialog extends BlurPopupWindow {
    private LinearLayout pixies_layout;
    private AnimationDrawable animationDrawable;
    private Button getpixie_100;
    private Button getpixie_200;
    private Button getpixie_300;
    private Button get_premium;
    private DataHelper dataHelper;
    private BillingClient billingClient;
    List<String> skuList;
    public PurchaseDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_pixie_purchase, parent, false);
        pixies_layout=view.findViewById(R.id.pixies_layout);
        skuList = new ArrayList<>();
        skuList.add("100_pixies");
        skuList.add("200_pixies");
        skuList.add("300_pixies");
        dataHelper=new DataHelper(getContext());
        ImageView closeDialog= view.findViewById(R.id.closeDialog);
        getpixie_100=view.findViewById(R.id.getpixie_100);
        getpixie_200=view.findViewById(R.id.getpixie_200);
        getpixie_300=view.findViewById(R.id.getpixie_300);
        get_premium=view.findViewById(R.id.get_premium);
        closeDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setupBilling();
        animationDrawable=(AnimationDrawable) pixies_layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        return view;
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
                                                    getpixie_100.setOnClickListener(new OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            billingClient.launchBillingFlow(getActivity(),BillingFlowParams.newBuilder()
                                                                    .setSkuDetails(skuDetails)
                                                                    .build());

                                                        }
                                                    });
                                                    break;
                                                case "200_pixies":getpixie_200.setText(price);
                                                    getpixie_200.setOnClickListener(new OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            billingClient.launchBillingFlow(getActivity(),BillingFlowParams.newBuilder()
                                                                    .setSkuDetails(skuDetails)
                                                                    .build());

                                                        }
                                                    });
                                                    break;
                                                case "300_pixies":getpixie_300.setText(price);
                                                    getpixie_300.setOnClickListener(new OnClickListener() {
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

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
    public void handlePurchase(Purchase purchase){
        Log.d("lol",purchase.getPurchaseState()+"");
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            switch(purchase.getSku()){
                case"100_pixies": dataHelper.addPixies(100);
                    new BillingAckDialog.Builder(getContext(),false,100).build().show();
                    ((MainActivity)getActivity()).refreshHome();
                    dismiss();
                    break;
                case"200_pixies": dataHelper.addPixies(200);
                    new BillingAckDialog.Builder(getContext(),false,200).build().show();
                    ((MainActivity)getActivity()).refreshHome();
                    dismiss();
                    break;
                case"300_pixies": dataHelper.addPixies(300);
                    new BillingAckDialog.Builder(getContext(),false,300).build().show();
                    ((MainActivity)getActivity()).refreshHome();
                    dismiss();
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

    public void startPremiumPayment(String TXN_AMOUNT){
        //next version
    }

    public class MyPurchaseUpdateListener implements PurchasesUpdatedListener{
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK)
            dismiss();
        return super.onKeyDown(keyCode, event);
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

    public static class Builder extends BlurPopupWindow.Builder<PurchaseDialog> {
        public Builder(Context context) {
            super(context);
            this.setScaleRatio(0.25f).setBlurRadius(0).setTintColor(context.getColor(R.color.colorPrimaryLight))
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected PurchaseDialog createPopupWindow() {
            return new PurchaseDialog(mContext);
        }
    }
}
