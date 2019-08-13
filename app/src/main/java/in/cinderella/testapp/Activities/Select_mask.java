package in.cinderella.testapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.GridImageAdapter;
import in.cinderella.testapp.Utils.MaskSelector;

public class Select_mask extends BaseActivity {

    private static String TAG = "Select_Mask";
    private static final int NUM_GRID_COLUMNS = 3;
    private DataHelper dataHelper;

    //widgets
    private GridView gridView;
    private ImageView maskImage;


    //vars
    private Integer mSelectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mask);
        dataHelper=new DataHelper(this);
        maskImage = (ImageView) findViewById(R.id.galleryImageView);
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
        final Integer[] masks= MaskSelector.maskURLs ;

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,  masks);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try {
            setImage(dataHelper.getMask());
            mSelectedImage = dataHelper.getMask();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + masks[position]);

                setImage(masks[position]);
                mSelectedImage = masks[position];
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Select Mask and press Next", Toast.LENGTH_SHORT).show();
    }

    private void setImage(int mask) {
        Log.d(TAG, "setImage: setting image");
        maskImage.setImageResource(mask);
    }
}
