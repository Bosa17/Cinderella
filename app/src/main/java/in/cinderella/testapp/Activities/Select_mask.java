package in.cinderella.testapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.GridImageAdapter;
import in.cinderella.testapp.Utils.MaskSelector;
import in.cinderella.testapp.Utils.Permissions;

public class Select_mask extends AppCompatActivity {

    private static String TAG = "Select_Mask";
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private GridView gridView;
    private ImageView galleryImage;


    //vars
    private Integer mSelectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mask);
        galleryImage = (ImageView) findViewById(R.id.galleryImageView);
        gridView = (GridView) findViewById(R.id.gridView);
        Log.d(TAG, "onCreateView: started.");

        TextView nextScreen = (TextView) findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                returnWithSelectedImage();

            }
        });
        setupGridView();
    }


    private void returnWithSelectedImage(){
        Log.d(TAG, "onClick: navigating to the final share screen.");
        Intent intent =new Intent().putExtra(getString(R.string.mask),mSelectedImage);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    private void setupGridView() {
        Log.d(TAG, "setupGridView: directory chosen: ");
        final Integer[] imgURLs= MaskSelector.maskURLs ;

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,  imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try {
            setImage(imgURLs[0], galleryImage);
            mSelectedImage = imgURLs[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs[position]);

                setImage(imgURLs[position], galleryImage);
                mSelectedImage = imgURLs[position];
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Select Mask and press Next", Toast.LENGTH_SHORT).show();
    }

    private void setImage(Integer imgURL, ImageView image) {
        Log.d(TAG, "setImage: setting image");
        image.setImageResource(imgURL);

    }
}
