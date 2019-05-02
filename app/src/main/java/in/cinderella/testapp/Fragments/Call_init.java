package in.cinderella.testapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import in.cinderella.testapp.Activities.Call;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.ShakeListener;

public class Call_init extends Fragment {

    private ShakeListener mShaker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_call_init, container, false);
        final Vibrator vibe = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);

        mShaker = new ShakeListener(getContext());
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
            public void onShake()
            {
                startActivity(new Intent(getActivity(), Call.class));
                vibe.vibrate(100);
                Toast.makeText(getContext(),"Starting Call!",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
    @Override
    public void onResume()
    {
        mShaker.resume();
        super.onResume();
    }
    @Override
    public void onPause()
    {
        mShaker.pause();
        super.onPause();
    }
}
