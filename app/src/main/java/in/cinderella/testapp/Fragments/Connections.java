package in.cinderella.testapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;

public class Connections extends Fragment {
    private DataHelper dataHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataHelper=new DataHelper(getContext());
        View view=inflater.inflate(R.layout.fragment_connections, container, false);
        Button btn=view.findViewById(R.id.showpopup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RemoteCardDialog.Builder(getActivity()).build().show();
            }
        });

        return view;
    }
}
