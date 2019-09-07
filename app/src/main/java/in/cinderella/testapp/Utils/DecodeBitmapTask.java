package in.cinderella.testapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;


public class DecodeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private final String filePath;
    private final int reqWidth;
    private final int reqHeight;

    private final Reference<Listener> refListener;

    public interface Listener {
        void onPostExecuted(Bitmap bitmap);
    }

    public DecodeBitmapTask( String filePath,
                            int reqWidth, int reqHeight,
                            @NonNull Listener listener)
    {
        this.filePath = filePath;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.refListener = new WeakReference<>(listener);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            final int width = options.outWidth;
            final int height = options.outHeight;

            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                int halfWidth = width / 2;
                int halfHeight = height / 2;

                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth
                        && !isCancelled()) {
                    inSampleSize *= 2;
                }
            }

            if (isCancelled()) {
                return null;
            }

            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            final Bitmap decodedBitmap = BitmapFactory.decodeFile(filePath, options);

            final Bitmap result;
            result = decodedBitmap;

            return result;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    final protected void onPostExecute(Bitmap bitmap) {
        final Listener listener = this.refListener.get();
        if (listener != null) {
            listener.onPostExecuted(bitmap);
        }
    }

}