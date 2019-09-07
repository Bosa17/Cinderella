package in.cinderella.testapp.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.PaymentsHelper;

public class PurchaseDialog extends BlurPopupWindow {
    private LinearLayout pixies_layout;
    private AnimationDrawable animationDrawable;
    private Button getpixie_100;
    private Button getpixie_200;
    private Button getpixie_300;
    private Button get_premium;
    public PurchaseDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_pixie_purchase, parent, false);
        PaymentsHelper paymentsHelper=new PaymentsHelper(getContext());
        pixies_layout=view.findViewById(R.id.pixies_layout);
        ImageView closeDialog= view.findViewById(R.id.closeDialog);
        getpixie_100=view.findViewById(R.id.getpixie_100);
        getpixie_100.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                paymentsHelper.startPayment("69",100);
            }
        });
        getpixie_200=view.findViewById(R.id.getpixie_200);
        getpixie_200.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                paymentsHelper.startPayment("149",200);
            }
        });
        getpixie_300=view.findViewById(R.id.getpixie_300);
        getpixie_300.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                paymentsHelper.startPayment("209",300);
            }
        });
        get_premium=view.findViewById(R.id.get_premium);
        get_premium.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                paymentsHelper.startPremiumPayment("249");
            }
        });
        closeDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        animationDrawable=(AnimationDrawable) pixies_layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        return view;
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
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected PurchaseDialog createPopupWindow() {
            return new PurchaseDialog(mContext);
        }
    }
}
