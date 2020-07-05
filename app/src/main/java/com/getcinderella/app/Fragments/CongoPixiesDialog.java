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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import com.getcinderella.app.R;

public class CongoPixiesDialog extends BlurPopupWindow {
    private Button cont;
    private static boolean isPremium;
    public CongoPixiesDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_congo_pixies, parent, false);
        cont=view.findViewById(R.id.cont);
        cont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView congo_pixies=view.findViewById(R.id.congo_pixies);
        if (isPremium)
            congo_pixies.setText("7");
        else
            congo_pixies.setText("3");
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

    public static class Builder extends BlurPopupWindow.Builder<CongoPixiesDialog> {
        public Builder(Context context,boolean isP) {
            super(context);
            isPremium=isP;
            this.setScaleRatio(0.25f).setBlurRadius(0).setTintColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected CongoPixiesDialog createPopupWindow() {
            return new CongoPixiesDialog(mContext);
        }
    }
}
