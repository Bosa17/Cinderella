package in.cinderella.testapp.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.cinderella.testapp.Activities.Feedback;
import in.cinderella.testapp.Activities.PartnerCallActivity;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.DecodeBitmapTask;
import in.cinderella.testapp.Utils.SinchService;

public class RemoteConnectionCardDialog extends BlurPopupWindow {
//    vars
    private static String dp_file;
    private static String remoteUserId;
    private DataHelper dataHelper;
    //    widgets
    private ImageView mRemoteUserDp;
    private Button connectButton;
    public RemoteConnectionCardDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_remote_connect_dialog, parent, false);
        dataHelper=new DataHelper(getContext());
        LinearLayout call_cost=view.findViewById(R.id.call_cost);
        mRemoteUserDp=view.findViewById(R.id.remoteUserConnectDp);
        connectButton=view.findViewById(R.id.call_partner);
        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (dataHelper.getPixies()<3){
                    new PurchaseDialog.Builder(getContext()).build().show();
                    Toast.makeText(getContext(), R.string.insufficient_pixies, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getContext(), PartnerCallActivity.class);
                    intent.putExtra("remoteUser", remoteUserId);
                    intent.putExtra("userName", dataHelper.getUsername());
                    intent.putExtra("pixies",dataHelper.getPixies());
                    intent.putExtra(SinchService.CALL_TYPE, "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            }
        });
        ImageView closeDialog= view.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        loadBitmap(dp_file);
        if(dataHelper.getIsPremium())
            call_cost.setVisibility(GONE);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setVisibility(VISIBLE);
        return view;
    }
    private void loadBitmap(String filePath) {
        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bmp;
                try {
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

                    bmp = BitmapFactory.decodeFile(filePath,options);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return bmp;
            }


            protected void onPostExecute(Bitmap result) {

                //Add image to ImageView
                if (result!=null) {
                    mRemoteUserDp.setImageBitmap(result);
                }
                else
                    return;

            }


        }.execute();
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

    public static class Builder extends BlurPopupWindow.Builder<RemoteConnectionCardDialog> {
        public Builder(Context context, String filePath,String uid) {
            super(context);
            dp_file=filePath;
            remoteUserId=uid;
            this.setScaleRatio(0.75f).setBlurRadius(0).setTintColor(context.getColor(R.color.colorPrimary))
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected RemoteConnectionCardDialog createPopupWindow() {
            return new RemoteConnectionCardDialog(mContext);
        }
    }
}