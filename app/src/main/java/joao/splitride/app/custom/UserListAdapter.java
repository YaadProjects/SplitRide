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
		SegmentHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new SegmentHolder();
		holder.user = items.get(position);
		holder.edit_segment = (ImageButton)row.findViewById(R.id.edit);
		holder.remove_segment = (ImageButton)row.findViewById(R.id.remove);
		holder.edit_segment.setTag(holder.user);
		holder.remove_segment.setTag(holder.user);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}
	
	private void setupItem(SegmentHolder holder) {
		holder.name.setText(""+holder.user.getUsername());
	}
	
	public static class SegmentHolder{
		ParseUser user;
		TextView name;
		ImageButton edit_segment;
		ImageButton remove_segment;
	}	
}
