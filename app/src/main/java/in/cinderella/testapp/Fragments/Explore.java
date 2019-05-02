package in.cinderella.testapp.Fragments;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.UserListAdapter;

public class Explore extends Fragment {


    private Context mContext = getContext();
    private DatabaseReference reference;

    //widgets
    private EditText mSearchParam;
    private ListView mListView;

    //vars
    private List<UserModel> mUserList;
    private UserListAdapter mAdapter;

    private String TAG="Explore_fragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search, container, false);

        mSearchParam = (EditText) view.findViewById(R.id.search_param);
        mListView = (ListView) view.findViewById(R.id.listView);
            Log.d(TAG, "onCreate: started.");

        initTextListener();
        return view;
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.user_db));

        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });
    }

    private void searchForMatch(String keyword){
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        updateUsersList();
        if(keyword.length() ==0){

        }else {
            Query query =reference.orderByChild(getString(R.string.username)).startAt(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(UserModel.class).toString());

                        mUserList.add(singleSnapshot.getValue(UserModel.class));
                        //update the users list view
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");

        mAdapter = new UserListAdapter(getActivity(), R.layout.layout_user_listitem, mUserList);

        mListView.setAdapter(mAdapter);
    }

}