package in.zeitgeist.testapp.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import in.zeitgeist.testapp.Models.UserModel;
import in.zeitgeist.testapp.R;

public class UserListAdapter extends ArrayAdapter<UserModel> {

    private static final String TAG = "UserListAdapter";


    private LayoutInflater mInflater;
    private List<UserModel> mUsers = null;
    private int layoutResource;
    private Context mContext;

    public UserListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<UserModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{
        TextView username;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.search_username);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.username.setText(getItem(position).getUsername());
        return convertView;
    }
}
