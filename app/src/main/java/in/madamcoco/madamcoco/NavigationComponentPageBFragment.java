package in.madamcoco.madamcoco;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class NavigationComponentPageBFragment extends NavigationComponentPageAFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvText.setText("B");
    }
}