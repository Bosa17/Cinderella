package in.cinderella.testapp.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * created by Bosa
 */
public class FileSearch {

    /**
     * Explore a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        if (listfiles!=null){
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isDirectory()){
                if(getFilePaths(listfiles[i].getAbsolutePath())!=null) {
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }
        }}
        return pathArray;
    }

    /**
     * Explore a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * returns list of image buckets
     * @param mContext
     * @return
     */
    public static ArrayList<String> getImageBuckets(Context mContext){
        ArrayList<String> buckets = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(fisrtImage);
                if (file.exists() && !buckets.contains(bucketPath)) {
                    buckets.add(bucketPath);
                }
            }
            cursor.close();
        }
        return buckets;
    }

    /**
     * returns all the image paths inside the bucket
     * @param mContext
     * @param bucketPath
     * @return
     */
    public static ArrayList<String> getImagesByBucket(Context mContext , @NonNull String bucketPath){

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        ArrayList<String> images = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection,new String[]{bucketPath}, orderBy);

        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(path)) {
                    images.add(path);
                }
            }
            cursor.close();
        }
        return images;
    }

}
