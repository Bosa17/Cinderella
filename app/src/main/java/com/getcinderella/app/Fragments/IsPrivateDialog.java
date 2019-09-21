package com.getcinderella.app.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import com.getcinderella.app.R;

public class IsPrivateDialog extends BlurPopupWindow {
    private LinearLayout isPrivate;
    private LinearLayout isNotPrivate;
    private Button cont;
    private Button cont2;
    private static boolean isPrivateMode;
    public IsPrivateDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_private_mode_dialog, parent, false);
        isPrivate= view.findViewById(R.id.isPrivate);
        isNotPrivate=view.findViewById(R.id.isNotPrivate);
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
        if (!isPrivateMode){
            isPrivate.setVisibility(GONE);
            isNotPrivate.setVisibility(VISIBLE);
        }
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setVisibility(INVISIBLE);
        return view;
    }
    private void bye(){
        dismiss();
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

    public static class Builder extends BlurPopupWindow.Builder<IsPrivateDialog> {
        public Builder(Context context,boolean pm) {
            super(context);
            isPrivateMode=pm;
            this.setScaleRatio(0.25f).setBlurRadius(0).setTintColor(context.getColor(R.color.colorPrimary))
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected IsPrivateDialog createPopupWindow() {
            return new IsPrivateDialog(mContext);
        }
    }
}
