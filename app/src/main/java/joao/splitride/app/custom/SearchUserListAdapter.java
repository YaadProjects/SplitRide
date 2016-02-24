package joao.splitride.app.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import joao.splitride.R;


public class SearchUserListAdapter extends ArrayAdapter<ParseUser> {

    private List<ParseUser> items;
    private int layoutResourceId;
    private Context context;


    public SearchUserListAdapter(Context context, int layoutResourceId, List<ParseUser> items) {
        super(context, layoutResourceId, items);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        SegmentHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new SegmentHolder();
        holder.user = items.get(position);
        holder.checkbox = (CheckBox) row.findViewById(R.id.user_checkbox);
        holder.checkbox.setTag(holder.user);

        holder.name = (TextView) row.findViewById(R.id.line_name);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(SegmentHolder holder) {
        holder.name.setText("" + holder.user.getUsername());
    }

    public static class SegmentHolder {
        ParseUser user;
        TextView name;
        CheckBox checkbox;
    }
}
