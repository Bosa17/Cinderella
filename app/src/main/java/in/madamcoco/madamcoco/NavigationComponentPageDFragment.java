package in.madamcoco.madamcoco;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class NavigationComponentPageDFragment extends NavigationComponentPageAFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvText.setText("D");
    }
}