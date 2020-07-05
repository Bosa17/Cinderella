package com.getcinderella.app.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import com.getcinderella.app.Activities.PartnerChatActivity;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.DataHelper;
import com.getcinderella.app.Utils.ChatService;

public class PartnerDialog extends BlurPopupWindow {
//    vars
    private static String dp_file;
    private static String remoteUserId;
    private DataHelper dataHelper;
    //    widgets
    private ImageView mRemoteUserDp;
    private ImageView block;
    private ImageView unblock;
    private RelativeLayout connectButton;
    public PartnerDialog(@NonNull Context context) {
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
                if (dataHelper.getPixies()<3 && !dataHelper.getIsPremium()){
                    Toast.makeText(getContext(), R.string.insufficient_pixies, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getContext(), PartnerChatActivity.class);
                    intent.putExtra("remoteUser", remoteUserId);
                    intent.putExtra("userName", dataHelper.getA());
                    intent.putExtra("isPremium",dataHelper.getIsPremium());
                    intent.putExtra("pixies",dataHelper.getPixies());
                    intent.putExtra(ChatService.CHAT_TYPE, "1");
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
        block=(ImageView) view.findViewById(R.id.block);
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.blockUser(remoteUserId);
                checkBlock();
                Toast.makeText(getContext(), "User Blocked", Toast.LENGTH_SHORT).show();
            }
        });
        unblock=(ImageView) view.findViewById(R.id.unblock);
        unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.unblockUser(remoteUserId);
                checkBlock();
                Toast.makeText(getContext(), "User Unblocked", Toast.LENGTH_SHORT).show();
            }
        });

        checkBlock();
        loadBitmap(dp_file);
        if(dataHelper.getIsPremium())
            call_cost.setVisibility(GONE);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setVisibility(VISIBLE);
        return view;
    }
    private void checkBlock(){
        if (dataHelper.getBlockUserCallerId().contains(remoteUserId)){
            unblock.setVisibility(VISIBLE);
            block.setVisibility(GONE);
        }
        else{
            unblock.setVisibility(GONE);
            block.setVisibility(VISIBLE);
        }
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

    public static class Builder extends BlurPopupWindow.Builder<PartnerDialog> {
        public Builder(Context context, String filePath,String uid) {
            super(context);
            dp_file=filePath;
            remoteUserId=uid;
            this.setScaleRatio(0.75f).setBlurRadius(0).setTintColor(ContextCompat.getColor(context,R.color.colorPrimary)).setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(false);
        }

        @Override
        protected PartnerDialog createPopupWindow() {
            return new PartnerDialog(mContext);
        }
    }
}
