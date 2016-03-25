package joao.splitride.app.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;


public class SearchUserListAdapter extends ArrayAdapter<ParseUser> {

    public ArrayList<ParseUser> selected = new ArrayList<ParseUser>();
    private List<ParseUser> items;
    private int layoutResourceId;
    private Context context;
    private ArrayList<Boolean> status = new ArrayList<Boolean>();

    public SearchUserListAdapter(Context context, int layoutResourceId, List<ParseUser> items) {
        super(context, layoutResourceId, items);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;

        for (int i = 0; i < items.size(); i++) {
            status.add(false);
        }
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        UserHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new UserHolder();
        holder.user = items.get(position);
        holder.checkbox = (CheckBox) row.findViewById(R.id.user_checkbox);
        holder.checkbox.setTag(holder.user);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status.set(position, true);
                    selected.add(items.get(position));
                } else {
                    status.set(position, false);
                    selected.remove(items.get(position));
                }
            }
        });
        holder.checkbox.setChecked(status.get(position));

        holder.name = (TextView) row.findViewById(R.id.line_name);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(UserHolder holder) {
        holder.name.setText("" + holder.user.getUsername());
    }


    public static class UserHolder {
        ParseUser user;
        TextView name;
        CheckBox checkbox;
    }
}
