package in.cinderella.testapp.Fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.cinderella.testapp.Models.RemoteUserConnection;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FileUtils;
import in.cinderella.testapp.Utils.FirebaseHelper;
import in.cinderella.testapp.Utils.Permissions;
import in.cinderella.testapp.Utils.ScratchCardView;

import static org.webrtc.ContextUtils.getApplicationContext;

public class RemoteCardDialog extends BlurPopupWindow {

    private static String mRemoteUserFb_dp;
    private static String mRemoteUserId;
    private static String mRemoteUserName;
    private static long mRemoteUserKarma;
    private Bitmap bmp;
    private RemoteUserConnection remoteUser;
    private DataHelper dataHelper;
    private TextView mUserRemote;
    private TextView mUserRemoteKarma;
    private Button addConnectionConfirm;
    private LinearLayout addKarmaDialog;
    private LinearLayout remoteScv;
    private RatingBar mUserAddKarma;
    private Button addKarmaConfirm;
    private ScratchCardView scv;
    public RemoteCardDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.scratchcard_remote, parent, false);
        scv=view.findViewById(R.id.remoteUserMask_scv);
        remoteUser=new RemoteUserConnection();
        dataHelper=new DataHelper(getContext());
        mUserRemote=view.findViewById(R.id.remoteUserName_scv);
        mUserRemoteKarma=view.findViewById(R.id.remoteUserkarma_scv);
        mUserRemoteKarma.setText("Karma : "+mRemoteUserKarma);
        mUserAddKarma=view.findViewById(R.id.remoteUserAddKarma);
        addKarmaConfirm=view.findViewById(R.id.addKarmaConfirm);
        remoteScv=view.findViewById(R.id.remoteScv);
        addConnectionConfirm=view.findViewById(R.id.addConnectionConfirm);
        addKarmaDialog=view.findViewById(R.id.addKarmaDialog);
        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... voids) {
                bmp = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(mRemoteUserFb_dp).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    final int width = options.outWidth;
                    final int height = options.outHeight;

                    int inSampleSize = 1;
                    if (height > 300 || width > 300) {
                        int halfWidth = width / 2;
                        int halfHeight = height / 2;

                        while ((halfHeight / inSampleSize) >= 300 && (halfWidth / inSampleSize) >= 300
                                && !isCancelled() )
                        {
                            inSampleSize *= 2;
                        }
                    }

                    if (isCancelled()) {
                        return null;
                    }

                    options.inSampleSize = inSampleSize;
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    bmp = BitmapFactory.decodeStream(input,null,options);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                return bmp;
            }

            protected void onPostExecute(Bitmap result) {

                //Add image to ImageView
                if (result!=null) {
                    scv.setImageBitmap(result);
                    addKarmaDialog.setVisibility(VISIBLE);
                }
                else
                    return;

            }


        }.execute();
        scv.setRevealListener(new ScratchCardView.IRevealListener() {
            @Override
            public void onRevealed(ScratchCardView tv) {
                mUserRemote.setVisibility(VISIBLE);
                mUserRemoteKarma.setVisibility(VISIBLE);
                addConnectionConfirm.setVisibility(VISIBLE);
            }

            @Override
            public void onRevealPercentChangedListener(ScratchCardView siv, float percent) {
                if (percent>0.7){
                    siv.reveal();
                }
            }
        });
        addKarmaConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.updateKarmaWithUid(mRemoteUserId,mRemoteUserKarma+(long)mUserAddKarma.getRating());
                addKarmaDialog.setVisibility(GONE);
                remoteScv.setVisibility(VISIBLE);
            }
        });
        addConnectionConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                long conn_no=dataHelper.getConnection()+1;
                dataHelper.putConnection(conn_no);
                remoteUser.setRemoteUserName(mRemoteUserName);
                remoteUser.setRemoteUserKarma(mRemoteUserKarma);
                if (Permissions.hasStoragePermissions(getContext())) {
                    remoteUser.setRemoteUserDp(FileUtils.storeImage(bmp));
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot make connections because permissions not granted", Toast.LENGTH_LONG).show();
                }
                dataHelper.addNewRemoteUser(remoteUser,conn_no);
                dismiss();
            }
        });
        mUserRemote.setText(mRemoteUserName);
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
        public Builder(Context context,String ruid,long karma,String url,String name) {
            super(context);
            mRemoteUserFb_dp=url;
            mRemoteUserId=ruid;
            mRemoteUserKarma=karma;
            mRemoteUserName=name;
            this.setScaleRatio(0.25f).setBlurRadius(8).setTintColor(0x30000000).setDismissOnClickBack(false)
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected RemoteCardDialog createPopupWindow() {
            return new RemoteCardDialog(mContext);
        }
    }
}