package joao.splitride.app.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import joao.splitride.R;


public class UserListAdapter extends ArrayAdapter<ParseUser> {

	private List<ParseUser> items;
	private int layoutResourceId;
	private Context context;


	public UserListAdapter(Context context, int layoutResourceId, List<ParseUser> items){
		super(context, layoutResourceId, items);
		
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent){
		
		View row = convertView;
		UserHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new UserHolder();
		holder.user = items.get(position);
		holder.remove_user = (ImageButton) row.findViewById(R.id.remove);
		holder.remove_user.setTag(holder.user);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}

	private void setupItem(UserHolder holder) {
		holder.name.setText(""+holder.user.getUsername());
	}

	public static class UserHolder {
		ParseUser user;
		TextView name;
		ImageButton remove_user;
	}	
}
