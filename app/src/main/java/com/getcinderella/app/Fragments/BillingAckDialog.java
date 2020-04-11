package com.getcinderella.app.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import com.getcinderella.app.R;

public class BillingAckDialog extends BlurPopupWindow {
    private Button cont;
    private static boolean isFailed;
    private static int pixies_bought;
    public BillingAckDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_billing_ack, parent, false);
        cont=view.findViewById(R.id.cont);
        cont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button cont2=view.findViewById(R.id.cont2);
        cont2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        LinearLayout congo=view.findViewById(R.id.congo);
        LinearLayout failed=view.findViewById(R.id.failed);
        if (isFailed)
        {
            congo.setVisibility(GONE);
            failed.setVisibility(VISIBLE);
        }
        else {
            TextView ack_pixies = view.findViewById(R.id.ack_pixies);
            ack_pixies.setText(pixies_bought + "");
        }
        ImageView closeDialog= view.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setVisibility(INVISIBLE);
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

    public static class Builder extends BlurPopupWindow.Builder<BillingAckDialog> {
        public Builder(Context context,boolean isFail, int pixies) {
            super(context);
            isFailed=isFail;
            pixies_bought=pixies;
            this.setScaleRatio(0.25f).setBlurRadius(0).setTintColor(context.getColor(R.color.colorPrimary))
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected BillingAckDialog createPopupWindow() {
            return new BillingAckDialog(mContext);
        }
    }
}
