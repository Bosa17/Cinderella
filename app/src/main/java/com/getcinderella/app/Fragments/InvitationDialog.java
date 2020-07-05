package com.getcinderella.app.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import com.getcinderella.app.R;
import com.getcinderella.app.Utils.StringUtils;

public class InvitationDialog extends BlurPopupWindow {
    public static String name;

    public InvitationDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_invitation, parent, false);
        Button cont=view.findViewById(R.id.cont);
        cont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView invitation_txt=view.findViewById(R.id.invitation_txt);
        invitation_txt.setText(StringUtils.extractFirstName(name)+" is happy that you joined the world's first Social Role-Playing Game\n\nYou both get 5 Pixies");
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
    protected ObjectAnimator createShowAnimator() {
        return null;
    }

    @Override
    protected ObjectAnimator createDismissAnimator() {
        int height = getContentView().getMeasuredHeight();
        return ObjectAnimator.ofFloat(getContentView(), "translationY", 0, height).setDuration(getAnimationDuration());
    }

    public static class Builder extends BlurPopupWindow.Builder<InvitationDialog> {
        public Builder(Context context,String username) {
            super(context);
            name=username;
            this.setScaleRatio(0.25f).setBlurRadius(0).setTintColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected InvitationDialog createPopupWindow() {
            return new InvitationDialog(mContext);
        }
    }
}
