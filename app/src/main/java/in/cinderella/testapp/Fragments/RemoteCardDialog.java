package in.cinderella.testapp.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.ScratchCardView;

public class RemoteCardDialog extends BlurPopupWindow {

    private ScratchCardView scv;
    private ImageView close_scv;
    public RemoteCardDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        final String urlImage = "https://graph.facebook.com/v3.2/1966656470064216/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D";
        View view = LayoutInflater.from(getContext()).inflate(R.layout.scratchcard_remote, parent, false);
        scv=view.findViewById(R.id.remoteUserMask_scv);
        close_scv=view.findViewById(R.id.close_scv);
        new AsyncTask<String, Integer, Drawable>(){

            @Override
            protected Drawable doInBackground(String... strings) {
                Bitmap bmp = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bmp = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new BitmapDrawable(bmp);
            }

            protected void onPostExecute(Drawable result) {

                //Add image to ImageView
                if (result!=null)
                    scv.setImageDrawable(result);
                else
                    return;

            }


        }.execute();
        scv.setRevealListener(new ScratchCardView.IRevealListener() {
            @Override
            public void onRevealed(ScratchCardView tv) {
                // on reveal
            }

            @Override
            public void onRevealPercentChangedListener(ScratchCardView siv, float percent) {
                if (percent>0.7){
                    siv.reveal();
                }
            }
        });
        close_scv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
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

    public static class Builder extends BlurPopupWindow.Builder<RemoteCardDialog> {
        public Builder(Context context) {
            super(context);
            this.setScaleRatio(0.25f).setBlurRadius(8).setTintColor(0x30000000).setDismissOnClickBack(false)
                    .setDismissOnTouchBackground(false).setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(BlurPopupWindow popupWindow) {

                }
            });
        }

        @Override
        protected RemoteCardDialog createPopupWindow() {
            return new RemoteCardDialog(mContext);
        }
    }
}