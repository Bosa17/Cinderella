package in.cinderella.testapp.Fragments;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.SliderAdapter;

public class Pixies extends Fragment {

    //vars
    private DataHelper dataHelper;
    private int currentPosition;
    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private TextSwitcher nameSwitcher;
    private TextSwitcher karmaSwitcher;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_pixies, container, false);
        dataHelper=new DataHelper(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final ArrayList<String> pics = dataHelper.getRemoteUserDps();
        final SliderAdapter sliderAdapter = new SliderAdapter(pics,pics.size(), new OnCardClickListener());
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        new CardSnapHelper().attachToRecyclerView(recyclerView);
        nameSwitcher=view.findViewById(R.id.name_switcher);
        nameSwitcher.setFactory(new TextViewFactory(R.style.NameTextView, true));
        nameSwitcher.setCurrentText("Rimjhim");
        karmaSwitcher=view.findViewById(R.id.karma_switcher);
        karmaSwitcher.setFactory(new TextViewFactory(R.style.KarmaTextView, true));
        karmaSwitcher.setCurrentText("Karma : 69");
        return view;
    }


    private void onActiveCardChange() {

    }


    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
           //TODO
        }
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(getContext());

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(getContext(), styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }


}